package com.victoria.bleled.data.local

import android.content.Context
import android.content.SharedPreferences

class PrefMgr {
    private var mPreferences: SharedPreferences? = null

    constructor(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        mPreferences = prefs
    }

    private constructor() {}

    fun init(sharedPreferences: SharedPreferences?) {
        mPreferences = sharedPreferences
    }

    fun put(key: String?, value: Any?) {
        if (value == null) {
            return
        }
        val editor = mPreferences!!.edit()
        if (value.javaClass == String::class.java) {
            editor.putString(key, value as String?)
        } else if (value.javaClass == Boolean::class.java) {
            editor.putBoolean(key, (value as Boolean?)!!)
        } else if (value.javaClass == Int::class.java) {
            editor.putInt(key, (value as Int?)!!)
        } else if (value.javaClass == Float::class.java || value.javaClass == Double::class.java) {
            editor.putFloat(key, (value as Float?)!!)
        } else if (value.javaClass == Long::class.java) {
            editor.putLong(key, (value as Long?)!!)
        }
        editor.apply()
    }

    fun getString(key: String?, defaultValue: String?): String? {
        return mPreferences!!.getString(key, defaultValue)
    }

    fun getFloat(key: String?, defaultValue: Float): Float {
        return mPreferences!!.getFloat(key, defaultValue)
    }

    fun getLong(key: String?, defaultValue: Long): Long {
        return mPreferences!!.getLong(key, defaultValue)
    }

    fun getBoolean(key: String?, defaultValue: Boolean): Boolean {
        return mPreferences!!.getBoolean(key, defaultValue)
    }

    fun getInt(key: String?, defaultValue: Int): Int {
        return try {
            mPreferences!!.getInt(key, defaultValue)
        } catch (e: Exception) {
            defaultValue
        }
    }

    companion object {
        const val PREFS_NAME = "MT_Prefs"
        private val preferences: PrefMgr? = null
    }
}