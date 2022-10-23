package org.myrobotlab.kotlin.framework

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.runBlocking

expect class JsonSerde() {
    fun <T> deserialize(json: String): T
    fun <T> serialize(o: T): String
}

object MrlClient {
    val eventBus = MutableSharedFlow<Message>()
    var url: String = "localhost"
    private val serde: JsonSerde = JsonSerde()


    fun connect() = runBlocking {
        connectCoroutine()
    }

    fun <R> callService(name: String, method: String, vararg data: Any?): R? = runBlocking {
        callServiceCoroutine(name, method, data)
    }

    suspend fun connectCoroutine() {
        TODO("Open a websocket connection within a coroutine")
    }

    suspend fun <R> callServiceCoroutine(name: String, method: String, vararg data: Any?): R? {
        val message = Message(name=name, method=method, data=data.asList())
        println("Calling service: $message")
        return serde?.deserialize("")
    }

    suspend fun sendCommand(name: String, method: String, data: List<Any?>) {
        TODO("Need to implement sendCommand()")
    }

}