package com.victoria.bleled.data

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.victoria.bleled.common.AppExecutors
import com.victoria.bleled.data.remote.response.ApiEmptyResponse
import com.victoria.bleled.data.remote.response.ApiErrorResponse
import com.victoria.bleled.data.remote.response.ApiResponse
import com.victoria.bleled.data.remote.response.ApiSuccessResponse
import com.victoria.bleled.util.architecture.AbsentLiveData
import com.victoria.bleled.util.architecture.remote.Resource

abstract class CommonNetworkResource<ResultType, RequestType> @MainThread constructor(private val appExecutors: AppExecutors) {
    private val result = MediatorLiveData<Resource<ResultType>>()

    init {
        result.value = Resource.loading(null)
        fetchFromNetwork(AbsentLiveData.create())
    }

    @MainThread
    private fun setValue(newValue: Resource<ResultType>) {
        if (result.value != newValue) {
            result.value = newValue
        }
    }

    private fun fetchFromNetwork(source: LiveData<ResultType>) {
        val apiResponse = createCall()
        result.addSource(apiResponse) { response ->
            result.removeSource(apiResponse)
            when (response) {
                is ApiSuccessResponse -> {
                    appExecutors.mainThread().execute {
                        // we specially request a new live data,
                        // otherwise we will get immediately last cached value,
                        // which may not be updated with latest results received from network.
                        result.addSource(processResponse(response)) { newData ->
                            setValue(Resource.success(newData))
                        }
                    }
                }
                is ApiEmptyResponse -> {
                    appExecutors.mainThread().execute {
                        // reload from disk whatever we had
                        result.addSource(processResponse(response)) { newData ->
                            setValue(Resource.success(newData))
                        }
                    }
                }
                is ApiErrorResponse -> {
                    onFetchFailed()
                    result.addSource(source) { newData ->
                        setValue(Resource.error(response.errorMessage, newData))
                    }
                }
            }
        }
    }

    protected open fun onFetchFailed() {}

    fun asLiveData() = result as LiveData<Resource<ResultType>>

    @MainThread
    protected abstract fun processResponse(response: ApiResponse<RequestType>): LiveData<ResultType>

    @MainThread
    protected abstract fun createCall(): LiveData<ApiResponse<RequestType>>
}