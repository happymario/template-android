package com.mario.template.ui.splash

import android.Manifest
import android.os.Build
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.mario.template.R
import com.mario.template.TemplateAppState
import com.mario.template.ui.component.ActionType
import com.mario.template.ui.component.AppScaffold
import com.mario.template.ui.component.InfinityText
import com.mario.template.ui.theme.CustomTheme
import com.mario.template.ui.theme.MyWeatherTheme
import kotlinx.coroutines.delay

private const val SplashWaitTime: Long = 3000

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun Splash(appState: TemplateAppState, viewModel: SplashViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val locationPermissionState = rememberMultiplePermissionsState(
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) listOf(
            Manifest.permission.CAMERA,
            Manifest.permission.READ_MEDIA_IMAGES
        ) else listOf(
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    ) { permissions ->
        when {
            permissions.all { it.value } -> viewModel.getAppInfo()
            else -> viewModel.permissionIsNotGranted()
        }
    }

    LaunchedEffect(state) {
        val requestPermission = state.isRequestPermission

        when {
            requestPermission -> {
                when {
                    locationPermissionState.allPermissionsGranted -> {
                        viewModel.getAppInfo()
                    }

                    locationPermissionState.shouldShowRationale -> {
                        viewModel.permissionIsNotGranted()
                    }

                    else -> {
                        locationPermissionState.launchMultiplePermissionRequest()
                    }
                }
            }

            else -> return@LaunchedEffect
        }
        viewModel.completePermissionRequest()
    }

    SplashScreen(
        state,
        checkNext = viewModel::checkNextScreen,
        onNavTuto = appState::navigateToTuto,
        onNavHome = {
            appState.goMainActivity(context)
        },
        onDismissErrorDialog = {
            viewModel.hideError()
        },
        onErrorPositiveAction = { action, _ ->
            action?.let {
                when (it) {
                    ActionType.CANCEL -> {
                        viewModel.retry()
                    }

                    ActionType.CONFIRM -> {
                        appState.openAppSetting(context)
                    }
                }
            }
        },
    )
}

@Composable
fun SplashScreen(
    state: SplashViewState,
    checkNext: () -> Unit = {},
    onNavTuto: () -> Unit = {},
    onNavHome: () -> Unit = {},
    onDismissErrorDialog: () -> Unit = {},
    onErrorPositiveAction: (action: ActionType?, value: Any?) -> Unit = { _, _ -> },
) {
    AppScaffold(
        state = state,
        onDismissErrorDialog = onDismissErrorDialog,
        onErrorPositiveAction = onErrorPositiveAction
    ) { _, viewState ->
        SplashContent(state, checkNext = checkNext, onNavTuto = onNavTuto, onNavHome = onNavHome)
    }
}

@Composable
fun SplashContent(
    state: SplashViewState,
    checkNext: () -> Unit = {},
    onNavTuto: () -> Unit = {},
    onNavHome: () -> Unit = {}
) {
    if (state.isLoadedData) {
        LaunchedEffect(true) {
            delay(SplashWaitTime)
            checkNext.invoke()
        }
    }

    if (state.naviTuto) {
        LaunchedEffect(true) {
            onNavTuto.invoke()
        }
    }

    if (state.naviHome) {
        LaunchedEffect(true) {
            onNavHome.invoke()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(CustomTheme.colors.splash_gradient),
                shape = RectangleShape,
                alpha = 1.0f
            )
    ) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_logo),
                contentDescription = "logo",
                modifier = Modifier.size(240.dp, 180.dp)
            )
            Text(
                text = stringResource(id = R.string.splash_logo1),
                style = CustomTheme.typography.heading01.copy(color = CustomTheme.colors.title1)
            )
            InfinityText(
                texts = arrayListOf(stringResource(id = R.string.splash_logo2)),
                delayTime = 4000L,
                modifier = Modifier.offset(y = (-10).dp),
                content = { targetState ->
                    Text(
                        text = targetState,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = CustomTheme.typography.heading02.copy(color = CustomTheme.colors.subtitle1),
                    )
                }
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DefaultPreview() {
    MyWeatherTheme {
        SplashScreen(SplashViewState())
    }
}