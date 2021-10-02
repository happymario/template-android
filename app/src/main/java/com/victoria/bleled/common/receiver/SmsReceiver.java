package com.victoria.bleled.common.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.victoria.bleled.common.Constants;

import timber.log.Timber;

/**
 * Created by HappyMario on 1/12/2018.
 */

public class SmsReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
            Bundle bundle = intent.getExtras();           //---get the SMS message passed in---
            SmsMessage[] msgs = null;
            String msg_from;
            if (bundle != null) {
                //---retrieve the SMS message received---
                try {
                    Object[] pdus = (Object[]) bundle.get("pdus");
                    msgs = new SmsMessage[pdus.length];
                    for (int i = 0; i < msgs.length; i++) {
                        msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                        String authNum = msgs[i].getMessageBody().replaceAll("[^0-9]", "");

                        Intent retIntent = new Intent(Constants.BROADCAST_SMS_RECEIVED);
                        retIntent.putExtra(Constants.ARG_TYPE, authNum);
                        LocalBroadcastManager.getInstance(context).sendBroadcast(retIntent);
                    }
                } catch (Exception e) {
                    Timber.e("onReceive - Exception : " + e.getMessage());
                }
            }
        }
    }
}