package com.mario.template

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.mario.template.ui.main.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun rememberTemplateAppState(
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    controller: NavHostController = rememberNavController(),
    drawer: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed),
    snackbarHost: SnackbarHostState = remember { SnackbarHostState() }
): TemplateAppState = remember(coroutineScope, controller, drawer, snackbarHost) {
    TemplateAppState(coroutineScope, controller, drawer, snackbarHost)
}

class TemplateAppState(
    private val coroutineScope: CoroutineScope,
    val controller: NavHostController,
    val drawer: DrawerState,
    val snackbarHost: SnackbarHostState,
) {
    val shouldEnableGesture: Boolean
        @Composable get() = controller.currentBackStackEntryAsState().value?.destination?.route == NestedGraph.HOME.route

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

    fun navigateToHome() {
        closeDrawer()
        controller.navigate(route = NestedGraph.HOME.route) {
            popUpTo(NestedGraph.SPLASH.route) {
                inclusive = true
            }
        }
    }

    fun navigateToTuto() {
        closeDrawer()
        controller.navigate(route = NestedGraph.TUTO.route) {
            popUpTo(NestedGraph.SPLASH.route) {
                inclusive = true
            }
        }
    }

    fun navigateToLogin() {
        closeDrawer()
        controller.navigate(route = NestedGraph.LOGIN.route) {
            popUpTo(NestedGraph.LOGIN.route) {
                inclusive = true
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
        val intent = Intent(context, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
        context.startActivity(intent)
    }
}

enum class NestedGraph(val route: String) {
    HOME(route = "home_nav"),
    SPLASH(route = "splash_nav"),
    TUTO(route = "tuto_nav"),
    LOGIN(route = "login_nav"),
}