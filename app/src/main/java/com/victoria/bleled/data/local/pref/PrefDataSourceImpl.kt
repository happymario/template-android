package com.victoria.bleled.data.local.pref

import android.content.Context
import com.google.gson.Gson
import com.victoria.bleled.data.model.Repo

class PrefDataSourceImpl internal constructor(mContext: Context) : IPrefDataSource {
    private val mPreference: PrefMgr = PrefMgr(mContext)

    override var pushToken: String
        get() = mPreference.getString(PREFS_TOKEN, "") as String
        set(token) = mPreference.put(PREFS_TOKEN, token)

    override var appInfo: Repo?
        get() = Gson().fromJson(mPreference.getString(PREFS_APP_INFO, ""), Repo::class.java)
        set(user) {
            if (user == null) {
                mPreference.put(PREFS_APP_INFO, "")
                return
            }
            mPreference.put(PREFS_APP_INFO, Gson().toJson(user))
        }

    override var installReferer: String
        get() = mPreference.getString(PREFS_REFERER, "") as String
        set(token) = mPreference.put(PREFS_REFERER, token)

    override var videoAdsTime: Long
        get() = mPreference.getLong(PREFS_ADS_VIDEO_TIME, 0) as Long
        set(time) = mPreference.put(PREFS_ADS_VIDEO_TIME, time)

    override var firstRun: Boolean
        get() = mPreference.getBoolean(PREFS_FIRST_RUN, true) as Boolean
        set(run) = mPreference.put(PREFS_FIRST_RUN, run)

    companion object {
        private val PREFS_USER_INFO = "PREFS_USER_INFO"
        private val PREFS_APP_INFO = "PREFS_APP_INFO"
        private val PREFS_TOKEN = "PREFS_TOKEN"
        private val PREFS_REFERER = "PREFS_REFERER"
        private val PREFS_ADS_VIDEO_TIME = "PREFS_ADS_VIDEO_TIME"
        private val PREFS_FIRST_RUN = "PREFS_FIRST_RUN"

        fun getInstance(context: Context): PrefDataSourceImpl {
            return PrefDataSourceImpl(context)
        }
    }
}
