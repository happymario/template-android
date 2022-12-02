package com.victoria.bleled.app.more

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.victoria.bleled.data.DataRepository
import com.victoria.bleled.data.model.BaseModel
import com.victoria.bleled.data.model.ModelUser
import com.victoria.bleled.data.remote.NetworkObserver
import com.victoria.bleled.data.remote.myservice.response.BaseResp
import com.victoria.bleled.base.BaseViewModel
import com.victoria.bleled.data.remote.NetworkResult
import com.victoria.bleled.data.remote.myservice.response.RespData

class SettingViewModel constructor(private val repository: DataRepository) : BaseViewModel() {

    private val _userInfo = MutableLiveData<ModelUser>()
    val userInfo: LiveData<ModelUser> = _userInfo

    fun start() {
        val prefDataSource = repository.prefDataSource
        _userInfo.value = prefDataSource.user
    }

    fun signOut() {
        val prefDataSource = repository.prefDataSource
        if (prefDataSource.user == null) {
            return
        }

        val api = repository.remoteService.userSignout(
            "Bearer " + prefDataSource.user!!.access_token!!
        )

//        _dataLoading.value = true
        repository.callApi(api, object : NetworkObserver<RespData<BaseModel>>() {
            override fun onChanged(result: NetworkResult<RespData<BaseModel>>) {
                super.onChanged(result)

                if (result != null && result.status.value != NetworkResult.Status.loading) {
                    if (result.status.value == NetworkResult.Status.success) {
                        val prefDataSource = repository.prefDataSource
                        prefDataSource.user = null
                        _userInfo.value = null
                    } else if (result.status.value == NetworkResult.Status.error) {
//                        _networkErrorLiveData.value = result
                    }
//                    _dataLoading.value = false
                }
            }
        })
    }
}