package com.victoria.bleled.util.arch;

import androidx.lifecycle.LiveData;

public class AbsentLiveData<T> extends LiveData<T> {
    AbsentLiveData() {
        postValue(null);
    }

    public static <T> AbsentLiveData<T> create() {
        return new AbsentLiveData<T>();
    }
}
