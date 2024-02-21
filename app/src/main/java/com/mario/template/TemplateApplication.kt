package com.mario.template

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.multidex.MultiDex
import com.mario.lib.base.util.LocaleUtil
import com.mario.template.ui.main.MainActivity
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import java.io.PrintWriter
import java.io.StringWriter
import java.io.Writer
import java.util.Locale


enum class AppStatus(private var value: Int) {
    BACKGROUND(0),                  // app is background
    RETURNED_TO_FOREGROUND(1),      // app returned to foreground(or first launch)
    FOREGROUND(2),                  // app is foreground
    EXITED(3);                      // app is exited
}


@HiltAndroidApp
class TemplateApplication : Application() {

    /************************************************************
     *  UI controls & Data members
     ************************************************************/
    private var allActivities: HashSet<Activity> = HashSet()
    private var curActivity: Activity? = null
    private var appStatus: AppStatus = AppStatus.BACKGROUND

    private var uncaughtExceptionHandler: Thread.UncaughtExceptionHandler? = null


    /************************************************************
     *  Overrides
     ************************************************************/
    override fun onCreate() {
        super.onCreate()

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        registerActivityLifecycleCallbacks(MyActivityLifecycleCallbacks(this))
        uncaughtExceptionHandler = UncaughtExceptionHandlerApplication(this)
        Thread.setDefaultUncaughtExceptionHandler(uncaughtExceptionHandler)
    }

    override fun attachBaseContext(base: Context) {
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

    class MyActivityLifecycleCallbacks constructor(private var application: TemplateApplication) :
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

    internal class UncaughtExceptionHandlerApplication constructor(private var application: TemplateApplication) :
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
            Timber.tag("uncaughtException").e(getStackTrace(ex))
        }
    }
}