package com.victoria.bleled.data.remote.myservice.response

class RespArray<T> : java.io.Serializable {
    var contents: List<T>? = null

    fun <T> convertFromRespArray(response: RespData<RespArray<T>>): RespData<List<T>> {
        val resultResponse = RespData<List<T>>()
        if (response.data == null && response.data?.contents != null) {
            resultResponse.setData(ArrayList())
        } else {
            resultResponse.setData(response.data!!.contents!!)
        }
        resultResponse.message = response.message
        resultResponse.reason = response.reason
        resultResponse.result = response.result
        return resultResponse
    }
}