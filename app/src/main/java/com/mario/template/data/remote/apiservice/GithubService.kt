package com.mario.template.data.remote.apiservice

import com.mario.template.data.model.GithubRepoData
import com.mario.template.data.remote.response.ApiResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface GithubService {
    companion object {
        const val BASE_URL = "https://github.com/"
    }

    @GET("common/get_repos?sort=stars")
    suspend fun searchRepo(
        @Query("q") q: String,
        @Query("page") page: Int,
    ): ApiResponse<GithubRepoData>
}