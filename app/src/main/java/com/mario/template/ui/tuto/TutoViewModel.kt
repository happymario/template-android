package com.mario.template.ui.tuto

import com.mario.template.base.BaseViewModel
import com.mario.template.base.BaseViewState
import com.mario.template.data.repository.LocalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class TutoViewModel @Inject constructor(
    private var localRepository: LocalRepository
) : BaseViewModel() {
    private val _state = MutableStateFlow(TutoViewState(isLoading = false))
    val state: StateFlow<TutoViewState> = _state

    fun skip() {
        launchScope {
            localRepository.setTutofinished(true)
            _state.update {
                it.copy(isFinished = true)
            }
        }
    }
}


data class TutoViewState(
    override val isLoading: Boolean = false,
    override val error: Throwable? = null,
    val isFinished: Boolean = false
) : BaseViewState()