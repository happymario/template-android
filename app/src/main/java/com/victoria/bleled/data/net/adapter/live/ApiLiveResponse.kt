package com.victoria.bleled.data.net.adapter.live

import com.victoria.bleled.data.net.adapter.ApiResponse

open class ApiLiveResponse<T> constructor(var status: Status, var data: T?, var error: Throwable?) :
    ApiResponse<T>() {

    enum class Status(private val value: Int) {
        none(0), loading(2), success(1), error(-1);
    }

    companion object {
        @JvmStatic
        fun <T> success(data: T): ApiLiveResponse<T> {
            return ApiLiveResponse(Status.success, data, null)
        }

        @JvmStatic
        fun <T> error(error: Throwable): ApiLiveResponse<T> {
            return ApiLiveResponse(Status.error, null, error)
        }

        @JvmStatic
        fun <T> loading(): ApiLiveResponse<T> {
            return ApiLiveResponse(Status.loading, null, null)
        }
    }
}