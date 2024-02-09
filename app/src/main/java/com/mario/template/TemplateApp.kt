package com.mario.template

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.mario.template.ui.component.NavigationDrawerLabel
import com.mario.template.ui.home.Home
import com.mario.template.ui.splash.Splash
import com.mario.template.ui.tuto.Tuto

@Composable
fun TemplateApp(appState: TemplateAppState = rememberTemplateAppState()) {

    val localFocusManager = LocalFocusManager.current
    val localContext = LocalContext.current

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
                NavigationDrawerLabel {
                    Text(text = stringResource(id = R.string.login))
                }
            }
        }
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent,
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
        ) {
            NavHost(
                navController = appState.controller,
                startDestination = NestedGraph.SPLASH.route,
                modifier = Modifier.padding(it)
            ) {
                composable(NestedGraph.SPLASH.route) { from ->
                    Splash(appState)
                }
                composable(NestedGraph.TUTO.route) { from ->
                    Tuto(appState)
                }
                composable(NestedGraph.HOME.route) { from ->
                    Home(appState)
                }
            }
        }
    }
}