package com.victoria.bleled.common;


import com.victoria.bleled.BuildConfig;

public interface Constants {
    boolean IS_TEST = false;

    String MARKET = "google"; // apple,onestore,google

    String SDCARD_FOLDER = BuildConfig.APPLICATION_ID;
    int MIN_AGE = 18;
    int MAX_AGE = 39;

    /********************************************************************
     * INTENT NAMES
     ********************************************************************/
    String INTENT_USER = "INTENT_USER";
    String INTENT_PUSH_MSG = "INTENT_PUSH_MSG";
    String INTENT_TYPE = "INTENT_TYPE";
    String INTENT_UID = "INTENT_UID";
    String INTENT_CHAT_MSG = "INTENT_CHAT_MSG";
    String INTENT_DELETED = "INTENT_DELETED";
    String INTENT_CHANGED = "INTENT_CHANGED";

    /********************************************************************
     * BROADCAST NAMES
     ********************************************************************/
    String BROADCAST_SET_PUSH_TOKEN = "BROADCAST_EVENT_SET_PUSH_TOKEN";
    String BROADCAST_UPDATE_GPS = "BROADCAST_UPDATE_GPS";
    String BROADCAST_RECV_PUSHMSG = "BROADCAST_RECV_PUSHMSG";
    String BROADCAST_NETWORK_STATUS = "BROADCAST_NETWORK_STATUS";
    String BROADCAST_SMS_RECEIVED = "BROADCAST_SMS_RECEIVED";
    String BROADCAST_SIM_CHANGED = "BROADCAST_SIM_CHANGED";
    String BROADCAST_MYINFO_UPDATE = "BROADCAST_MYINFO_UPDATE";
}
