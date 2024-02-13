package com.mario.template.data.model

import com.google.gson.annotations.SerializedName

class GithubRepoData : java.io.Serializable {
    var total = 0

    @JvmField
    @SerializedName("list")
    var items: List<User> = emptyList()
}