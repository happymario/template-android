package com.mario.template.util

import android.content.Context
import android.widget.Toast

object CommonUtil {
    fun showToast(p_context: Context, strId: Int) {
        val msg = p_context.getString(strId)
        showToast(p_context, msg)
    }

    fun showToast(p_context: Context?, msg: String?) {
        Toast.makeText(p_context, msg, Toast.LENGTH_SHORT).show()
    }
}