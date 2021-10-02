package com.victoria.bleled.data.remote.myservice

import com.victoria.bleled.data.remote.ApiException
import java.io.Serializable
import java.util.*

class BaseResponse<T> : Serializable {
    var result: Int = ApiException.Companion.SUCCESS
    var msg = ""
    var reason = ""
    var data: T? = null
        private set

    fun setData(data: T) {
        this.data = data
    }

    companion object {
        fun <T> convertFromResponseArray(response: BaseResponse<ResponseArray<T>>): BaseResponse<List<T>> {
            val resultResponse = BaseResponse<List<T>>()
            if (response.data == null && response.data?.contents != null) {
                resultResponse.setData(ArrayList())
            } else {
                resultResponse.setData(response.data!!.contents!!)
            }
            resultResponse.msg = response.msg
            resultResponse.reason = response.reason
            resultResponse.result = response.result
            return resultResponse
        }
    }
}