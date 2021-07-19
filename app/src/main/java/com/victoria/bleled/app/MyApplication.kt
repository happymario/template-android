package com.victoria.bleled.app

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import androidx.multidex.MultiDex
import com.facebook.stetho.Stetho
import com.victoria.bleled.app.main.MainActivity
import com.victoria.bleled.util.feature.logger.MyDiskLogAdapter
import java.io.PrintWriter
import java.io.StringWriter
import java.io.Writer
import java.util.*
import kotlin.collections.HashSet


class MyApplication : Application() {

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

    private var allActivities:HashSet<Activity> = HashSet()
    private var curActivity:Activity? = null
    private var appStatus:AppStatus = AppStatus.BACKGROUND
    private var uncaughtExceptionHandler: Thread.UncaughtExceptionHandler? = null
    private lateinit var diskLogAdapter: MyDiskLogAdapter

    override fun onCreate() {
        super.onCreate()

        instance = this

        Stetho.initializeWithDefaults(this)
        diskLogAdapter = MyDiskLogAdapter(this)
        registerActivityLifecycleCallbacks(MyActivityLifecycleCallbacks(this))
        uncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler(uncaughtExceptionHandler)
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(base)
    }


    fun addActivity(act: Activity) {
        if (allActivities == null) {
            allActivities = HashSet()
        }
        allActivities.add(act)
    }

    fun removeActivity(act: Activity) {
        if (allActivities != null) {
            allActivities.remove(act)
        }
    }

    fun getCurActivity(): Activity? {
        return curActivity
    }

    fun isForeground(): Boolean {
        return if (appStatus.ordinal > AppStatus.BACKGROUND.ordinal) {
            curActivity != null
        } else false
    }

    fun finishAllActivityWithoutMain() {
        if (allActivities != null) {
            synchronized(allActivities) {
                for (act in allActivities) {
                    if (!(act is MainActivity)) {
                        act.finish()
                    }
                }
            }
        }
    }

    fun exitApp() {
        android.os.Process.killProcess(android.os.Process.myPid())
        System.exit(0)
    }

    fun startLogger() {
        com.orhanobut.logger.Logger.addLogAdapter(diskLogAdapter)
    }

    fun stopLogger() {
        com.orhanobut.logger.Logger.clearLogAdapters()
    }

    fun cleanLoggerHistory() {
        if (diskLogAdapter != null) {
            diskLogAdapter.clearData()
        }
        stopLogger()
        startLogger()
    }


    class MyActivityLifecycleCallbacks constructor(private var application: MyApplication) : ActivityLifecycleCallbacks {
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

    internal class UncaughtExceptionHandlerApplication constructor(private var application: MyApplication) : Thread.UncaughtExceptionHandler {
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
            Log.e("uncaughtException", getStackTrace(ex))

            //new CrashLogCall().execute(getStackTrace(ex));
//            BaseViewModel viewModel = new BaseViewModel();
//            viewModel.initContext(MyApplication.this);
//            viewModel.crashLog(getStackTrace(ex));
//
            application.uncaughtExceptionHandler?.uncaughtException(thread, ex)
        }
    }

    class CrashLogCall constructor(private var application: MyApplication) :
        AsyncTask<String?, Void?, String?>() {
        private var exception: Exception? = null
        override fun doInBackground(vararg params: String?): String? {
            var response = ""

            try {
                //                    URL url = new URL(serverUrl);
//                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//                    conn.setReadTimeout(10000);
//                    conn.setConnectTimeout(15000);
//                    conn.setRequestMethod("POST");
//                    conn.setDoInput(true);
//                    conn.setDoOutput(true);
//
//                    HashMap<String, String> params = new HashMap<>();
//                    String userUId = (IMeetingHttpService.APP_ID + Util.getDeviceId(context));
//                    params.put("type", "crash");
//                    params.put("id", userUId);
//                    params.put("android_version", Util.getOSVersion(context));
//                    params.put("account_email", Util.getGmailAccount(context));
//                    params.put("appid", IMeetingHttpService.APP_ID);
//                    params.put("login_mode", IMeetingHttpService.LOGIN_MODE);
//                    params.put("app_version", Util.getVersionName(context));
//                    params.put("lang", IMeetingHttpService.LANG);
//                    params.put("hp_model", Util.getDeviceName());
//                    params.put("log", urls[0]);
//
//                    String encrypted = RetrofitHelper.encryptParamsForApach(params);
//                    String strParams = "r=" + encrypted + "&XDEBUG_SESSION_START=Star_Man";
//                    OutputStream os = conn.getOutputStream();
//                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
//                    writer.write(strParams);
//                    writer.flush();
//                    writer.close();
//                    os.close();
//
//                    conn.connect();
//
//                    int responseCode = conn.getResponseCode();
//
//                    if (responseCode == HttpsURLConnection.HTTP_OK) {
//                        String line;
//                        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//                        while ((line = br.readLine()) != null) {
//                            response += line;
//                        }
//                    } else {
//                        response = "";
//                    }
            }
            catch (e:java.lang.Exception) {
                e.printStackTrace()
            }


            return  response
        }

        override fun onPostExecute(feed: String?) {
            // TODO: check this.exception
            // TODO: do something with the feed
        }
    }
}

enum class AppStatus(private var value: Int) {
    BACKGROUND(0),                // app is background
    RETURNED_TO_FOREGROUND(1),    // app returned to foreground(or first launch)
    FOREGROUND(2),                 // app is foreground
    EXITED(3);     // app is exited
}
