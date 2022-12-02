package com.victoria.bleled.data.remote.github

import com.victoria.bleled.data.model.ModelUser

class RepoListResp : java.io.Serializable {
    var total = 0

    @JvmField
    var items: List<ModelUser>? = null
}