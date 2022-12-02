package com.victoria.bleled.data.remote.myservice.response

import com.google.gson.annotations.SerializedName
import com.victoria.bleled.data.remote.ApiException
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response
import java.io.Serializable

open class BaseResp : Serializable {
    @SerializedName("result")
    var result: Int = ApiException.Companion.SUCCESS

    @SerializedName("msg")
    var message: String? = null

    @SerializedName("reason")
    var reason = ""

    fun <T> error(): Response<T> {
        val errorBody = "dummy error body"
        return Response.error<T>(
            (errorBody).toResponseBody(),
            okhttp3.Response.Builder()
                .body(null)
                .code(result)
                .message(message ?: "no message")
                .protocol(Protocol.HTTP_1_1)
                .request(Request.Builder().url("http://localhost/").build())
                .build()
        )
    }
}