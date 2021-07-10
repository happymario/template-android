package com.victoria.bleled.app

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import com.facebook.stetho.Stetho


class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        instance = this
        Stetho.initializeWithDefaults(this)
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(base)
    }

    fun exitApp() {
        android.os.Process.killProcess(android.os.Process.myPid())
        System.exit(0)
    }

    companion object {
        private var instance: MyApplication? = null

        /**
         * singleton 애플리케이션 객체를 얻는다.
         *
         * @return singleton 애플리케이션 객체
         */
        val globalApplicationContext: MyApplication?
            get() {
                return instance
            }
    }

}
