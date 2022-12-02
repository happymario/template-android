package com.victoria.bleled.data.net.mytemplate

import android.util.Log
import androidx.lifecycle.LiveData
import com.google.gson.Gson
import com.victoria.bleled.data.model.BaseModel
import com.victoria.bleled.data.model.ModelAppInfo
import com.victoria.bleled.data.model.ModelUpload
import com.victoria.bleled.data.model.ModelUser
import com.victoria.bleled.data.net.adapter.live.ApiLiveResponse
import com.victoria.bleled.data.net.adapter.live.LiveDataCallAdapterFactory
import com.victoria.bleled.data.net.mytemplate.response.RespData
import com.victoria.bleled.util.thirdparty.retrofit.AddParamsInterceptor
import com.victoria.bleled.util.thirdparty.retrofit.RetrofitHelper
import okhttp3.MultipartBody.Part
import retrofit2.http.*
import java.lang.reflect.Type

interface IMyTemplateService {
    /************************************************************
     * Common
     ***********************************************************/
    @FormUrlEncoded
    @POST("common/app_info")
    fun appInfo(@Field("dev_type") dev_type: String): LiveData<ApiLiveResponse<RespData<ModelAppInfo>>>

    @Multipart
    @POST("common/upload_file")
    fun upload(@retrofit2.http.Part file: Part): LiveData<ApiLiveResponse<RespData<ModelUpload>>>

    @Multipart
    @POST("common/multi_upload_file")
    fun uploadList(@retrofit2.http.Part files: List<Part>): LiveData<ApiLiveResponse<RespData<List<ModelUpload>>>>

    @FormUrlEncoded
    @POST("auth/login")
    fun userLogin(
        @Field("id") id: String,
        @Field("pwd") pwd: String,
        @Field("push_token") push_token: String,
        @Field("dev_type") dev_type: String,
    ): LiveData<ApiLiveResponse<RespData<ModelUser>>>

    @FormUrlEncoded
    @POST("auth/signup")
    fun userSignup(
        @Field("id") id: String,
        @Field("pwd") pwd: String,
        @Field("profile_url") profile_url: String,
        @Field("name") name: String,
    ): LiveData<ApiLiveResponse<RespData<ModelUser>>>

    @POST("user/signout")
    fun userSignout(
        @Header("Authorization") token: String,
    ): LiveData<ApiLiveResponse<RespData<BaseModel>>>

    companion object {
        /************************************************************
         * Functions
         */
        fun encrypt(vararg params: String): String {
            val map = HashMap<String, String>()
            var i = 0
            while (i < params.size) {
                map[params[i]] = params[i + 1]
                i += 2
            }
            return RetrofitHelper.encryptParams(API_KEY, map)
        }

        fun encrypt(map: HashMap<String, String>): String {
            return RetrofitHelper.encryptParams(API_KEY, map)
        }

        //  BaseResponse<ModelAppInfo> baseResponse = decrypt(new TypeToken<BaseResponse<ModelAppInfo>>() {}.getType(), response.data);
        fun <T> decrypt(respType: Type, response: String): RespData<T> {
            val strJson = RetrofitHelper.decrypt(API_KEY, response)
            val baseResponse: RespData<T> = Gson().fromJson(strJson, respType)
            Log.d("IShequService", strJson)
            return baseResponse
        }

        /************************************************************
         * Constants
         */
        const val BASE_URL = "http://192.168.0.22:8101"
        const val API_BASE_URL = "$BASE_URL/api/"
        const val API_KEY = "clubonline20200519key"

        private fun getCommonParams(): AddParamsInterceptor? {
            return AddParamsInterceptor.Builder()
                .addParameter("Authorization", "")
                .build()
        }

        @JvmStatic
        fun provideMyTemplateService(): IMyTemplateService? {
            val retrofit = RetrofitHelper.createRetrofit(API_BASE_URL,
                LiveDataCallAdapterFactory(),
                getCommonParams())
            return retrofit.create(IMyTemplateService::class.java)
        }
    }
}