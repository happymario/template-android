package com.mario.lib.base.util

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
}