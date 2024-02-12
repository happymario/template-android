package com.mario.template.data.repository

import com.mario.template.data.model.AppInfo
import com.mario.template.data.model.User
import kotlinx.coroutines.flow.Flow

interface TemplateRepository {
    suspend fun getAppInfo(): Flow<AppInfo>
    suspend fun loginUser(id: String, pwd: String, token: String, dev: String): Flow<User>
}
