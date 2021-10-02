package com.victoria.bleled.data.remote;

import androidx.annotation.MainThread;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.victoria.bleled.util.arch.AppExecutors;
import com.victoria.bleled.util.arch.network.NetworkResult;

public abstract class LiveDataConverter<RequestType, ResultType> {
    MediatorLiveData<NetworkResult<ResultType>> result = new MediatorLiveData();
    AppExecutors appExecutors = null;

    @MainThread
    public LiveDataConverter(AppExecutors appExecutors) {
        this.appExecutors = appExecutors;
        result.setValue(NetworkResult.loading());
        fetchFromNetwork(new MutableLiveData<>(null));
    }

    public LiveData<NetworkResult<ResultType>> asLiveData() {
        return result;
    }

    @MainThread
    protected abstract LiveData<NetworkResult<RequestType>> createCall();

    @MainThread
    protected abstract LiveData<ResultType> processResponse(NetworkResult<RequestType> response);

    private void fetchFromNetwork(LiveData<ResultType> source) {
        LiveData<NetworkResult<RequestType>> apiCall = createCall();
        result.addSource(apiCall, response -> {
            if (response.status.getValue() == NetworkResult.Status.loading) {
                return;
            }
            result.removeSource(apiCall);
            if (response.status.getValue() == NetworkResult.Status.success) {
                appExecutors.getMainThread().execute(() -> {
                    LiveData<ResultType> newData = processResponse(response);
                    ResultType responseData = newData.getValue();

                    if (responseData == null) {
                        result.setValue(NetworkResult.error(new ApiException(ApiException.ERR_NO_DATA, "", "")));
                    } else if (responseData instanceof Exception) {
                        result.setValue(NetworkResult.error((Exception) responseData));
                    } else {
                        result.setValue(NetworkResult.success(responseData));
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
