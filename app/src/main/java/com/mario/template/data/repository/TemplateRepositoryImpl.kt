package com.mario.template.data.repository

import com.mario.template.base.BaseModel
import com.mario.template.data.model.AppInfo
import com.mario.template.data.model.UploadFile
import com.mario.template.data.model.User
import com.mario.template.data.remote.apiservice.MyTemplateApiService
import com.mario.template.data.remote.apiservice.OneCallApiService
import com.mario.template.data.remote.response.ResponseTransformer.transformBaseResponse
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import javax.inject.Inject

class TemplateRepositoryImpl @Inject constructor(
    private val myTemplateApiService: MyTemplateApiService,
    private val oneCallApiService: OneCallApiService,
) : TemplateRepository {

    override suspend fun getAppInfo(): Flow<AppInfo> =
        transformBaseResponse(myTemplateApiService.appInfo("android"))

    override suspend fun loginUser(
        id: String,
        pwd: String,
        token: String,
        devtype: String
    ): Flow<User> =
        transformBaseResponse(myTemplateApiService.userLogin(id, pwd, token, devtype))


    override suspend fun uploadFile(file: MultipartBody.Part): Flow<UploadFile> =
        transformBaseResponse(myTemplateApiService.upload(file))

    override suspend fun signupUser(
        id: String,
        pwd: String,
        profile_url: String,
        name: String
    ): Flow<User> =
        transformBaseResponse(myTemplateApiService.userSignup(id, pwd, profile_url, name))

    override suspend fun signoutUser(token: String): Flow<BaseModel> =
        transformBaseResponse(myTemplateApiService.userSignout(token))
}