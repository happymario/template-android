package com.mario.template.data.remote.calladapter.coroutines

import com.mario.template.data.remote.response.ApiResponse
import okio.Timeout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

internal class ApiResponseCallDelegate<T>(proxy: Call<T>) : CallDelegate<T, ApiResponse<T>>(proxy) {
    override fun enqueueImpl(callback: Callback<ApiResponse<T>>) = proxy.enqueue(
        object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                val apiResponse = ApiResponse.of { response }
                callback.onResponse(this@ApiResponseCallDelegate, Response.success(apiResponse))
            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                callback.onResponse(
                    this@ApiResponseCallDelegate, Response.success(
                        ApiResponse.error(t)
                    )
                )
            }
        }
    )

    override fun cloneImpl(): Call<ApiResponse<T>> = ApiResponseCallDelegate(proxy.clone())

    override fun timeout(): Timeout = Timeout.NONE
}