package com.victoria.bleled.data.remote;

import androidx.lifecycle.LiveData;

import com.victoria.bleled.data.model.ModelAppInfo;
import com.victoria.bleled.data.remote.resp.BaseResponse;
import com.victoria.bleled.util.arch.network.NetworkResult;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface IRemoteService {
    /************************************************************
     *  Constants
     ************************************************************/
    String API_BASE_URL = "http://192.168.0.13:9901/api/";
    String API_KEY = "clubonline20200519key";


    /************************************************************
     *  Common
     ************************************************************/
    @FormUrlEncoded
    @POST("common/app_info")
    LiveData<NetworkResult<BaseResponse<ModelAppInfo>>> appInfo(@Field("dev_type") String dev_type);
}
