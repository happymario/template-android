package com.victoria.bleled.app.main

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.victoria.bleled.R
import com.victoria.bleled.app.MyApplication
import com.victoria.bleled.base.BaseViewModel
import com.victoria.bleled.base.internal.Event
import com.victoria.bleled.data.model.ModelUser
import com.victoria.bleled.data.repository.DataStoreRepository
import com.victoria.bleled.data.repository.MyTemplateRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(private val repository: MyTemplateRepository, private  val dataStoreRepository: DataStoreRepository) : BaseViewModel() {

    private val _openTaskEvent = MutableLiveData<Event<View>>()
    val openTaskEvent: LiveData<Event<View>> = _openTaskEvent

    private val _userInfo = MutableLiveData<ModelUser>()
    val userInfo: LiveData<ModelUser> = _userInfo

    private val _query = MutableLiveData<String>()

    //  val viewState by viewModel.state.collectAsState()
    private val _queryState = MutableStateFlow("")

    private val _items: LiveData<List<String>> = _query.switchMap { search ->
        val context = MyApplication.globalApplicationContext
        val arrIds =
            arrayOf(R.array.arr_main_tech, R.array.arr_recent_tech, R.array.arr_special_tech)
        val arrTitle =
            if (currentPageIdx < arrIds.size) context?.resources!!.getStringArray(arrIds[currentPageIdx]) else {
                context?.resources!!.getStringArray(arrIds[0])
            }

        val result = MutableLiveData<List<String>>()
        result.value = arrTitle.filter { it.toUpperCase().contains(search.toUpperCase()) }

        result
    }

    // sharedflow는 Event같이 값이 없지만 구독을 해야 하는 객체일시
    // replay = 0: 새로운 구독자에겐 이전 event를 전달하지 않음, extraBuffer:buffer크기, onBufferOverflow: buffer가득찾을때 처리
    private val _systemEvent: MutableSharedFlow<Event<String>> =
        MutableSharedFlow(replay = 0, extraBufferCapacity = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    val systemEvent = _systemEvent.asSharedFlow()


    val items: LiveData<List<String>> = _items

    private var currentPageIdx: Int = 0

    init {
        viewModelScope.launch {
            _queryState.collect { query ->
                _query.value = query
            }

            systemEvent.collect { systemEvent ->
                TODO()
            }

        }
    }

    suspend fun setSystemEvent(error:String) {
        _systemEvent.emit(Event(error))
    }

    suspend fun start() {
        var user = dataStoreRepository.getModel(ModelUser::class.java)
        if (user == null) {
            user = ModelUser(0)
            user.name = "Guest"
        }
        _userInfo.value = user
    }

    fun openTask(view: View, taskId: String) {
        view.tag = taskId
        _openTaskEvent.value = Event(view)
    }

    suspend fun logout() {
        val user:ModelUser? = null
        dataStoreRepository.setModel(user)
        _userInfo.value = null
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
        // Refresh the repository and the task will be updated automatically.
//        _dataLoading.value = true
//        viewModelScope.launch {
//            _query.value = ""
//            _dataLoading.value = false
//        }
    }
}