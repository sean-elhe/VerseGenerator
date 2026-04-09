package com.example.versegenerator.ViewModels

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.versegenerator.SettingsManager
import com.example.versegenerator.data.BibleDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class VerseViewModel(private val bibleDao: BibleDao,
                        application: Application): AndroidViewModel(application) {
    private val settingsManager = SettingsManager(application)

// THEME
    val themeConfig: StateFlow<ThemeConfig> = settingsManager.themeFlow
        .stateIn(scope = viewModelScope, started = SharingStarted.WhileSubscribed(5000),
            initialValue = ThemeConfig.FOLLOW_SYSTEM)
    fun updateTheme(newConfig: ThemeConfig) {
        viewModelScope.launch {
            settingsManager.saveTheme(newConfig)
        }
    }
 // DIFFICULTIES
    val difficultiesText = listOf<String>("Easy", "Medium", "Hard")
    var selectedDifficulty by mutableStateOf("Easy")

// TRANSLATIONS
    var selectedTranslation by mutableStateOf("NIV")
    val translations = listOf<String>("NIV", "ESV", "NLT")
// BOOKS
    var selectedBook by mutableStateOf("Genesis")
    val booksList = bibleDao.getAllBooks()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
// CHAPTERS
    var selectedChapter by mutableStateOf(1)
    val chaptersList = snapshotFlow { selectedBook }
        .flatMapLatest { book ->
            bibleDao.getChapterCount(book)
        }
        .map { count -> (1..count).map { it.toString() } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    val currentChapter = snapshotFlow {
        Triple(selectedTranslation, selectedBook, selectedChapter) }
        .flatMapLatest { (t, b, c) ->
            bibleDao.getVersesOrder(t, b, c)
        }
        .onEach {
            _currentVerseIndex.value = 0
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    val currentChapterRandom = snapshotFlow {
        Triple(selectedTranslation, selectedBook, selectedChapter) }
        .flatMapLatest { (t, b, c) ->
            bibleDao.getVersesRandom(t, b, c)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
// INDEX
    private val _currentVerseIndex = MutableStateFlow(0)
    val currentVerseIndex = _currentVerseIndex.asStateFlow()
    fun nextVerse(totalVerses: Int){
        if (_currentVerseIndex.value < totalVerses - 1) {
            _currentVerseIndex.value++
        }
    }
    fun resetIndex(){
        _currentVerseIndex.value = 0
    }
}

enum class ThemeConfig{
    FOLLOW_SYSTEM, LIGHT, DARK
}