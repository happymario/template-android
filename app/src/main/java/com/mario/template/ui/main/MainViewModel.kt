package com.mario.template.ui.main

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mario.template.base.BaseViewModel
import com.mario.template.base.BaseViewState
import com.mario.template.data.model.User
import com.mario.template.data.repository.LocalRepository
import com.mario.template.data.repository.TemplateRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class MainViewState(
    val curRoute: String = ""
) : BaseViewState(isLoading = false)

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class MainViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private var repository: TemplateRepository,
    private var localRepository: LocalRepository
) : BaseViewModel() {

    private val _state = MutableStateFlow(MainViewState())
    val state: StateFlow<MainViewState> = _state
    private val _userInfo = MutableLiveData<User>()
    val userInfo: LiveData<User> = _userInfo

    init {
        launchScope {
            _state.update {
                it.copy(curRoute = "")
            }
        }
    }

    suspend fun start() {
        localRepository.getLoginUser().collect {
            _userInfo.value = it
        }
    }

}