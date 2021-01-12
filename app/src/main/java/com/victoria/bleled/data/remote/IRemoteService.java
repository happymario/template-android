package com.victoria.bleled.data.remote;

import androidx.lifecycle.LiveData;

import com.victoria.bleled.util.arch.network.NetworkResult;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface IRemoteService {
    /************************************************************
     *  Constants
     ************************************************************/
    String API_BASE_URL = "https://club.kyadmeeting.com/api/";
    String API_KEY = "clubonline20200519key";


    /************************************************************
     *  Common
     ************************************************************/
    @FormUrlEncoded
    @POST("appinfo")
    LiveData<NetworkResult<String>> appInfo(@Field("r") String r);
}
