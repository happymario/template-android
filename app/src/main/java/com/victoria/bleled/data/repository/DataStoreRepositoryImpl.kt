package com.victoria.bleled.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.victoria.bleled.data.model.BaseModel
import com.victoria.bleled.data.model.ModelUser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import javax.inject.Inject


private const val PREFERENCES_NAME = "Template-Android_Prefs"
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = PREFERENCES_NAME)

class DataStoreKey{
    companion object {
        const val PREFS_USER_INFO = "PREFS_USER_INFO"
        const val PREFS_APP_INFO = "PREFS_APP_INFO"
        const val PREFS_APP_SETTING = "PREFS_APP_SETTING"
        const val PREFS_PUSH_TOKEN = "PREFS_PUSH_TOKEN"
        const val PREFS_REFERER = "PREFS_REFERER"
        const val PREFS_GEO_LATITUDE = "PREFS_GEO_LATITUDE"
        const val PREFS_GEO_LOGITUDE = "PREFS_GEO_LOGITUDE"
        const val PREFS_MODEL = "PREFS_MODEL"
    }
}

class DataStoreRepositoryImpl @Inject constructor(private val context: Context)
    : DataStoreRepository {

    override suspend fun putString(key: String, value: String) {
        val preferencesKey = stringPreferencesKey(key)
        context.dataStore.edit { preferences ->
            preferences[preferencesKey] = value
        }
    }

    override suspend fun clearString(key:String){
        val preferencesKey = stringPreferencesKey(key)
        context.dataStore.edit { preferences ->
            preferences.remove(preferencesKey)
        }
    }

    override suspend fun putInt(key: String, value: Int) {
        val preferencesKey = intPreferencesKey(key)
        context.dataStore.edit { preferences ->
            preferences[preferencesKey] = value
        }
    }

    override suspend fun putLong(key: String, value:Long){
        val preferencesKey = longPreferencesKey(key)
        context.dataStore.edit { preferences ->
            preferences[preferencesKey] = value
        }
    }

    override suspend fun putBoolean(key: String, value: Boolean) {
        val preferencesKey = booleanPreferencesKey(key)
        context.dataStore.edit { preferences ->
            preferences[preferencesKey] = value
        }
    }

    override suspend fun putStringArray(key: String, value: ArrayList<String>) {
        val jsonArr =JSONArray()
        for( a in value){
            jsonArr.put(a)
        }
        val setItem = jsonArr.toString()
        val preferencesKey = stringPreferencesKey(key)
        context.dataStore.edit { preferences ->
            preferences[preferencesKey] =setItem
        }
    }

    override suspend fun getString(key: String): String? {
        val preferencesKey = stringPreferencesKey(key)
        val preferences = context.dataStore.data.first()
        return preferences[preferencesKey]
    }

    override suspend fun getInt(key: String): Int? {
        val preferencesKey = intPreferencesKey(key)
        val preferences = context.dataStore.data.first()
        return preferences[preferencesKey]
    }

    override suspend fun getLong(key: String): Long? {
        val preferencesKey = longPreferencesKey(key)
        val preferences = context.dataStore.data.first()
        return preferences[preferencesKey]
    }

    override suspend fun getBoolean(key: String): Boolean? {
        val preferencesKey = booleanPreferencesKey(key)
        val preferences = context.dataStore.data.first()
        return preferences[preferencesKey]
    }

    override suspend fun getStringArray(key: String): ArrayList<String>? {
        val preferencesKey = stringPreferencesKey(key)
        val preferences = context.dataStore.data.first()
        val item = preferences[preferencesKey] ?: return null
        val resultArray = ArrayList<String>()
        val jsonItem =JSONArray(item)
        for(a in 0 until jsonItem.length()){
            if(jsonItem.optString(a)=="") continue
            resultArray.add(jsonItem.optString(a))
        }
        return resultArray
    }

    override suspend fun getBooleanFlow(key:String): Flow<Boolean> =
        context.dataStore.data.map { preference ->
            preference[booleanPreferencesKey(key)] ?: false
        }

    override suspend fun getIntFlow(key: String): Flow<Int?> =
        context.dataStore.data.map { preference ->
            preference[intPreferencesKey(key)]
        }

    override suspend fun <T : BaseModel> setModel(model: T?) {
        val key: String = DataStoreKey.PREFS_MODEL + model?.javaClass?.name
        val value = if(model == null) "" else Gson().toJson(model)

        val preferencesKey = stringPreferencesKey(key)
        context.dataStore.edit { preferences ->
            preferences[preferencesKey] = value
        }
    }

    override suspend fun <T : BaseModel> removeModel(type: Class<T>) {
        val key: String = DataStoreKey.PREFS_MODEL + type.name
        val preferencesKey = stringPreferencesKey(key)
        context.dataStore.edit { preferences ->
            preferences[preferencesKey] = ""
        }
    }

    override suspend fun <T : BaseModel?> getModel(type: Class<T>): T? {
        val key: String = DataStoreKey.PREFS_MODEL + type.name
        val preferencesKey = stringPreferencesKey(key)
        val preferences = context.dataStore.data.first()
        return Gson().fromJson(preferences[preferencesKey], type)
    }
}