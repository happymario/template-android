package com.victoria.bleled.data.local.pref

import android.content.Context
import android.content.SharedPreferences

class PrefMgr {

    private var mPreferences: SharedPreferences? = null

    constructor(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        mPreferences = prefs
    }

    fun init(sharedPreferences: SharedPreferences) {
        mPreferences = sharedPreferences
    }

    fun put(key: String, value: Any?) {
        if (value == null) {
            return
        }

        val editor = mPreferences!!.edit()
        if (value is String) {
            editor.putString(key, value as String?)
        } else if (value is Boolean) {
            editor.putBoolean(key, (value as Boolean?)!!)
        } else if (value is Int) {
            editor.putInt(key, (value as Int?)!!)
        } else if (value is Float || value is Double) {
            editor.putFloat(key, (value as Float?)!!)
        } else if (value is Long) {
            editor.putLong(key, (value as Long?)!!)
        }

        editor.apply()
    }

    fun getString(key: String, defaultValue: String): String? {
        return mPreferences!!.getString(key, defaultValue)
    }

    fun getFloat(key: String, defaultValue: Float): Float {
        return mPreferences!!.getFloat(key, defaultValue)
    }

    fun getLong(key: String, defaultValue: Long): Long? {
        return mPreferences!!.getLong(key, defaultValue)
    }

    fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return mPreferences!!.getBoolean(key, defaultValue)
    }

    fun getInt(key: String, defaultValue: Int): Int {
        try {
            return mPreferences!!.getInt(key, defaultValue)
        } catch (e: Exception) {
            return defaultValue
        }

    }

    companion object {
        val PREFS_NAME = "my_Prefs"

        private val preferences: PrefMgr? = null
    }
}
