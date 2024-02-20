package com.mario.lib.base.util

import android.util.Patterns
import android.webkit.URLUtil
import java.net.MalformedURLException
import java.net.URL
import java.util.regex.Pattern

object ValidUtil {
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

    fun isValidUrl(urlString: String?): Boolean {
        try {
            val url = URL(urlString)
            return URLUtil.isValidUrl(urlString) && Patterns.WEB_URL.matcher(urlString).matches()
        } catch (e: MalformedURLException) {
        }
        return false
    }
}