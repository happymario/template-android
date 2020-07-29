package com.victoria.bleled.data.remote;

import androidx.annotation.MainThread;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.victoria.bleled.data.remote.resp.BaseResponse;
import com.victoria.bleled.util.architecture.AppExecutors;
import com.victoria.bleled.util.architecture.network.NetworkResult;

public abstract class BaseResponseConvert<ResultType, RequestType> {
    MediatorLiveData<NetworkResult<ResultType>> result = new MediatorLiveData<NetworkResult<ResultType>>();
    AppExecutors appExecutors = null;

    @MainThread
    public BaseResponseConvert(AppExecutors appExecutors) {
        this.appExecutors = appExecutors;
        result.setValue(NetworkResult.loading(null));
        fetchFromNetwork(new MutableLiveData<>(null));
    }

    public LiveData<NetworkResult<ResultType>> asLiveData() {
        return result;
    }

    @MainThread
    protected abstract LiveData<NetworkResult<RequestType>> createCall();

    @MainThread
    protected abstract LiveData<BaseResponse<ResultType>> processResponse(NetworkResult<RequestType> response);

    private void fetchFromNetwork(LiveData<ResultType> source) {
        LiveData<NetworkResult<RequestType>> apiCall = createCall();
        result.addSource(apiCall, response -> {
            result.removeSource(apiCall);

            if (response.status.getValue() == NetworkResult.Status.success) {
                appExecutors.getMainThread().execute(() -> {
                    LiveData<BaseResponse<ResultType>> newData = processResponse(response);
                    BaseResponse<ResultType> baseResponse = newData.getValue();

                    if (baseResponse != null) {
                        int error = baseResponse.getResult();
                        if (error != ApiException.SUCCESS) {
                            result.setValue(NetworkResult.error(new ApiException(error, baseResponse.getMsg(), baseResponse.getMsg())));
                        } else {
                            result.setValue(NetworkResult.success(baseResponse.getData()));
                        }
                    } else {
                        result.setValue(NetworkResult.error(new ApiException(ApiException.ERR_NO_DATA, "", "")));
                    }
                });
            } else {
                appExecutors.getMainThread().execute(() -> {
                    result.setValue(NetworkResult.error(response.error));
                });
            }
        });
    }
}
