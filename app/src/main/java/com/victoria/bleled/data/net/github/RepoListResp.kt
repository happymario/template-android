package com.victoria.bleled.data.net.github

import com.google.gson.annotations.SerializedName
import com.victoria.bleled.data.model.ModelUser

class RepoListResp : java.io.Serializable {
    var total = 0

    @JvmField
    @SerializedName("list")
    var items: List<ModelUser> = emptyList()
}