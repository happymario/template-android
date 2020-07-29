package com.victoria.bleled.util.architecture.network;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

public class NetworkResult<T> {
    public LiveData<Status> status = new MediatorLiveData<>();
    public T data = null;
    public Throwable error = null;

    public NetworkResult(Status status, T data, Throwable error) {
        this.status = new MutableLiveData<>(status);
        this.data = data;
        this.error = error;
    }

    public NetworkResult(LiveData<Status> status, T data, Throwable error) {
        this.status = status;
        this.data = data;
        this.error = error;
    }

    public static <T> NetworkResult<T> success(T data) {
        return new NetworkResult<T>(Status.success, data, null);
    }

    public static <T> NetworkResult<T> error(Throwable error) {
        return new NetworkResult<T>(Status.error, null, error);
    }

    public static <T> NetworkResult<T> loading(T data) {
        return new NetworkResult<T>(Status.loading, data, null);
    }

    public enum Status {
        success("success"),
        loading("loading"),
        error("error");

        private String value;

        Status(String value) {
            this.value = value;
        }
    }
}
