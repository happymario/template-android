package com.victoria.bleled.data.local

import android.content.Context
import com.google.gson.Gson
import com.victoria.bleled.data.model.ModelAppInfo
import com.victoria.bleled.data.model.ModelUser

class PrefDataSource internal constructor(private val mContext: Context) {
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
        fun getInstance(context: Context): PrefDataSource {
            return PrefDataSource(context)
        }
    }

}