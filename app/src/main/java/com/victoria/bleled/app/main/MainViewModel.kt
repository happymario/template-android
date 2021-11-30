package com.victoria.bleled.app.main

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.victoria.bleled.R
import com.victoria.bleled.app.MyApplication
import com.victoria.bleled.data.DataRepository
import com.victoria.bleled.data.model.ModelUser
import com.victoria.bleled.util.arch.Event
import com.victoria.bleled.util.arch.base.BaseViewModel
import kotlinx.coroutines.launch

class MainViewModel constructor(private val repository: DataRepository) : BaseViewModel() {

    private val _openTaskEvent = MutableLiveData<Event<View>>()
    val openTaskEvent: LiveData<Event<View>> = _openTaskEvent

    private val _userInfo = MutableLiveData<ModelUser>()
    val userInfo: LiveData<ModelUser> = _userInfo

    private val _query = MutableLiveData<String>()

    private val _items: LiveData<List<String>> = _query.switchMap { search ->
        val context = MyApplication.globalApplicationContext
        val arrIds =
            arrayOf(R.array.arr_main_tech, R.array.arr_recent_tech, R.array.arr_special_tech)
        var arrTitle = if (currentPageIdx < arrIds.size) context?.resources!!.getStringArray(arrIds[currentPageIdx]) else {
            context?.resources!!.getStringArray(arrIds[0])
        }

        val result = MutableLiveData<List<String>>()
        result.value = arrTitle.filter { it.uppercase().contains(search.uppercase()) }
        result
    }

    val items: LiveData<List<String>> = _items

    private var currentPageIdx: Int = 0

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

    fun searchTask(query:String) {
        _query.value = query
    }

    fun setPage(page:Int) {
        currentPageIdx = page
        _query.value = ""
    }

    fun refresh() {
        // Refresh the repository and the task will be updated automatically.
        _dataLoading.value = true
        viewModelScope.launch {
            _query.value = ""
            _dataLoading.value = false
        }
    }
}