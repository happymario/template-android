package com.victoria.bleled.app.recent.hilt

import androidx.lifecycle.viewModelScope
import com.victoria.bleled.base.BaseViewModel
import com.victoria.bleled.base.internal.ResourceLiveData
import com.victoria.bleled.data.model.ModelUser
import com.victoria.bleled.data.repository.GithubSearchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.single
import javax.inject.Inject

@HiltViewModel
class GithubRepoViewModel @Inject constructor(
    private var repository: GithubSearchRepository,
) : BaseViewModel() {
    var itemList = ResourceLiveData<List<ModelUser>>()
    var curPage = 0

    fun getList(page: Int, word: String) {

        itemList.loading()

        viewModelScope.launchInSafe({
            itemList.error(it)
        }) {
            val result = repository.fetchRepoList(page, word).single()

            val newList = mutableListOf<ModelUser>()
            if (page != 1 && itemList.value != null && itemList.value!!.data != null) {
                newList.addAll(itemList.value!!.data!!)
            }
            newList.addAll(result.items)
            curPage = page
            itemList.success(newList)
        }
    }

}