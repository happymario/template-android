package com.victoria.bleled.data.remote

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import com.victoria.bleled.common.AppExecutors
import com.victoria.bleled.data.model.Repo
import com.victoria.bleled.data.remote.response.RepoSearchResponse
import com.victoria.bleled.util.architecture.remote.Status
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PageKeyedRepoSearchDataSource(
    private val githubService: GithubService,
    private val key: String,
    private val appExecutors: AppExecutors
) : PageKeyedDataSource<Long, Repo>() {

    // keep a function reference for the retry event
    private var retry: (() -> Any)? = null

    val networkState = MutableLiveData<Status>()

    override fun loadInitial(
        params: LoadInitialParams<Long>,
        callback: LoadInitialCallback<Long, Repo>
    ) {
        networkState.postValue(Status.LOADING)

        githubService.searchReposWithPage(key, 1).enqueue(object :
            Callback<RepoSearchResponse> {
            override fun onFailure(call: Call<RepoSearchResponse>?, t: Throwable) {
                networkState.postValue(Status.ERROR)
            }

            override fun onResponse(
                call: Call<RepoSearchResponse>?,
                response: Response<RepoSearchResponse>
            ) {
                if (response.isSuccessful) {
                    val repos = response.body()?.items ?: emptyList()
                    callback.onResult(repos, null, 2)
                    networkState.postValue(Status.SUCCESS)
                } else {
                    networkState.postValue(Status.ERROR)
                }
            }
        })
    }

    override fun loadBefore(params: LoadParams<Long>, callback: LoadCallback<Long, Repo>) {
        // ignored, since we only ever append to our initial load
    }

    override fun loadAfter(params: LoadParams<Long>, callback: LoadCallback<Long, Repo>) {
        networkState.postValue(Status.LOADING_MORE)

        githubService.searchReposWithPage(key, params.key.toInt()).enqueue(object :
            Callback<RepoSearchResponse> {
            override fun onFailure(call: Call<RepoSearchResponse>?, t: Throwable) {
                networkState.postValue(Status.ERROR)
            }

            override fun onResponse(
                call: Call<RepoSearchResponse>?,
                response: Response<RepoSearchResponse>
            ) {
                if (response.isSuccessful) {
                    val repos = response.body()?.items ?: emptyList()
                    callback.onResult(repos, (params.key + 1))
                    networkState.postValue(Status.SUCCESS)
                } else {
                    networkState.postValue(Status.ERROR)
                }
            }
        })
    }

    fun retryAllFailed() {
        val prevRetry = retry
        retry = null
        prevRetry?.let {
            appExecutors.networkIO().execute {
                it.invoke()
            }
        }
    }
}

/**
 * A simple data source factory which also provides a way to observe the last created data source.
 * This allows us to channel its network request status etc back to the UI. See the Listing creation
 * in the Repository class.
 */
class RepoSearchDataSourceFactory(
    private val redditApi: GithubService,
    private val key: String,
    private val appExecutors: AppExecutors
) : DataSource.Factory<Long, Repo>() {
    val sourceLiveData = MutableLiveData<PageKeyedRepoSearchDataSource>()
    override fun create(): DataSource<Long, Repo> {
        val source = PageKeyedRepoSearchDataSource(redditApi, key, appExecutors)
        sourceLiveData.postValue(source)
        return source
    }
}
