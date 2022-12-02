package com.victoria.bleled.data.net.mytemplate.request

data class FcmRegisterBody(
    var token: String,
    var osType: String = "android",
) {
}