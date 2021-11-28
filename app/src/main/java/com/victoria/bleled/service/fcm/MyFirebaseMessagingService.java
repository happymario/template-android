package com.victoria.bleled.service.fcm;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.victoria.bleled.R;
import com.victoria.bleled.app.SplashActivity;

import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);

        Log.d(TAG, s);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        processMessage(this, remoteMessage.getData());
    }

    private void processMessage(Context context, Map<String, String> message) {
        PushMessage pushMessage = new PushMessage(message);
//        PushMessage.EPushType ePushType = PushMessage.EPushType.toEnum(pushMessage.getType());
//
//        if (ePushType == PushMessage.EPushType.none) {
//            return;
//        }
//
//        String image_url = message.get("img_url");
//        if (image_url != null && image_url.isEmpty() == false) {
//            new sendNotification(context, pushMessage).execute(image_url);
//        } else {
        showNotification(context, pushMessage, null);
//        }
    }

    public static void showNotification(Context context, PushMessage pushMessage, Bitmap bitmap) {
        Intent intent = new Intent(context, SplashActivity.class);
        //Intent intent = new Intent(this, SplashActivity.class);
        //intent.putExtra(PARAM_PUSH_MSG, pushMessage);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0 /* Request code */, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(pushMessage.getDisplayTitle(context))
                .setContentText(pushMessage.getDisplayMsg(context))
                .setAutoCancel(true)
                .setLights(000000255, 500, 2000)
                .setContentIntent(pendingIntent);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notificationBuilder.setColor(context.getResources().getColor(R.color.black));
        }

        if (bitmap != null) {
            NotificationCompat.BigPictureStyle style = new NotificationCompat.BigPictureStyle();
            style.bigPicture(bitmap);
            notificationBuilder.setStyle(style);
        }

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String allChannelId = "알림";
            NotificationChannel channel_all = new NotificationChannel(allChannelId, allChannelId, NotificationManager.IMPORTANCE_HIGH);
            channel_all.enableVibration(true);
            notificationManager.createNotificationChannel(channel_all);
            notificationBuilder.setChannelId(allChannelId);
        } else {
            //Sound and Vibrate
            notificationBuilder.setDefaults(Notification.DEFAULT_ALL);
            notificationBuilder.setSound(defaultSoundUri);
        }

        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                "MyApp::MyWakelockTag");
        wakeLock.acquire(5000);

        int id = 0;
        notificationManager.notify(id /* ID of notification */, notificationBuilder.build());
    }
}
