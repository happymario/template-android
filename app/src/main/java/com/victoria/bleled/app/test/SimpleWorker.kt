package com.victoria.bleled.app.test

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.victoria.bleled.data.DataRepository


class SimpleWorker(appContext: Context, workerParameters: WorkerParameters) :
    Worker(appContext, workerParameters) {
    override fun doWork(): Result {
        Log.d("SimpleWorker", "doWork1");

        val prefImple = DataRepository.provideDataRepository().prefDataSource

        if (prefImple.isAutoLogin) {
            Log.d("SimpleWorker", "doWork2");
            prefImple.isAutoLogin = false
            return Result.retry()
        }

        Log.d("SimpleWorker", "doWork3");

        // 이곳에 처리해야할 작업 코드를 추가합니다. lg에서만 동작하는것을 삭갈리지 말라. startActivity는 절대 안됨.
//        AppExecutors().networkIO().execute {
//            val intent = Intent(applicationContext, EtcActivity::class.java).apply {
//                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//            }
//            applicationContext.startActivity(intent)
//        }

        return Result.success()
    }
}