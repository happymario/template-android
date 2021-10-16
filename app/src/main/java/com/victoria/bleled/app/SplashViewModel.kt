package com.victoria.bleled.app

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.victoria.bleled.data.DataRepository
import com.victoria.bleled.data.model.ModelAppInfo
import com.victoria.bleled.data.remote.NetworkObserver
import com.victoria.bleled.data.remote.myservice.BaseResponse
import com.victoria.bleled.util.arch.Event
import com.victoria.bleled.util.arch.base.BaseViewModel
import com.victoria.bleled.util.arch.network.NetworkResult

class SplashViewModel constructor(private val repository: DataRepository) : BaseViewModel() {
    /************************************************************
     *  Variables
     ************************************************************/
    private val _openEvent = MutableLiveData<Event<Int>>()
    val openEvent: LiveData<Event<Int>> = _openEvent

    /************************************************************
     *  Public Functions
     ************************************************************/
    fun start() {
        loadAppInfo()
    }

    /************************************************************
     *  Private Functions
     ************************************************************/
    private fun loadAppInfo() {
        val api = repository.remoteService.appInfo("android")

        _dataLoading.value = true
        repository.callApi(api, object : NetworkObserver<BaseResponse<ModelAppInfo>>() {
            override fun onChanged(result: NetworkResult<BaseResponse<ModelAppInfo>>) {
                super.onChanged(result)

                if (result.status.value == NetworkResult.Status.success) {
                    val prefDataSource = repository.prefDataSource
                    prefDataSource.appInfo = result.data.data

                    if (prefDataSource.user != null) {
                        _openEvent.value = Event(1)
                    } else {
                        _openEvent.value = Event(0)
                    }

                    _dataLoading.value = false
                } else if (result.status.value == NetworkResult.Status.error) {
                    _networkErrorLiveData.value = result
                    _openEvent.value = Event(0)

                    _dataLoading.value = false
                }
            }
        })
    }
}