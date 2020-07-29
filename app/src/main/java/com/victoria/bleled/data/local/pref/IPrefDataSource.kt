package com.victoria.bleled.data.local.pref

import com.victoria.bleled.data.model.Repo

interface IPrefDataSource {
    var pushToken: String

    var appInfo: Repo?

    var installReferer: String

    var videoAdsTime: Long

    var firstRun: Boolean
}
