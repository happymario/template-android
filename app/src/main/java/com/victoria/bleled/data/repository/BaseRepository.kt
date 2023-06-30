package com.victoria.bleled.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.victoria.bleled.base.internal.AbsentLiveData
import com.victoria.bleled.base.internal.AppException
import com.victoria.bleled.data.net.adapter.ApiResponse
import com.victoria.bleled.data.net.adapter.live.ApiLiveResponse
import com.victoria.bleled.data.net.adapter.live.ApiLiveResponseObserver
import com.victoria.bleled.data.net.adapter.live.LiveDataConverter
import com.victoria.bleled.data.net.adapter.onError
import com.victoria.bleled.data.net.adapter.onException
import com.victoria.bleled.data.net.mytemplate.response.RespData
import java.util.concurrent.Executors


abstract class BaseRepository {

    companion object {

    }

    fun <T> callApi(
        originApi: LiveData<ApiLiveResponse<RespData<T>>>,
        callback: ApiLiveResponseObserver<RespData<T>>,
    ) {
        val apiObserver: Observer<*> =
            object : ApiLiveResponseObserver<RespData<T>>() {
                override fun onChanged(baseResponseNetworkResult: ApiLiveResponse<RespData<T>>) {
                    super.onChanged(baseResponseNetworkResult)
                    if (baseResponseNetworkResult == null) {
                        return
                    }
                    val status = baseResponseNetworkResult.status
                    if (status === ApiLiveResponse.Status.loading) {
                        callback.onChanged(ApiLiveResponse.loading())
                    } else {
                        originApi.removeObserver(this)
                        if (status === ApiLiveResponse.Status.success) {
                            if (baseResponseNetworkResult.data == null) {
                                callback.onChanged(ApiLiveResponse.error(Exception()))
                                return
                            }
                            //BaseResponse<T> baseResponse = new Gson().fromJson(baseResponseNetworkResult.data, new TypeToken<BaseResponse<T>>() {}.getType());
                            callback.onChanged(
                                ApiLiveResponse.success(
                                    baseResponseNetworkResult.data!!
                                )
                            )
                        } else {
                            callback.onChanged(
                                ApiLiveResponse.error(
                                    baseResponseNetworkResult.error!!
                                )
                            )
                        }
                    }
                }
            }
        originApi.observeForever(apiObserver as Observer<in ApiLiveResponse<RespData<T>>>)
    }

    fun <T> callLiveDataApi(originApi: LiveData<ApiLiveResponse<RespData<T>>>): LiveData<ApiLiveResponse<T>?> {
        return object : LiveDataConverter<RespData<T>, T>(Executors.newSingleThreadExecutor()) {
            override fun createCall(): LiveData<ApiLiveResponse<RespData<T>>> {
                return originApi
            }

            override fun processResponse(response: ApiLiveResponse<RespData<T>>?): LiveData<T> {
                if (response!!.data != null && response.status === ApiLiveResponse.Status.success) {
                    //BaseResponse<T> baseResponse = IMyRemoteService.decrypt(retType, response.data);
                    val baseResponse: RespData<T>? = response.data
                    return if (baseResponse?.result == 0) {
                        MutableLiveData(response.data!!.data)
                    } else {
                        MutableLiveData(AppException.ServerHttp(baseResponse!!.result, baseResponse!!.message) as T)
                    }
                }
                return AbsentLiveData.create()
            }
        }.asLiveData()
    }

    fun <T> ApiResponse<T>.nextThrow() {
        this.onError {
            throw AppException.ServerHttp(statusCode.code, this.response.message())
        }.onException {
            throw AppException.Network(this.exception)
        }
    }

    fun <T> ApiLiveResponse<T>.nextThrow() {
        this.onError {
            throw AppException.ServerHttp(statusCode.code, this.response.message())
        }.onException {
            throw AppException.Network(this.exception)
        }
    }
}