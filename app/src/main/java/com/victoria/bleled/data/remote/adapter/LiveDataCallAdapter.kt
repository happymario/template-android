package com.victoria.bleled.data.remote.adapter

import androidx.lifecycle.LiveData
import com.victoria.bleled.data.remote.ApiException
import com.victoria.bleled.data.remote.NetworkResult
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.lang.reflect.Type
import java.util.concurrent.atomic.AtomicBoolean

class LiveDataCallAdapter<R>(private val responseType: Type) :
    CallAdapter<R, LiveData<NetworkResult<R>>> {
    override fun responseType(): Type {
        return responseType
    }

    override fun adapt(call: Call<R>): LiveData<NetworkResult<R>> {
        return object : LiveData<NetworkResult<R>>() {
            private val started = AtomicBoolean(false)
            override fun onActive() {
                super.onActive()
                if (started.compareAndSet(false, true)) {
                    postValue(NetworkResult.loading())
                    call.enqueue(object : Callback<R?> {
                        override fun onResponse(call: Call<R?>, response: Response<R?>) {
                            if (response.body() != null) {
                                postValue(NetworkResult.success(response.body()))
                            } else {
                                var exception =
                                    ApiException(response.code(), response.message(), "")
                                try {
                                    if (response.errorBody() != null) {
                                        exception = ApiException(response.code(),
                                            response.message(),
                                            response.errorBody()!!
                                                .string())
                                    }
                                } catch (e: IOException) {
                                    e.printStackTrace()
                                }
                                postValue(NetworkResult.error(exception))
                            }
                        }

                        override fun onFailure(call: Call<R?>, t: Throwable) {
                            postValue(NetworkResult.error(t))
                        }
                    })
                }
            }
        }
    }
}