package com.mario.template

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.mario.template.ui.auth.LoginRoute
import com.mario.template.ui.splash.SplashRoute
import com.mario.template.ui.test.TestRoute
import com.mario.template.ui.tuto.TutoRoute

@Composable
fun TemplateApp(appState: TemplateAppState = rememberTemplateAppState()) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
    ) {
        NavHost(
            navController = appState.navController,
            startDestination = NestedGraph.SPLASH.route,
            modifier = Modifier.padding(it)
        ) {
            composable(NestedGraph.TEST.route) { from ->
                TestRoute(appState)
            }
            composable(NestedGraph.SPLASH.route) { from ->
                SplashRoute(appState)
            }
            composable(NestedGraph.TUTO.route) { from ->
                TutoRoute(appState)
            }
            composable(NestedGraph.LOGIN.route) { from ->
                LoginRoute(appState)
            }
        }
    }
}