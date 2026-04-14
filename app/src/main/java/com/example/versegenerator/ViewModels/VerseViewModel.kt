package com.example.versegenerator.ViewModels

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.versegenerator.SettingsManager
import com.example.versegenerator.data.BibleDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class VerseViewModel(private val bibleDao: BibleDao,
                        application: Application): AndroidViewModel(application) {
    private val settingsManager = SettingsManager(application)
    var stage = mutableIntStateOf(1)

// INPUT

    val inputConfig: StateFlow<InputConfig>  = settingsManager.inputFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), InputConfig.ENABlED)
    fun updateInput(newConfig: InputConfig) {
        viewModelScope.launch {
            settingsManager.saveInput(newConfig)
        }
    }

// NAVIGATION
    fun nextVerse(totalVerses: Int){
        if (_currentVerseIndex.value < totalVerses - 1) {
            _currentVerseIndex.value++
        } else {
            resetIndex()
        }
    }
    fun previousVerse(){
        if (_currentVerseIndex.value > 0) {
            _currentVerseIndex.value-- }
    }
// RELOAD
    val _reloadTrigger = MutableStateFlow(0)
    val reloadTrigger = _reloadTrigger.asStateFlow()
    fun reloadTrigger(){
        _reloadTrigger.value++
    }
// THEME
    val themeConfig: StateFlow<ThemeConfig> = settingsManager.themeFlow
        .stateIn(scope = viewModelScope, started = SharingStarted.WhileSubscribed(5000),
            initialValue = ThemeConfig.FOLLOW_SYSTEM)
    fun updateTheme(newConfig: ThemeConfig) {
        viewModelScope.launch {
            settingsManager.saveTheme(newConfig)
        }
    }
// STYLE
    val styleConfig: StateFlow<StyleConfig> = settingsManager.styleFlow
        .stateIn(scope = viewModelScope, started = SharingStarted.WhileSubscribed(5000),
            initialValue = StyleConfig.ORDER)
    fun updateStyle(newConfig: StyleConfig) {
        viewModelScope.launch {
            settingsManager.saveStyle(newConfig)
        }
    }
 // DIFFICULTIES
    val difficultiesText = listOf<String>("Easy", "Medium", "Hard")
    var selectedDifficulty = settingsManager.difficultyFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "Easy")
    fun updateDifficulty(newDifficulty: String){
        viewModelScope.launch {
            settingsManager.saveDifficulty(newDifficulty)
        }
    }
// TRANSLATIONS
    val translations = listOf<String>("NIV", "ESV", "NLT")
    val selectedTranslation = settingsManager.translationFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "NIV")
    fun updateTranslation(newTranslation: String){
        viewModelScope.launch {
            settingsManager.saveTranslation(newTranslation)
        }
    }
// BOOKS
    val booksList = bibleDao.getAllBooks()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    var selectedBook = settingsManager.bookFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "Genesis")
    fun updateBook(newBook: String){
        viewModelScope.launch {
            settingsManager.saveBook(newBook)
        }
    }
    // CHAPTERS
    val chaptersList = selectedBook
        .flatMapLatest { book ->
            bibleDao.getChapterCount(book)
        }
        .map { count -> (1..count).map { it.toString() } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    var selectedChapter = settingsManager.chapterFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 1)
    fun updateChapter(newChapter: Int){
        viewModelScope.launch {
            settingsManager.saveChaper(newChapter)
        }
    }

// ORDERING
    val versesByOrder = combine(
        selectedTranslation,          // Flow 1
        selectedBook, // Flow 2 (Converts Compose state to Flow)
        selectedChapter // Flow 3
    ) { t, b, c ->
        Triple(t, b, c)
    }.flatMapLatest { (t, b, c) ->
        // Every time T, B, or C changes, this triggers a new Room query
        bibleDao.getVersesOrder(t, b, c)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

// INDEX
    private val _currentVerseIndex = MutableStateFlow(0)
    val currentVerseIndex = _currentVerseIndex.asStateFlow()
    fun resetIndex(){
        _currentVerseIndex.value = 0
    }
}

var userGuess by mutableStateOf(mapOf<Int, String>())
var resultShown by mutableStateOf(false)

fun updateGuess(index: Int, text: String){
    userGuess = userGuess + (index to text)
}

enum class ThemeConfig{
    FOLLOW_SYSTEM, LIGHT, DARK
}
enum class StyleConfig{
    ORDER, RANDOM
}

enum class InputConfig{
    ENABlED, DISABLED
}