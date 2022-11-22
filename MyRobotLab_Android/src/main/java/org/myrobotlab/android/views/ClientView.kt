package org.myrobotlab.android.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.myrobotlab.android.MrlAndroidTheme

enum class ClientWindowDialog {
    OVERVIEW, CONNECTION
}

@Composable
fun ClientScreen(isConnected: Boolean, onConnect: (host: String, port: Int) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
            .wrapContentSize(Alignment.TopCenter)
    ) {
        var currentWindow = remember {
            mutableStateOf(ClientWindowDialog.OVERVIEW)
        }
        when(currentWindow.value) {
            ClientWindowDialog.OVERVIEW -> OverviewDialog(isConnected) {
                currentWindow.value = ClientWindowDialog.CONNECTION
            }
            ClientWindowDialog.CONNECTION -> ConnectDialog { host, port ->
                currentWindow.value = ClientWindowDialog.OVERVIEW
                onConnect(host, port)
            }
        }

    }
}

@Composable
fun OverviewDialog(isConnected: Boolean, onConnectClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
            .wrapContentSize(Alignment.TopCenter)
    ) {
        Spacer(modifier = Modifier.height(25.dp))
        Button(
            onClick = onConnectClick,
            colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.secondaryVariant)
        ) {
            Text(text = if (!isConnected) "Connect" else "Disconnect")
        }
    }
}

@Composable
fun ConnectDialog(onConnectClick: (host: String, port: Int) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
            .wrapContentSize(Alignment.TopCenter)
    ) {
        var host by remember { mutableStateOf(TextFieldValue()) }
        var port by remember {
            mutableStateOf(TextFieldValue())
        }

        Spacer(modifier = Modifier.height(25.dp))

        TextField(
            value = host,
            onValueChange = { host = it }
        )
        Text("MRL Host: " + host.text)

        Spacer(modifier = Modifier.height(25.dp))

        TextField(
            value = port,
            onValueChange = { port = it },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Text("MRL Port: " + port.text)


        Spacer(modifier = Modifier.height(25.dp))
        Button(
            onClick = {
                        onConnectClick(host.text, port.text.toInt())
                      },
            colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.secondaryVariant)
        ) {
            Text(text = "Connect")
        }
    }
}

@Composable
fun ClientMainWindow(onConnectClick: () -> Unit) {
    Button(onClick = onConnectClick) {
        Text(text = "Connect")
    }
}


@Preview(showBackground = true)
@Composable
fun ClientScreenPreview() {
    MrlAndroidTheme {
        ClientScreen(false){ host, port ->

        }
    }
}

@Preview(showBackground = true)
@Composable
fun OverviewWindowPreview() {
    MrlAndroidTheme {
        OverviewDialog(false) {

        }
    }
}


@Preview(showBackground = true)
@Composable
fun ConnectWindowPreview() {
    MrlAndroidTheme {
        ConnectDialog { host, port ->

        }
    }
}