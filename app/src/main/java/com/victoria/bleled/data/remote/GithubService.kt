package com.victoria.bleled.data.remote

import androidx.lifecycle.LiveData
import com.victoria.bleled.data.remote.adapter.LiveDataCallAdapterFactory
import com.victoria.bleled.data.remote.response.ApiResponse
import com.victoria.bleled.data.remote.response.RepoSearchResponse
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface GithubService {
    companion object {
        fun provideGithubService(): GithubService {
            return Retrofit.Builder()
                .baseUrl("https://api.github.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(LiveDataCallAdapterFactory())
                .build()
                .create(GithubService::class.java)
        }
    }

    @GET("search/repositories")
    fun searchRepos(@Query("q") query: String): LiveData<ApiResponse<RepoSearchResponse>>

    @GET("search/repositories?sort=stars")
    fun searchReposWithPage(
        @Query("q") query: String,
        @Query("page") page: Int
    ): Call<RepoSearchResponse>
}