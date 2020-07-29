package com.victoria.bleled.service.fcm

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.os.Build
import android.os.PowerManager
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.google.firebase.messaging.RemoteMessage
import com.victoria.bleled.R
import com.victoria.bleled.app.MyApplication
import com.victoria.bleled.app.etc.SimpleWorker
import com.victoria.bleled.app.SplashActivity
import com.victoria.bleled.common.MyConstants
import com.victoria.bleled.data.local.pref.PrefDataSourceImpl
import com.victoria.bleled.util.CommonUtil
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

/*
    https://fcm.googleapis.com/fcm/send
    POST
    Headers
    key=AAAAEnAgO90:APA91bEmu0xqW48JZUIxfA-F01nTF1_DCfXMOqSVRlSVbGAyTe16blYQvglt-rTbm0t4v_oRy97SJKAASbJzA0mx5-NdNu1os88E7gXPbtqwJQXVHcN9MDYpSGa8qn_oSsgtqiv-KfBO
    raw
    {
        "to":"e0VcTMR6Oec:APA91bG5wHKwnjjvOlDW5nyRyHT5LCVZq-LUeO30nfrovYR0pLT93KamdCRIxXqwEXVkQreS9ganUv-jX5-oLiO4R3wPkxUj6eGnuKbkmNpjSKOhjcXJVODO_KVWe-iZ8XW5ac7SDC45",
        "priority": "high",
        "collapse_key": "type_a",
        "data": {
             "type": "chat_msg",
             "content": "안녕하세요.",
             "calluserid": "admin"
        }
    }
    * */
class FirebaseMessagingService : com.google.firebase.messaging.FirebaseMessagingService() {
    override fun onNewToken(s: String) {
        super.onNewToken(s)

        // Get updated InstanceID token.

        val retIntent = Intent(MyConstants.BROADCAST_SET_PUSH_TOKEN)
        val dataManager = PrefDataSourceImpl.getInstance(this)
        dataManager.pushToken = s
        LocalBroadcastManager.getInstance(this).sendBroadcast(retIntent)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        if (remoteMessage.data == null) {
            return
        }

        sendPushNotification(remoteMessage.data)
    }

    private fun sendPushNotification(message: Map<String, String>) {
        val application = application as MyApplication
        val pushMessage = PushMessage(message)


        val intent = Intent(this, SplashActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        intent.putExtra(MyConstants.INTENT_PUSH_MSG, pushMessage)


        val pendingIntent = PendingIntent.getActivity(
            this,
            0 /* Request code */,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notificationBuilder = NotificationCompat.Builder(this)
            .setSmallIcon(R.mipmap.ic_launcher)
            //.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_icon))
            .setContentTitle(getString(R.string.app_name))
            .setContentText(pushMessage.content)
            .setAutoCancel(true)
            .setLights(173, 500, 2000)
            .setContentIntent(pendingIntent)

        //        if (pushMessage.img_url != null && pushMessage.img_url.isEmpty() == false) {
        //            Bitmap bitmap = getBitmapFromURL(pushMessage.img_url);
        //            notificationBuilder.setLargeIcon(bitmap);
        //        }

        var sound = true
        var vibrate = true

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val allChannelId = "hz_notification_all"
            val soundChannelId = "hz_notification_sound"
            val vibrateChannelId = "hz_notification_vibrate"
            val noneChannelId = "hz_notification_none"


            val channel_all =
                NotificationChannel(allChannelId, allChannelId, NotificationManager.IMPORTANCE_HIGH)
            channel_all.enableVibration(true)
            notificationManager.createNotificationChannel(channel_all)

            val channel_sound = NotificationChannel(
                soundChannelId,
                soundChannelId,
                NotificationManager.IMPORTANCE_HIGH
            )
            channel_sound.enableVibration(false)
            notificationManager.createNotificationChannel(channel_sound)

            val channel_vibrate = NotificationChannel(
                vibrateChannelId,
                vibrateChannelId,
                NotificationManager.IMPORTANCE_HIGH
            )
            channel_vibrate.setSound(null, null)
            channel_vibrate.enableVibration(true)
            notificationManager.createNotificationChannel(channel_vibrate)

            val channel_none = NotificationChannel(
                noneChannelId,
                noneChannelId,
                NotificationManager.IMPORTANCE_HIGH
            )
            channel_none.setSound(null, null)
            channel_none.enableVibration(false)
            notificationManager.createNotificationChannel(channel_none)

            var channelId = allChannelId
            if (sound && vibrate) {
                channelId = allChannelId
            } else if (sound && !vibrate) {
                channelId = soundChannelId
            } else if (!sound && vibrate) {
                channelId = vibrateChannelId
            } else if (!sound && !vibrate) {
                channelId = noneChannelId
            }
            notificationBuilder.setChannelId(channelId)
        } else {
            if (sound && vibrate) {
                //Sound and Vibrate
                notificationBuilder.setDefaults(Notification.DEFAULT_ALL)

                notificationBuilder.setSound(defaultSoundUri)
                CommonUtil.vibrate(applicationContext, 500)
            } else if (sound && !vibrate) {
                //Sound
                notificationBuilder.setDefaults(Notification.DEFAULT_SOUND)
                notificationBuilder.setSound(defaultSoundUri)
            } else if (!sound && vibrate) {
                //Vibrate
                notificationBuilder.setDefaults(Notification.DEFAULT_VIBRATE)
                CommonUtil.vibrate(applicationContext, 500)
            } else if (!sound && !vibrate) {
                //None
                //Do nothing! just notification with no sound or vibration
            }
        }

        val pm = this.getSystemService(Context.POWER_SERVICE) as PowerManager

        val wakelock =
            pm.newWakeLock(PowerManager.FULL_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP, "TAG")
        wakelock.acquire(5000)

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build())

        //        String strPushMsg = new Gson().toJson(pushMessage, PushMessage.class);
        //        preferences.put(MyConstants.INTENT_PUSH_MSG, strPushMsg);
        startMain();
    }

    fun startMain() {
        // 1분이상부터는 제대로 동작함... 왜냐면 workmanager는 시스템상태에 우선도를 부여하기때문이다.
        val workRequest = OneTimeWorkRequestBuilder<SimpleWorker>().build()
        val workManager = WorkManager.getInstance(this)
        workManager.cancelAllWork()

        val prefImple = PrefDataSourceImpl(applicationContext)
        prefImple.firstRun = false

        workManager?.enqueue(workRequest)
    }


    fun getBitmapFromURL(strURL: String): Bitmap? {

        try {
            val url = URL(strURL)
            val connection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val input = connection.inputStream

            return BitmapFactory.decodeStream(input)
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }

    }

    companion object {

        private val TAG = "FirebaseMsgService"

        fun setBadge(context: Context, count: Int) {
            val launcherClassName = getLauncherClassName(context) ?: return
            val intent = Intent("android.intent.action.BADGE_COUNT_UPDATE")
            intent.putExtra("badge_count", count)
            intent.putExtra("badge_count_package_name", context.packageName)
            intent.putExtra("badge_count_class_name", launcherClassName)
            context.sendBroadcast(intent)
        }

        fun getLauncherClassName(context: Context): String? {

            val pm = context.packageManager

            val intent = Intent(Intent.ACTION_MAIN)
            intent.addCategory(Intent.CATEGORY_LAUNCHER)

            val resolveInfos = pm.queryIntentActivities(intent, 0)
            for (resolveInfo in resolveInfos) {
                val pkgName = resolveInfo.activityInfo.applicationInfo.packageName
                if (pkgName.equals(context.packageName, ignoreCase = true)) {
                    return resolveInfo.activityInfo.name
                }
            }
            return null
        }
    }

}
