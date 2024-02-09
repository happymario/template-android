package com.mario.template.data.model

import com.google.gson.annotations.SerializedName
import com.mario.template.base.BaseModel

data class OneCallData(
    @SerializedName("lat") val lat: Double? = 0.0,
    @SerializedName("lon") val long: Double? = 0.0,
    @SerializedName("time_zone") val timeZone: String? = "",
    @SerializedName("timezone_offset") val timeZoneOffSet: Int? = 0,
) : BaseModel()
