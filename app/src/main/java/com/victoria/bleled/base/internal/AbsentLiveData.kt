package com.victoria.bleled.base.internal

import androidx.lifecycle.LiveData

class AbsentLiveData<T> internal constructor() : LiveData<T>() {
    init {
        postValue(null)
    }

    companion object {
        @JvmStatic
        fun <T> create(): AbsentLiveData<T> {
            return AbsentLiveData()
        }
    }
}