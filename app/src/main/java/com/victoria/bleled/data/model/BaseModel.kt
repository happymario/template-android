package com.victoria.bleled.data.model

import androidx.databinding.BaseObservable
import com.google.gson.Gson
import java.io.Serializable

open class BaseModel : Serializable, BaseObservable() {
    fun toJSON(): String {
        val gson = Gson()
        return gson.toJson(this)
    }
}