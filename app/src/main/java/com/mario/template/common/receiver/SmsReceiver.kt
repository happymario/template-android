package com.mario.template.common.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.SmsMessage
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.mario.template.Constants
import timber.log.Timber

/**
 * Created by HappyMario on 1/12/2018.
 */
class SmsReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "android.provider.Telephony.SMS_RECEIVED") {
            val bundle = intent.extras //---get the SMS message passed in---
            var msgs: Array<SmsMessage?>? = null
            var msg_from: String
            if (bundle != null) {
                //---retrieve the SMS message received---
                try {
                    val pdus = bundle["pdus"] as Array<Any>?
                    msgs = arrayOfNulls(pdus!!.size)
                    for (i in msgs.indices) {
                        msgs[i] = SmsMessage.createFromPdu(pdus[i] as ByteArray)
                        val authNum = msgs[i]!!.getMessageBody().replace("[^0-9]".toRegex(), "")
                        val retIntent = Intent(Constants.BROADCAST_SMS_RECEIVED)
                        retIntent.putExtra(Constants.ARG_TYPE, authNum)
                        LocalBroadcastManager.getInstance(context).sendBroadcast(retIntent)
                    }
                } catch (e: Exception) {
                    Timber.e("onReceive - Exception : " + e.message)
                }
            }
        }
    }
}