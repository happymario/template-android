package com.mario.template.data.model

import com.google.gson.annotations.SerializedName
import com.mario.template.base.BaseModel

data class AppInfo(
    @SerializedName("api_ver") val version: String = "0.0",
    @SerializedName("client_center") val cs_phone: String? = ""
) : BaseModel()