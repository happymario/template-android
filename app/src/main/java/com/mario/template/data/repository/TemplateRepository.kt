package com.mario.template.data.repository

import com.mario.template.base.BaseModel
import com.mario.template.data.model.AppInfo
import com.mario.template.data.model.UploadFile
import com.mario.template.data.model.User
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody

interface TemplateRepository {
    suspend fun getAppInfo(): Flow<AppInfo>
    suspend fun loginUser(id: String, pwd: String, token: String, dev: String): Flow<User>
    suspend fun uploadFile(file: MultipartBody.Part): Flow<UploadFile>
    suspend fun signupUser(id: String, pwd: String, profile_url: String, name: String): Flow<User>
    suspend fun signoutUser(token: String): Flow<BaseModel>
}
