package org.myrobotlab.kotlin.framework

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.websocket.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.filter
import org.myrobotlab.kotlin.service.Runtime
import org.myrobotlab.kotlin.utils.Url
import kotlin.properties.Delegates

/**
 * Handles serialization and
 * deserialization on a platform-specific
 * basis.
 */
expect class JsonSerde() {
    inline fun <reified T> deserialize(json: String): T
    fun <T> serialize(o: T): String
}

/**
 * Basic Logger interface.
 *
 * FIXME replace with cross-platform
 *  logging library
 */
interface Logger {
    fun info(toLog: String)
}

/**
 * Primary entrypoint into the client subsystem
 * of mrlkt. This object handles all websocket connections
 * and synchronous service calls, as well as managing the
 * system event bus.
 *
 * @author AutonomicPerfectionist
 */
object MrlClient {
    /**
     * Bus that routes messages destined for
     * services running on this mrlkt instance.
     * Each service has an inbox coroutine
     * that takes messages from this bus, filtered
     * by the message destination name. A message
     * with the method of `"shutdown"` will
     * be sent to indicate the inbox shutdown
     * of the destination service.
     */
    val eventBus = MutableSharedFlow<Message>()

    var connectionFailedListener: (e: Exception) -> Unit = {}

    /**
     * Function that is called when [connected]
     * changes.
     *
     * FIXME should be replaced with service pub/sub
     */
    var connectedListener: (isConnected: Boolean) -> Unit = {}

    /**
     * Whether this client has an active websocket
     * connection to an MRL instance.
     */
    var connected by Delegates.observable(false) { _, _, new ->
        if (new == false)
            remoteId = null
        connectedListener(new)
    }
        internal set

    var remoteId: String? = null
        internal set


    /**
     * [Job] returned by starting the websocket
     * coroutines.
     */
    private var websocketJob: Job? = null

    /**
     * The URL pointing to an MRL instance.
     * If set while connected, will throw [IllegalStateException].
     */
    var url = Url("localhost", 8888)
        set(value) {
            if(connected) {
                throw IllegalStateException("Cannot change url while connected, disconnect first")
            }
            field = value
        }

    /**
     * The logger for this client
     */
    var logger: Logger = object : Logger {
        override fun info(toLog: String) {
            println(toLog)
        }
    }
    val serde: JsonSerde = JsonSerde()
    var client = HttpClient(CIO) {
        install(WebSockets) {
            pingInterval = -1L
        }
    }

    private var session: WebSocketSession? = null
    private val sendChannel: Channel<Message> = Channel()


    /**
     * Blocking variant of [connectCoroutine]
     */
    fun connect() = runBlocking {
        connectCoroutine(this)
    }

    /**
     * Kill the websocket job,
     * effectively forcing a disconnect.
     *
     * FIXME switch to graceful disconnect
     */
    fun disconnect() {
        websocketJob?.cancel()
    }

    /**
     * Blocking variant of [callServiceCoroutine]
     */
    inline fun <reified R> callService(name: String, method: String, vararg data: Any?): R? =
        runBlocking {
            callServiceCoroutine(name, method, data)
        }

    /**
     * Connect to a websocket API endpoint given by [url],
     * starting the websocket send and receive coroutines
     * in the given [scope]
     */
    suspend fun connectCoroutine(scope: CoroutineScope) {
        websocketJob = scope.async {
            try {
                client.webSocket(
                    method = HttpMethod.Get,
                    host = url.host,
                    port = url.port,
                    path = "/api/messages?id=${Runtime.runtimeID}"
                ) {

                    try {
                        session = this@webSocket
                        val inputRoutine = launch {
                            for (receivedFrame in incoming) {
                                if (receivedFrame !is Frame.Text) {

                                    continue
                                }
                                val text = receivedFrame.readText()
                                logger.info("Received text: $text")
                                if (text == "X") {
                                    logger.info("Heartbeat detected")
                                    continue
                                }
                                val receivedMessage = serde.deserialize<Message>(text)
                                logger.info("Received message: $receivedMessage")
                                eventBus.emit(receivedMessage)
                            }
                        }

                        val outputRoutine = launch {
                            for (message in sendChannel) {
                                logger.info("Got message to send: $message")
                                val messageStr = try {
                                    serde.serialize(message)
                                } catch (e: Exception) {
                                    logger.info("Got exception while serializing message: $e")
                                    throw e
                                }
                                logger.info("Sending message: $messageStr")
                                send(messageStr)
                            }
                        }

                        inputRoutine.join()
                        outputRoutine.cancelAndJoin()
                    } catch (e: ClosedReceiveChannelException) {
                        logger.info("Session closed: $e")
                    } catch (ce: CancellationException) {
                        connected = false
                        throw ce
                    }
                }
            } catch (e: Exception) {
                logger.info("Caught exception while connecting: $e")
                connectionFailedListener(e)
            }
        }
    }

    /**
     * Call a service method using the data from [message]
     * and via the services API of the instance pointed to by [url].
     * If [url] does not point to a running instance, returns `null`.
     * Otherwise, returns the response body, which should be
     * a json-encoded String of the return value of the
     * called method.
     */
    suspend fun callServicesAPI(message: Message): String? {
        val ret = client.get("http://$url/api/service/${message.name}/${message.method}") {
            headers {
                append("Content-Type", "application/json")
            }
            setBody(serde.serialize(message.data))
        }
        if (ret.status != HttpStatusCode.OK) {
            logger.info("Error calling service API with url $url: ${ret.status}")
            return null
        }
        return ret.bodyAsText()
    }

    /**
     * Calls a service method synchronously
     * via the Services API of MRL instance
     * pointed to by [url]. If [url] does not
     * point to a running instance, returns `null`.
     * Otherwise, returns the deserialized return value
     * of the called method.
     */
    suspend inline fun <reified R> callServiceCoroutine(
        name: String,
        method: String,
        vararg data: Any?
    ): R? {
        val message = Message(name = name, method = method, data = data.asList().toMutableList())
        logger.info("Calling service: $message")


        return serde.deserialize(callServicesAPI(message) ?: return null)
    }

    /**
     * Send a message to the connected MRL instance,
     * acts as a no-op if not connected.
     */
    suspend fun sendMessage(msg: Message) {
        sendChannel.send(msg)
    }

    /**
     * Construct a [Message] that calls [method] on
     * the service with [name] and sends to the connected
     * MRL instance. Acts as a no-op if no instance connected.
     */
    suspend fun sendCommand(name: String, method: String, data: List<Any?>) {

        sendMessage(Message(name, method, data.toMutableList()))
    }

}