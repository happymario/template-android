package com.mario.template.data.remote.apiservice

import com.mario.template.base.BaseModel
import com.mario.template.data.model.AppInfo
import com.mario.template.data.model.UploadFile
import com.mario.template.data.model.User
import com.mario.template.data.remote.response.BaseResponse
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST

interface MyTemplateApiService {
    /************************************************************
     * Common
     ***********************************************************/
    @FormUrlEncoded
    @POST("common/app_info")
    fun appInfo(@Field("dev_type") dev_type: String): Flow<BaseResponse<AppInfo>>

    @Multipart
    @POST("common/upload_file")
    fun upload(@retrofit2.http.Part file: MultipartBody.Part): Flow<BaseResponse<UploadFile>>

    @Multipart
    @POST("common/multi_upload_file")
    fun uploadList(@retrofit2.http.Part files: List<MultipartBody.Part>): Flow<BaseResponse<List<UploadFile>>>


    /************************************************************
     * User
     ***********************************************************/
    @FormUrlEncoded
    @POST("auth/login")
    fun userLogin(
        @Field("id") id: String,
        @Field("pwd") pwd: String,
        @Field("push_token") push_token: String,
        @Field("dev_type") dev_type: String,
    ): Flow<BaseResponse<User>>

    @FormUrlEncoded
    @POST("auth/signup")
    fun userSignup(
        @Field("id") id: String,
        @Field("pwd") pwd: String,
        @Field("profile_url") profile_url: String,
        @Field("name") name: String,
    ): Flow<BaseResponse<User>>

    @POST("user/signout")
    fun userSignout(
        @Header("Authorization") token: String,
    ): Flow<BaseResponse<BaseModel>>
}