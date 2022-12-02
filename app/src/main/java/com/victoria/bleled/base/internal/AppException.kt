package com.victoria.bleled.base.internal

import androidx.annotation.StringRes

sealed class AppException(
    override var message: String? = null,
    var throwable: Throwable? = null,
) : RuntimeException(message ?: "") {
    constructor(throwable: Throwable) : this(message = "", throwable)

    // 시나리오 에러
    class Flow(message: String) : AppException(message)

    class BadParameter(message: String = "") : AppException("매개변수부족:$message")

    // 알수 없는 에러
    class Unknown(throwable: Throwable) : AppException("", throwable)

    class Handle(val code: Int, message: String = "") : AppException(message)

    // 알림목적 에러
    class Alert(message: String) : AppException(message)
    class Strings(@StringRes message: Int) : AppException("")

    // 통신 (서버오류)
    class ServerHttp(code: Int, message: String?) : AppException(message ?: "")

    // 통신 (네트워크 오류)
    class Network(throwable: Throwable) : AppException(throwable.message ?: "")
}