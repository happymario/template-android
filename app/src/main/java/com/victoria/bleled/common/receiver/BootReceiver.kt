package com.victoria.bleled.common.receiver

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

class BootReceiver : BroadcastReceiver() {

    companion object {
        const val TAG = "BootReceiver"
    }

    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "Received intent : $intent")

        // lock boot receiver: ACTION_LOCKED_BOOT_COMPLETED
        // network change receiver: android.net.conn.CONNECTIVITY_CHANGE
        // sms receiver: android.provider.Telephony.SMS_RECEIVED

        if (intent.action == "android.intent.action.BOOT_COMPLETED") {

            val intent = Intent(context, AlarmReceiver::class.java)
            val pendingIntent =
                PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
            val alarmManager =
                context.getSystemService(AppCompatActivity.ALARM_SERVICE) as AlarmManager
            val repeatInterval: Long = 60 * 1000
            val triggerTime = (SystemClock.elapsedRealtime()
                    + repeatInterval)
            alarmManager.setInexactRepeating(
                AlarmManager.ELAPSED_REALTIME_WAKEUP,
                triggerTime, repeatInterval,
                pendingIntent
            )
        }
    }
}