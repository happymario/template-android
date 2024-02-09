package com.mario.template.data.repository

import com.mario.template.data.local.room.HistorySearchAddressEntity
import com.mario.template.data.model.AppInfo
import kotlinx.coroutines.flow.Flow

interface LocalRepository {
    suspend fun setAppInfo(appInfo: AppInfo)

    suspend fun getAppInfo(): Flow<AppInfo>

    suspend fun setTutofinished(boolean: Boolean)

    fun isTutoFinished(): Flow<Boolean>

    fun getSearchAddress(): Flow<List<HistorySearchAddressEntity>>

    suspend fun addSearchAddress(historySearchAddressEntity: HistorySearchAddressEntity)

    suspend fun clearAllSearchAddress()
}
