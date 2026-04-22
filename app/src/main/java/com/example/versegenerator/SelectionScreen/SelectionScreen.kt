package com.example.versegenerator.SelectionScreen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.versegenerator.ViewModels.InputConfig
import com.example.versegenerator.ViewModels.StyleConfig
import com.example.versegenerator.ViewModels.ThemeConfig
import com.example.versegenerator.ViewModels.VerseViewModel


@Composable
fun EnabledInput(viewModel: VerseViewModel, modifier: Modifier) {
    val books by viewModel.booksList.collectAsStateWithLifecycle()
    val chapters by viewModel.chaptersList.collectAsStateWithLifecycle()
    val versesOrder by viewModel.versesByOrder.collectAsStateWithLifecycle()
    val currentIndex by viewModel.currentVerseIndex.collectAsStateWithLifecycle()
    val reloadKey by viewModel.reloadTrigger.collectAsStateWithLifecycle()
    val themeState by viewModel.themeConfig.collectAsStateWithLifecycle()
    val styleState by viewModel.styleConfig.collectAsStateWithLifecycle()
    val inputState by viewModel.inputConfig.collectAsStateWithLifecycle()
    val difficulty by viewModel.selectedDifficulty.collectAsState("Easy")
    val translation by viewModel.selectedTranslation.collectAsState("NIV")
    val book by viewModel.selectedBook.collectAsState("Genesis")
    val chapter by viewModel.selectedChapter.collectAsState(1)
    val difficulties = viewModel.difficultiesText
    var stage by viewModel.stage
    val isDark = themeState == ThemeConfig.DARK
    val isRandom = styleState == StyleConfig.RANDOM
    val isInput = inputState == InputConfig.ENABlED


    val verses = remember(isRandom, versesOrder) {
        if (isRandom) {
            versesOrder.shuffled()
        } else {
            versesOrder.toList()
        }
    }


    val verseData =
        remember(verses, translation, book, chapter, currentIndex, difficulty, reloadKey) {
            if (verses.isNotEmpty() && currentIndex in verses.indices) {
                ReplacingWordsIE(
                    text = verses[currentIndex].text,
                    difficultyLevel = difficulty
                )
            } else {
                null
            }
        }

    if (verseData != null) {
        Column(modifier = Modifier.fillMaxSize()) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(25.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.elevatedCardElevation(10.dp),
                border = BorderStroke(width = 2.dp, Color.DarkGray)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 25.dp, start = 40.dp),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Text(
                        text = "[${verses[currentIndex].chapter}:${verses[currentIndex].verse}]",
                        style = MaterialTheme.typography.displayMedium,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
                Box(modifier = Modifier.fillMaxWidth().padding(10.dp)) {
                    YourVerseIE(stage, verseData)
                }
                SelectionButtonsLower(viewModel, versesOrder)
            }
        }
    } else {
        CircularProgressIndicator()
    }
}

@Composable
fun DisabledInput(viewModel: VerseViewModel, modifier: Modifier) {
    val books by viewModel.booksList.collectAsStateWithLifecycle()
    val chapters by viewModel.chaptersList.collectAsStateWithLifecycle()
    val versesOrder by viewModel.versesByOrder.collectAsStateWithLifecycle()
    val currentIndex by viewModel.currentVerseIndex.collectAsStateWithLifecycle()
    val reloadKey by viewModel.reloadTrigger.collectAsStateWithLifecycle()
    val themeState by viewModel.themeConfig.collectAsStateWithLifecycle()
    val styleState by viewModel.styleConfig.collectAsStateWithLifecycle()
    val difficulty by viewModel.selectedDifficulty.collectAsState("Easy")
    val translation by viewModel.selectedTranslation.collectAsState("NIV")
    val book by viewModel.selectedBook.collectAsState("Genesis")
    val chapter by viewModel.selectedChapter.collectAsState(1)
    val difficulties = viewModel.difficultiesText
    var stage by viewModel.stage
    val isDark = themeState == ThemeConfig.DARK
    val isRandom = styleState == StyleConfig.RANDOM
    val inputState by viewModel.inputConfig.collectAsStateWithLifecycle()
    val isInput = inputState == InputConfig.ENABlED

    val verses = remember(isRandom, versesOrder) {
        if (isRandom) {
            versesOrder.shuffled()
        } else {
            versesOrder.toList()
        }
    }

    val verseData =
        remember(verses, translation, book, chapter, currentIndex, difficulty, reloadKey) {
            if (verses.isNotEmpty() && currentIndex in verses.indices) {
                ReplacingWordsID(verses[currentIndex].text, difficulty)
            } else {
                null
            }
        }

    if (verseData != null) {
        Column(modifier = Modifier.fillMaxSize()) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(25.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.elevatedCardElevation(10.dp),
                border = BorderStroke(width = 2.dp, Color.DarkGray)
            ) {

                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 50.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "${verses[currentIndex].book} ${verses[currentIndex].chapter}:${verses[currentIndex].verse}",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
                Box(modifier = Modifier.fillMaxWidth().padding(10.dp)) {
                    YourVerseID(stage, verseData.hiddenVerse, verseData.revealedVerse)
                }

                SelectionButtonsLower(viewModel, versesOrder)
            }
        }
    } else {
        CircularProgressIndicator()
    }
}

@Composable
 fun SelectionScreen(viewModel: VerseViewModel, modifier: Modifier) {
    val books by viewModel.booksList.collectAsStateWithLifecycle()
    val chapters by viewModel.chaptersList.collectAsStateWithLifecycle()
    val versesOrder by viewModel.versesByOrder.collectAsStateWithLifecycle()
    val currentIndex by viewModel.currentVerseIndex.collectAsStateWithLifecycle()
    val reloadKey by viewModel.reloadTrigger.collectAsStateWithLifecycle()
    val themeState by viewModel.themeConfig.collectAsStateWithLifecycle()
    val styleState by viewModel.styleConfig.collectAsStateWithLifecycle()
    val difficulty by viewModel.selectedDifficulty.collectAsState("Easy")
    val translation by viewModel.selectedTranslation.collectAsState("NIV")
    val book by viewModel.selectedBook.collectAsState("Genesis")
    val chapter by viewModel.selectedChapter.collectAsState(1)
    val difficulties = viewModel.difficultiesText
    var stage by viewModel.stage
    val isDark = themeState == ThemeConfig.DARK
    val isRandom = styleState == StyleConfig.RANDOM
    val inputState by viewModel.inputConfig.collectAsStateWithLifecycle()
    val isInput = inputState == InputConfig.ENABlED
    val isSaved by viewModel.isSaved.collectAsState()
    val shortcuts by viewModel.savedShortcuts.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 25.dp, bottom = 50.dp)
    ) {
        val inputModifier = modifier.fillMaxSize()

        Row(modifier = Modifier.fillMaxWidth().padding(top = 30.dp).height(60.dp)) {
            SelectionMenu(
                modifier.weight(0.25f), viewModel, books, book,
                chapters, chapter, difficulties,
                difficulty, translation, isDark,
                isRandom, isInput, versesOrder, stage,
            )
            SelectionSearcher(modifier.weight(0.75f), viewModel, book, chapter,
                isSaved, shortcuts)
        }

        if (isInput) {
            EnabledInput(viewModel, inputModifier)
        } else {
            DisabledInput(viewModel, inputModifier)
        }
    }
}