package com.victoria.bleled.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.paging.PagedList
import androidx.paging.toLiveData
import com.victoria.bleled.common.AppExecutors
import com.victoria.bleled.data.local.db.GithubDb
import com.victoria.bleled.data.local.pref.IPrefDataSource
import com.victoria.bleled.data.model.Repo
import com.victoria.bleled.data.remote.GithubService
import com.victoria.bleled.data.remote.RepoSearchDataSourceFactory
import com.victoria.bleled.data.remote.response.ApiResponse
import com.victoria.bleled.data.remote.response.ApiSuccessResponse
import com.victoria.bleled.data.remote.response.RepoSearchResponse
import com.victoria.bleled.util.architecture.AbsentLiveData
import com.victoria.bleled.util.architecture.remote.Resource


class DataRepository constructor(
    private val appExecutors: AppExecutors,
    private val githubService: GithubService,
    private val db: GithubDb,
    private val preferences: IPrefDataSource
) {
    fun searchFirst(query: String): LiveData<Resource<List<Repo>>> {
        return object : CommonNetworkResource<List<Repo>, RepoSearchResponse>(appExecutors) {
            override fun createCall() = githubService.searchRepos(query)

            override fun processResponse(response: ApiResponse<RepoSearchResponse>): LiveData<List<Repo>> {
                if (response is ApiSuccessResponse) {
                    val body = response.body
                    return MutableLiveData<List<Repo>>(body.items)
                }

                return AbsentLiveData.create()
            }
        }.asLiveData()
    }

    fun searchPage(query: String, pageSize: Int): Resource<LiveData<PagedList<Repo>>> {
        val sourceFactory = RepoSearchDataSourceFactory(githubService, query, appExecutors)

        val livePagedList = sourceFactory.toLiveData(
            pageSize = pageSize,
            // provide custom executor for network requests, otherwise it will default to
            // Arch Components' IO pool which is also used for disk access
            fetchExecutor = appExecutors.networkIO()
        )

        val state = Transformations.switchMap(sourceFactory.sourceLiveData) {
            it.networkState
        }
        return Resource(state, livePagedList, null)
    }
}