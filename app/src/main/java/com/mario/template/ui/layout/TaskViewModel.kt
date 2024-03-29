package com.mario.template.ui.layout

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.mario.lib.base.architecture.Event
import com.mario.template.R
import com.mario.template.base.BaseViewModel
import com.mario.template.data.model.User
import com.mario.template.data.repository.LocalRepository
import com.mario.template.data.repository.TemplateRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@SuppressLint("StaticFieldLeak")
@HiltViewModel
class TaskViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private var repository: TemplateRepository,
    private var localRepository: LocalRepository
) : BaseViewModel() {

    private val _openTaskEvent = MutableLiveData<Event<View>>()
    val openTaskEvent: LiveData<Event<View>> = _openTaskEvent

    private val _userInfo = MutableLiveData<User>()
    val userInfo: LiveData<User> = _userInfo

    private val _query = MutableLiveData<String>()
    private val _queryState = MutableStateFlow("")

    private val _items: LiveData<List<String>> = _query.switchMap { search ->
        val arrIds =
            arrayOf(R.array.arr_basic, R.array.arr_latest, R.array.arr_special)
        val arrTitle =
            if (currentPageIdx < arrIds.size) context?.resources!!.getStringArray(arrIds[currentPageIdx]) else {
                context?.resources!!.getStringArray(arrIds[0])
            }

        val result = MutableLiveData<List<String>>()
        result.value = arrTitle.filter { it.toUpperCase().contains(search.toUpperCase()) }

        result
    }
    val items: LiveData<List<String>> = _items

    // sharedflow는 Event같이 값이 없지만 구독을 해야 하는 객체일시
    // replay = 0: 새로운 구독자에겐 이전 event를 전달하지 않음, extraBuffer:buffer크기, onBufferOverflow: buffer가득찾을때 처리
    private val _systemEvent: MutableSharedFlow<Event<String>> =
        MutableSharedFlow(
            replay = 0,
            extraBufferCapacity = 1,
            onBufferOverflow = BufferOverflow.DROP_OLDEST
        )
    val systemEvent = _systemEvent.asSharedFlow()

    private var currentPageIdx: Int = 0

    init {
        viewModelScope.launch {
            _queryState.collect { query ->
                _query.value = query
            }
        }
    }

    suspend fun setSystemEvent(error: String) {
        _systemEvent.emit(Event(error))
    }

    suspend fun start() {
//        var user = localRepository.getModel(ModelUser::class.java)
//        if (user == null) {
//            user = ModelUser(0)
//            user.name = "Guest"
//        }
//        _userInfo.value = user
    }

    fun openTask(view: View, taskId: String) {
        view.tag = taskId
        _openTaskEvent.value = Event(view)
    }

    suspend fun logout() {
//        val user:ModelUser? = null
//        dataStoreRepository.setModel(user)
//        _userInfo.value = null
    }

    fun searchTask(query: String) {
        //_query.value = query
        _queryState.value = query
    }

    fun setPage(page: Int) {
        currentPageIdx = page
        _query.value = ""
    }

    fun refresh() {

    }
}