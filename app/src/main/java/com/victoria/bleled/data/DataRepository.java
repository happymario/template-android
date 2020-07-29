package com.victoria.bleled.data;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.victoria.bleled.app.MyApplication;
import com.victoria.bleled.common.Constants;
import com.victoria.bleled.data.local.IPrefDataSource;
import com.victoria.bleled.data.local.PrefDataSourceImpl;
import com.victoria.bleled.data.model.ModelAppInfo;
import com.victoria.bleled.data.model.ModelUser;
import com.victoria.bleled.data.remote.BaseResponseConvert;
import com.victoria.bleled.data.remote.IGithubService;
import com.victoria.bleled.data.remote.IRemoteService;
import com.victoria.bleled.data.remote.NormalResultConvert;
import com.victoria.bleled.data.remote.resp.BaseResponse;
import com.victoria.bleled.data.remote.resp.ResponseSearchRepo;
import com.victoria.bleled.util.architecture.AbsentLiveData;
import com.victoria.bleled.util.architecture.AppExecutors;
import com.victoria.bleled.util.architecture.network.LiveDataCallAdapterFactory;
import com.victoria.bleled.util.architecture.network.NetworkResult;
import com.victoria.bleled.util.architecture.network.RetrofitHelper;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;

import retrofit2.Retrofit;

public class DataRepository {
    private AppExecutors appExecutors;
    private IRemoteService remoteService;
    private IGithubService githubService;
    private IPrefDataSource prefDataSource;

    public static IGithubService provideGithubService() {
        final Retrofit retrofit = RetrofitHelper.createRetrofit(IGithubService.API_BASE_URL, new LiveDataCallAdapterFactory());
        return retrofit.create(IGithubService.class);
    }

    public static IRemoteService provideRemoteService() {
        final Retrofit retrofit = RetrofitHelper.createRetrofit(IRemoteService.API_BASE_URL, new LiveDataCallAdapterFactory());
        return retrofit.create(IRemoteService.class);
    }

    public static DataRepository provideDataRepository() {
        return new DataRepository(new AppExecutors(), DataRepository.provideRemoteService(), PrefDataSourceImpl.getInstance(MyApplication.Companion.getGlobalApplicationContext()));
    }

    public DataRepository(AppExecutors executors, IRemoteService remoteService, IPrefDataSource localData) {
        this.appExecutors = executors;
        this.remoteService = remoteService;
        this.prefDataSource = localData;
        this.githubService = DataRepository.provideGithubService();
    }

    /************************************************************
     *  Helpers
     ************************************************************/
    private String encrypt(String... params) {
        HashMap<String, String> map = new HashMap<>();
        for (int i = 0; i < params.length; i += 2) {
            map.put(params[i], params[(i + 1)]);
        }
        String r = RetrofitHelper.encryptParams(IRemoteService.API_KEY, map);
        return r;
    }

    private <T> BaseResponse<T> decrypt(Type respType, String response) {
        String strJson = RetrofitHelper.decrypt(IRemoteService.API_KEY, response);
        BaseResponse<T> baseResponse = new Gson().fromJson(strJson, respType);
        return baseResponse;
    }

    /************************************************************
     *  From Remote
     ************************************************************/
    public LiveData<NetworkResult<List<ModelUser>>> loadListForTest(String q, int page) {
        return new NormalResultConvert<List<ModelUser>, ResponseSearchRepo>(appExecutors) {
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


    ///////////////////////////////////////////////////////////////////////////
    // Common
    ///////////////////////////////////////////////////////////////////////////
    public LiveData<NetworkResult<ModelAppInfo>> getAppInfo() {
        return new BaseResponseConvert<ModelAppInfo, String>(appExecutors) {
            @Override
            protected LiveData<NetworkResult<String>> createCall() {
                return remoteService.appInfo(encrypt("os_type", Constants.MARKET));
            }

            @Override
            protected LiveData<BaseResponse<ModelAppInfo>> processResponse(NetworkResult<String> response) {
                if (response.data != null && response.status.getValue() == NetworkResult.Status.success) {
                    BaseResponse<ModelAppInfo> baseResponse = decrypt(new TypeToken<BaseResponse<ModelAppInfo>>() {
                    }.getType(), response.data);
                    if (baseResponse != null) {
                        return new MutableLiveData<BaseResponse<ModelAppInfo>>(baseResponse);
                    }
                }
                return AbsentLiveData.create();
            }
        }.asLiveData();
    }

    /************************************************************
     *  From Local
     ************************************************************/
    public IPrefDataSource getPrefDataSource() {
        return prefDataSource;
    }
}

