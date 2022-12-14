package org.myrobotlab.android

import android.annotation.SuppressLint
import android.app.Activity
import android.hardware.SensorManager
import android.os.Bundle
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import com.fondesa.kpermissions.allGranted
import com.google.accompanist.pager.*
import kotlinx.coroutines.launch
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.myrobotlab.android.framework.AndroidServiceMeta
import org.myrobotlab.android.models.MrlClientViewModel
import org.myrobotlab.android.views.StartServiceListener
import org.myrobotlab.android.views.TabItem
import org.myrobotlab.kotlin.framework.ServiceInterface
import org.myrobotlab.kotlin.utils.Url
import org.myrobotlab.kotlin.framework.generated.services.serviceRegistry
import kotlin.reflect.KClass
import kotlin.reflect.full.companionObjectInstance
import com.fondesa.kpermissions.extension.permissionsBuilder
import com.fondesa.kpermissions.extension.send
import org.myrobotlab.kotlin.framework.MrlClient
import java.net.ConnectException
import java.net.UnknownHostException


class MainActivity : FragmentActivity() {
    private val clientViewModel: MrlClientViewModel by viewModels()

    private fun onStartService(name: String, service: KClass<out ServiceInterface>) {
        val companion = service.companionObjectInstance
        if(companion != null
            && companion is AndroidServiceMeta
            && companion.requiredPermissions.isNotEmpty()) {
            Log.e("MRLAndroid", "Got companion object: ${service.companionObjectInstance}")
            permissionsBuilder(companion.requiredPermissions[0], *companion.requiredPermissions.slice(
                1 until companion.requiredPermissions.size
            )
                .toTypedArray()).build().send { result->
                    if(result.allGranted()) {
                        clientViewModel.startService(name, service)
                    }
            }
        } else {
            clientViewModel.startService(name, service)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("MrlAndroid", "Registry: $serviceRegistry")


        MrlClient.connectionFailedListener = { e ->
            when(e) {
                is ConnectException -> Toast.makeText(this,
                    "Unable to connect, check instance URL", Toast.LENGTH_LONG).show()
                is UnknownHostException -> Toast.makeText(this,
                        "Host is unknown, try again", Toast.LENGTH_LONG).show()
                else -> Toast.makeText(this, "Unknown error occurred", Toast.LENGTH_SHORT).show()
            }

        }


        val appModule = module {
            factory { ContextContainer(androidContext()) }
            factory { androidContext().getSystemService(Activity.SENSOR_SERVICE) as SensorManager}
            factory { TextToSpeech(androidContext()) {} }
            factory { SpeechRecognizer.createSpeechRecognizer(androidContext()) }
        }

        // Simple fix to ensure config changes
        // like changing orientation don't crash the app
        if (GlobalContext.getOrNull() != null)
            stopKoin()

        startKoin {
            androidLogger()
            androidContext(this@MainActivity)
            modules(appModule)
        }
        setContent {
            MrlAndroidTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    val isConnected by remember{ clientViewModel.connected }
                    var url by remember { mutableStateOf(clientViewModel.url.value ?: Url("localhost", 8888)) }
                    MainScreen(url, serviceRegistry, this::onStartService, isConnected, { host, port, id ->
                        url = Url(host, port)
                        clientViewModel.connect(id, url)
                    }) {
                        clientViewModel.disconnect()
                    }
                }
            }
        }
    }
}

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@OptIn(ExperimentalPagerApi::class)
@Composable
fun MainScreen(
    url: Url,
    services: List<KClass<out ServiceInterface>>,
    onServiceStart: StartServiceListener,
    isConnected: Boolean,
    onConnect: (host: String, port:Int, id: String) -> Unit,
    onDisconnect: () -> Unit) {
    MrlAndroidTheme {

        val clientScreen = TabItem.Client(services, onServiceStart, isConnected, onConnect, onDisconnect)
        val tabs = listOf(clientScreen, TabItem.WebGui(isConnected, url), TabItem.About)
        val pagerState = rememberPagerState()
        Scaffold(
            topBar = { TopBar() },
        ) { padding ->
            Column(modifier = Modifier.padding(padding)) {
                Surface(
                    elevation = 8.dp
                ) {
                    Tabs(tabs = tabs, pagerState = pagerState)
                }
                Surface {
                    TabsContent(tabs = tabs, pagerState = pagerState)
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    MainScreen(Url("localhost", 8888), serviceRegistry, {_: String, _: KClass<out ServiceInterface>->}, false, { _, _, _ ->
        
    }) {}
}

@Composable
fun TopBar() {
    MrlAndroidTheme {
        TopAppBar(
            title = { Text(text = "MyRobotLab Android", fontSize = 18.sp) },
            backgroundColor = MaterialTheme.colors.secondaryVariant,
            contentColor = Color.White

        )
    }
}

@Preview(showBackground = true)
@Composable
fun TopBarPreview() {
    TopBar()
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun Tabs(tabs: List<TabItem>, pagerState: PagerState) {
    val scope = rememberCoroutineScope()
    // OR ScrollableTabRow()
    TabRow(
        selectedTabIndex = pagerState.currentPage,
        backgroundColor = MaterialTheme.colors.secondaryVariant,
        indicator = { tabPositions ->
            TabRowDefaults.Indicator(
                Modifier.pagerTabIndicatorOffset(pagerState, tabPositions)
            )
        }) {
        tabs.forEachIndexed { index, tab ->
            // OR Tab()
            LeadingIconTab(
                icon = { Icon(painter = painterResource(id = tab.icon), contentDescription = "") },
                text = { Text(tab.title) },
                selected = pagerState.currentPage == index,
                onClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                },
            )
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Preview(showBackground = true)
@Composable
fun TabsPreview() {
    MrlAndroidTheme {
        val tabs = listOf(
            TabItem.Client(listOf(), {name: String, service: KClass<out ServiceInterface> ->}, false, { _, _, _ ->
                          
            }){},
            TabItem.WebGui(false, Url("localhost", 8888)),
            TabItem.About
        )
        val pagerState = rememberPagerState()
        Tabs(tabs = tabs, pagerState = pagerState)
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun TabsContent(tabs: List<TabItem>, pagerState: PagerState) {
    HorizontalPager(state = pagerState, count = tabs.size) { page ->
        tabs[page].screen()
    }
}

@OptIn(ExperimentalPagerApi::class)
@Preview(showBackground = true)
@Composable
fun TabsContentPreview() {
    MrlAndroidTheme {
        val tabs = listOf(
            TabItem.Client(listOf(), {name: String, service: KClass<out ServiceInterface> ->}, false, { _, _, _ ->
                          
            }){},
            TabItem.WebGui(false, Url("localhost", 8888)),
            TabItem.About
        )
        val pagerState = rememberPagerState()
        TabsContent(tabs = tabs, pagerState = pagerState)
    }
}
