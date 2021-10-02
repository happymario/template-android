package com.victoria.bleled.data.remote.github

import com.victoria.bleled.data.model.BaseModel
import com.victoria.bleled.data.model.ModelUser

class ResponseSearchRepo : BaseModel() {
    var total = 0

    @JvmField
    var items: List<ModelUser>? = null
}