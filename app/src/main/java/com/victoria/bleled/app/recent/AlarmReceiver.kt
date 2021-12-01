package com.victoria.bleled.app.recent

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.victoria.bleled.service.fcm.MyFirebaseMessagingService
import com.victoria.bleled.service.fcm.PushMessage

class AlarmReceiver : BroadcastReceiver() {

    companion object {
        const val TAG = "AlarmReceiver"
        const val NOTIFICATION_ID = 0
    }

    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "Received intent : $intent")
        val data = HashMap<String, String>()
        data.put("type", "AlarmManager")
        data.put("body", "Click AlarmManager")
        val message = PushMessage(data)
        MyFirebaseMessagingService.showNotification(context, message, null)
    }
}