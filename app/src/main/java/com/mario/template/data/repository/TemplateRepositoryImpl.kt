package com.mario.template.data.repository

import com.mario.template.data.model.AppInfo
import com.mario.template.data.remote.apiservice.MyTemplateApiService
import com.mario.template.data.remote.apiservice.OneCallApiService
import com.mario.template.data.remote.response.ResponseTransfomer.trasformBaseResponse
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TemplateRepositoryImpl @Inject constructor(
    private val myTemplateApiService: MyTemplateApiService,
    private val oneCallApiService: OneCallApiService,
) : TemplateRepository {
    override suspend fun getAppInfo(): Flow<AppInfo> =
        trasformBaseResponse(myTemplateApiService.appInfo("android"))
}