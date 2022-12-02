package com.victoria.bleled.base.internal

sealed class Resource<T>(
    val data: T? = null,
    val errorCode: Int? = null,
    open val message: String? = null,
) {
    class Success<T>(data: T?) : Resource<T>(data) {
        fun data(): T? = data
    }

    class Loading<T>(data: T? = null) : Resource<T>(data)

    class Error<T>(
        errorCode: Int = UnknownError,
        override var message: String? = null,
        data: T? = null,
    ) : Resource<T>(data, errorCode, message) {

        constructor(throwable: Throwable) : this() {
            message = throwable.message
        }
    }

    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[data=$data]"
            is Error -> "Error[exception=$errorCode]"
            is Loading<T> -> "Loading"
        }
    }

    companion object {
        const val UnknownError = -1
    }
}