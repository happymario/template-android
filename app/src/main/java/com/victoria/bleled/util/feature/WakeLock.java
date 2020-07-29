package com.victoria.bleled.util.feature;

import android.content.Context;
import android.os.PowerManager;
import android.util.Log;


public class WakeLock {

    private static PowerManager.WakeLock sCpuWakeLock;

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
        sCpuWakeLock.acquire();
    }

    public static void releaseCpuLock() {
        Log.e("PushWakeLock", "Releasing cpu wake lock");

        if (sCpuWakeLock != null) {
            sCpuWakeLock.release();
            sCpuWakeLock = null;
        }
    }
}
