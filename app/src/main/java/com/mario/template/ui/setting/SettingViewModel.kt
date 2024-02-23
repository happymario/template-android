package com.mario.template.ui.setting

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mario.template.base.BaseViewModel
import com.mario.template.data.model.User
import com.mario.template.data.repository.LocalRepository
import com.mario.template.data.repository.TemplateRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private var repository: TemplateRepository,
    private var localRepository: LocalRepository
) : BaseViewModel() {

    private val _userInfo = MutableLiveData<User>()
    val userInfo: LiveData<User> = _userInfo

    suspend fun start() {
        localRepository.getLoginUser().collect {
            _userInfo.value = it
        }
    }

    suspend fun signOut() {

    }
}