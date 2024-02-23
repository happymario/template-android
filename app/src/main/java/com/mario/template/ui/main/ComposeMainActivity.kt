package com.mario.template.ui.main

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.os.ConfigurationCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.mario.template.MainBottomTabsData
import com.mario.template.NestedGraph
import com.mario.template.TemplateAppState
import com.mario.template.base.BaseComposeActivity
import com.mario.template.rememberTemplateAppState
import com.mario.template.ui.component.AppScaffold
import com.mario.template.ui.main.basic.BasicRoute
import com.mario.template.ui.main.latest.LatestScreen
import com.mario.template.ui.main.special.SpecialScreen
import com.mario.template.ui.setting.SettingActivity
import com.mario.template.ui.theme.CustomTheme
import com.mario.template.ui.theme.MyTemplateTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSession
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

@AndroidEntryPoint
class ComposeMainActivity : BaseComposeActivity() {
    @Composable
    override fun ComposeContent() {
        MyTemplateTheme {
            MainRoot()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        handleSSLHandshake()
    }

    private fun handleSSLHandshake() {
        try {

            val trustAllCerts: Array<TrustManager> = arrayOf(object : X509TrustManager {
                override fun checkClientTrusted(
                    chain: Array<out X509Certificate>?, authType: String?,
                ) = Unit

                override fun checkServerTrusted(
                    chain: Array<out X509Certificate>?, authType: String?,
                ) = Unit

                override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
            })
            val sc: SSLContext = SSLContext.getInstance("TLS")
            // trustAllCerts信任所有的证书
            sc.init(null, trustAllCerts, SecureRandom())
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory())
            HttpsURLConnection.setDefaultHostnameVerifier(object : HostnameVerifier {


                override fun verify(hostname: String?, session: SSLSession?): Boolean {


                    return true
                }
            })
        } catch (ignored: Exception) {


        }
    }
}

@Composable
fun MainRoot(
    appState: TemplateAppState = rememberTemplateAppState(),
    viewModel: MainViewModel = hiltViewModel()
) {
    val localFocusManager = LocalFocusManager.current
    val activity = (LocalContext.current as? Activity)
    val state by viewModel.state.collectAsStateWithLifecycle()

    ModalNavigationDrawer(
        drawerState = appState.drawer,
        modifier = Modifier.pointerInput(Unit) {
            detectTapGestures {
                localFocusManager.clearFocus()
            }
        },
        gesturesEnabled = appState.shouldEnableGesture,
        drawerContent = {
            ModalDrawerSheet {
                DrawerContentComponent(
                    closeDrawer = { appState.coroutineScope.launch { appState.drawer.close() } }
                )
            }
        }
    ) {
        BackHandler {
            appState.coroutineScope.launch {
                if (appState.drawer.isOpen) {
                    appState.drawer.close()
                } else {
                    activity?.finish()
                }
            }
        }

        MainScreen(appState, state, onDetail = {})
    }
}

@Composable
fun MainScreen(
    appState: TemplateAppState,
    state: MainViewState,
    onDetail: (menu: String) -> Unit = {}
) {
    AppScaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.Transparent,
//        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        state = state, onDismissRequest = { /*TODO*/ }) { padding, viewstate ->
        MainContent(appState, viewstate)
    }
}


@Composable
fun MainContent(
    appState: TemplateAppState,
    state: MainViewState,
    padding: PaddingValues = PaddingValues(0.dp)
) {
    Column {
        NavHost(
            navController = appState.navController,
            startDestination = NestedGraph.MAIN_BASIC.route,
            modifier = Modifier
                .padding(padding)
                .weight(1.0f)
        ) {
            composable(MainBottomTabsData.Basic.route) { from ->
                BasicRoute(appState)
            }
            composable(MainBottomTabsData.Latest.route) { from ->
                LatestScreen()
            }
            composable(MainBottomTabsData.Special.route) { from ->
                SpecialScreen()
            }
        }

        if (appState.shouldShowBottomBar) {
            MainBottomBar(
                tabs = appState.bottomBarTabs,
                currentRoute = if (appState.currentRoute == null) {
                    MainBottomTabsData.Basic.route
                } else {
                    appState.currentRoute!!
                },
                navigateToRoute = {
                    appState.navigateToBottomBarRoute(it)
                    state.copy(curRoute = it)
                }
            )
        }
    }
}


@Composable
fun MainBottomBar(
    tabs: Array<MainBottomTabsData>,
    currentRoute: String,
    navigateToRoute: (String) -> Unit,
    color: Color = CustomTheme.colors.primary,
    contentColor: Color = Color.White,
) {
    val currentSection = tabs.first { it.route == currentRoute }
    var selectedIndex by remember { mutableStateOf(0) }
    BottomNavigation(
        modifier = Modifier
            .padding(0.dp)
    ) {
        tabs.forEachIndexed { index, mainBottomTabsData ->
            val selected = mainBottomTabsData == currentSection
            //val selected = selectedIndex == index
            val tint by animateColorAsState(
                if (selected) {
                    contentColor
                } else {
                    CustomTheme.colors.title1
                }, label = ""
            )
            BottomNavigationItem(
                modifier = Modifier.background(color),
                icon = {
                    Icon(
                        imageVector = mainBottomTabsData.icon,
                        tint = tint,
                        contentDescription = null
                    )
                },
                label = {
                    androidx.compose.material.Text(
                        text = stringResource(mainBottomTabsData.title).uppercase(
                            ConfigurationCompat.getLocales(
                                LocalConfiguration.current
                            ).get(0)!!
                        ),
                        color = tint,
                        style = MaterialTheme.typography.button,
                        maxLines = 1
                    )
                },
                selected = selected,
                onClick = {
                    navigateToRoute(mainBottomTabsData.route)
                    selectedIndex = index
                },
            )
        }
    }
}


@Composable
fun DrawerContentComponent(
    closeDrawer: () -> Unit,
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(Modifier.clickable(onClick = {
            closeDrawer()

            val intent = Intent(context, SettingActivity::class.java)
            context.startActivity(intent)
        }), content = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
            ) {
                androidx.compose.material.Text(
                    text = "Setting",
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colors.onSecondary
                )
            }
        })

        Column(Modifier.clickable(onClick = {
            closeDrawer()
        }), content = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
            ) {
                androidx.compose.material.Text(
                    text = "Exit",
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colors.onSecondary
                )
            }
        })
    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview2() {
    MyTemplateTheme {
        DrawerContentComponent({})
    }
}