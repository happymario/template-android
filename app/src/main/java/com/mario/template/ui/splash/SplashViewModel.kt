package com.mario.template.ui.splash

import android.annotation.SuppressLint
import android.content.Context
import com.mario.template.NestedGraph
import com.mario.template.R
import com.mario.template.base.BaseViewModel
import com.mario.template.base.BaseViewState
import com.mario.template.data.exception.AppException
import com.mario.template.data.model.AppInfo
import com.mario.template.data.repository.LocalRepository
import com.mario.template.data.repository.TemplateRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class SplashViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private var repository: TemplateRepository,
    private var localRepository: LocalRepository
) : BaseViewModel() {
    private val _state = MutableStateFlow(SplashViewState(isLoading = false))
    val state: StateFlow<SplashViewState> = _state

    init {
        _state.update {
            it.copy(isRequestPermission = true)
        }
    }

    fun getAppInfo() {
        launchScope {
            showLoading()
            repository.getAppInfo().collect {
                val appinfo = it
                localRepository.setAppInfo(appinfo)
                _state.update { viewstate ->
                    viewstate.copy(
                        appInfo = appinfo,
                    )
                }
            }
        }
    }

    fun permissionIsNotGranted() {
        val error = AppException.AlertException(
            context.getString(R.string.error_message_permission_not_granted)
        )
        showError(error)
    }

    fun completePermissionRequest() {
        _state.update {
            it.copy(
                isRequestPermission = false,
            )
        }
    }

    fun checkNextScreen() {
        launchScope {
            localRepository.isTutoFinished().collect {finished ->
                if(finished) {
                    checkLogin()
                }
                else {
                    _state.update {
                        it.copy(naviRoute = NestedGraph.TUTO)
                    }
                }
            }
        }
    }

    fun checkLogin() {
        launchScope {
            localRepository.getLoginUser().catch {
                _state.update {
                    it.copy(naviRoute = NestedGraph.LOGIN)
                }
            }.collect {localUser ->
                showLoading()
                repository.loginUser(localUser.id!!, localUser.pwd, token = "", "android").collect {user ->
                    val loginUser = user
                    loginUser.pwd = localUser.pwd
                    localRepository.setLoginUser(loginUser)
                    _state.update {
                        it.copy(naviRoute = NestedGraph.MAIN)
                    }
                }
            }
        }
    }

    override fun showLoading() {
        _state.update {
            it.copy(isLoading = true)
        }
    }

    override fun hideLoading() {
        _state.update {
            it.copy(
                isLoading = false,
            )
        }
    }

    override fun showError(error: Throwable) {
        if (_state.value.error == null) {
            _state.update {
                it.copy(isLoading = false, error = error)
            }
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
}

data class SplashViewState(
    override val isLoading: Boolean = false,
    override val error: Throwable? = null,
    val isRequestPermission: Boolean = false,
    val appInfo:AppInfo? = null,
    var naviRoute:NestedGraph? = null,
) : BaseViewState()