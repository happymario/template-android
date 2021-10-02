package com.victoria.bleled.data.remote.myservice

import com.victoria.bleled.data.model.BaseModel

class ResponsePipeList<T> : BaseModel() {
    var first_uid // 첫번째 채팅 UID
            = 0
    var last_uid // 마지막 채팅 UID
            = 0
    var contents: List<T>? = null
}