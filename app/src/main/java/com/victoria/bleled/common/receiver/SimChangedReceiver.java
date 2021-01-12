package com.victoria.bleled.common.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.victoria.bleled.common.Constants;

import timber.log.Timber;

public class SimChangedReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Timber.d("SimChangedReceiver - SIM state changed");
        Intent retIntent = new Intent(Constants.BROADCAST_SIM_CHANGED);
        LocalBroadcastManager.getInstance(context).sendBroadcast(retIntent);
    }

}