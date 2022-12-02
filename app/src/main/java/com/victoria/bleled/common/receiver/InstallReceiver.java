package com.victoria.bleled.common.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.victoria.bleled.common.manager.PreferenceManager;

public class InstallReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        String rawReferrerString = intent.getStringExtra("referrer");
        if (rawReferrerString != null) {
            Log.i("MyApp", "Received the following intent " + rawReferrerString);
            PreferenceManager dataSource = PreferenceManager.getInstance(context);
            dataSource.setReferer(rawReferrerString);
        }
    }
}