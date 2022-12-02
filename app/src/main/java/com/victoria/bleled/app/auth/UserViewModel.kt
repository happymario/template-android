package com.victoria.bleled.app.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.victoria.bleled.app.MyApplication
import com.victoria.bleled.data.DataRepository
import com.victoria.bleled.data.model.ModelUpload
import com.victoria.bleled.data.model.ModelUser
import com.victoria.bleled.data.remote.NetworkObserver
import com.victoria.bleled.data.remote.myservice.response.BaseResp
import com.victoria.bleled.util.CommonUtil
import com.victoria.bleled.base.BaseViewModel
import com.victoria.bleled.base.internal.Event
import com.victoria.bleled.data.remote.NetworkResult
import com.victoria.bleled.data.remote.myservice.response.RespData
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class UserViewModel constructor(private val repository: DataRepository) : BaseViewModel() {
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

        val api = repository.remoteService.upload(multipartBody)
//        _dataLoading.value = true
        repository.callApi(api, object : NetworkObserver<RespData<ModelUpload>>() {
            override fun onChanged(result: NetworkResult<RespData<ModelUpload>>) {
                super.onChanged(result)

                if (result != null && result.status.value != NetworkResult.Status.loading) {
                    if (result.status.value == NetworkResult.Status.success) {
                        _profile.value = result.data.data
                    } else if (result.status.value == NetworkResult.Status.error) {
//                        _networkErrorLiveData.value = result
                    }
//                    _dataLoading.value = false
                }
            }
        })
    }


    fun loginUser() {
        val context = MyApplication.globalApplicationContext
        if (id.value == null || !CommonUtil.isValidEmail(id.value)) {
//            _toastMessage.value = context?.getString(R.string.input_valid_email)
            return
        }

        if (pwd.value == null || pwd.value!!.length < 6) {
//            _toastMessage.value = context?.getString(R.string.input_valid_pwd)
            return
        }

        val prefDataSource = repository.prefDataSource
        val api = repository.remoteService.userLogin(
            id.value!!,
            pwd.value!!,
            if (prefDataSource.pushToken != null) prefDataSource.pushToken!! else "",
            "android"
        )

//        _dataLoading.value = true
        repository.callApi(api, object : NetworkObserver<RespData<ModelUser>>() {
            override fun onChanged(result: NetworkResult<RespData<ModelUser>>) {
                super.onChanged(result)

                if (result.status.value != NetworkResult.Status.loading) {
                    if (result.status.value == NetworkResult.Status.success) {
                        val user = result.data.data
                        user?.pwd = pwd.value!!
                        prefDataSource.user = user
                        _loginCompleteEvent.value = Event(Unit)
                    } else if (result.status.value == NetworkResult.Status.error) {
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

        val api = repository.remoteService.userSignup(
            id.value!!,
            pwd.value!!,
            if (profile.value != null) profile.value!!.file_url else "",
            motto.value ?: ""
        )
//        _dataLoading.value = true
        repository.callApi(api, object : NetworkObserver<RespData<ModelUser>>() {
            override fun onChanged(result: NetworkResult<RespData<ModelUser>>) {
                super.onChanged(result)

                if (result != null && result.status.value != NetworkResult.Status.loading) {
                    if (result.status.value == NetworkResult.Status.success) {
                        loginUser()
                    } else if (result.status.value == NetworkResult.Status.error) {
//                        _networkErrorLiveData.value = result
                    }
//                    _dataLoading.value = false
                }
            }
        })
    }
}