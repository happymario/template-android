package com.victoria.bleled.data.remote.myservice.response

class RespPageList<T> : java.io.Serializable {
    var total_count = 0
    var total_page = 0
    var isIs_last = false
        private set
    var contents: List<T>? = null
    fun setIs_last(is_last: Boolean) {
        isIs_last = is_last
    }
}