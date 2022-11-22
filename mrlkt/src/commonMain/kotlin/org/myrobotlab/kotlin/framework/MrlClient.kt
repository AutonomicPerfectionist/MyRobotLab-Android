package org.myrobotlab.kotlin.framework

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.util.reflect.*
import io.ktor.websocket.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.flow.MutableSharedFlow
import org.myrobotlab.kotlin.service.Runtime
import org.myrobotlab.kotlin.utils.Url
import kotlin.properties.Delegates

expect class JsonSerde() {
    inline fun <reified T> deserialize(json: String): T
    fun <T> serialize(o: T): String
}

interface Logger {
    fun info(toLog: String)
}

object MrlClient {
    val eventBus = MutableSharedFlow<Message>()
    var connectedListener: (isConnected: Boolean) -> Unit = {}
    var connected by Delegates.observable(false) { _, old, new ->
        connectedListener(new)
    }
        internal set
    private var websocketJob: Job? = null
    var url = Url("localhost", 8888)
    var logger: Logger = object : Logger {
        override fun info(toLog: String) {
            println(toLog)
        }
    }
    val serde: JsonSerde = JsonSerde()
    private val client = HttpClient(CIO) {
        install(WebSockets) {
            pingInterval = -1L
        }
    }

    private var session: WebSocketSession? = null
    private val sendChannel: Channel<Message> = Channel()


    fun connect() = runBlocking {
        connectCoroutine(this)
    }

    inline fun <reified R> callService(name: String, method: String, vararg data: Any?): R? =
        runBlocking {
            callServiceCoroutine(name, method, data)
        }

    suspend fun connectCoroutine(scope: CoroutineScope) {
        websocketJob = scope.async {
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
                            val messageStr = serde.serialize(message)
                            logger.info("Sending message: $messageStr")
                            send(messageStr)
                        }
                    }

                    inputRoutine.join()
                    outputRoutine.cancelAndJoin()
                } catch (e: ClosedReceiveChannelException) {
                    logger.info("Session closed")
                }
            }
        }
    }

    suspend fun callServicesAPI(message: Message): String {
        val ret = client.get("http://$url/api/service/${message.name}/${message.method}") {
            headers {
                append("Content-Type", "application/json")
            }
            setBody(serde.serialize(message.data))
        }
        return ret.bodyAsText()
    }

    suspend inline fun <reified R> callServiceCoroutine(
        name: String,
        method: String,
        vararg data: Any?
    ): R? {
        val message = Message(name = name, method = method, data = data.asList().toMutableList())
        logger.info("Calling service: $message")

        return serde.deserialize(callServicesAPI(message))
    }

    suspend fun sendCommand(name: String, method: String, data: List<Any?>) {

        sendChannel.send(Message(name, method, data.toMutableList()))
    }

}