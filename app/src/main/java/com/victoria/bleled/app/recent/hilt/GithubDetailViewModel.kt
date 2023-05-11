package com.victoria.bleled.app.recent.hilt

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.victoria.bleled.base.BaseViewModel
import com.victoria.bleled.data.model.ModelUser
import com.victoria.bleled.data.net.repository.GithubSearchRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class GithubDetailViewModel @AssistedInject constructor(
    @Assisted val id: String,
    private var historyRepository: GithubSearchRepository,
) : BaseViewModel() {
    var detail = MutableLiveData<ModelUser>()

    fun setData(data: ModelUser) {
        viewModelScope.launchInSafe {
            //val result = historyRepository.fetchHistoryDetail(id).single()
            detail.value = data
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(id: String): GithubDetailViewModel
    }

    companion object {
        fun provideFactory(
            assistedFactory: Factory,
            id: String,
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return assistedFactory.create(id) as T
            }
        }
    }
}