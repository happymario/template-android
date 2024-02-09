package com.mario.template.data.repository

import com.mario.template.data.model.AppInfo
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {
    suspend fun getAppInfo(): Flow<AppInfo>
}
