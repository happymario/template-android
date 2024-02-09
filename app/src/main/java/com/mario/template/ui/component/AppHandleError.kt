package com.mario.template.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.mario.template.R
import com.mario.template.base.BaseViewState
import com.mario.template.data.exception.AppException
import com.mario.template.util.CommonUtil

enum class ActionType {
    CONFIRM, CANCEL
}

@Composable
fun <VS : BaseViewState> AppHandleError(
    modifier: Modifier = Modifier,
    state: VS,
    onShowSnackBar: (message: String) -> Unit = {},
    onPositiveAction: (action: ActionType?, value: Any?) -> Unit = { _, _ -> },
    onNegativeAction: (action: ActionType?, value: Any?) -> Unit = { _, _ -> },
    onDismissErrorDialog: () -> Unit = {},
    content: @Composable (VS) -> Unit,
) {
    Box(modifier = modifier) {
        content(state)

        if (state.isLoading) {
            FullScreenLoading()
        }

        if (state.error != null) {
            HandleError(
                error = state.error!!, // Not null
                onPositiveAction = onPositiveAction,
                onNegativeAction = onNegativeAction,
                onShowSnackBar = onShowSnackBar,
                onDismissRequest = onDismissErrorDialog,
            )
        }
    }
}

@Composable
fun FullScreenLoading(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.then(
            Modifier
                .fillMaxSize()
                .background(Color.Transparent)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                    onClick = {},
                ),
        ), contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.background(Color.Transparent),
            strokeWidth = 5.dp,
        )
    }
}

@Composable
fun HandleError(
    error: Throwable,
    onPositiveAction: (action: ActionType?, value: Any?) -> Unit = { _, _ -> },
    onNegativeAction: (action: ActionType?, value: Any?) -> Unit = { _, _ -> },
    onShowSnackBar: (message: String) -> Unit = {},
    onDismissRequest: () -> Unit = {},
) {
    val context = LocalContext.current

    when (error) {
        is AppException.AlertException -> {
            AppErrorAlertDialog(
                title = "알림",
                message = error.message,
                positiveButtonTitle = "확인",
                negativeButtonTitle = "취소",
                data = error,
                onDismissRequest = onDismissRequest,
                positiveAction = onPositiveAction,
                negativeAction = onNegativeAction,
            )
        }

        is AppException.ToastException -> {
            CommonUtil.showToast(context, error.message)
        }

        is AppException.ServerHttp -> {
            CommonUtil.showToast(context, error.message)
        }

        is AppException.Network -> {
            CommonUtil.showToast(context, context.getString(R.string.error_message_network))
        }

        else -> {
            LaunchedEffect(key1 = true) {
                onShowSnackBar.invoke(
                    error.message ?: context.getString(R.string.error_message_default)
                )
                onDismissRequest.invoke()
            }
        }
    }
}

@Composable
fun AppErrorAlertDialog(
    title: String = "",
    message: String = "",
    positiveButtonTitle: String? = null,
    negativeButtonTitle: String? = null,
    data: Throwable? = null,
    positiveAction: (action: ActionType?, value: Any?) -> Unit = { _, _ -> },
    negativeAction: (action: ActionType?, value: Any?) -> Unit = { _, _ -> },
    onDismissRequest: () -> Unit = {},
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(text = title)
        },
        text = {
            Text(text = message)
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onDismissRequest.invoke()
                    positiveAction(ActionType.CONFIRM, data)
                    //positiveAction.invoke(alertDialog.positiveAction, alertDialog.positiveObject)
                },
            ) {
                Text(text = positiveButtonTitle ?: stringResource(id = R.string.retry))
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest.invoke()
                    negativeAction(ActionType.CANCEL, data)
                    //negativeAction.invoke(alertDialog.positiveAction, alertDialog.positiveObject)
                },
            ) {
                Text(text = negativeButtonTitle ?: stringResource(id = android.R.string.cancel))
            }
        },
    )
}
