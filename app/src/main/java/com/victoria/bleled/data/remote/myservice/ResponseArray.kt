package com.victoria.bleled.data.remote.myservice

import com.victoria.bleled.data.model.BaseModel

class ResponseArray<T> : BaseModel() {
    var contents: List<T>? = null
}