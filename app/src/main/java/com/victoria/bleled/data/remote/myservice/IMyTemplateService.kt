package com.victoria.bleled.data.remote.myservice

import android.util.Log
import androidx.lifecycle.LiveData
import com.google.gson.Gson
import com.victoria.bleled.data.model.BaseModel
import com.victoria.bleled.data.model.ModelAppInfo
import com.victoria.bleled.data.model.ModelUpload
import com.victoria.bleled.data.model.ModelUser
import com.victoria.bleled.data.remote.NetworkResult
import com.victoria.bleled.data.remote.myservice.response.RespData
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
    fun appInfo(@Field("dev_type") dev_type: String): LiveData<NetworkResult<RespData<ModelAppInfo>>>

    @Multipart
    @POST("common/upload_file")
    fun upload(@retrofit2.http.Part file: Part): LiveData<NetworkResult<RespData<ModelUpload>>>

    @Multipart
    @POST("common/multi_upload_file")
    fun uploadList(@retrofit2.http.Part files: List<Part>): LiveData<NetworkResult<RespData<List<ModelUpload>>>>

    @FormUrlEncoded
    @POST("auth/login")
    fun userLogin(
        @Field("id") id: String,
        @Field("pwd") pwd: String,
        @Field("push_token") push_token: String,
        @Field("dev_type") dev_type: String,
    ): LiveData<NetworkResult<RespData<ModelUser>>>

    @FormUrlEncoded
    @POST("auth/signup")
    fun userSignup(
        @Field("id") id: String,
        @Field("pwd") pwd: String,
        @Field("profile_url") profile_url: String,
        @Field("name") name: String,
    ): LiveData<NetworkResult<RespData<ModelUser>>>

    @POST("user/signout")
    fun userSignout(
        @Header("Authorization") token: String,
    ): LiveData<NetworkResult<RespData<BaseModel>>>

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
    }
}