package com.victoria.bleled.app.main

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.victoria.bleled.data.DataRepository
import com.victoria.bleled.data.model.ModelUser
import com.victoria.bleled.util.arch.Event
import com.victoria.bleled.util.arch.base.BaseViewModel

class MainViewModel constructor(private val repository: DataRepository) : BaseViewModel() {

    private val _openTaskEvent = MutableLiveData<Event<View>>()
    val openTaskEvent: LiveData<Event<View>> = _openTaskEvent

    private val _userInfo = MutableLiveData<ModelUser>()
    val userInfo: LiveData<ModelUser> = _userInfo

    fun start() {
        val prefDataSource = repository.prefDataSource
        _userInfo.value = prefDataSource.user
    }

    fun openTask(view: View, taskId: String) {
        view.tag = taskId
        _openTaskEvent.value = Event(view)
    }

    fun logout() {
        val prefDataSource = repository.prefDataSource
        prefDataSource.user = null
        _userInfo.value = null
    }
}