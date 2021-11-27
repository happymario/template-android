package com.victoria.bleled.app.main

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.victoria.bleled.data.DataRepository
import com.victoria.bleled.util.arch.Event
import com.victoria.bleled.util.arch.base.BaseViewModel

class MainViewModel constructor(private val repository: DataRepository) : BaseViewModel() {

    private val _openTaskEvent = MutableLiveData<Event<View>>()
    val openTaskEvent: LiveData<Event<View>> = _openTaskEvent

    fun start() {
        // live api
    }

    fun openTask(view: View, taskId: String) {
        view.tag = taskId
        _openTaskEvent.value = Event(view)
    }
}