package com.victoria.bleled.app.network

import android.content.Context
import androidx.lifecycle.Observer
import com.victoria.bleled.R
import com.victoria.bleled.util.CommonUtil
import com.victoria.bleled.util.architecture.remote.Resource
import com.victoria.bleled.util.architecture.remote.Status

open class GithubServiceObserver<T> : Observer<Resource<T>> {
    private var context: Context? = null
    private var showError: Boolean = true

    constructor(context: Context, isShowError: Boolean) {
        this.context = context
        this.showError = isShowError
    }

    /**
     * Called when the data is changed.
     * @param t  The new data
     */
    override fun onChanged(t: Resource<T>) {
        if (t.status == Status.ERROR && showError) {
            onError(t)
        }
    }

    fun onError(t: Resource<T>) {
        val msg = t.message ?: context?.getString(R.string.data_fetch_error)
        CommonUtil.showToast(context!!, msg!!)
    }
}
