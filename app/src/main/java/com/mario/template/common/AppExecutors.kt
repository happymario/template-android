package com.mario.template.common

import android.os.Handler
import android.os.Looper
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class AppExecutors {
    val diskIO: Executor
    val networkIO: Executor
    val mainThread: Executor

    init {
        diskIO = Executors.newSingleThreadExecutor()
        networkIO = Executors.newFixedThreadPool(3)
        mainThread = MainThreadExecutor()
    }

    private inner class MainThreadExecutor : Executor {
        private val mainThreadHandler = Handler(Looper.getMainLooper())
        override fun execute(command: Runnable) {
            mainThreadHandler.post(command)
        }
    }
}
