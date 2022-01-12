package com.victoria.bleled.app.recent

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.victoria.bleled.service.fcm.MyFirebaseMessagingService
import com.victoria.bleled.service.fcm.PushMessage
import com.victoria.bleled.util.CommonUtil

class MyAlarmService : Service() {
    val NOTIFICATION_ID = 1111  // cannot be 0

    override fun onCreate() {
        super.onCreate()

        CommonUtil.showToast(this, "MyAlarmService.onCreate()")

        val data = HashMap<String, String>()
        data.put("type", "AlarmManager")
        data.put("body", "Click AlarmManager")
        val message = PushMessage(data)
        val notification = MyFirebaseMessagingService.getNotification(this, message, null)
        startForeground(NOTIFICATION_ID, notification)
    }

    override fun onBind(intent: Intent?): IBinder? {
        CommonUtil.showToast(this, "MyAlarmService.onBind()")
        return null
    }

    override fun onDestroy() {
        CommonUtil.showToast(this, "MyAlarmService.onDestroy()")
        super.onDestroy()
        stopForeground(true)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        CommonUtil.showToast(this, "MyAlarmService.onStartCommand()")

        return START_STICKY
    }

    override fun onUnbind(intent: Intent?): Boolean {
        CommonUtil.showToast(this, "MyAlarmService.onUnbind()")
        return super.onUnbind(intent)
    }
}