package com.mario.template.ui.auth

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mario.lib.base.architecture.Event
import com.mario.lib.base.architecture.ResourceLiveData
import com.mario.template.R
import com.mario.template.base.BaseViewModel
import com.mario.template.data.exception.AppException
import com.mario.template.data.model.UploadFile
import com.mario.template.data.model.User
import com.mario.template.data.repository.LocalRepository
import com.mario.template.data.repository.TemplateRepository
import com.mario.template.helper.CommonUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import javax.inject.Inject

@HiltViewModel
class SignupViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private var repository: TemplateRepository,
    private var localRepository: LocalRepository
) : BaseViewModel() {
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

    private val _profile = MutableLiveData<UploadFile>()
    val profile: LiveData<UploadFile> = _profile

    var signupUser = ResourceLiveData<User>()


    /************************************************************
     *  Override
     ************************************************************/
    override fun showLoading() {
        signupUser.loading()
    }

    override fun hideLoading() {
        signupUser.none()
    }

    override fun showError(error: Throwable) {
        signupUser.error(error)
    }

    override fun hideError() {
        signupUser.error(null)
    }

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

        launchScope {
            showLoading()
            repository.uploadFile(multipartBody).collect {
                hideLoading()
                _profile.value = it
            }
        }
    }


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
                    it.pwd = pwd.value!!
                    localRepository.setLoginUser(it)
                    _loginCompleteEvent.value = Event(Unit)
                }
            }
        }
    }

    fun signupUser() {
        launchScope {
            if (id.value == "" || !CommonUtil.isValidEmail(id.value!!)) {
                showError(AppException.ToastException(context.getString(R.string.input_valid_email)))
                return@launchScope
            }
            if (pwd.value == "" || pwd.value!!.length < 6) {
                showError(AppException.ToastException(context.getString(R.string.input_valid_pwd)))
                return@launchScope
            }
            if (pwd.value != pwdConfirm.value) {
                showError(AppException.ToastException(context.getString(R.string.hint_pwd_confirm)))
                return@launchScope
            }
            showLoading()
            repository.signupUser(
                id.value!!,
                pwd.value!!,
                if (profile.value != null) profile.value!!.file_url else "",
                motto.value ?: ""
            ).collect {
                loginUser()
            }
        }
    }
}