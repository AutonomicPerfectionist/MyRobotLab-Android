package org.myrobotlab.android.views

import androidx.compose.runtime.Composable
import org.myrobotlab.android.R

typealias ComposableFun = @Composable () -> Unit

sealed class TabItem(var icon: Int, var title: String, var screen: ComposableFun) {
    class Client(var isConnected: Boolean, val onConnect:(host: String, port: Int) -> Unit) : TabItem(R.drawable.ic_square, "Client", { ClientScreen(isConnected, onConnect) })
    object WebGui : TabItem( R.drawable.ic_square,"WebGui", { WebGuiScreen() })
    object Donate : TabItem( R.drawable.ic_square,"Donate", { DonationScreen() })
}
