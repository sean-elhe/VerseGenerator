package com.example.versegenerator

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.versegenerator.ViewModels.InputConfig
import com.example.versegenerator.ViewModels.StyleConfig
import com.example.versegenerator.ViewModels.ThemeConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "settings")

class SettingsManager(context: Context) {
    private val dataStore = context.dataStore

    companion object {
        val THEME_KEY = stringPreferencesKey("theme_config")
        val STYLE_KEY = stringPreferencesKey("style_config")
        val INPUT_KEY = stringPreferencesKey("input_config")
        val TRANSLATION_KEY = stringPreferencesKey("translation_config")
        val DIFFICULTY_KEY = stringPreferencesKey("difficulty_config")
        val BOOK_KEY = stringPreferencesKey("book_config")
        val CHAPTER_KEY = intPreferencesKey("chapter_config")
    }
    val bookFlow: Flow<String> = dataStore.data.map { preferences ->
        preferences[BOOK_KEY] ?: "Genesis"
    }
    suspend fun saveBook(book: String){
        dataStore.edit { preferences ->
            preferences[BOOK_KEY] = book
        }
    }
    val chapterFlow: Flow<Int> = dataStore.data.map { preferences ->
        preferences[CHAPTER_KEY] ?: 1
    }
    suspend fun saveChaper(chapter: Int){
        dataStore.edit { preferences ->
            preferences[CHAPTER_KEY] = chapter
        }
    }
    val difficultyFlow: Flow<String> = dataStore.data.map { preferences ->
        preferences[DIFFICULTY_KEY] ?: "Easy"
    }
    suspend fun saveDifficulty(difficulty: String){
        dataStore.edit { preferences ->
            preferences[DIFFICULTY_KEY] = difficulty
        }
    }
    val translationFlow: Flow<String> = dataStore.data.map { preferences ->
        preferences[TRANSLATION_KEY] ?: "NIV"
    }
    suspend fun saveTranslation(translation: String){
        dataStore.edit { preferences ->
            preferences[TRANSLATION_KEY] = translation
        }
    }
    val themeFlow: Flow<ThemeConfig> = dataStore.data.map { preferences ->
        val name = preferences[THEME_KEY] ?: ThemeConfig.FOLLOW_SYSTEM.name
        ThemeConfig.valueOf(name)
    }
    suspend fun saveTheme(themeConfig: ThemeConfig) {
        dataStore.edit { preferences ->
            preferences[THEME_KEY] = themeConfig.name
        }
    }
    val styleFlow: Flow<StyleConfig> = dataStore.data.map { preferences ->
        val name = preferences[STYLE_KEY] ?: StyleConfig.ORDER.name
        StyleConfig.valueOf(name)
    }
    suspend fun saveStyle(styleConfig: StyleConfig){
        dataStore.edit { preferences ->
            preferences[STYLE_KEY] = styleConfig.name
        }
    }
    val inputFlow: Flow<InputConfig> = dataStore.data.map { preferences ->
        val name = preferences[INPUT_KEY] ?: InputConfig.ENABlED.name
        InputConfig.valueOf(name)
    }
    suspend fun saveInput(inputConfig: InputConfig){
        dataStore.edit { preferences ->
            preferences[INPUT_KEY] = inputConfig.name
        }
    }
}