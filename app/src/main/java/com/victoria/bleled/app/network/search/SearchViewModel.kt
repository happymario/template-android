package com.victoria.bleled.app.network.search

import androidx.lifecycle.*
import androidx.paging.PagedList
import com.victoria.bleled.data.DataRepository
import com.victoria.bleled.data.model.Repo
import com.victoria.bleled.util.architecture.AbsentLiveData
import com.victoria.bleled.util.architecture.remote.Resource
import java.util.*


class SearchViewModel constructor(repoRepository: DataRepository) : ViewModel() {

    private val _query = MutableLiveData<String>()
    val query: LiveData<String> = _query

    val detailLoadMore = MediatorLiveData<Boolean>()

    val mainLiveData: LiveData<Resource<List<Repo>>> = Transformations
        .switchMap(_query) { search ->
            if (search.isNullOrBlank()) {
                AbsentLiveData.create()
            } else {
                repoRepository.searchFirst(search)
            }
        }

    val detailLiveData = Transformations.map(query) {
        repoRepository.searchPage(it, PAGE_SIZE)
    }

    val detailList: LiveData<PagedList<Repo>> =
        Transformations.switchMap(detailLiveData, { it.data })

    fun setQuery(originalInput: String) {
        val input = originalInput.toLowerCase(Locale.getDefault()).trim()
        if (input == _query.value) {
            return
        }
        _query.value = input
    }

    fun refresh() {
        _query.value?.let {
            _query.value = it
        }
    }

    fun testPagedList(pos: Int) {
        // java.lang.UnsupportedOperationException
        // ????
        val pagedList = detailList.value
        val data = pagedList?.get(pos)
        data?.stars = 100000
        pagedList?.set(pos, data)
    }

    companion object {
        val PAGE_SIZE = 30
    }
}

