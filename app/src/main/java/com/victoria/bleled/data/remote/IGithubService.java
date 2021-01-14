package com.victoria.bleled.data.remote;

import androidx.lifecycle.LiveData;

import com.victoria.bleled.data.remote.response.ResponseSearchRepo;
import com.victoria.bleled.util.arch.network.NetworkResult;

import retrofit2.http.GET;
import retrofit2.http.Query;

public interface IGithubService {
    String API_BASE_URL = "https://api.github.com/";

    @GET("search/repositories?sort=stars")
    LiveData<NetworkResult<ResponseSearchRepo>> searchRepo(@Query("q") String q, @Query("page") int page);
}
