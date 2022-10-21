package org.myrobotlab.kotlin.framework

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.runBlocking

interface JsonSerde {
    fun <T> deserialize(json: String): T
    fun <T> serialize(o: T): String
}

object MrlClient {
    val eventBus = MutableSharedFlow<Message>()
    var url: String = "localhost"
    var serde: JsonSerde? = null


    fun connect() = runBlocking {
        connectCoroutine()
    }

    fun <R> callService(name: String, method: String, vararg data: Any?): R? = runBlocking {
        callServiceCoroutine(name, method, data)
    }

    suspend fun connectCoroutine() {

    }

    suspend fun <R> callServiceCoroutine(name: String, method: String, vararg data: Any?): R? {
        val message = Message(name=name, method=method, data=data.asList())
        println("Calling service: $message")
        return serde?.deserialize("")
    }

}