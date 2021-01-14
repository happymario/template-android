package com.victoria.bleled.data;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.victoria.bleled.app.MyApplication;
import com.victoria.bleled.data.local.IPrefDataSource;
import com.victoria.bleled.data.local.PrefDataSourceImpl;
import com.victoria.bleled.data.model.ModelUser;
import com.victoria.bleled.data.remote.IGithubService;
import com.victoria.bleled.data.remote.IRemoteService;
import com.victoria.bleled.data.remote.convert.BaseResponseConvert;
import com.victoria.bleled.data.remote.convert.NormalResultConvert;
import com.victoria.bleled.data.remote.resp.BaseResponse;
import com.victoria.bleled.data.remote.resp.ResponseSearchRepo;
import com.victoria.bleled.util.arch.AbsentLiveData;
import com.victoria.bleled.util.arch.AppExecutors;
import com.victoria.bleled.util.arch.network.LiveDataCallAdapterFactory;
import com.victoria.bleled.util.arch.network.NetworkResult;
import com.victoria.bleled.util.arch.network.RetrofitHelper;

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
    public IRemoteService getRemoteService() {
        return remoteService;
    }

    public <ResultType> LiveData<NetworkResult<ResultType>> callRemoteService1(LiveData<NetworkResult<ResultType>> call) {
        return new NormalResultConvert<ResultType, ResultType>(appExecutors) {
            @Override
            protected LiveData<NetworkResult<ResultType>> createCall() {
                return call;
            }

            @Override
            protected LiveData<ResultType> processResponse(NetworkResult<ResultType> response) {
                if (response.data != null && response.status.getValue() == NetworkResult.Status.success) {
                    return new MutableLiveData<>(response.data);
                }
                return AbsentLiveData.create();
            }
        }.asLiveData();
    }

    public <ResultType> LiveData<NetworkResult<ResultType>> callRemoteService2(LiveData<NetworkResult<BaseResponse<ResultType>>> call) {
        return new BaseResponseConvert<BaseResponse<ResultType>, ResultType>(appExecutors) {
            @Override
            protected LiveData<NetworkResult<BaseResponse<ResultType>>> createCall() {
                return call;
            }

            @Override
            protected LiveData<BaseResponse<ResultType>> processResponse(NetworkResult<BaseResponse<ResultType>> response) {
                if (response.data != null && response.status.getValue() == NetworkResult.Status.success) {
                    BaseResponse<ResultType> baseResponse = response.data;
                    if (baseResponse != null) {
                        return new MutableLiveData<>(baseResponse);
                    }
                }
                return AbsentLiveData.create();
            }
        }.asLiveData();
    }

    ///////////////////////////////////////////////////////////////////////////
    // Test
    ///////////////////////////////////////////////////////////////////////////
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

//    public LiveData<NetworkResult<ModelAppInfo>> reqAppInfo() {
//        return new BaseResponseConvert<String, ModelAppInfo>(appExecutors) {
//
//            @Override
//            protected LiveData<NetworkResult<String>> createCall() {
//                return remoteService.appInfo(encrypt("os_type", Constants.Market));
//            }
//
//            @Override
//            protected LiveData<BaseResponse<ModelAppInfo>> processResponse(NetworkResult<String> response) {
//                if (response.data != null && response.status.getValue() == NetworkResult.Status.success) {
//                    BaseResponse<ModelAppInfo> baseResponse = decrypt(new TypeToken<BaseResponse<ModelAppInfo>>() {
//                    }.getType(), response.data);
//                    if (baseResponse != null) {
//                        return new MutableLiveData<>(baseResponse);
//                    }
//                }
//                return AbsentLiveData.create();
//            }
//        }.asLiveData();
//    }


    /************************************************************
     *  From Local
     ************************************************************/
    public IPrefDataSource getPrefDataSource() {
        return prefDataSource;
    }
}

