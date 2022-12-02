package com.victoria.bleled.data.remote.adapter

/**
 * A scope function for handling success response [ApiResponse.Success] a unit
 * block of code within the context of the response.
 */
@JvmSynthetic
fun <T> ApiResponse<T>.onSuccess(onResult: ApiResponse.Success<T>.() -> Unit): ApiResponse<T> {
    if (this is ApiResponse.Success) {
        onResult(this)
    }
    return this
}

/**
 * A suspend scope function for handling success response [ApiResponse.Success] a unit
 * block of code within the context of the response.
 */
@JvmSynthetic
suspend fun <T> ApiResponse<T>.suspendOnSuccess(
    onResult: suspend ApiResponse.Success<T>.() -> Unit
): ApiResponse<T> {
    if (this is ApiResponse.Success) {
        onResult(this)
    }
    return this
}

/**
 * A suspend scope function for handling error response [ApiResponse.Failure.Error] a unit
 * block of code within the context of the response.
 */
@JvmSynthetic
suspend fun <T> ApiResponse<T>.suspendOnError(
    onResult: suspend ApiResponse.Failure.Error<T>.() -> Unit
): ApiResponse<T> {
    if (this is ApiResponse.Failure.Error) {
        onResult(this)
    }
    return this
}

@JvmSynthetic
suspend fun <T> ApiResponse<T>.suspendOnException(
    onResult: suspend ApiResponse.Failure.Exception<T>.() -> Unit
): ApiResponse<T> {
    if (this is ApiResponse.Failure.Exception) {
        onResult(this)
    }
    return this
}

@JvmSynthetic
fun <T> ApiResponse<T>.onError(onResult: ApiResponse.Failure.Error<T>.() -> Unit): ApiResponse<T> {
    if (this is ApiResponse.Failure.Error) {
        onResult(this)
    }
    return this
}

/**
 * A scope function for handling exception response [ApiResponse.Failure.Exception] a unit
 * block of code within the context of the response.
 */
@JvmSynthetic
fun <T> ApiResponse<T>.onException(onResult: ApiResponse.Failure.Exception<T>.() -> Unit): ApiResponse<T> {
    if (this is ApiResponse.Failure.Exception) {
        onResult(this)
    }
    return this
}

/** Map [ApiResponse.Failure.Error] to a customized error response model. */
@JvmSynthetic
fun <T, V> ApiResponse.Failure.Error<T>.map(
    converter: ApiErrorModelMapper<V>,
    onResult: V.() -> Unit
) {
    onResult(converter.map(this))
}

/** A message from the [ApiResponse.Failure.Error]. */
fun <T> ApiResponse.Failure.Error<T>.message(): String = toString()

/** A message from the [ApiResponse.Failure.Exception]. */
fun <T> ApiResponse.Failure.Exception<T>.message(): String = toString()