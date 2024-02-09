package com.mario.lib.base.architecture

// typealias ResourceLiveData<T> = SingleLiveEvent<Resource<T>>()

class ResourceLiveData<T> : SingleLiveEvent<Resource<T>>() {

    fun isLoading() = this.value is Resource.Loading

    fun loading() {
        this.value = Resource.Loading()
    }

    fun success(data: T) {
        this.value = Resource.Success(data)
    }

    fun error(e: Throwable) {
        this.value = Resource.Error(e)
    }
}