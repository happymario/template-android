package com.victoria.bleled.base

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import com.victoria.bleled.base.internal.AppException
import com.victoria.bleled.base.internal.SingleLiveEvent
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber


open class BaseViewModel : ViewModel() {

    var _error = SingleLiveEvent<Exception>()
    var _alert = SingleLiveEvent<Any>()

    fun error(exception: AppException) {
        _error.value = exception
    }

    fun alert(@StringRes message: Int) {
        _alert.value = message
    }

    fun alert(message: String?) {
        if (message == null) {
            Timber.w("Alert Message Empty!!")
            // Nothings..
        } else {
            _alert.value = message!!
        }
    }

    override fun onCleared() {
        //disposebag.dispose()
        super.onCleared()
    }

    inline fun CoroutineScope.launchInSafe(
        crossinline error: ((Throwable) -> Unit) = {},
        crossinline handle: ((AppException.Handle) -> Unit) = {},
        crossinline block: suspend CoroutineScope.() -> Unit,
    ): Job {
        return this.launch(CoroutineExceptionHandler { _, throwable ->
            error(throwable)
            throwable.printStackTrace()
            when (throwable) {
                is AppException.ServerHttp -> {
                    _error.value = throwable
                }
                is AppException.Network -> {
                    _error.value = throwable
                }
                is AppException.Handle -> {
                    handle(throwable)
                }
                else -> {
                    _error.value = AppException.Unknown(throwable)
                }
            }
        }) {
            block()
        }
    }
}