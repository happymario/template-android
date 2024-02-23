package com.mario.template.ui.main.latest

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.mario.template.Constants

class BackgroundWorker(appContext: Context, workerParameters: WorkerParameters) :
    Worker(appContext, workerParameters) {
    override fun doWork(): Result {
        Log.d("SimpleWorker", "doWork1");

//        val prefImple = DataRepository.provideDataRepository(applicationContext).prefDataSource
//
//        if (prefImple.isAutoLogin) {
//            Log.d("SimpleWorker", "doWork2");
//            prefImple.isAutoLogin = false
//            return Result.retry()
//        }

        // 이곳에 처리해야할 작업 코드를 추가합니다. lg에서만 동작하는것을 삭갈리지 말라. startActivity는 절대 안됨.
        val data = HashMap<String, String>()
        val title = inputData.getString(Constants.ARG_TYPE)
        if (title != null) {
            data.put("type", title)
        } else {
            data.put("type", "WorkManager")
        }
        data.put("body", "Click WorkManager")
//        val message = PushMessage(data)
//        MyFirebaseMessagingService.showNotification(applicationContext, message, null)

        return Result.success()
    }
}