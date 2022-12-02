package com.victoria.bleled.data.net.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.victoria.bleled.base.internal.AbsentLiveData;
import com.victoria.bleled.base.internal.AppException;
import com.victoria.bleled.common.AppExecutors;
import com.victoria.bleled.data.net.adapter.live.ApiLiveResponse;
import com.victoria.bleled.data.net.adapter.live.ApiLiveResponseObserver;
import com.victoria.bleled.data.net.adapter.live.LiveDataConverter;
import com.victoria.bleled.data.net.mytemplate.IMyTemplateService;
import com.victoria.bleled.data.net.mytemplate.response.RespData;


public class MyTemplateRepository extends Repository {
    private AppExecutors appExecutors;
    private IMyTemplateService remoteService;

    public MyTemplateRepository(AppExecutors appExecutors, IMyTemplateService provideMyTemplateService) {
        this.appExecutors = appExecutors;
        this.remoteService = provideMyTemplateService;
    }

    public static MyTemplateRepository provideDataRepository() {
        return new MyTemplateRepository(new AppExecutors(), IMyTemplateService.provideMyTemplateService());
    }


    /************************************************************
     *  From Remote
     ************************************************************/
    public IMyTemplateService getRemoteService() {
        return remoteService;
    }

    public <T> void callApi(LiveData<ApiLiveResponse<RespData<T>>> originApi, ApiLiveResponseObserver<RespData<T>> callback) {
        LiveData<ApiLiveResponse<RespData<T>>> liveData = originApi;
        Observer apiObserver = new ApiLiveResponseObserver<RespData<T>>() {
            @Override
            public void onChanged(ApiLiveResponse<RespData<T>> baseResponseNetworkResult) {
                super.onChanged(baseResponseNetworkResult);

                if (baseResponseNetworkResult == null) {
                    return;
                }

                ApiLiveResponse.Status status = baseResponseNetworkResult.getStatus();
                if (status == ApiLiveResponse.Status.loading) {
                    callback.onChanged(ApiLiveResponse.loading());
                } else {
                    liveData.removeObserver(this);

                    if (status == ApiLiveResponse.Status.success) {
                        if (baseResponseNetworkResult.getData() == null) {
                            callback.onChanged(ApiLiveResponse.error(new Exception()));
                            return;
                        }
//                        BaseResponse<T> baseResponse = new Gson().fromJson(baseResponseNetworkResult.data, new TypeToken<BaseResponse<T>>() {
//                        }.getType());
                        callback.onChanged(ApiLiveResponse.success(baseResponseNetworkResult.getData()));
                    } else {
                        callback.onChanged(ApiLiveResponse.error(baseResponseNetworkResult.getError()));
                    }
                }
            }
        };

        liveData.observeForever(apiObserver);
    }

    public <T> LiveData<ApiLiveResponse<T>> callLiveDataApi(LiveData<ApiLiveResponse<RespData<T>>> originApi) {
        return new LiveDataConverter<RespData<T>, T>(appExecutors.getNetworkIO()) {
            @Override
            protected LiveData<ApiLiveResponse<RespData<T>>> createCall() {
                return originApi;
            }

            @Override
            protected LiveData<T> processResponse(ApiLiveResponse<RespData<T>> response) {
                if (response.getData() != null && response.getStatus() == ApiLiveResponse.Status.success) {
                    //BaseResponse<T> baseResponse = IMyRemoteService.decrypt(retType, response.data);
                    RespData<T> baseResponse = response.getData();
                    if (baseResponse.getResult() == 0) {
                        return new MutableLiveData(response.getData().getData());
                    } else {
                        return new MutableLiveData(new AppException.ServerHttp(baseResponse.getResult(), baseResponse.getMessage()));
                    }
                }
                return AbsentLiveData.create();
            }
        }.asLiveData();
    }
}

