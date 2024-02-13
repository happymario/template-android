package com.mario.template.data.remote.apiservice

import com.mario.template.data.model.OneCallData
import kotlinx.coroutines.flow.Flow
import retrofit2.http.GET
import retrofit2.http.Query

interface OneCallApiService {
    @GET("common/get_repos?sort=stars")
    fun searchRepo(
        @Query("q") q: String,
        @Query("page") page: Int,
    ): Flow<OneCallData>
}