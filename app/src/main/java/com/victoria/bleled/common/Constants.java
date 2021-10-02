package com.victoria.bleled.common;

public interface Constants {
    boolean IS_TEST = false;

    String MARKET = "google"; // onestore, google
    String CLIENT_PHONE_NUMBER = "191-245-3575";

    /********************************************************************
     * INTENT NAMES
     ********************************************************************/
    String ARG_TYPE = "type";
    String ARG_DATA = "data";
    String ARG_WIDGET = "widget";

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
