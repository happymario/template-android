package com.mario.template.data.remote.response


/** An interface for mapping [ApiResponse.Failure.Error] to custom error response model. */
fun interface ApiErrorModelMapper<V> {

    /** maps [ApiResponse.Failure.Error] to another model. */
    fun map(apiErrorResponse: ApiResponse.Failure.Error<*>): V
}
