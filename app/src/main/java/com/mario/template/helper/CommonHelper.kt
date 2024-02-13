package com.mario.template.helper

import android.app.Activity
import android.content.Context
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import com.mario.template.R

object CommonHelper {
    fun showNIToast(p_context: Context) {
        showToast(p_context, R.string.ready_service)
    }

    fun showToast(p_context: Context, strId: Int) {
        val msg = p_context.getString(strId)
        showToast(p_context, msg)
    }

    fun showToast(p_context: Context?, msg: String?) {
        Toast.makeText(p_context, msg, Toast.LENGTH_SHORT).show()
    }

    fun showKeyboard(edit: EditText) {
        val imm = edit.context
            .getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(edit, 0)
    }

    fun hideKeyboard(edit: EditText) {
        val imm = edit.context
            .getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(edit.windowToken, 0)
    }
}