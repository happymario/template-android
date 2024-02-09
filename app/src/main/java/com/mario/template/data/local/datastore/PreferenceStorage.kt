package com.mario.template.data.local.datastore

import com.mario.template.base.BaseModel
import kotlinx.coroutines.flow.Flow

interface PreferenceStorage {
    suspend fun <T : BaseModel> setModel(model: T?)
    suspend fun <T : BaseModel> removeModel(type: Class<T>)
    suspend fun <T : BaseModel?> getModel(type: Class<T>): Flow<T>
    suspend fun setTutoFinished(isFinished: Boolean)
    val isTutoFinished: Flow<Boolean>
}
