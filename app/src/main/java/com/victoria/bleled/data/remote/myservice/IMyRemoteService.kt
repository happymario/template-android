package com.victoria.bleled.data.remote.myservice

import android.util.Log
import androidx.lifecycle.LiveData
import com.google.gson.Gson
import com.victoria.bleled.data.model.ModelAppInfo
import com.victoria.bleled.data.model.ModelUpload
import com.victoria.bleled.util.arch.network.NetworkResult
import com.victoria.bleled.util.arch.network.RetrofitHelper
import okhttp3.MultipartBody.Part
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Multipart
import retrofit2.http.POST
import java.lang.reflect.Type
import java.util.*

interface IMyRemoteService {
    /************************************************************
     * Common
     */
    @FormUrlEncoded
    @POST("common/app_info")
    fun appInfo(@Field("dev_type") dev_type: String): LiveData<NetworkResult<BaseResponse<ModelAppInfo>>>

    @Multipart
    @POST("common/upload_file")
    fun upload(@retrofit2.http.Part file: Part): LiveData<NetworkResult<BaseResponse<ModelUpload>>>

    @Multipart
    @POST("common/multi_upload_file")
    fun uploadList(@retrofit2.http.Part files: List<Part>): LiveData<NetworkResult<BaseResponse<List<ModelUpload>>>>

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
        fun <T> decrypt(respType: Type, response: String): BaseResponse<T> {
            val strJson = RetrofitHelper.decrypt(API_KEY, response)
            val baseResponse: BaseResponse<T> = Gson().fromJson(strJson, respType)
            Log.d("IShequService", strJson)
            return baseResponse
        }

        /************************************************************
         * Constants
         */
        const val API_BASE_URL = "http://192.168.0.13:9101/api/"
        const val API_KEY = "clubonline20200519key"
    }
}