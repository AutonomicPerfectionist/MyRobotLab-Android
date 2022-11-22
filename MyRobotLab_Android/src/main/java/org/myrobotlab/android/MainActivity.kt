package org.myrobotlab.android

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
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
import androidx.lifecycle.lifecycleScope
import com.google.accompanist.pager.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.myrobotlab.android.models.MrlClientViewModel
import org.myrobotlab.android.service.TestKotlinService
import org.myrobotlab.android.views.TabItem
import org.myrobotlab.kotlin.framework.Logger
import org.myrobotlab.kotlin.framework.MrlClient
import org.myrobotlab.kotlin.service.Runtime.initRuntime
import org.myrobotlab.kotlin.utils.Url
import org.myrobotlab.kotlin.service.Runtime



class MainActivity : ComponentActivity() {
    private val clientViewModel: MrlClientViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MrlAndroidTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    val isConnected by remember{ clientViewModel.connected }
                    MainScreen(isConnected) { host, port ->
                        clientViewModel.connect("android", Url(host, port))
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(text: String) {
    Text(text = text)
}

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@OptIn(ExperimentalPagerApi::class)
@Composable
fun MainScreen(isConnected: Boolean, onConnect: (host: String, port:Int) -> Unit) {
    MrlAndroidTheme {

        val clientScreen = TabItem.Client(isConnected, onConnect)
        val tabs = listOf(clientScreen, TabItem.WebGui, TabItem.Donate)
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
    MainScreen(false) { host, port ->
        
    }
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
            TabItem.Client(false){ host, port ->
                          
            },
            TabItem.WebGui,
            TabItem.Donate
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
            TabItem.Client(false){ host, port ->
                          
            },
            TabItem.WebGui,
            TabItem.Donate
        )
        val pagerState = rememberPagerState()
        TabsContent(tabs = tabs, pagerState = pagerState)
    }
}
