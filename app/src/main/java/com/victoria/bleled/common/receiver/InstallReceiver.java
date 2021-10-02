package com.victoria.bleled.common.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.victoria.bleled.data.DataRepository;
import com.victoria.bleled.data.local.PrefDataSource;

public class InstallReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        String rawReferrerString = intent.getStringExtra("referrer");
        if (rawReferrerString != null) {
            Log.i("MyApp", "Received the following intent " + rawReferrerString);
            PrefDataSource dataSource = DataRepository.provideDataRepository(context).getPrefDataSource();
            dataSource.setReferer(rawReferrerString);
        }
    }
}