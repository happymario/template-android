package com.mario.template.ui.auth

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mario.lib.base.architecture.Event
import com.mario.lib.base.util.ValidUtil
import com.mario.template.R
import com.mario.template.base.BaseViewModel
import com.mario.template.data.exception.AppException
import com.mario.template.data.repository.LocalRepository
import com.mario.template.data.repository.TemplateRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
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
            if (id.value == "" || !ValidUtil.isValidEmail(id.value!!)) {
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
}