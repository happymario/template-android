package com.victoria.bleled.base.extensions

import android.annotation.SuppressLint
import android.view.View
import androidx.lifecycle.*
import com.victoria.bleled.base.internal.Resource
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

inline fun <T> LiveData<T>.nonNullObserve(
    owner: LifecycleOwner,
    crossinline observer: (t: T) -> Unit,
) {
    this.observe(owner, Observer {
        it?.let(observer)
    })
}


inline fun <T : Resource<R>, R> LiveData<T>.observeResource2(
    viewLifecycleOwner: LifecycleOwner,
    loadingView: View? = null,
    targetView: View? = null,
    crossinline block: (R) -> Unit,
) {
    observe(viewLifecycleOwner, Observer { status ->
        when (status) {
            is Resource.Loading<*> -> {
                loadingView?.visibility = View.VISIBLE
                targetView?.visibility = View.GONE
            }
            is Resource.Error<*> -> {
                loadingView?.visibility = View.GONE
                targetView?.visibility = View.GONE
            }
            is Resource.Success<*> -> {
                loadingView?.visibility = View.GONE
                targetView?.visibility = View.VISIBLE
                block.invoke(status.data!!)
            }
        }
    })
}

@SuppressLint("UnsafeRepeatOnLifecycleDetector")
inline fun <T : Any> StateFlow<T>.launchWithStarted(
    lifecycleScope: LifecycleCoroutineScope,
    lifecycle: Lifecycle,
    crossinline f: (T) -> Unit,
) {
    lifecycleScope.launch {
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            this@launchWithStarted.collect { f(it) }
        }
    }
}