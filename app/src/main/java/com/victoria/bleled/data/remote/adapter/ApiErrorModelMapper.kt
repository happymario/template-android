package com.victoria.bleled.data.remote.adapter


/** An interface for mapping [ApiResponse.Failure.Error] to custom error response model. */
fun interface ApiErrorModelMapper<V> {

    /** maps [ApiResponse.Failure.Error] to another model. */
    fun map(apiErrorResponse: ApiResponse.Failure.Error<*>): V
}
