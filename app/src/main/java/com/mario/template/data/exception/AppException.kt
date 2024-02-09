package com.mario.template.data.exception

sealed class AppException(override val message: String) : Throwable(message) {
    data class ServerHttp(val code: Int, override val message: String) : AppException(message)

    data class Network(val throwable: Throwable) : AppException(throwable.message ?: "")

    data class ToastException(
        override val message: String,
    ) : AppException(message)

    data class AlertException(
        override val message: String,
    ) : AppException(message)
}