package com.victoria.bleled.app.recent


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.BackoffPolicy
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.victoria.bleled.R
import com.victoria.bleled.common.Constants
import java.util.concurrent.TimeUnit

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        Log.d("AlarmReceiver", "onReceive");
        intent?.let {
            val title = context.getString(R.string.app_name)
            val message = "Please click this."
            val notificationData = Data.Builder()
                .putString(Constants.ARG_TYPE, title)
                .putString(Constants.ARG_DATA, message)
                .build()

            // WorkManager 사용
            val workRequest =
                OneTimeWorkRequestBuilder<SimpleWorker>()
                    .setInputData(notificationData)
                    .setBackoffCriteria(BackoffPolicy.LINEAR, 30000, TimeUnit.MILLISECONDS)
                    .build()

            val workManager = WorkManager.getInstance(context)
            workManager.enqueue(workRequest)
        }
    }
}