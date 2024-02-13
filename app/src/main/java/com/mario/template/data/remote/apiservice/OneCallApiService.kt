package com.mario.template.data.remote.apiservice

import androidx.lifecycle.LiveData
import com.mario.template.data.model.OneCallData
import com.mario.template.data.remote.response.ApiResponse
import com.mario.template.data.remote.response.BaseResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface OneCallApiService {
    companion object {
        const val BASE_URL = "https://api.openweathermap.org/data/3.0/"
    }

    @GET("onecall")
    fun getWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("exclude") exclude: String = "current,daily,alerts,minutes",
        @Query("units") units: String = "metric",
        @Query("lang") lang: String = "en",
        @Query("appid") appId: String
    ): LiveData<ApiResponse<BaseResponse<OneCallData>>>
}