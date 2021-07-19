package com.victoria.bleled.util.arch.base;


import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.victoria.bleled.util.arch.network.NetworkResult;


public class BaseViewModel extends ViewModel {
    public MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    public MutableLiveData<NetworkResult> networkErrorLiveData = new MutableLiveData();

    public BaseViewModel() {
        super();
    }

}
