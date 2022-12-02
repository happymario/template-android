package com.victoria.bleled.data.remote

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.victoria.bleled.R
import com.victoria.bleled.app.MyApplication.Companion.globalApplicationContext
import com.victoria.bleled.data.remote.myservice.response.BaseResp
import com.victoria.bleled.util.CommonUtil

open class NetworkObserver<T> : Observer<NetworkResult<T>> {
    private var context: Context? = null
    private var showError = true

    constructor() {
        showError = false
        context = globalApplicationContext
    }

    constructor(context: Context?, showError: Boolean) {
        this.context = context
        this.showError = showError
    }

    override fun onChanged(result: NetworkResult<T>) {
        if (result == null) {
            return
        }
        if (result.status.value == NetworkResult.Status.success && result.data is BaseResp) {
            val data = result.data as BaseResp
            val error = data.result
            if (error != ApiException.Companion.SUCCESS) {
                result.error = ApiException(error, data.message, data.reason)
                result.status = MutableLiveData(NetworkResult.Status.error)
            }
        }

        // 재시도 등 UI처리가 필요할때 여기서 공통처리 진행
        if (result.status.value == NetworkResult.Status.error && showError) {
            onError(result)
        }
    }

    private fun onError(result: NetworkResult<T>) {
        if (result.error != null && result.error is ApiException) {
            val exception = result.error as ApiException
            CommonUtil.showToast(
                context,
                if (exception.msg != null && !exception.msg!!.isEmpty()) exception.msg else context!!.resources.getString(
                    R.string.server_problem
                )
            )
        } else {
            CommonUtil.showToast(context, R.string.network_connect_error)
        }
    }

    fun getErrStr(result: NetworkResult<T>): String? {
        return getErrorMsg(context, result)
    }

    companion object {
        fun <T> getErrorException(result: NetworkResult<T>): Throwable {
            if (result.status.value == NetworkResult.Status.success && result.data is BaseResp) {
                val data = result.data as BaseResp
                val error = data.result
                if (error != ApiException.Companion.SUCCESS) {
                    result.error = ApiException(error, data.message, data.reason)
                    result.status = MutableLiveData(NetworkResult.Status.error)
                }
            }
            return result.error
        }

        fun <T> getErrorMsg(p_context: Context?, result: NetworkResult<T>): String? {
            val error = getErrorException(result)
            return if (result.error != null && result.error is ApiException) {
                val exception = result.error as ApiException
                if (exception.msg != null) exception.msg else p_context!!.resources.getString(R.string.server_problem)
            } else {
                p_context!!.resources.getString(R.string.network_connect_error)
            }
        }
    }
}