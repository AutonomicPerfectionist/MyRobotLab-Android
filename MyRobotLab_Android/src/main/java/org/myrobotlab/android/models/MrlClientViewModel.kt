package org.myrobotlab.android.models

import android.util.Log
import androidx.compose.runtime.MutableState
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

/**
 * Provides primary access to mrlkt in order
 * to ensure separation between the activity
 * and the core logic. This view model's
 * [viewModelScope] is used for any mrlkt
 * `suspend` calls to ensure the lifecycle
 * of mrlkt coroutines follow the
 * view model's lifecycle.
 */
class MrlClientViewModel: ViewModel() {
    private val _url = MutableStateFlow<Url?>(null)

    /**
     * Provides a [MutableStateFlow] that
     * follows [MrlClient.url] except is null before
     * [connect] is called.
     */
    val url = _url.asStateFlow()

    /**
     * Provides a [MutableState] that follows
     * [MrlClient.connected] for use in Compose and
     * [org.myrobotlab.android.MainActivity]
     */
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

    /**
     * Directs mrlkt to connect to the
     * MRL instance at [url] and sets
     * this instance's runtime ID to [id].
     * [MrlClientViewModel.url] / [MrlClient.url]
     * are set to [url] as a consequence of calling this method
     */
    fun connect(id: String, url: Url) {
        _url.value = url

        MrlClient.url = url
        Runtime.initRuntime(id)

        viewModelScope.launch {
            Runtime.runInbox(this)
            MrlClient.connectCoroutine(this)
        }
    }

    /**
     * Disconnect this mrlkt instance from
     * the connected MRL instance. No-op
     * if not connected.
     */
    fun disconnect() {
        MrlClient.disconnect()
    }

    /**
     * Start a service pointed to by [type] and
     * initialize it with the given [name]. The class
     * pointed to by [type] *must* have a primary
     * constructor with a single String parameter for
     * the name.
     */
    fun startService(name: String, type: KClass<out ServiceInterface>) {
        viewModelScope.launch {
            Runtime.start(name, type)?.serviceScope = this
        }
    }
}