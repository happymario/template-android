package com.victoria.bleled.data.net.adapter.live

import android.content.Context
import androidx.lifecycle.Observer
import com.victoria.bleled.R
import com.victoria.bleled.app.MyApplication.Companion.globalApplicationContext
import com.victoria.bleled.base.internal.AppException
import com.victoria.bleled.data.net.mytemplate.response.BaseResp
import com.victoria.bleled.util.CommonUtil

open class ApiLiveResponseObserver<T> : Observer<ApiLiveResponse<T>> {
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

    override fun onChanged(result: ApiLiveResponse<T>) {
        if (result == null) {
            return
        }
        if (result.status == ApiLiveResponse.Status.success && result.data is BaseResp) {
            val data = result.data as BaseResp
            val error = data.result
            if (error != 0) {
                result.error = AppException.ServerHttp(error, data.message)
                result.status = ApiLiveResponse.Status.error
            }
        }

        // 재시도 등 UI처리가 필요할때 여기서 공통처리 진행
        if (result.status == ApiLiveResponse.Status.error && showError) {
            onError(result)
        }
    }

    private fun onError(result: ApiLiveResponse<T>) {
        if (result.error != null && result.error is AppException.ServerHttp) {
            val exception = result.error as AppException.ServerHttp
            CommonUtil.showToast(
                context,
                if (exception.message != null && !exception.message!!.isEmpty()) exception.message else context!!.resources.getString(
                    R.string.server_problem
                )
            )
        } else {
            CommonUtil.showToast(context, R.string.network_connect_error)
        }
    }

    fun getErrStr(result: ApiLiveResponse<T>): String? {
        return getErrorMsg(context, result)
    }

    companion object {
        fun <T> getErrorException(result: ApiLiveResponse<T>): Throwable {
            if (result.status == ApiLiveResponse.Status.success && result.data is BaseResp) {
                val data = result.data as BaseResp
                val error = data.result
                if (error != 0) {
                    result.error = AppException.ServerHttp(error, data.message)
                    result.status = ApiLiveResponse.Status.error
                }
            }
            return result.error!!
        }

        fun <T> getErrorMsg(p_context: Context?, result: ApiLiveResponse<T>): String? {
            val error = getErrorException(result)
            return if (result.error != null && result.error is AppException.ServerHttp) {
                val exception = result.error as AppException.ServerHttp
                if (exception.message != null) exception.message else p_context!!.resources.getString(
                    R.string.server_problem)
            } else {
                p_context!!.resources.getString(R.string.network_connect_error)
            }
        }
    }
}