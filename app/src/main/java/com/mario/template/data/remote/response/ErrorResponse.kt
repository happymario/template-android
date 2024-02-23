package com.mario.template.data.remote.response

import com.google.gson.annotations.SerializedName

open class ErrorResponse(
    @SerializedName("result") val code: Int? = 0,
    @SerializedName("msg") val message: String? = "",
)
