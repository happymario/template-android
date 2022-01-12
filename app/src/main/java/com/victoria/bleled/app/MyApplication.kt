package com.victoria.bleled.app

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.multidex.MultiDex
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.facebook.stetho.Stetho
import com.orhanobut.logger.Logger
import com.victoria.bleled.app.main.MainActivity
import com.victoria.bleled.util.feature.LocaleUtil
import com.victoria.bleled.util.feature.logger.MyDiskLogAdapter
import java.io.PrintWriter
import java.io.StringWriter
import java.io.Writer
import java.util.*
import kotlin.collections.HashSet


class MyApplication : Application() {
    /************************************************************
     *  Static & Global Members
     ************************************************************/
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

    /************************************************************
     *  UI controls & Data members
     ************************************************************/
    private var allActivities: HashSet<Activity> = HashSet()
    private var curActivity: Activity? = null
    private var appStatus: AppStatus = AppStatus.BACKGROUND

    private var uncaughtExceptionHandler: Thread.UncaughtExceptionHandler? = null
    private lateinit var diskLogAdapter: MyDiskLogAdapter


    /************************************************************
     *  Overrides
     ************************************************************/
    override fun onCreate() {
        super.onCreate()

        instance = this

        Stetho.initializeWithDefaults(this)
        diskLogAdapter = MyDiskLogAdapter(this)
        registerActivityLifecycleCallbacks(MyActivityLifecycleCallbacks(this))
        uncaughtExceptionHandler = UncaughtExceptionHandlerApplication(this)
        Thread.setDefaultUncaughtExceptionHandler(uncaughtExceptionHandler)
    }

    override fun attachBaseContext(base: Context?) {
        var languageCode = Locale.getDefault().language
//        if (base != null) {
//            val preference = PrefDataSource.getInstance(base)
//            val setting = preference.setting
//            if (setting.lang.isNotEmpty()) {
//                languageCode = setting.lang
//            }
//        }
        super.attachBaseContext(LocaleUtil.setLocale(base, languageCode))
        MultiDex.install(base)
    }


    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        var languageCode = Locale.getDefault().language
//        if (base != null) {
//            val preference = PrefDataSource.getInstance(base)
//            val setting = preference.setting
//            if (setting.lang.isNotEmpty()) {
//                languageCode = setting.lang
//            }
//        }
        LocaleUtil.setLocale(this, languageCode)
    }


    /************************************************************
     *  public functions
     ************************************************************/
    fun getAppDir(): String {
        val dir = filesDir.absolutePath.substring(
            0,
            filesDir.absolutePath.indexOf("/files")
        )
        return dir
    }

    fun addActivity(act: Activity) {
        allActivities.add(act)
    }

    fun removeActivity(act: Activity) {
        allActivities.remove(act)
    }

    fun isForeground(): Boolean {
        return if (appStatus.ordinal > AppStatus.BACKGROUND.ordinal) {
            curActivity != null
        } else false
    }

    fun finishAllActivityWithoutMain() {
        synchronized(allActivities) {
            for (act in allActivities) {
                if (act !is MainActivity) {
                    act.finish()
                }
            }
        }
    }

    fun restartApp() {
        val i = Intent(this, MainActivity::class.java)
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(i)
    }

    fun exitApp() {
        android.os.Process.killProcess(android.os.Process.myPid())
        System.exit(0)
    }

    fun startLogger() {
        Logger.addLogAdapter(diskLogAdapter)
    }

    fun stopLogger() {
        Logger.clearLogAdapters()
    }

    fun cleanLoggerHistory() {
        diskLogAdapter.clearData()
        stopLogger()
        startLogger()
    }

    class MyActivityLifecycleCallbacks constructor(private var application: MyApplication) :
        ActivityLifecycleCallbacks {
        // running activity count
        private var running = 0
        override fun onActivityCreated(activity: Activity, bundle: Bundle?) {
            application.addActivity(activity)
        }

        override fun onActivityStarted(activity: Activity) {
            if (++running == 1) {
                // running activity is 1,
                // app must be returned from background just now (or first launch)
                application.appStatus = AppStatus.RETURNED_TO_FOREGROUND
            }
        }

        override fun onActivityResumed(activity: Activity) {
            application.curActivity = activity
            if (running >= 1) {
                // 2 or more running activities,
                // should be foreground already.
                application.appStatus = AppStatus.FOREGROUND
            }
        }

        override fun onActivityPaused(activity: Activity) {}
        override fun onActivityStopped(activity: Activity) {
            if (--running == 0) {
                // no active activity
                // app goes to background
                application.appStatus = AppStatus.BACKGROUND
            }
        }

        override fun onActivitySaveInstanceState(activity: Activity, bundle: Bundle) {}
        override fun onActivityDestroyed(activity: Activity) {
            if (activity.javaClass == MainActivity::class.java) {
                application.appStatus = AppStatus.EXITED
            }
            application.removeActivity(activity)
        }
    }

    internal class UncaughtExceptionHandlerApplication constructor(private var application: MyApplication) :
        Thread.UncaughtExceptionHandler {
        /**
         * Exception 로그 메시지
         *
         * @param th
         * @return
         */
        private fun getStackTrace(th: Throwable): String {
            val result: Writer = StringWriter()
            val printWriter = PrintWriter(result)
            var cause: Throwable? = th
            while (cause != null) {
                cause.printStackTrace(printWriter)
                cause = cause.cause
            }
            val stacktraceAsString = result.toString()
            printWriter.close()
            return stacktraceAsString
        }

        override fun uncaughtException(thread: Thread, ex: Throwable) {
            Logger.e("uncaughtException", getStackTrace(ex))

            val url = "https://crashlog.com/add_crash"

            val headers: MutableMap<String, String> = HashMap()
            headers["Content-Type"] = "application/json"

            val parameters: MutableList<String> = ArrayList()
            parameters.add("log=" + getStackTrace(ex))
            val parametersList = StringBuffer()
            for (i in parameters.indices) {
                parametersList.append((if (i > 0) "&" else "") + parameters[i])
            }
            val queue = Volley.newRequestQueue(application)
            // Request a string response from the provided URL.
            val stringRequest = StringRequest(
                Request.Method.GET, url,
                Response.Listener<String> { response ->
                    // Display the first 500 characters of the response string.
                    Log.e("CrashLog", "Response is: ${response.substring(0, 500)}")
                },
                Response.ErrorListener {
                    Log.e("CrashLog",  "That didn't work!")
                })

            // Add the request to the RequestQueue.
            queue.add(stringRequest)
            application.uncaughtExceptionHandler?.uncaughtException(thread, ex)
        }
    }
}

enum class AppStatus(private var value: Int) {
    BACKGROUND(0),                // app is background
    RETURNED_TO_FOREGROUND(1),    // app returned to foreground(or first launch)
    FOREGROUND(2),                 // app is foreground
    EXITED(3);     // app is exited
}
