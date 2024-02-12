package com.mario.template

object Constants {
    const val BASE_URL = "http://192.168.0.22:8101/"
    const val CLIENT_PHONE_NUMBER = "191-245-3575"

    val URL_ABOUT: String = BASE_URL + "/admin/login/term?term_kind=about"
    val URL_USE_AGREE: String = BASE_URL + "/admin/login/term?term_kind=use"

    /********************************************************************
     * INTENT NAMES
     *******************************************************************/
    const val ARG_TYPE = "type"
    const val ARG_DATA = "data"
    const val ARG_WIDGET = "widget"

    /********************************************************************
     * BROADCAST NAMES
     *******************************************************************/
    const val BROADCAST_SET_PUSH_TOKEN = "BROADCAST_EVENT_SET_PUSH_TOKEN"
    const val BROADCAST_UPDATE_GPS = "BROADCAST_UPDATE_GPS"
    const val BROADCAST_RECV_PUSHMSG = "BROADCAST_RECV_PUSHMSG"
    const val BROADCAST_SMS_RECEIVED = "BROADCAST_SMS_RECEIVED"
    const val BROADCAST_MYINFO_UPDATE = "BROADCAST_MYINFO_UPDATE"

    object DateFormat {
        const val HH_mm = "HH:mm"
        const val EE_MM_dd = "EEEE, MMM dd"
    }

    object Key {
        const val LAT_LNG = "lat_lng"
        const val LAT = "lat"
        const val LNG = "lng"
        const val FROM_ROUTE = "from_route"
        const val ADDRESS_NAME = "address_name"
    }
}
