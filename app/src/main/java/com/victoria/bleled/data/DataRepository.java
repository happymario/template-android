package com.victoria.bleled.data;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.victoria.bleled.base.internal.AbsentLiveData;
import com.victoria.bleled.common.AppExecutors;
import com.victoria.bleled.common.manager.PreferenceManager;
import com.victoria.bleled.data.model.ModelUser;
import com.victoria.bleled.data.remote.ApiException;
import com.victoria.bleled.data.remote.LiveDataConverter;
import com.victoria.bleled.data.remote.NetworkObserver;
import com.victoria.bleled.data.remote.NetworkResult;
import com.victoria.bleled.data.remote.adapter.LiveDataCallAdapterFactory;
import com.victoria.bleled.data.remote.github.IGithubService;
import com.victoria.bleled.data.remote.github.RepoListResp;
import com.victoria.bleled.data.remote.myservice.IMyTemplateService;
import com.victoria.bleled.data.remote.myservice.response.RespData;
import com.victoria.bleled.util.thirdparty.retrofit.AddParamsInterceptor;
import com.victoria.bleled.util.thirdparty.retrofit.RetrofitHelper;

import java.util.List;

import retrofit2.Retrofit;


public class DataRepository {
    private AppExecutors appExecutors;
    private IMyTemplateService remoteService;
    private IGithubService githubService;
    private PreferenceManager prefDataSource;

    public static IGithubService provideGithubService() {
        final Retrofit retrofit = RetrofitHelper.createRetrofit(IGithubService.API_BASE_URL, new LiveDataCallAdapterFactory());
        return retrofit.create(IGithubService.class);
    }

    public static IMyTemplateService provideRemoteService() {
        final Retrofit retrofit = RetrofitHelper.createRetrofit(IMyTemplateService.API_BASE_URL, new LiveDataCallAdapterFactory(), getCommonParams());
        return retrofit.create(IMyTemplateService.class);
    }

    public static DataRepository provideDataRepository(Context context) {
        return new DataRepository(new AppExecutors(), DataRepository.provideRemoteService(), PreferenceManager.getInstance(context));
    }

    public DataRepository(AppExecutors executors, IMyTemplateService remoteService, PreferenceManager localData) {
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
        return new LiveDataConverter<RepoListResp, List<ModelUser>>(appExecutors.getNetworkIO()) {
            @Override
            protected LiveData<NetworkResult<RepoListResp>> createCall() {
                return githubService.searchRepo(q, page);
            }

            @Override
            protected LiveData<List<ModelUser>> processResponse(NetworkResult<RepoListResp> response) {
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
    public IMyTemplateService getRemoteService() {
        return remoteService;
    }

    public <T> void callApi(LiveData<NetworkResult<RespData<T>>> originApi, NetworkObserver<RespData<T>> callback) {
        LiveData<NetworkResult<RespData<T>>> liveData = originApi;
        Observer apiObserver = new NetworkObserver<RespData<T>>() {
            @Override
            public void onChanged(NetworkResult<RespData<T>> baseResponseNetworkResult) {
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

    public <T> LiveData<NetworkResult<T>> callLiveDataApi(LiveData<NetworkResult<RespData<T>>> originApi) {
        return new LiveDataConverter<RespData<T>, T>(appExecutors.getNetworkIO()) {
            @Override
            protected LiveData<NetworkResult<RespData<T>>> createCall() {
                return originApi;
            }

            @Override
            protected LiveData<T> processResponse(NetworkResult<RespData<T>> response) {
                if (response.data != null && response.status.getValue() == NetworkResult.Status.success) {
                    //BaseResponse<T> baseResponse = IMyRemoteService.decrypt(retType, response.data);
                    RespData<T> baseResponse = response.data;
                    if (baseResponse.getResult() == ApiException.SUCCESS) {
                        return new MutableLiveData(response.data.getData());
                    } else {
                        return new MutableLiveData(new ApiException(baseResponse.getResult(), baseResponse.getMessage(), baseResponse.getReason()));
                    }
                }
                return AbsentLiveData.create();
            }
        }.asLiveData();
    }

    /************************************************************
     *  From Local
     ************************************************************/
    public PreferenceManager getPrefDataSource() {
        return prefDataSource;
    }
}

