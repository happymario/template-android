package com.victoria.bleled.data.remote.myservice;

import android.util.Log;

import androidx.lifecycle.LiveData;

import com.google.gson.Gson;
import com.victoria.bleled.data.model.ModelAppInfo;
import com.victoria.bleled.data.model.ModelUpload;
import com.victoria.bleled.util.arch.network.NetworkResult;
import com.victoria.bleled.util.arch.network.RetrofitHelper;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface IMyRemoteService {
    /************************************************************
     *  Constants
     ************************************************************/
    String API_BASE_URL = "http://192.168.0.13:9101/api/";
    String API_KEY = "clubonline20200519key";

    /************************************************************
     *  Functions
     ************************************************************/
    static String encrypt(String... params) {
        HashMap<String, String> map = new HashMap<>();
        for (int i = 0; i < params.length; i += 2) {
            map.put(params[i], params[(i + 1)]);
        }
        String r = RetrofitHelper.encryptParams(IMyRemoteService.API_KEY, map);
        return r;
    }

    static String encrypt(HashMap<String, String> map) {
        String r = RetrofitHelper.encryptParams(IMyRemoteService.API_KEY, map);
        return r;
    }

    //  BaseResponse<ModelAppInfo> baseResponse = decrypt(new TypeToken<BaseResponse<ModelAppInfo>>() {}.getType(), response.data);
    static <T> BaseResponse<T> decrypt(Type respType, String response) {
        String strJson = RetrofitHelper.decrypt(IMyRemoteService.API_KEY, response);
        BaseResponse<T> baseResponse = new Gson().fromJson(strJson, respType);
        Log.d("IShequService", strJson);
        return baseResponse;
    }


    /************************************************************
     *  Common
     ************************************************************/
    @FormUrlEncoded
    @POST("common/app_info")
    LiveData<NetworkResult<BaseResponse<ModelAppInfo>>> appInfo(@Field("dev_type") String dev_type);

    @Multipart
    @POST("common/upload_file")
    LiveData<NetworkResult<BaseResponse<ModelUpload>>> upload(@Part MultipartBody.Part file);

    @Multipart
    @POST("common/multi_upload_file")
    LiveData<NetworkResult<BaseResponse<List<ModelUpload>>>> upload(@Part List<MultipartBody.Part> files);
}
