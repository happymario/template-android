package com.victoria.bleled.app.more

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.victoria.bleled.base.BaseViewModel
import com.victoria.bleled.common.manager.PrefManager
import com.victoria.bleled.data.model.BaseModel
import com.victoria.bleled.data.model.ModelUser
import com.victoria.bleled.data.net.adapter.live.ApiLiveResponse
import com.victoria.bleled.data.net.adapter.live.ApiLiveResponseObserver
import com.victoria.bleled.data.net.mytemplate.response.RespData
import com.victoria.bleled.data.net.repository.MyTemplateRepository

class SettingViewModel constructor(private val repository: MyTemplateRepository) : BaseViewModel() {

    private val _userInfo = MutableLiveData<ModelUser>()
    val userInfo: LiveData<ModelUser> = _userInfo

    fun start() {
        val prefDataSource = PrefManager.getInstance()
        _userInfo.value = prefDataSource.user
    }

    fun signOut() {
        val prefDataSource = PrefManager.getInstance()
        if (prefDataSource.user == null) {
            return
        }

        val api = repository.remoteService.userSignout(
            "Bearer " + prefDataSource.user!!.access_token!!
        )

//        _dataLoading.value = true
        repository.callApi(api, object : ApiLiveResponseObserver<RespData<BaseModel>>() {
            override fun onChanged(result: ApiLiveResponse<RespData<BaseModel>>) {
                super.onChanged(result)

                if (result != null && result.status != ApiLiveResponse.Status.loading) {
                    if (result.status == ApiLiveResponse.Status.success) {
                        val prefDataSource = PrefManager.getInstance()
                        prefDataSource.user = null
                        _userInfo.value = null
                    } else if (result.status == ApiLiveResponse.Status.error) {
//                        _networkErrorLiveData.value = result
                    }
//                    _dataLoading.value = false
                }
            }
        })
    }
}