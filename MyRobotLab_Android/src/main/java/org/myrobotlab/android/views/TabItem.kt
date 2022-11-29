package org.myrobotlab.android.views

import androidx.compose.runtime.Composable
import org.myrobotlab.android.R
import org.myrobotlab.kotlin.framework.ServiceInterface
import org.myrobotlab.kotlin.utils.Url
import kotlin.reflect.KClass

typealias ComposableFun = @Composable () -> Unit

sealed class TabItem(var icon: Int, var title: String, var screen: ComposableFun) {
    class Client(
        val services: List<KClass<out ServiceInterface>>,
        val onStartService: StartServiceListener,
        var isConnected: Boolean,
        val onConnect:(host: String, port: Int) -> Unit,
    ) : TabItem(R.drawable.ic_square, "Client", { ClientScreen(services, onStartService, isConnected, onConnect) })
    class WebGui(
        val connected: Boolean,
        val url: Url
    ) : TabItem( R.drawable.ic_square,"WebGui", { WebGuiScreen(connected, url) })
    object About : TabItem( R.drawable.ic_square,"About", { AboutScreen() })
}
