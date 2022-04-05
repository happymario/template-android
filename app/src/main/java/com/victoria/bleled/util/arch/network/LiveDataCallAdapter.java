package com.victoria.bleled.util.arch.network;

import androidx.lifecycle.LiveData;

import com.victoria.bleled.data.remote.ApiException;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.concurrent.atomic.AtomicBoolean;

import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Callback;
import retrofit2.Response;

public class LiveDataCallAdapter<R> implements CallAdapter<R, LiveData<NetworkResult<R>>> {
    private Type responseType;

    public LiveDataCallAdapter(Type type) {
        this.responseType = type;
    }

    @Override
    public Type responseType() {
        return responseType;
    }

    @Override
    public LiveData<NetworkResult<R>> adapt(Call<R> call) {
        return new LiveData<NetworkResult<R>>() {
            private AtomicBoolean started = new AtomicBoolean(false);

            @Override
            protected void onActive() {
                super.onActive();

                if (started.compareAndSet(false, true)) {
                    postValue(NetworkResult.loading());

                    call.enqueue(new Callback<R>() {
                        @Override
                        public void onResponse(Call<R> call, Response<R> response) {
                            if (response.body() != null) {
                                postValue(NetworkResult.success(response.body()));
                            } else {
                                ApiException exception = new ApiException(response.code(), response.message(), "");

                                try {
                                    if (response.errorBody() != null) {
                                        exception = new ApiException(response.code(), response.message(), response.errorBody().string());
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                postValue(NetworkResult.error(exception));
                            }
                        }

                        @Override
                        public void onFailure(Call<R> call, Throwable t) {
                            postValue(NetworkResult.error(t));
                        }
                    });
                }
            }
        };
    }
}
