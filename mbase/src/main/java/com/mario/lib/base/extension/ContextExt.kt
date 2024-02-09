package com.mario.lib.base.extension

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.widget.Toast
import kotlinx.coroutines.awaitCancellation

suspend fun Context.onReceive(
    filter: IntentFilter,
    block: (context: Context, intent: Intent) -> Unit,
): Nothing {
    val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            block(context, intent)
        }
    }
    registerReceiver(receiver, filter)

    try {
        awaitCancellation()
    } finally {
        unregisterReceiver(receiver)
    }
}

fun Context.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}