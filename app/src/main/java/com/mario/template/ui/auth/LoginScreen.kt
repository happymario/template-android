package com.mario.template.ui.auth

import android.Manifest
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.OutlinedButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.mario.lib.base.extension.getActivity
import com.mario.template.Constants
import com.mario.template.R
import com.mario.template.TemplateAppState
import com.mario.template.helper.CommonHelper
import com.mario.template.helper.IntentShareHelper
import com.mario.template.ui.component.ActionType
import com.mario.template.ui.component.AppScaffold
import com.mario.template.ui.component.ComposableLifecycle
import com.mario.template.ui.theme.CustomTheme
import com.mario.template.ui.theme.MyTemplateTheme
import kotlinx.coroutines.launch

sealed class SignInEvent {
    data class SignIn(val email: String, val pwd: String) : SignInEvent()
    object SignInAsGuest : SignInEvent()
    object SignUp : SignInEvent()
    object Ask : SignInEvent()
}


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun LoginRoute(
    appState: TemplateAppState,
    viewModel: LoginViewModel = hiltViewModel<LoginViewModel>()
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsStateWithLifecycle()
    val loginCompleted by viewModel.loginCompleteEvent.observeAsState()
    val lifecycleScope = rememberCoroutineScope()
    val callPermissionState = rememberPermissionState(Manifest.permission.CALL_PHONE)
    val isGoPhone = remember { mutableStateOf(false) }
    val isClickedAsk = remember { mutableStateOf(false) }
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted && !isGoPhone.value && isClickedAsk.value) {
            // Permission granted
            isGoPhone.value = true
            isClickedAsk.value = false

            IntentShareHelper.gotoPhone(
                context,
                Constants.CLIENT_PHONE_NUMBER
            )
        } else {
            // Handle permission denial
            CommonHelper.showToast(context, R.string.pms_des)
        }
    }

    ComposableLifecycle { source, event ->
        when (event) {
            Lifecycle.Event.ON_CREATE -> {
                Log.d("TAG", "MainScreen: ON CREATE")
            }

            Lifecycle.Event.ON_START -> {
                Log.d("TAG", "MainScreen: ON START")
            }

            Lifecycle.Event.ON_RESUME -> {
                Log.d("TAG", "MainScreen: ON RESUME")
                if (callPermissionState.status.isGranted && !isGoPhone.value && isClickedAsk.value) {
                    isGoPhone.value = true
                    isClickedAsk.value = false

                    IntentShareHelper.gotoPhone(
                        context,
                        Constants.CLIENT_PHONE_NUMBER
                    )
                }
            }

            Lifecycle.Event.ON_PAUSE -> {
                Log.d("TAG", "MainScreen: ON PAUSE")
            }

            Lifecycle.Event.ON_STOP -> {
                Log.d("TAG", "MainScreen: ON STOP")
            }

            Lifecycle.Event.ON_DESTROY -> {
                Log.d("TAG", "MainScreen: ON DESTROY")
            }

            else -> {
                Log.d("TAG", "MainScreen: Out of life cycle")
            }
        }
    }

    if (loginCompleted != null && !loginCompleted!!.hasBeenHandled) {
        appState.goMainActivity(context)
    }

    LoginScreen(
        state = state,
        event = {
            when (it) {
                is SignInEvent.SignIn -> {
                    // dev1@gmail.com, test123
                    viewModel.id.value = it.email
                    viewModel.pwd.value = it.pwd
                    lifecycleScope.launch {
                        viewModel.loginUser()
                    }
                }

                SignInEvent.Ask -> {
                    isClickedAsk.value = true
                    if (!callPermissionState.status.shouldShowRationale && !callPermissionState.status.isGranted) { // permanently denied
                        CommonHelper.showToast(context, R.string.pms_des)
                        context.getActivity()?.let { it1 -> IntentShareHelper.gotoSetting(it1, 1) }
                    } else if (callPermissionState.status.shouldShowRationale) {
                        CommonHelper.showToast(context, R.string.pms_des)
                    } else {
                        requestPermissionLauncher.launch(Manifest.permission.CALL_PHONE)
                    }
                }

                SignInEvent.SignUp -> appState.goSignupActivity(context)
                SignInEvent.SignInAsGuest -> appState.goMainActivity(context)
            }
        },
        onDismissRequest = {
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
        })
}


@Composable
fun LoginScreen(
    state: LoginViewState, event: (SignInEvent) -> Unit,
    onDismissRequest: () -> Unit = {},
    onErrorPositiveAction: (action: ActionType?, value: Any?) -> Unit = { _, _ -> },
) {
    // A surface container using the 'background' color from the theme
    AppScaffold(
        state = state,
        onDismissRequest = onDismissRequest,
        onErrorPositiveAction = onErrorPositiveAction
    ) { _, _ ->
        LoginContent(state, event)
    }
}


@Composable
fun LoginContent(uiState: LoginViewState, onEvent: (SignInEvent) -> Unit) {
    var showBranding by remember { mutableStateOf(true) }
    val focusManager = LocalFocusManager.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CustomTheme.colors.primary)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() } // This is mandatory
                ) {
                    focusManager.clearFocus()
                }
        ) {
            Row(
                modifier = Modifier.height(200.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Logo(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(horizontal = 76.dp)
                    )
                    Text(
                        text = stringResource(id = R.string.app_name),
                        style = CustomTheme.typography.heading04,
                        fontSize = 24.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .padding(top = 10.dp)
                            .fillMaxWidth()
                    )
                }
            }
            SignInLayout(
                onEvent = onEvent,
                onFocusChange = { focused -> showBranding = !focused },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            )
        }
    }
}

@Composable
private fun Logo(
    modifier: Modifier = Modifier,
    lightTheme: Boolean = !CustomTheme.colors.isDark,
) {
    val assetId = if (lightTheme) {
        R.drawable.btn_setting
    } else {
        R.drawable.btn_setting_white
    }
    Image(
        painter = painterResource(id = assetId),
        modifier = modifier
            .width(50.dp)
            .height(50.dp),
        contentDescription = null
    )
}

@Composable
private fun SignInLayout(
    onEvent: (SignInEvent) -> Unit,
    onFocusChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val emailState = remember { SignInEmailState() }
    val passwordState = remember { SignInPasswordState() }
    val passwordFocusRequest = remember { FocusRequester() }

    val onSubmit = {
        if (emailState.isValid && passwordState.isValid) {
            onEvent(SignInEvent.SignIn(emailState.text, passwordState.text))
        } else {
            if (emailState.isValid)
                emailState.enableShowErrors()

            if (passwordState.isValid)
                emailState.enableShowErrors()
        }
    }
    onFocusChange(emailState.isFocused)

    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
            Text(
                text = stringResource(id = R.string.login),
                style = CustomTheme.typography.heading04,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 64.dp, bottom = 12.dp)
            )
        }
        Email(emailState = emailState,
            imeAction = ImeAction.Done,
            onImeAction = { passwordFocusRequest.requestFocus() })
        Spacer(modifier = Modifier.height(16.dp))
        Password(
            label = stringResource(id = R.string.pwd),
            passwordState = passwordState,
            imeAction = ImeAction.Next,
            onImeAction = onSubmit,
            modifier = Modifier.focusRequester(passwordFocusRequest)
        )

        Button(
            onClick = onSubmit,
            modifier = Modifier.fillMaxWidth(),
            enabled = emailState.isValid && passwordState.isValid
        ) {
            Text(text = stringResource(id = R.string.login))
        }

        OrSignInAsGuest(
            onEvent = onEvent,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun Email(
    emailState: TextFieldState = remember { SignInEmailState() },
    imeAction: ImeAction = ImeAction.Next,
    onImeAction: () -> Unit = {},
) {
    OutlinedTextField(
        value = emailState.text,
        onValueChange = {
            emailState.text = it
        },
        label = {
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                Text(
                    text = stringResource(id = R.string.id),
                    style = CustomTheme.typography.heading04
                )
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 10.dp)
            .onFocusChanged { focusState ->
                emailState.onFocusChange(focusState.isFocused)
                if (!focusState.isFocused) {
                    emailState.enableShowErrors()
                }
            },
        textStyle = CustomTheme.typography.heading04,
        isError = emailState.showErrors(),
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = imeAction),
        keyboardActions = KeyboardActions(
            onDone = {
                onImeAction()
            }
        )
    )

    emailState.getError()?.let { error -> TextFieldError(textError = error) }
}

@Composable
fun Password(
    label: String,
    passwordState: TextFieldState,
    modifier: Modifier = Modifier,
    imeAction: ImeAction = ImeAction.Done,
    onImeAction: () -> Unit = {},
) {
    val showPassword = remember { mutableStateOf(false) }
    OutlinedTextField(
        value = passwordState.text,
        onValueChange = {
            passwordState.text = it
            passwordState.enableShowErrors()
        },
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 10.dp)
            .onFocusChanged { focusState ->
                passwordState.onFocusChange(focusState.isFocused)
                if (!focusState.isFocused) {
                    passwordState.enableShowErrors()
                }
            },
        textStyle = CustomTheme.typography.heading04,
        label = {
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                Text(
                    text = label,
                    style = CustomTheme.typography.heading04
                )
            }
        },
        trailingIcon = {
            if (showPassword.value) {
                IconButton(onClick = { showPassword.value = false }) {
                    Icon(
                        imageVector = Icons.Filled.Visibility,
                        contentDescription = stringResource(id = R.string.pwd)
                    )
                }
            } else {
                IconButton(onClick = { showPassword.value = true }) {
                    Icon(
                        imageVector = Icons.Filled.VisibilityOff,
                        contentDescription = stringResource(id = R.string.pwd_confirm)
                    )
                }
            }
        },
        visualTransformation = if (showPassword.value) {
            VisualTransformation.None
        } else {
            PasswordVisualTransformation()
        },
        isError = passwordState.showErrors(),
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = imeAction),
        keyboardActions = KeyboardActions(
            onDone = {
                onImeAction()
            }
        )
    )

    passwordState.getError()?.let { error -> TextFieldError(textError = error) }
}

/**
 * To be removed when [TextField]s support error
 */
@Composable
fun TextFieldError(textError: String) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = textError,
            modifier = Modifier.fillMaxWidth(),
            style = LocalTextStyle.current.copy(color = CustomTheme.colors.primary)
        )
    }
}

@Composable
fun OrSignInAsGuest(
    onEvent: (SignInEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface {
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                Text(
                    text = stringResource(id = R.string.to_ask),
                    style = CustomTheme.typography.btnTitle1,
                    modifier = Modifier
                        .paddingFromBaseline(top = 25.dp)
                        .clickable {
                            onEvent(SignInEvent.Ask)
                        },
                    textDecoration = TextDecoration.Underline
                )
            }
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(
                onClick = { onEvent(SignInEvent.SignUp) },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(top = 20.dp, bottom = 24.dp)
            ) {
                Text(text = stringResource(id = R.string.signup))
            }
            Spacer(modifier = Modifier.width(20.dp))
            OutlinedButton(
                onClick = { onEvent(SignInEvent.SignInAsGuest) },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(top = 20.dp, bottom = 24.dp)
            ) {
                Text(text = stringResource(id = R.string.to_main))
            }
        }
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LoginScreenPreview() {
    MyTemplateTheme {
        LoginScreen(LoginViewState(true, null), event = {})
    }
}
