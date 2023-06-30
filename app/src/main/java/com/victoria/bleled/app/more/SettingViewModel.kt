package com.victoria.bleled.app.more

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.victoria.bleled.base.BaseViewModel
import com.victoria.bleled.data.model.BaseModel
import com.victoria.bleled.data.model.ModelUser
import com.victoria.bleled.data.net.adapter.live.ApiLiveResponse
import com.victoria.bleled.data.net.adapter.live.ApiLiveResponseObserver
import com.victoria.bleled.data.net.mytemplate.response.RespData
import com.victoria.bleled.data.repository.DataStoreRepository
import com.victoria.bleled.data.repository.MyTemplateRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(private val repository: MyTemplateRepository, private val dataStoreRepository: DataStoreRepository) : BaseViewModel() {

    private val _userInfo = MutableLiveData<ModelUser>()
    val userInfo: LiveData<ModelUser> = _userInfo

    suspend fun start() {
        val user = dataStoreRepository.getModel(ModelUser::class.java)
        _userInfo.value = user
    }

    suspend fun signOut() {
        val user = dataStoreRepository.getModel(ModelUser::class.java) ?: return

        val api = repository.apiService.userSignout(
            "Bearer " + user.access_token!!
        )

//        _dataLoading.value = true
        repository.callApi(api, object : ApiLiveResponseObserver<RespData<BaseModel>>() {
            override fun onChanged(result: ApiLiveResponse<RespData<BaseModel>>) {
                super.onChanged(result)

                if (result != null && result.status != ApiLiveResponse.Status.loading) {
                    if (result.status == ApiLiveResponse.Status.success) {
                        viewModelScope.launch {
                            val user:ModelUser? = null
                            dataStoreRepository.setModel(user)
                        }
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