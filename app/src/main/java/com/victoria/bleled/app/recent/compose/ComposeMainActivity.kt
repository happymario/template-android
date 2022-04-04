package com.victoria.bleled.app.recent.compose

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.StringRes
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Assignment
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.Stars
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.os.ConfigurationCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.google.accompanist.insets.ProvideWindowInsets
import com.victoria.bleled.R
import com.victoria.bleled.app.recent.compose.main.Essential
import com.victoria.bleled.app.recent.compose.main.Recent
import com.victoria.bleled.app.recent.compose.main.Special
import com.victoria.bleled.app.recent.compose.theme.MyApplicationTheme
import kotlinx.coroutines.launch


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ComposeMainActivityPreview() {
    MyApplicationTheme {
        LayoutMainActivity()
    }
}

class ComposeMainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                LayoutMainActivity()
            }
        }
    }
}

/**
 * Destinations used in the [ComposeMainActivity].
 */
object MainDestinations {
    val HOME_ROUTE = MainBottomTabsData.HOME.route
}

/**
 * Bottoms Tabs' Data used in the [ComposeMainActivity].
 */
enum class MainBottomTabsData(
    @StringRes val title: Int,
    val icon: ImageVector,
    val route: String,
) {
    HOME(R.string.tab_main, Icons.Outlined.Dashboard, "main/home"),
    RECENT(R.string.tab_latest, Icons.Outlined.Assignment, "main/recent"),
    SPECIAL(R.string.tab_special, Icons.Outlined.Stars, "main/special")
}

@Composable
fun LayoutMainActivity() {
    val drawerState = rememberDrawerState(DrawerValue.Closed)

    ProvideWindowInsets {
        MyApplicationTheme {
            val mainState = rememberComposeMainState()
            ModalDrawer(
                drawerState = drawerState,
                gesturesEnabled = true,
                drawerContent = {
                    DrawerContentComponent(
                        closeDrawer = { mainState.coroutineScope.launch { drawerState.close() } }
                    )
                },
                content = {
                    Column {
                        Row(modifier = Modifier
                            .fillMaxWidth()
                            .weight(1.0f)) {
                            NavHost(
                                navController = mainState.navController,
                                startDestination = MainDestinations.HOME_ROUTE,
                                modifier = Modifier.padding(0.dp)
                            ) {
                                composable(MainBottomTabsData.HOME.route) { from ->
                                    Essential()
                                }
                                composable(MainBottomTabsData.RECENT.route) { from ->
                                    Recent()
                                }
                                composable(MainBottomTabsData.SPECIAL.route) { from ->
                                    Special()
                                }
                            }
                        }
                        if (mainState.shouldShowBottomBar) {
                            MainBottomBar(
                                tabs = mainState.bottomBarTabs,
                                currentRoute = if (mainState.currentRoute == null) {
                                    MainBottomTabsData.HOME.route
                                } else {
                                    mainState.currentRoute!!
                                },
                                navigateToRoute = mainState::navigateToBottomBarRoute
                            )
                        }
                    }
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
    color: Color = MyApplicationTheme.colors.purple700,
    contentColor: Color = Color.White,
) {
    val currentSection = tabs.first { it.route == currentRoute }
    var selectedIndex by remember { mutableStateOf(0) }
    BottomNavigation(modifier = Modifier
        .padding(0.dp)) {
        tabs.forEachIndexed { index, mainBottomTabsData ->
            val selected = mainBottomTabsData == currentSection
            //val selected = selectedIndex == index
            val tint by animateColorAsState(
                if (selected) {
                    contentColor
                } else {
                    MyApplicationTheme.colors.purple200
                }
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
                    Text(
                        text = stringResource(mainBottomTabsData.title).uppercase(
                            ConfigurationCompat.getLocales(
                                LocalConfiguration.current
                            ).get(0)
                        ),
                        color = tint,
                        style = MaterialTheme.typography.button,
                        maxLines = 1
                    )
                },
                selected = selected,
                //onClick = { selectedIndex = index },
                onClick = { navigateToRoute(mainBottomTabsData.route) },
            )
        }
    }
}


@Composable
fun DrawerContentComponent(
    closeDrawer: () -> Unit,
) {
    val context = LocalContext.current

    Column(modifier = Modifier
        .fillMaxSize()
        .background(Color.White)) {
        Column(Modifier.clickable(onClick = {
            closeDrawer()

            val intent = Intent(context, ComposeCodelabActivity::class.java)
            context.startActivity(intent)
        }), content = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(text = "Setting", modifier = Modifier.padding(16.dp), color = Color.Black)
            }
        })

        Column(Modifier.clickable(onClick = {
            closeDrawer()
        }), content = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(text = "Exit", modifier = Modifier.padding(16.dp), color = Color.Black)
            }
        })
    }
}
