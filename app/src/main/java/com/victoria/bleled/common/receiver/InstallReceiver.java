package com.victoria.bleled.common.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.victoria.bleled.data.repository.DataStoreRepository;

import javax.inject.Inject;

public class InstallReceiver extends BroadcastReceiver {
    @Inject
    DataStoreRepository dataStoreRepository;

    public void onReceive(Context context, Intent intent) {
        String rawReferrerString = intent.getStringExtra("referrer");
        if (rawReferrerString != null) {
            Log.i("MyApp", "Received the following intent " + rawReferrerString);
            //dataStoreRepository.putString(DataStoreKey.PREFS_REFERER, rawReferrerString, GlobalScope.INSTANCE.);
        }
    }
}