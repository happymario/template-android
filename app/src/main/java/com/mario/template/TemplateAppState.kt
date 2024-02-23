package com.mario.template

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.annotation.StringRes
import androidx.compose.material.ScaffoldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Assignment
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.Stars
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.mario.template.ui.auth.SignupActivity
import com.mario.template.ui.main.ComposeMainActivity
import com.mario.template.ui.main.basic.CameraTestActivity
import com.mario.template.ui.main.basic.WebViewActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Bottoms Tabs' Data used in the [ComposeMainActivity].
 */
enum class MainBottomTabsData(
    @StringRes val title: Int,
    val icon: ImageVector,
    val route: String,
) {
    Basic(R.string.tab_basic, Icons.Outlined.Dashboard, NestedGraph.MAIN_BASIC.route),
    Latest(R.string.tab_latest, Icons.Outlined.Assignment, NestedGraph.MAIN_LATEST.route),
    Special(R.string.tab_special, Icons.Outlined.Stars, NestedGraph.MAIN_SPECIAL.route)
}

@Composable
fun rememberTemplateAppState(
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    controller: NavHostController = rememberNavController(),
    drawer: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed),
    snackbarHost: SnackbarHostState = remember { SnackbarHostState() }
): TemplateAppState = remember(scaffoldState, coroutineScope, controller, drawer, snackbarHost) {
    TemplateAppState(scaffoldState, coroutineScope, controller, drawer, snackbarHost)
}

class TemplateAppState(
    val scaffoldState: ScaffoldState,
    val coroutineScope: CoroutineScope,
    val navController: NavHostController,
    val drawer: DrawerState,
    val snackbarHost: SnackbarHostState,
) {
    // ----------------------------------------------------------
    // BottomBar state source of truth
    // ----------------------------------------------------------
    val bottomBarTabs = MainBottomTabsData.values()
    private val bottomBarRoutes = bottomBarTabs.map { it.route }

    // Reading this attribute will cause recompositions when the bottom bar needs shown, or not.
    // Not all routes need to show the bottom bar.
    val shouldShowBottomBar: Boolean
        @Composable get() = navController.currentBackStackEntryAsState().value?.destination?.route in bottomBarRoutes
    val shouldEnableGesture: Boolean
        @Composable get() = navController.currentBackStackEntryAsState().value?.destination?.route in bottomBarRoutes // == NestedGraph.TEST.route

    // ----------------------------------------------------------
    // Navigation state source of truth
    // ----------------------------------------------------------
    val currentRoute: String?
        get() = navController.currentDestination?.route

    fun openDrawer() {
        coroutineScope.launch {
            drawer.open()
        }
    }

    fun closeDrawer() {
        coroutineScope.launch {
            drawer.close()
        }
    }

    fun navigateToTest() {
        closeDrawer()
        navController.navigate(route = NestedGraph.TEST.route) {
            popUpTo(NestedGraph.SPLASH.route) {
                inclusive = true
            }
        }
    }

    fun navigateToTuto() {
        closeDrawer()
        navController.navigate(route = NestedGraph.TUTO.route) {
            popUpTo(NestedGraph.SPLASH.route) {
                inclusive = true
            }
        }
    }

    fun navigateToLogin() {
        closeDrawer()
        navController.navigate(route = NestedGraph.LOGIN.route) {
            popUpTo(NestedGraph.LOGIN.route) {
                inclusive = true
            }
        }
    }

    fun navigateToBottomBarRoute(route: String) {
        if (route != currentRoute) {
            navController.navigate(route) {
                launchSingleTop = true
                restoreState = true
                // Pop up backstack to the first destination and save state. This makes going back
                // to the start destination when pressing back in any other bottom tab.
                popUpTo(findStartDestination(navController.graph).id) {
                    saveState = true
                }
            }
        }
    }

    fun openAppSetting(context: Context) {
        val intent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", context.packageName, null)
        ).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }

    fun goMainActivity(context: Context) {
        val intent = Intent(context, ComposeMainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
        context.startActivity(intent)
    }

    fun goSignupActivity(context: Context) {
        val intent = Intent(context, SignupActivity::class.java).apply {
        }
        context.startActivity(intent)
    }

    fun goWebViewActivity(context: Context) {
        val intent = Intent(context, WebViewActivity::class.java).apply {
        }
        context.startActivity(intent)
    }

    fun goCameraActivity(context: Context) {
        val intent = Intent(context, CameraTestActivity::class.java).apply {
        }
        context.startActivity(intent)
    }
}

enum class NestedGraph(val route: String) {
    TEST(route = "test_nav"),
    SPLASH(route = "splash_nav"),
    TUTO(route = "tuto_nav"),
    LOGIN(route = "login_nav"),
    MAIN_BASIC(route = "main/basic"),
    MAIN_LATEST(route = "main/latest"),
    MAIN_SPECIAL(route = "main/special"),
}

/**
 * If the lifecycle is not resumed it means this NavBackStackEntry already processed a nav event.
 *
 * This is used to de-duplicate navigation events.
 */
private val NavGraph.startDestination: NavDestination?
    get() = findNode(startDestinationId)

/**
 * Copied from similar function in NavigationUI.kt
 *
 * https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:navigation/navigation-ui/src/main/java/androidx/navigation/ui/NavigationUI.kt
 */
private tailrec fun findStartDestination(graph: NavDestination): NavDestination {
    return if (graph is NavGraph) findStartDestination(graph.startDestination!!) else graph
}