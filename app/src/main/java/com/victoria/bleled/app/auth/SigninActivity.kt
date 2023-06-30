package com.victoria.bleled.app.auth

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
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
import androidx.lifecycle.lifecycleScope
import com.victoria.bleled.R
import com.victoria.bleled.app.main.MainActivity
import com.victoria.bleled.app.recent.compose.components.MySurface
import com.victoria.bleled.app.theme.MyApplicationTheme
import com.victoria.bleled.base.BaseComposeActivity
import com.victoria.bleled.common.Constants
import com.victoria.bleled.util.CommonUtil
import com.victoria.bleled.util.compose.supportWideScreen
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SigninActivityPreview() {
    MyApplicationTheme {
        ComposeSigninScreen {}
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SigninActivityPreviewDark() {
    MyApplicationTheme(darkTheme = true) {
        ComposeSigninScreen {}
    }
}

@AndroidEntryPoint
class SigninActivity : BaseComposeActivity<UserViewModel>() {
    override val viewModel by viewModels<UserViewModel>()

    @Composable
    override fun ComposeContent() {
        MyApplicationTheme {
            ComposeSigninScreen(
                viewModel = viewModel,
                onEvent = {
                    when (it) {
                        is SignInEvent.SignIn -> {
                            viewModel.id.value = it.email
                            viewModel.pwd.value = it.pwd

                            lifecycleScope.launch {
                                viewModel.loginUser()
                            }
                        }

                        SignInEvent.Ask -> CommonUtil.gotoPhone(
                            this,
                            Constants.CLIENT_PHONE_NUMBER
                        )

                        SignInEvent.SignUp -> goSignup()
                        SignInEvent.SignInAsGuest -> goMain()
                    }
                })
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initViewModel()
    }


    private fun initViewModel() {
//        viewModel.toastMessage.observe(this) { msg ->
//            CommonUtil.showToast(this, msg)
//        }
//
//        viewModel.dataLoading.observe(this) { loading ->
//            if (loading) {
//                showProgress()
//            } else {
//                hideProgress()
//            }
//        }
//
//        viewModel.networkErrorLiveData.observe(this) { error ->
//            val exception = error.error
//            if (exception is ApiException && exception.code == ApiException.ERR_NO_USER) {
//                CommonUtil.showToast(this, R.string.msg_no_login_user)
//            } else {
//                val msg = NetworkObserver.getErrorMsg(this, error)
//                CommonUtil.showToast(
//                    this,
//                    if (msg == null || msg.isEmpty()) getString(R.string.network_connect_error) else msg
//                )
//            }
//        }

        viewModel.loginCompleteEvent.observe(this) {
            goMain()
        }
    }

    private fun goMain() {
        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            putExtra(Constants.ARG_TYPE, true)
        }
        startActivity(intent)
    }

    private fun goSignup() {
        val intent = Intent(this, SignupActivity::class.java)
        startActivity(intent)
    }
}

sealed class SignInEvent {
    data class SignIn(val email: String, val pwd: String) : SignInEvent()
    object SignInAsGuest : SignInEvent()
    object SignUp : SignInEvent()
    object Ask : SignInEvent()
}


@Composable
fun ComposeSigninScreen(viewModel: UserViewModel? = null, onEvent: (SignInEvent) -> Unit) {
    var showBranding by remember { mutableStateOf(true) }
    val focusManager = LocalFocusManager.current

    MySurface(
        modifier = Modifier.supportWideScreen(),
        color = MaterialTheme.colors.background,
        contentColor = MaterialTheme.colors.onBackground
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
                        style = MaterialTheme.typography.subtitle1,
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
    lightTheme: Boolean = !MyApplicationTheme.colors.isDark,
) {
    val assetId = if (lightTheme) {
        R.drawable.btn_setting_top
    } else {
        R.drawable.btn_top_setting_white
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
                style = MaterialTheme.typography.subtitle2,
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
                    style = MaterialTheme.typography.body2
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
        textStyle = MaterialTheme.typography.body2,
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
        textStyle = MaterialTheme.typography.body2,
        label = {
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.body2
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
            style = LocalTextStyle.current.copy(color = MaterialTheme.colors.error)
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
                    style = MaterialTheme.typography.subtitle2,
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
