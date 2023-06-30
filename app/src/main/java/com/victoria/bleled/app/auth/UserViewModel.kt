package com.victoria.bleled.app.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.victoria.bleled.app.MyApplication
import com.victoria.bleled.base.BaseViewModel
import com.victoria.bleled.base.internal.Event
import com.victoria.bleled.data.model.ModelUpload
import com.victoria.bleled.data.model.ModelUser
import com.victoria.bleled.data.net.adapter.live.ApiLiveResponse
import com.victoria.bleled.data.net.adapter.live.ApiLiveResponseObserver
import com.victoria.bleled.data.net.mytemplate.response.RespData
import com.victoria.bleled.data.repository.DataStoreKey
import com.victoria.bleled.data.repository.DataStoreRepository
import com.victoria.bleled.data.repository.MyTemplateRepository
import com.victoria.bleled.util.CommonUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(private val repository: MyTemplateRepository, private val dataRepository:DataStoreRepository) : BaseViewModel() {
    /************************************************************
     *  Variables
     ************************************************************/
    // Two-way databinding, exposing MutableLiveData
    val id = MutableLiveData<String>()
    val pwd = MutableLiveData<String>()
    val pwdConfirm = MutableLiveData<String>()
    val motto = MutableLiveData<String>()

    private val _loginCompleteEvent = MutableLiveData<Event<Unit>>()
    val loginCompleteEvent: LiveData<Event<Unit>> = _loginCompleteEvent

    private val _profile = MutableLiveData<ModelUpload>()
    val profile: LiveData<ModelUpload> = _profile


    /************************************************************
     *  Public
     ************************************************************/
    fun uploadFile(file: File) {
        val requestBody = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file)
        var multipartBody = MultipartBody.Part.createFormData("uploadfile", file.name, requestBody)

        /*val arrMultipartBody: List<MultipartBody.Part> = ArrayList()
        for (i in filePaths.indices) {
            val file: File = File(filePaths.get(i))
            val requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file)
            multipartBody =
                MultipartBody.Part.createFormData("uploadfile[]", file.name, requestFile)
            arrMultipartBody.add(multipartBody)
        }*/

        val api = repository.apiService.upload(multipartBody)
//        _dataLoading.value = true
        repository.callApi(api, object : ApiLiveResponseObserver<RespData<ModelUpload>>() {
            override fun onChanged(result: ApiLiveResponse<RespData<ModelUpload>>) {
                super.onChanged(result)

                if (result.status != ApiLiveResponse.Status.loading) {
                    if (result.status == ApiLiveResponse.Status.success) {
                        _profile.value = result.data!!.data
                    } else if (result.status == ApiLiveResponse.Status.error) {
//                        _networkErrorLiveData.value = result
                    }
//                    _dataLoading.value = false
                }
            }
        })
    }


    suspend fun loginUser() {
        val context = MyApplication.globalApplicationContext
        if (id.value == null || !CommonUtil.isValidEmail(id.value)) {
//            _toastMessage.value = context?.getString(R.string.input_valid_email)
            return
        }

        if (pwd.value == null || pwd.value!!.length < 6) {
//            _toastMessage.value = context?.getString(R.string.input_valid_pwd)
            return
        }

        val token = dataRepository.getString(DataStoreKey.PREFS_PUSH_TOKEN)
        val api = repository.apiService.userLogin(
            id.value!!,
            pwd.value!!,
            token ?: "",
            "android"
        )

//        _dataLoading.value = true
        repository.callApi(api, object : ApiLiveResponseObserver<RespData<ModelUser>>() {
            override fun onChanged(result: ApiLiveResponse<RespData<ModelUser>>) {
                super.onChanged(result)

                if (result.status != ApiLiveResponse.Status.loading) {
                    if (result.status == ApiLiveResponse.Status.success) {
                        val user = result.data!!.data
                        user?.pwd = pwd.value!!

                        viewModelScope.launchInSafe {
                            dataRepository.setModel(user)
                        }

                        _loginCompleteEvent.value = Event(Unit)
                    } else if (result.status == ApiLiveResponse.Status.error) {
//                        _networkErrorLiveData.value = result
                    }
//                    _dataLoading.value = false
                }
            }
        })
    }

    fun signupUser() {
        val context = MyApplication.globalApplicationContext
        if (id.value == null || !CommonUtil.isValidEmail(id.value)) {
//            _toastMessage.value = context?.getString(R.string.input_valid_email)
            return
        }

        if (pwd.value == null || pwd.value!!.length < 6) {
//            _toastMessage.value = context?.getString(R.string.input_valid_pwd)
            return
        }

        if (pwd.value != pwdConfirm.value) {
//            _toastMessage.value = context?.getString(R.string.hint_pwd_confirm)
            return
        }

        val api = repository.apiService.userSignup(
            id.value!!,
            pwd.value!!,
            if (profile.value != null) profile.value!!.file_url else "",
            motto.value ?: ""
        )
//        _dataLoading.value = true
        repository.callApi(api, object : ApiLiveResponseObserver<RespData<ModelUser>>() {
            override fun onChanged(result: ApiLiveResponse<RespData<ModelUser>>) {
                super.onChanged(result)

                if (result != null && result.status != ApiLiveResponse.Status.loading) {
                    if (result.status == ApiLiveResponse.Status.success) {
                        viewModelScope.launchInSafe {
                            loginUser()
                        }
                    } else if (result.status == ApiLiveResponse.Status.error) {
//                        _networkErrorLiveData.value = result
                    }
//                    _dataLoading.value = false
                }
            }
        })
    }
}