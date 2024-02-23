package com.mario.template.data.local.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import com.google.gson.Gson
import com.mario.template.base.BaseModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class DataStorePreferenceStorage @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : PreferenceStorage {

    override suspend fun <T : BaseModel> setModel(model: T?) {
        val key: String = PreferenceKeys.PREFIX_MODEL.name + model?.javaClass?.name
        val value = if (model == null) "" else Gson().toJson(model)

        val preferencesKey = stringPreferencesKey(key)
        dataStore.setValue { preferences ->
            preferences[preferencesKey] = value
        }
    }

    override suspend fun <T : BaseModel> removeModel(type: Class<T>) {
        val key: String = PreferenceKeys.PREFIX_MODEL.name + type.name
        val preferencesKey = stringPreferencesKey(key)
        dataStore.setValue { preferences ->
            preferences[preferencesKey] = ""
        }
    }

    override suspend fun <T : BaseModel?> getModel(type: Class<T>): Flow<T> {
        val key: String = PreferenceKeys.PREFIX_MODEL.name + type.name
        val preferencesKey = stringPreferencesKey(key)
        val preferences = dataStore.data.first()
        return flow { Gson().fromJson(preferences[preferencesKey], type) }
    }

    override suspend fun setTutoFinished(isFinished: Boolean) {
        dataStore.setValue {
            it[PreferenceKeys.IS_TUTO_FINISHED] = isFinished
        }
    }

    override val isTutoFinished: Flow<Boolean> = dataStore.getValue {
        it[PreferenceKeys.IS_TUTO_FINISHED] ?: false
    }

    override suspend fun setToken(token: String) {
        dataStore.setValue {
            it[PreferenceKeys.PUSH_TOKEN] = token
        }
    }

    override val getToken: Flow<String> = dataStore.getValue {
        it[PreferenceKeys.PUSH_TOKEN] ?: ""
    }

    object PreferenceKeys {
        val PREFIX_MODEL = stringSetPreferencesKey("PREFIX_MODEL")
        val IS_TUTO_FINISHED = booleanPreferencesKey("IS_TUTO_FINISHED")
        val PUSH_TOKEN = stringPreferencesKey("PUSH_TOKEN")
    }

    companion object {
        const val PREFS_NAME = "my_template_datastore"
    }
}
