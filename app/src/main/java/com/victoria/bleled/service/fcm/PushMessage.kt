package com.victoria.bleled.service.fcm

import java.io.Serializable

class PushMessage : Serializable {
    var type: String = ""
    var content: String = ""

    constructor(data: Map<String, String>) {
        type = data.get("type") ?: ""
        content = data.get("content") ?: ""
    }
}