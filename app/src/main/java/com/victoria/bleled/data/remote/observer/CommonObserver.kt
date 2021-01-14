package com.victoria.bleled.data.remote.observer

import android.content.Context
import androidx.lifecycle.Observer
import com.victoria.bleled.R
import com.victoria.bleled.data.remote.ApiException
import com.victoria.bleled.util.CommonUtil
import com.victoria.bleled.util.arch.network.NetworkResult

class CommonObserver<T> constructor(val context: Context, val showError: Boolean = true) :
    Observer<NetworkResult<T>> {

    override fun onChanged(t: NetworkResult<T>?) {
        if (t == null) {
            return
        }

        // 재시도 등 UI처리가 필요할때 여기서 공통처리 진행
        if (t.status.value == NetworkResult.Status.error && showError) {
            onError(t)
        }
    }

    protected fun onError(tNetworkResult: NetworkResult<T>) {
        if (tNetworkResult.error != null && tNetworkResult.error is ApiException) {
            val exception: ApiException = tNetworkResult.error as ApiException
            CommonUtil.showToast(
                context,
                if (exception.getMsg() != null) exception.getMsg() else context.getString(R.string.server_problem)
            )
        } else {
            CommonUtil.showToast(context, R.string.error_404)
        }
    }
}