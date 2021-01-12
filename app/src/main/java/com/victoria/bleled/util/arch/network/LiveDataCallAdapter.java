package com.victoria.bleled.util.arch.network;

import androidx.lifecycle.LiveData;

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
                    call.enqueue(new Callback<R>() {
                        @Override
                        public void onResponse(Call<R> call, Response<R> response) {
                            postValue(NetworkResult.success(response.body()));
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
