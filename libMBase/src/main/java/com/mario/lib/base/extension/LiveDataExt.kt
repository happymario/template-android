package com.mario.lib.base.extension

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

inline fun <T> LiveData<T>.nonNullObserve(
    owner: LifecycleOwner,
    crossinline observer: (t: T) -> Unit,
) {
    this.observe(owner, Observer {
        it?.let(observer)
    })
}

