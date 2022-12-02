package com.victoria.bleled.data.remote

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import java.util.concurrent.Executor

abstract class LiveDataConverter<RequestType, ResultType> @MainThread constructor(var networkIO: Executor) {
    var result: MediatorLiveData<NetworkResult<ResultType>> = MediatorLiveData()
    fun asLiveData(): LiveData<NetworkResult<ResultType>?> {
        return result
    }

    @MainThread
    protected abstract fun createCall(): LiveData<NetworkResult<RequestType>>

    @MainThread
    protected abstract fun processResponse(response: NetworkResult<RequestType>?): LiveData<ResultType>
    private fun fetchFromNetwork(source: LiveData<ResultType?>) {
        val apiCall = createCall()
        result.addSource(apiCall) { response: NetworkResult<RequestType> ->
            if (response.status.value == NetworkResult.Status.loading) {
                return@addSource
            }
            result.removeSource(apiCall)
            if (response.status.value == NetworkResult.Status.success) {
                networkIO.execute {
                    val newData = processResponse(response)
                    val responseData = newData.value
                    if (responseData == null) {
                        result.setValue(
                            NetworkResult.error(
                                ApiException(
                                    ApiException.Companion.ERR_NO_DATA,
                                    "",
                                    ""
                                )
                            )
                        )
                    } else if (responseData is Exception) {
                        result.setValue(NetworkResult.error(responseData as Exception?))
                    } else {
                        result.setValue(NetworkResult.success(responseData))
                    }
                }
            } else {
                networkIO.execute { result.setValue(NetworkResult.error(response.error)) }
            }
        }
    }

    init {
        result.value = NetworkResult.loading()
        fetchFromNetwork(MutableLiveData(null))
    }
}