package com.victoria.bleled.util.arch.base;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.victoria.bleled.util.arch.network.NetworkResult;


public class BaseViewModel extends ViewModel {
    // one direction
    protected MutableLiveData<Boolean> _dataLoading = new MutableLiveData<Boolean>();
    public LiveData<Boolean> dataLoading = _dataLoading;

    protected MutableLiveData<NetworkResult> _networkErrorLiveData = new MutableLiveData<>();
    public LiveData<NetworkResult> networkErrorLiveData = _networkErrorLiveData;

    public BaseViewModel() {
        super();
    }

}
