package com.victoria.bleled.app

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.victoria.bleled.base.BaseViewModel
import com.victoria.bleled.base.internal.Event
import com.victoria.bleled.common.manager.PreferenceManager.Companion.getInstance
import com.victoria.bleled.data.model.ModelAppInfo
import com.victoria.bleled.data.model.ModelUser
import com.victoria.bleled.data.net.adapter.live.ApiLiveResponse
import com.victoria.bleled.data.net.adapter.live.ApiLiveResponseObserver
import com.victoria.bleled.data.net.mytemplate.response.RespData
import com.victoria.bleled.data.net.repository.MyTemplateRepository

class SplashViewModel constructor(
    private val repository: MyTemplateRepository,
) : BaseViewModel() {
    /************************************************************
     *  Variables
     ************************************************************/
    private val _openEvent = MutableLiveData<Event<Int>>()
    val openEvent: LiveData<Event<Int>> = _openEvent

    private val _isReady = MutableLiveData<Boolean>(false)
    val isReady: LiveData<Boolean> = _isReady

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

//        _dataLoading.value = true
        repository.callApi(api, object : ApiLiveResponseObserver<RespData<ModelAppInfo>>() {
            override fun onChanged(result: ApiLiveResponse<RespData<ModelAppInfo>>) {
                super.onChanged(result)

                if (result.status == ApiLiveResponse.Status.success) {

                    // get last location from preference
                    val prefDataSource = getInstance()
                    prefDataSource.appInfo = result.data!!.data

                    if (prefDataSource.user != null) {
                        loginUser(prefDataSource.user!!)
                    } else {
                        _openEvent.value = Event(0)
                    }

//                    _dataLoading.value = false
                } else if (result.status == ApiLiveResponse.Status.error) {
//                    _networkErrorLiveData.value = result
                    _openEvent.value = Event(0)

//                    _dataLoading.value = false
                }
            }
        })
    }

    fun loginUser(user: ModelUser) {
        val prefDataSource = getInstance()
        val api = repository.remoteService.userLogin(
            user.id!!,
            user.pwd,
            if (prefDataSource.pushToken != null) prefDataSource.pushToken!! else "",
            "android"
        )

//        _dataLoading.value = true
        repository.callApi(api, object : ApiLiveResponseObserver<RespData<ModelUser>>() {
            override fun onChanged(result: ApiLiveResponse<RespData<ModelUser>>) {
                super.onChanged(result)

                if (result != null && result.status != ApiLiveResponse.Status.loading) {
                    if (result.status == ApiLiveResponse.Status.success) {
                        val newUser = result.data!!.data
                        newUser?.pwd = user.pwd
                        prefDataSource.user = newUser

                        _openEvent.value = Event(1)
                    } else if (result.status == ApiLiveResponse.Status.error) {
//                        _networkErrorLiveData.value = result
                        _openEvent.value = Event(0)
                    }
//                    _dataLoading.value = false
                }
            }
        })
    }
}