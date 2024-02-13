package com.mario.template.data.remote.calladapter.live

import androidx.lifecycle.LiveData
import com.mario.template.data.exception.AppException
import com.mario.template.data.remote.response.ApiResponse
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Callback
import retrofit2.Response
import java.lang.reflect.Type
import java.util.concurrent.atomic.AtomicBoolean

class LiveDataCallAdapter<R>(private val responseType: Type) :
    CallAdapter<R, LiveData<ApiResponse<R>>> {
    override fun responseType(): Type {
        return responseType
    }

    override fun adapt(call: Call<R>): LiveData<ApiResponse<R>> {
        return object : LiveData<ApiResponse<R>>() {
            private val started = AtomicBoolean(false)
            override fun onActive() {
                super.onActive()
                if (started.compareAndSet(false, true)) {
                    postValue(ApiResponse.loading())
                    call.enqueue(object : Callback<R> {
                        override fun onResponse(call: Call<R>, response: Response<R>) {
                            if (response.errorBody() != null) {
                                val exception =
                                    AppException.ServerHttp(response.code(), response.message())
                                postValue(ApiResponse.error(exception))
                            } else {
                                val apiResponse = ApiResponse.of { response }
                                postValue(apiResponse)
                            }
                        }

                        override fun onFailure(call: Call<R>, t: Throwable) {
                            postValue(ApiResponse.error(t))
                        }
                    })
                }
            }
        }
    }
}