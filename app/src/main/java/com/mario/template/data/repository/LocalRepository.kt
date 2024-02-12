package com.mario.template.data.repository

import com.mario.template.data.local.room.HistorySearchAddressEntity
import com.mario.template.data.model.AppInfo
import com.mario.template.data.model.User
import kotlinx.coroutines.flow.Flow

interface LocalRepository {
    suspend fun setAppInfo(appInfo: AppInfo)

    suspend fun getAppInfo(): Flow<AppInfo>

    suspend fun setTutofinished(boolean: Boolean)

    fun isTutoFinished(): Flow<Boolean>

    suspend fun setLoginUser(user: User)

    suspend fun getLoginUser(): Flow<User>

    suspend fun setPushToken(token: String)

    fun getPushToken(): Flow<String>

    fun getSearchAddress(): Flow<List<HistorySearchAddressEntity>>

    suspend fun addSearchAddress(historySearchAddressEntity: HistorySearchAddressEntity)

    suspend fun clearAllSearchAddress()
}
