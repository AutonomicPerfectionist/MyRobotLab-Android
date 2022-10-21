package org.myrobotlab

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*

class Greeting {
    private val client = HttpClient()

    suspend fun greeting(): String {
        val response = client.get("http://10.36.102.127:8888/api/service/runtime/getUptime")
        return response.bodyAsText()
    }
}