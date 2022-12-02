package com.victoria.bleled.util.feature.form

object FormPasswordUtils {

    fun similar(phone: String, password: String): Boolean {
        val end = phone.substring(phone.length - 4)
        val reverse = end.reversed()
        return (password == end || password == reverse)
    }
}