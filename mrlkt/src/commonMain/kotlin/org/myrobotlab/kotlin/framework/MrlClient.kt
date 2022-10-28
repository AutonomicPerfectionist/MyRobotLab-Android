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
import kotlinx.coroutines.flow.MutableSharedFlow
import org.myrobotlab.kotlin.service.Runtime
import org.myrobotlab.kotlin.utils.Url

expect class JsonSerde() {
    inline fun <reified T> deserialize(json: String): T
    fun <T> serialize(o: T): String
}

object MrlClient {
    val eventBus = MutableSharedFlow<Message>()
    var url = Url("localhost", 8888)
    val serde: JsonSerde = JsonSerde()
    private val client = HttpClient(CIO) {
        install(WebSockets) {
            pingInterval = 20_000
        }
    }


    fun connect() = runBlocking {
        connectCoroutine()
    }

    inline fun <reified R> callService(name: String, method: String, vararg data: Any?): R? = runBlocking {
        callServiceCoroutine(name, method, data)
    }

    suspend fun connectCoroutine() {
        client.webSocket(method = HttpMethod.Get, host = url.host, port = url.port, path = "/api/messages?id=${Runtime.runtimeID}") {


            while (true) {
                val receivedFrame = incoming.receive() as? Frame.Text ?: continue
                val receivedMessage = serde.deserialize<Message>(receivedFrame.readText())
                println("Received message: $receivedMessage")
                eventBus.emit(receivedMessage)
            }
        }
    }

    suspend fun callServicesAPI(message: Message): String {
        val ret = client.get("$url/api/service/${message.name}/${message.method}") {
            headers {
                append("Content-Type", "application/json")
            }
            setBody(serde.serialize(message.data))
        }
        return  ret.bodyAsText()
    }

    suspend inline fun <reified R> callServiceCoroutine(name: String, method: String, vararg data: Any?): R? {
        val message = Message(name=name, method=method, data=data.asList())
        println("Calling service: $message")

        return serde.deserialize(callServicesAPI(message))
    }

    suspend fun sendCommand(name: String, method: String, data: List<Any?>) {
        TODO("Need to implement sendCommand()")
    }

}