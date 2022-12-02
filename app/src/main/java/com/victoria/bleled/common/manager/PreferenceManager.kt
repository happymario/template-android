package com.victoria.bleled.common.manager

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.victoria.bleled.app.MyApplication
import com.victoria.bleled.data.model.ModelAppInfo
import com.victoria.bleled.data.model.ModelUser

class PreferenceManager internal constructor(private val mContext: Context) {
    private val mPreference: PrefMgr = PrefMgr(mContext)
    var user: ModelUser?
        get() = Gson().fromJson(mPreference.getString(PREFS_USER_INFO, ""), ModelUser::class.java)
        set(user) {
            if (user == null) {
                mPreference.put(PREFS_USER_INFO, "")
                return
            }
            mPreference.put(PREFS_USER_INFO, Gson().toJson(user))
        }
    var pushToken: String?
        get() = mPreference.getString(PREFS_PUSH_TOKEN, "")
        set(token) {
            mPreference.put(PREFS_PUSH_TOKEN, token)
        }
    var latitude: Double
        get() {
            val strLatitude = mPreference.getString(PREFS_GEO_LATITUDE, "0")
            return strLatitude!!.toDouble()
        }
        set(lat) {
            mPreference.put(PREFS_GEO_LATITUDE, String.format("%f", lat))
        }
    var longitude: Double
        get() {
            val strLongititude = mPreference.getString(PREFS_GEO_LOGITUDE, "0")
            return strLongititude!!.toDouble()
        }
        set(lng) {
            mPreference.put(PREFS_GEO_LOGITUDE, String.format("%f", lng))
        }
    var appInfo: ModelAppInfo?
        get() = Gson().fromJson(mPreference.getString(PREFS_APP_INFO, ""), ModelAppInfo::class.java)
        set(user) {
            if (user == null) {
                mPreference.put(PREFS_APP_INFO, "")
                return
            }
            mPreference.put(PREFS_APP_INFO, Gson().toJson(user))
        }
    var referer: String?
        get() = mPreference.getString(PREFS_REFERER, "")
        set(referer) {
            mPreference.put(PREFS_REFERER, referer)
        }

    companion object {
        private const val PREFS_USER_INFO = "PREFS_USER_INFO"
        private const val PREFS_APP_INFO = "PREFS_APP_INFO"
        private const val PREFS_APP_SETTING = "PREFS_APP_SETTING"
        private const val PREFS_PUSH_TOKEN = "PREFS_PUSH_TOKEN"
        private const val PREFS_REFERER = "PREFS_REFERER"
        private const val PREFS_GEO_LATITUDE = "PREFS_GEO_LATITUDE"
        private const val PREFS_GEO_LOGITUDE = "PREFS_GEO_LOGITUDE"

        @JvmStatic
        fun getInstance(context: Context): PreferenceManager {
            return PreferenceManager(context)
        }

        @JvmStatic
        fun getInstance(): PreferenceManager {
            return PreferenceManager(MyApplication.globalApplicationContext!!)
        }
    }

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
}
