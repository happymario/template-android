package com.victoria.bleled.data.repository


import androidx.annotation.WorkerThread
import com.victoria.bleled.data.net.adapter.suspendOnSuccess
import com.victoria.bleled.data.net.github.IGithubService
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GithubSearchRepository @Inject constructor(
    private val apiService: IGithubService,
) : BaseRepository() {

    @WorkerThread
    fun fetchRepoList(page: Int, search: String) = flow {
        apiService.searchRepo(
            search, page
        ).suspendOnSuccess {
            val raw = data()
            emit(raw)
        }.nextThrow()
    }
}