package com.victoria.bleled.data.net.github

import com.victoria.bleled.data.net.adapter.ApiResponse
import com.victoria.bleled.data.net.adapter.CoroutinesResponseCallAdapterFactory
import com.victoria.bleled.util.thirdparty.retrofit.RetrofitHelper
import retrofit2.http.GET
import retrofit2.http.Query

interface IGithubService {
    @GET("common/get_repos?sort=stars")
    suspend fun searchRepo(
        @Query("q") q: String,
        @Query("page") page: Int,
    ): ApiResponse<RepoListResp>

    companion object {
        const val API_BASE_URL = "http://192.168.0.22:8101/api/"

        fun provideGithubService(): IGithubService {
            val retrofit =
                RetrofitHelper.createRetrofit(API_BASE_URL, CoroutinesResponseCallAdapterFactory())
            return retrofit.create(IGithubService::class.java)
        }
    }
}