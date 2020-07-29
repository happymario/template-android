package com.victoria.bleled.common

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.victoria.bleled.app.MyApplication
import com.victoria.bleled.data.local.pref.PrefDataSourceImpl

class InstallReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val rawReferrerString = intent.getStringExtra("referrer")

        if (rawReferrerString != null) {
            val dataManager =
                PrefDataSourceImpl.getInstance(MyApplication.globalApplicationContext!!)
            dataManager.installReferer = rawReferrerString
        }
    }
}