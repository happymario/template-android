package com.victoria.bleled.data.repository

import com.victoria.bleled.data.model.BaseModel
import kotlinx.coroutines.flow.Flow

interface DataStoreRepository {
    suspend fun putString(key: String, value: String)
    suspend fun clearString(key: String)
    suspend fun putInt(key: String, value: Int)
    suspend fun putLong(key: String, value: Long)
    suspend fun putBoolean(key: String, value: Boolean)
    suspend fun putStringArray(key: String, value: ArrayList<String>)
    suspend fun getString(key: String): String?
    suspend fun getInt(key: String): Int?
    suspend fun getLong(key: String): Long?
    suspend fun getBoolean(key: String): Boolean?
    suspend fun getBooleanFlow(key: String): Flow<Boolean?>
    suspend fun getIntFlow(key: String): Flow<Int?>
    suspend fun getStringArray(key: String): ArrayList<String>?

    suspend fun <T : BaseModel> setModel(model: T?)

    suspend fun <T : BaseModel> removeModel(type: Class<T>)

    suspend fun <T : BaseModel?> getModel(type: Class<T>): T?
}