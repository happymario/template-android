package com.victoria.bleled.data.net.mytemplate.response

class RespPipeList<T> : java.io.Serializable {
    var first_uid // 첫번째 채팅 UID
            = 0
    var last_uid // 마지막 채팅 UID
            = 0
    var contents: List<T>? = null
}