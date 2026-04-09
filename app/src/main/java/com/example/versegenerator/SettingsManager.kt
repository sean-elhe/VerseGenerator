package com.example.versegenerator

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.versegenerator.ViewModels.ThemeConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "settings")

class SettingsManager(context: Context) {
    private val dataStore = context.dataStore

    companion object {
        val THEME_KEY = stringPreferencesKey("theme_config")
    }

    // Read the setting as a Flow
    val themeFlow: Flow<ThemeConfig> = dataStore.data.map { preferences ->
        val name = preferences[THEME_KEY] ?: ThemeConfig.FOLLOW_SYSTEM.name
        ThemeConfig.valueOf(name)
    }

    // Save the setting
    suspend fun saveTheme(themeConfig: ThemeConfig) {
        dataStore.edit { preferences ->
            preferences[THEME_KEY] = themeConfig.name
        }
    }
}