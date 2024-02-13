package com.mario.template.common.receiver

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.mario.template.helper.CommonHelper

class AlarmService : Service() {
    val NOTIFICATION_ID = 1111  // cannot be 0

    override fun onCreate() {
        super.onCreate()

        CommonHelper.showToast(this, "MyAlarmService.onCreate()")

        val data = HashMap<String, String>()
        data.put("type", "AlarmManager")
        data.put("body", "Click AlarmManager")
//        val message = PushMessage(data)
//        val notification = MyFirebaseMessagingService.getNotification(this, message, null)
//        startForeground(NOTIFICATION_ID, notification)
    }

    override fun onBind(intent: Intent?): IBinder? {
        CommonHelper.showToast(this, "MyAlarmService.onBind()")
        return null
    }

    override fun onDestroy() {
        CommonHelper.showToast(this, "MyAlarmService.onDestroy()")
        super.onDestroy()
        stopForeground(true)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        CommonHelper.showToast(this, "MyAlarmService.onStartCommand()")

        return START_STICKY
    }

    override fun onUnbind(intent: Intent?): Boolean {
        CommonHelper.showToast(this, "MyAlarmService.onUnbind()")
        return super.onUnbind(intent)
    }
}