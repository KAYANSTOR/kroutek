package com.example.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsManager(private val context: Context) {

    companion object {
        val RECEIVE_SIM = stringPreferencesKey("receive_sim")
        val SEND_SIM = stringPreferencesKey("send_sim")
        val THEME_MODE = stringPreferencesKey("theme_mode") // "light", "dark", "system"
        val APP_LANGUAGE = stringPreferencesKey("app_language") // "ar", "en"
    }

    val receiveSimFlow: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[RECEIVE_SIM] ?: "sim1"
    }

    val sendSimFlow: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[SEND_SIM] ?: "sim1"
    }
    
    val themeModeFlow: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[THEME_MODE] ?: "system"
    }

    suspend fun saveReceiveSim(sim: String) {
        context.dataStore.edit { preferences ->
            preferences[RECEIVE_SIM] = sim
        }
    }

    suspend fun saveSendSim(sim: String) {
        context.dataStore.edit { preferences ->
            preferences[SEND_SIM] = sim
        }
    }
    
    suspend fun saveThemeMode(mode: String) {
        context.dataStore.edit { preferences ->
            preferences[THEME_MODE] = mode
        }
    }
}
