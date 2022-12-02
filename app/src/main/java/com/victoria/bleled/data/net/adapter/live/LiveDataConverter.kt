package com.victoria.bleled.data.net.adapter.live

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.victoria.bleled.base.internal.AppException
import com.victoria.bleled.data.net.adapter.StatusCode
import java.util.concurrent.Executor

abstract class LiveDataConverter<RequestType, ResultType> @MainThread constructor(var networkIO: Executor) {
    var result: MediatorLiveData<ApiLiveResponse<ResultType>> = MediatorLiveData()
    fun asLiveData(): LiveData<ApiLiveResponse<ResultType>?> {
        return result
    }

    @MainThread
    protected abstract fun createCall(): LiveData<ApiLiveResponse<RequestType>>

    @MainThread
    protected abstract fun processResponse(response: ApiLiveResponse<RequestType>?): LiveData<ResultType>
    private fun fetchFromNetwork(source: LiveData<ResultType?>) {
        val apiCall = createCall()
        result.addSource(apiCall) { response: ApiLiveResponse<RequestType> ->
            if (response.status == ApiLiveResponse.Status.loading) {
                return@addSource
            }
            result.removeSource(apiCall)
            if (response.status == ApiLiveResponse.Status.success) {
                networkIO.execute {
                    val newData = processResponse(response)
                    val responseData = newData.value
                    if (responseData == null) {
                        result.setValue(
                            ApiLiveResponse.error(
                                AppException.ServerHttp(
                                    StatusCode.Unknown.code,
                                    "",
                                )
                            )
                        )
                    } else if (responseData is Exception) {
                        result.setValue(ApiLiveResponse.error(responseData as Exception))
                    } else {
                        result.setValue(ApiLiveResponse.success(responseData))
                    }
                }
            } else {
                networkIO.execute { result.setValue(ApiLiveResponse.error(response.error!!)) }
            }
        }
    }

    init {
        result.value = ApiLiveResponse.loading()
        fetchFromNetwork(MutableLiveData(null))
    }
}