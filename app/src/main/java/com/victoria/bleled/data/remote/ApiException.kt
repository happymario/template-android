package com.victoria.bleled.data.remote

class ApiException(var code: Int, var msg: String?, var reason: String?) : Exception(
    msg
) {

    companion object {
        const val SUCCESS = 0 //성공
        const val ERR_NO_DATA = -1
    }
}