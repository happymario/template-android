package com.mario.template.helper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.PowerManager;
import android.util.Log;


public class WakeLockHelper {

    private static PowerManager.WakeLock sCpuWakeLock;

    @SuppressLint("InvalidWakeLockTag")
    @SuppressWarnings("deprecation")
    public static void acquireCpuWakeLock(Context context) {
        Log.e("PushWakeLock", "Acquiring cpu wake lock");
        if (sCpuWakeLock != null) {
            return;
        }

        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);

        sCpuWakeLock = pm.newWakeLock(
                PowerManager.SCREEN_BRIGHT_WAKE_LOCK |
                        PowerManager.ACQUIRE_CAUSES_WAKEUP |
                        PowerManager.ON_AFTER_RELEASE, "PushWakeLock");
        sCpuWakeLock.acquire(10*60*1000L /*10 minutes*/);
    }

    public static void releaseCpuLock() {
        Log.e("PushWakeLock", "Releasing cpu wake lock");

        if (sCpuWakeLock != null) {
            sCpuWakeLock.release();
            sCpuWakeLock = null;
        }
    }
}
