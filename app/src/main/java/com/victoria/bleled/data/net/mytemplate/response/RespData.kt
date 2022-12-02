package com.victoria.bleled.data.net.mytemplate.response

import com.google.gson.annotations.SerializedName

class RespData<T> : BaseResp() {
    @SerializedName("data")
    var data: T? = null
        private set

    fun setData(data: T) {
        this.data = data
    }
}