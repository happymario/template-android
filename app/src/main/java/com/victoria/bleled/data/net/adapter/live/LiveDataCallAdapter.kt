package com.victoria.bleled.data.net.adapter.live

import androidx.lifecycle.LiveData
import com.victoria.bleled.base.internal.AppException
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Callback
import retrofit2.Response
import java.lang.reflect.Type
import java.util.concurrent.atomic.AtomicBoolean

class LiveDataCallAdapter<R>(private val responseType: Type) :
    CallAdapter<R, LiveData<ApiLiveResponse<R>>> {
    override fun responseType(): Type {
        return responseType
    }

    override fun adapt(call: Call<R>): LiveData<ApiLiveResponse<R>> {
        return object : LiveData<ApiLiveResponse<R>>() {
            private val started = AtomicBoolean(false)
            override fun onActive() {
                super.onActive()
                if (started.compareAndSet(false, true)) {
                    postValue(ApiLiveResponse.loading())
                    call.enqueue(object : Callback<R?> {
                        override fun onResponse(call: Call<R?>, response: Response<R?>) {
                            if (response.errorBody() != null) {
                                val exception =
                                    AppException.ServerHttp(response.code(), response.message())
                                postValue(ApiLiveResponse.error(exception))
                            } else {
                                postValue(ApiLiveResponse.success(response.body()!!))
                            }
                        }

                        override fun onFailure(call: Call<R?>, t: Throwable) {
                            postValue(ApiLiveResponse.error(t))
                        }
                    })
                }
            }
        }
    }
}