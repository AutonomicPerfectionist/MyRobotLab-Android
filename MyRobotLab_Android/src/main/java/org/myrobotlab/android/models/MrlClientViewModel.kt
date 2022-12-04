package org.myrobotlab.android.models

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.websocket.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.myrobotlab.kotlin.framework.Logger
import org.myrobotlab.kotlin.framework.MrlClient
import org.myrobotlab.kotlin.framework.ServiceInterface
import org.myrobotlab.kotlin.service.Runtime
import org.myrobotlab.kotlin.utils.Url
import kotlin.reflect.KClass

class MrlClientViewModel: ViewModel() {
    private val _url = MutableStateFlow<Url?>(null)
    val url = _url.asStateFlow()
    var connected = mutableStateOf(MrlClient.connected)

    init {
        MrlClient.logger = object : Logger {
            override fun info(toLog: String) {
                Log.i("MrlClient", toLog)
            }

        }

        MrlClient.connectedListener = {
            connected.value = it
        }

        MrlClient.client = HttpClient(OkHttp) {
            install(WebSockets) {
                pingInterval = -1L
            }
        }
    }

    fun connect(id: String, url: Url) {
        _url.value = url

        MrlClient.url = url
        Runtime.initRuntime(id)

        viewModelScope.launch {
            Runtime.runInbox(this)
            MrlClient.connectCoroutine(this)
        }
    }

    fun disconnect() {
        MrlClient.disconnect()
    }

    fun startService(name: String, type: KClass<out ServiceInterface>) {
        viewModelScope.launch {
            Runtime.start(name, type)
        }
    }
}