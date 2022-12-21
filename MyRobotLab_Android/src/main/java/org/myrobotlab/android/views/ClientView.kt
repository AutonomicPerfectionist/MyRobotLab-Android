package org.myrobotlab.android.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.window.Dialog
import org.myrobotlab.android.MrlAndroidTheme
import org.myrobotlab.android.service.TestKotlinService
import org.myrobotlab.kotlin.framework.ServiceInterface
import org.myrobotlab.kotlin.service.Runtime.registry
import kotlin.reflect.KClass

enum class ClientWindowDialog {
    OVERVIEW, CONNECTION
}

typealias StartServiceListener = (name: String, klass: KClass<out ServiceInterface>) -> Unit

@Composable
fun ClientScreen(
    services: List<KClass<out ServiceInterface>>, onStartService: StartServiceListener,
    isConnected: Boolean, onConnect: (host: String, port: Int, id: String) -> Unit,
    onDisconnect: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
            .wrapContentSize(Alignment.TopCenter)
    ) {
        var currentWindow by remember {
            mutableStateOf(ClientWindowDialog.OVERVIEW)
        }

        var showStartService by remember {
            mutableStateOf(false)
        }



        when (currentWindow) {
            ClientWindowDialog.OVERVIEW -> OverviewDialog(isConnected, {
                currentWindow = ClientWindowDialog.CONNECTION
            }, onDisconnect, { showStartService = true })
            ClientWindowDialog.CONNECTION -> ConnectDialog { host, port, id ->
                currentWindow = ClientWindowDialog.OVERVIEW
                onConnect(host, port, id)
            }
        }

        if (showStartService) {
            StartServiceDialog({ showStartService = false }, services) { name, service ->
                showStartService = false
                onStartService(name, service)
            }
        }

    }
}

@Composable
fun OverviewDialog(
    isConnected: Boolean,
    onConnectClick: () -> Unit,
    onDisconnectClick: () -> Unit,
    onStartServiceClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
            .wrapContentSize(Alignment.TopCenter),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(25.dp))
        Button(
            onClick = if (!isConnected) onConnectClick else onDisconnectClick,
            colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.secondaryVariant)
        ) {
            Text(text = if (!isConnected) "Connect" else "Disconnect")
        }
        Spacer(modifier = Modifier.height(25.dp))
        Button(
            onClick = onStartServiceClick,
            colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.secondaryVariant)
        ) {
            Text(text = "Start Service")
        }
        Spacer(modifier = Modifier.height(20.dp))
        Text("Services:", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        LazyColumn {
            registry.values.forEach {
                item {
                    Text(it.name)
                }
            }
        }


    }
}

@Composable
fun ConnectDialog(onConnectClick: (host: String, port: Int, id: String) -> Unit) {
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
        var id by remember { mutableStateOf(TextFieldValue()) }

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

        TextField(
            value = id,
            onValueChange = { id = it },
            placeholder = {Text("android")}
        )
        Text("Client ID")


        Spacer(modifier = Modifier.height(25.dp))
        Button(
            onClick = {
                onConnectClick(host.text, port.text.toInt(), if(id.text == "") "android" else id.text)
            },
            colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.secondaryVariant)
        ) {
            Text(text = "Connect")
        }
    }
}

@Composable
fun StartServiceDialog(
    onDismissRequest: () -> Unit,
    services: List<KClass<out ServiceInterface>>,
    onStartService: StartServiceListener
) {
    Dialog(onDismissRequest = onDismissRequest) {

        Card(
            modifier = Modifier
                .fillMaxHeight(.6f)
                .fillMaxWidth(1f),
            shape = RoundedCornerShape(8.dp)
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth(1f)
                    .fillMaxHeight(1f),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                var menuExpanded by remember {
                    mutableStateOf(false)
                }

                var selectedIndex by remember {
                    mutableStateOf(0)
                }

                var name by remember {
                    mutableStateOf("")
                }

                Spacer(modifier = Modifier.height(25.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentSize(Alignment.TopStart)
                ) {

                    var mTextFieldSize by remember { mutableStateOf(Size.Zero) }
                    // Up Icon when expanded and down icon when collapsed
                    val icon = if (menuExpanded)
                        Icons.Filled.KeyboardArrowUp
                    else
                        Icons.Filled.KeyboardArrowDown

                    // Create a string value to store the selected city
                    var mSelectedText by remember {
                        mutableStateOf(
                            services[selectedIndex].simpleName ?: "Unknown Service"
                        )
                    }

                    // Create an Outlined Text Field
                    // with icon and not expanded
                    TextField(
                        value = mSelectedText,
                        onValueChange = { mSelectedText = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .onGloballyPositioned { coordinates ->
                                // This value is used to assign to
                                // the DropDown the same width
                                mTextFieldSize = coordinates.size.toSize()
                            }
                            .clickable { menuExpanded = !menuExpanded },
                        label = { Text("Service") },
                        trailingIcon = {
                            Icon(icon, "contentDescription")
                        },
                        enabled = false
                    )

                    DropdownMenu(
                        expanded = menuExpanded,
                        modifier = Modifier
                            .width(with(LocalDensity.current) { mTextFieldSize.width.toDp() }),
                        onDismissRequest = { menuExpanded = false }) {
                        services.forEachIndexed { index, service ->
                            DropdownMenuItem(onClick = {
                                selectedIndex = index
                                mSelectedText = service.simpleName ?: "Unknown Service"
                                menuExpanded = false
                            }) {
                                Text(text = service.simpleName ?: "Unknown Service")
                            }
                        }
                    }
                }

                TextField(value = name, onValueChange = { name = it }, placeholder = {
                    Text("unknown-service")
                })

                Button(
                    onClick = { onStartService(if (name == "") "unknown-service" else name, services[selectedIndex]) },
                    colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.secondaryVariant)
                ) {
                    Text(text = "Start Service")
                }
            }
        }

    }
}

@Preview
@Composable
fun StartServiceDialogPreview() {
    MrlAndroidTheme {
        StartServiceDialog({}, listOf(TestKotlinService::class)) { _, _ -> }
    }
}

@Preview(showBackground = true)
@Composable
fun ClientScreenPreview() {
    MrlAndroidTheme {
        ClientScreen(listOf(), { _, _ -> }, false, { host, port, id ->

        }) {}
    }
}

@Preview(showBackground = true)
@Composable
fun OverviewWindowPreview() {
    MrlAndroidTheme {
        OverviewDialog(false, {}, {}) {

        }
    }
}


@Preview(showBackground = true)
@Composable
fun ConnectWindowPreview() {
    MrlAndroidTheme {
        ConnectDialog { host, port, id ->

        }
    }
}