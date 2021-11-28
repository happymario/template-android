package com.victoria.bleled.data;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.victoria.bleled.data.local.PrefDataSource;
import com.victoria.bleled.data.model.ModelUser;
import com.victoria.bleled.data.remote.ApiException;
import com.victoria.bleled.data.remote.LiveDataConverter;
import com.victoria.bleled.data.remote.NetworkObserver;
import com.victoria.bleled.data.remote.github.IGithubService;
import com.victoria.bleled.data.remote.github.ResponseSearchRepo;
import com.victoria.bleled.data.remote.myservice.BaseResponse;
import com.victoria.bleled.data.remote.myservice.IMyRemoteService;
import com.victoria.bleled.util.arch.AbsentLiveData;
import com.victoria.bleled.util.arch.AppExecutors;
import com.victoria.bleled.util.arch.network.AddParamsInterceptor;
import com.victoria.bleled.util.arch.network.LiveDataCallAdapterFactory;
import com.victoria.bleled.util.arch.network.NetworkResult;
import com.victoria.bleled.util.arch.network.RetrofitHelper;

import java.util.List;

import retrofit2.Retrofit;

public class DataRepository {
    private AppExecutors appExecutors;
    private IMyRemoteService remoteService;
    private IGithubService githubService;
    private PrefDataSource prefDataSource;

    public static IGithubService provideGithubService() {
        final Retrofit retrofit = RetrofitHelper.createRetrofit(IGithubService.API_BASE_URL, new LiveDataCallAdapterFactory());
        return retrofit.create(IGithubService.class);
    }

    public static IMyRemoteService provideRemoteService() {
        final Retrofit retrofit = RetrofitHelper.createRetrofit(IMyRemoteService.API_BASE_URL, new LiveDataCallAdapterFactory(), getCommonParams());
        return retrofit.create(IMyRemoteService.class);
    }

    public static DataRepository provideDataRepository(Context context) {
        return new DataRepository(new AppExecutors(), DataRepository.provideRemoteService(), PrefDataSource.getInstance(context));
    }

    public DataRepository(AppExecutors executors, IMyRemoteService remoteService, PrefDataSource localData) {
        this.appExecutors = executors;
        this.remoteService = remoteService;
        this.prefDataSource = localData;
        this.githubService = DataRepository.provideGithubService();
    }


    /************************************************************
     *  Helpers
     ************************************************************/
    private static AddParamsInterceptor getCommonParams() {
        AddParamsInterceptor addParamsInterceptor = new AddParamsInterceptor.Builder()
                .addParameter("key", "abc")
                .build();

        return addParamsInterceptor;
    }


    /************************************************************
     *  From Github
     ************************************************************/
    public IGithubService getGithubService() {
        return githubService;
    }

    public LiveData<NetworkResult<List<ModelUser>>> loadRepoList(String q, int page) {
        return new LiveDataConverter<ResponseSearchRepo, List<ModelUser>>(appExecutors.getNetworkIO()) {
            @Override
            protected LiveData<NetworkResult<ResponseSearchRepo>> createCall() {
                return githubService.searchRepo(q, page);
            }

            @Override
            protected LiveData<List<ModelUser>> processResponse(NetworkResult<ResponseSearchRepo> response) {
                if (response.data != null && response.status.getValue() == NetworkResult.Status.success) {
                    return new MutableLiveData<List<ModelUser>>(response.data.items);
                }
                return AbsentLiveData.create();
            }
        }.asLiveData();
    }


    /************************************************************
     *  From Remote
     ************************************************************/
    public IMyRemoteService getRemoteService() {
        return remoteService;
    }

    public <T> void callApi(LiveData<NetworkResult<BaseResponse<T>>> originApi, NetworkObserver<BaseResponse<T>> callback) {
        LiveData<NetworkResult<BaseResponse<T>>> liveData = originApi;
        Observer apiObserver = new NetworkObserver<BaseResponse<T>>() {
            @Override
            public void onChanged(NetworkResult<BaseResponse<T>> baseResponseNetworkResult) {
                super.onChanged(baseResponseNetworkResult);

                if (baseResponseNetworkResult == null) {
                    return;
                }

                NetworkResult.Status status = baseResponseNetworkResult.status.getValue();
                if (status == NetworkResult.Status.loading) {
                    callback.onChanged(NetworkResult.loading());
                } else {
                    liveData.removeObserver(this);

                    if (status == NetworkResult.Status.success) {
                        if (baseResponseNetworkResult.data == null) {
                            callback.onChanged(NetworkResult.error(new Exception()));
                            return;
                        }
//                        BaseResponse<T> baseResponse = new Gson().fromJson(baseResponseNetworkResult.data, new TypeToken<BaseResponse<T>>() {
//                        }.getType());
                        callback.onChanged(NetworkResult.success(baseResponseNetworkResult.data));
                    } else {
                        callback.onChanged(NetworkResult.error(baseResponseNetworkResult.error));
                    }
                }
            }
        };

        liveData.observeForever(apiObserver);
    }

    public <T> LiveData<NetworkResult<T>> callLiveDataApi(LiveData<NetworkResult<BaseResponse<T>>> originApi) {
        return new LiveDataConverter<BaseResponse<T>, T>(appExecutors.getNetworkIO()) {
            @Override
            protected LiveData<NetworkResult<BaseResponse<T>>> createCall() {
                return originApi;
            }

            @Override
            protected LiveData<T> processResponse(NetworkResult<BaseResponse<T>> response) {
                if (response.data != null && response.status.getValue() == NetworkResult.Status.success) {
                    //BaseResponse<T> baseResponse = IMyRemoteService.decrypt(retType, response.data);
                    BaseResponse<T> baseResponse = response.data;
                    if (baseResponse.getResult() == ApiException.SUCCESS) {
                        return new MutableLiveData(response.data.getData());
                    } else {
                        return new MutableLiveData(new ApiException(baseResponse.getResult(), baseResponse.getMsg(), baseResponse.getReason()));
                    }
                }
                return AbsentLiveData.create();
            }
        }.asLiveData();
    }

    /************************************************************
     *  From Local
     ************************************************************/
    public PrefDataSource getPrefDataSource() {
        return prefDataSource;
    }
}

