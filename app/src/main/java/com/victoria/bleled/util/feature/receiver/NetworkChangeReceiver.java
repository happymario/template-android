package com.victoria.bleled.util.feature.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.victoria.bleled.common.Constants;

/**
 * Created by HappyMario on 2/19/2018.
 */

public class NetworkChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {

        int status = getConnectivityStatus(context);

        if (!"android.net.conn.CONNECTIVITY_CHANGE".equals(intent.getAction())) {
            Intent retIntent = new Intent(Constants.BROADCAST_NETWORK_STATUS);
            retIntent.putExtra(Constants.INTENT_TYPE, new Integer(status));
            LocalBroadcastManager.getInstance(context).sendBroadcast(retIntent);
        }
    }

    public static int getConnectivityStatus(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (cm == null) {
            return -1;
        }

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (null != activeNetwork) {
            return activeNetwork.getType();
        }
        return -1;
    }
}