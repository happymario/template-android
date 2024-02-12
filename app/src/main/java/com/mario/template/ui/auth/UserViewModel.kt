package com.mario.template.ui.auth

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mario.lib.base.architecture.Event
import com.mario.template.R
import com.mario.template.base.BaseViewModel
import com.mario.template.data.exception.AppException
import com.mario.template.data.model.UploadFile
import com.mario.template.data.repository.LocalRepository
import com.mario.template.data.repository.TemplateRepository
import com.mario.template.helper.CommonUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private var repository: TemplateRepository,
    private var localRepository: LocalRepository
) : BaseViewModel() {
    /************************************************************
     *  Variables
     ************************************************************/
    private val _state = MutableStateFlow(LoginViewState())
    val state: StateFlow<LoginViewState> = _state

    // Two-way databinding, exposing MutableLiveData
    val id = MutableLiveData<String>()
    val pwd = MutableLiveData<String>()

    private val _loginCompleteEvent = MutableLiveData<Event<Unit>>()
    val loginCompleteEvent: LiveData<Event<Unit>> = _loginCompleteEvent

    private val _profile = MutableLiveData<UploadFile>()
    val profile: LiveData<UploadFile> = _profile

    /************************************************************
     *  Override
     ************************************************************/
    override fun showLoading() {
        _state.update {
            it.copy(isLoading = true)
        }
    }

    override fun hideLoading() {
        _state.update {
            it.copy(isLoading = false)
        }
    }

    override fun showError(error: Throwable) {
        _state.update {
            it.copy(isLoading = false, error = error)
        }
    }

    override fun hideError() {
        _state.update {
            it.copy(
                isLoading = false,
                error = null,
            )
        }
    }

    /************************************************************
     *  Public
     ************************************************************/
    suspend fun loginUser() {
        launchScope {
            if (id.value == "" || !CommonUtil.isValidEmail(id.value!!)) {
                showError(AppException.ToastException(context.getString(R.string.input_valid_email)))
                return@launchScope
            }
            if (pwd.value == "" || pwd.value!!.length < 6) {
                showError(AppException.ToastException(context.getString(R.string.input_valid_pwd)))
                return@launchScope
            }
            localRepository.getPushToken().collect { token ->
                showLoading()
                repository.loginUser(
                    id.value!!,
                    pwd.value!!,
                    token,
                    "android"
                ).collect {
                    hideLoading()
                    _loginCompleteEvent.value = Event(Unit)
                }
            }
        }
    }


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

//        val api = repository.apiService.upload(multipartBody)
////        _dataLoading.value = true
//        repository.callApi(api, object : ApiLiveResponseObserver<RespData<ModelUpload>>() {
//            override fun onChanged(result: ApiLiveResponse<RespData<ModelUpload>>) {
//                super.onChanged(result)
//
//                if (result.status != ApiLiveResponse.Status.loading) {
//                    if (result.status == ApiLiveResponse.Status.success) {
//                        _profile.value = result.data!!.data
//                    } else if (result.status == ApiLiveResponse.Status.error) {
////                        _networkErrorLiveData.value = result
//                    }
////                    _dataLoading.value = false
//                }
//            }
//        })
    }


    fun signupUser() {
//        val context = MyApplication.globalApplicationContext
//        if (id.value == null || !CommonUtil.isValidEmail(id.value)) {
////            _toastMessage.value = context?.getString(R.string.input_valid_email)
//            return
//        }
//
//        if (pwd.value == null || pwd.value!!.length < 6) {
////            _toastMessage.value = context?.getString(R.string.input_valid_pwd)
//            return
//        }
//
//        if (pwd.value != pwdConfirm.value) {
////            _toastMessage.value = context?.getString(R.string.hint_pwd_confirm)
//            return
//        }
//
//        val api = repository.apiService.userSignup(
//            id.value!!,
//            pwd.value!!,
//            if (profile.value != null) profile.value!!.file_url else "",
//            motto.value ?: ""
//        )
////        _dataLoading.value = true
//        repository.callApi(api, object : ApiLiveResponseObserver<RespData<ModelUser>>() {
//            override fun onChanged(result: ApiLiveResponse<RespData<ModelUser>>) {
//                super.onChanged(result)
//
//                if (result != null && result.status != ApiLiveResponse.Status.loading) {
//                    if (result.status == ApiLiveResponse.Status.success) {
//                        viewModelScope.launchInSafe {
//                            loginUser()
//                        }
//                    } else if (result.status == ApiLiveResponse.Status.error) {
////                        _networkErrorLiveData.value = result
//                    }
////                    _dataLoading.value = false
//                }
//            }
//        })
    }
}