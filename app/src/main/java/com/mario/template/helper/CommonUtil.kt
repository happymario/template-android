package com.mario.template.helper

import android.content.Context
import android.widget.Toast
import com.mario.template.R
import java.util.regex.Pattern

object CommonUtil {
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

    fun isValidEmail(email: String): Boolean {
        var isValid = false
        val count = email.length - email.replace("@".toRegex(), "").length
        if (count >= 2) {
            return false
        }
        val expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$"
        val inputStr: CharSequence = email
        val pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE)
        val matcher = pattern.matcher(inputStr)
        if (matcher.matches()) {
            isValid = true
        }
        return isValid
    }
}