package com.mario.template.data.repository

import com.mario.template.data.model.AppInfo
import com.mario.template.data.remote.apiservice.MyWeatherApiService
import com.mario.template.data.remote.apiservice.OneCallApiService
import com.mario.template.data.remote.response.ResponseTransfomer.trasformBaseResponse
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    private val myWeatherApiService: MyWeatherApiService,
    private val oneCallApiService: OneCallApiService,
) : WeatherRepository {
    override suspend fun getAppInfo(): Flow<AppInfo> =
        trasformBaseResponse(myWeatherApiService.appInfo("android"))
}