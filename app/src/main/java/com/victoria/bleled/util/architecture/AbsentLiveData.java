package com.victoria.bleled.util.architecture;

import androidx.lifecycle.LiveData;

public class AbsentLiveData<T> extends LiveData<T> {
    AbsentLiveData() {
        postValue(null);
    }

    public static AbsentLiveData create() {
        return new AbsentLiveData();
    }
}
