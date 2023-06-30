package com.victoria.bleled.app

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.victoria.bleled.base.BaseViewModel
import com.victoria.bleled.base.internal.Event
import com.victoria.bleled.data.model.ModelAppInfo
import com.victoria.bleled.data.model.ModelUser
import com.victoria.bleled.data.net.adapter.live.ApiLiveResponse
import com.victoria.bleled.data.net.adapter.live.ApiLiveResponseObserver
import com.victoria.bleled.data.net.mytemplate.response.RespData
import com.victoria.bleled.data.repository.DataStoreKey
import com.victoria.bleled.data.repository.DataStoreRepository
import com.victoria.bleled.data.repository.MyTemplateRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val repository: MyTemplateRepository,
    private val dataRepository: DataStoreRepository,
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
        val api = repository.apiService.appInfo("android")

//        _dataLoading.value = true
        repository.callApi(api, object : ApiLiveResponseObserver<RespData<ModelAppInfo>>() {
            override fun onChanged(result: ApiLiveResponse<RespData<ModelAppInfo>>) {
                super.onChanged(result)

                if (result.status == ApiLiveResponse.Status.success) {
                    val appInfo = result.data?.data

                    // get last location from preference
                    viewModelScope.launchInSafe {
                        dataRepository.setModel(appInfo)

                        val user = dataRepository.getModel(ModelUser::class.java)
                        if (user != null) {
                            loginUser(user)
                        } else {
                            _openEvent.value = Event(0)
                        }
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

    suspend fun loginUser(user: ModelUser) {
        val token = dataRepository.getString(DataStoreKey.PREFS_PUSH_TOKEN)
        val api = repository.apiService.userLogin(
            user.id!!,
            user.pwd,
            token ?: "",
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

                        viewModelScope.launchInSafe {
                            dataRepository.setModel(newUser)
                        }

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