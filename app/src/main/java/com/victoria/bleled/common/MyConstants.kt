package com.victoria.bleled.common

import com.victoria.bleled.BuildConfig


object MyConstants {
    val SDCARD_FOLDER = BuildConfig.APPLICATION_ID
    val FCM_TOPIC = "/topics/new"

    /********************************************************************
     * INTENT NAMES
     *********************************************************************/
    val INTENT_FROM = "INTENT_FROM"
    val INTENT_PUSH_MSG = "INTENT_PUSH_MSG"
    val INTENT_TYPE = "INTENT_TYPE"
    val INTENT_USER = "INTENT_USER"

    /********************************************************************
     * BROADCAST NAMES
     *********************************************************************/
    val BROADCAST_SET_PUSH_TOKEN = "BROADCAST_EVENT_SET_PUSH_TOKEN"
}