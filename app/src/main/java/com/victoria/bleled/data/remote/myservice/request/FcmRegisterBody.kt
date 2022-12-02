package com.victoria.bleled.data.remote.myservice.request

data class FcmRegisterBody(
    var token: String,
    var osType: String = "android"
) {
}