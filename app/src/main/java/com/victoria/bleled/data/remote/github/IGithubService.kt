package com.victoria.bleled.data.remote.github

import androidx.lifecycle.LiveData
import com.victoria.bleled.util.arch.network.NetworkResult
import retrofit2.http.GET
import retrofit2.http.Query

interface IGithubService {
    @GET("search/repositories?sort=stars")
    fun searchRepo(
        @Query("q") q: String,
        @Query("page") page: Int
    ): LiveData<NetworkResult<ResponseSearchRepo>>

    companion object {
        const val API_BASE_URL = "https://api.github.com/"
    }
}