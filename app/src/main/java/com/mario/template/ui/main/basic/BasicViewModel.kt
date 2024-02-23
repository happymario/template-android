package com.mario.template.ui.main.basic

import android.annotation.SuppressLint
import android.content.Context
import com.mario.template.R
import com.mario.template.base.BaseViewModel
import com.mario.template.base.BaseViewState
import com.mario.template.data.repository.LocalRepository
import com.mario.template.data.repository.TemplateRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class BasicViewState(
    val list: List<String> = emptyList()
) : BaseViewState(isLoading = false)

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class BasicViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private var repository: TemplateRepository,
    private var localRepository: LocalRepository
) : BaseViewModel() {

    private val _state = MutableStateFlow(BasicViewState())
    val state: StateFlow<BasicViewState> = _state

    init {
        launchScope {
            _state.update {
                it.copy(list = context.resources.getStringArray(R.array.arr_basic).toList())
            }
        }
    }
}