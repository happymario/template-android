package com.mario.template.data.repository

import com.mario.template.data.local.datastore.PreferenceStorage
import com.mario.template.data.local.room.HistorySearchAddressDao
import com.mario.template.data.local.room.HistorySearchAddressEntity
import com.mario.template.data.model.AppInfo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocalRepositoryImpl @Inject constructor(
    private val preferenceStorage: PreferenceStorage,
    private val searchAddressDao: HistorySearchAddressDao,
) : LocalRepository {

    override suspend fun setAppInfo(appInfo: AppInfo) = preferenceStorage.setModel(appInfo)

    override suspend fun getAppInfo() = preferenceStorage.getModel(AppInfo::class.java)

    override suspend fun setTutofinished(boolean: Boolean) {
        preferenceStorage.setTutoFinished(boolean)
    }

    override fun isTutoFinished(): Flow<Boolean> = preferenceStorage.isTutoFinished

    override suspend fun addSearchAddress(historySearchAddressEntity: HistorySearchAddressEntity) {
        searchAddressDao.insertOrUpdate(historySearchAddressEntity)
    }

    override suspend fun clearAllSearchAddress() = searchAddressDao.deleteAll()

    override fun getSearchAddress(): Flow<List<HistorySearchAddressEntity>> =
        searchAddressDao.getAll()
}
