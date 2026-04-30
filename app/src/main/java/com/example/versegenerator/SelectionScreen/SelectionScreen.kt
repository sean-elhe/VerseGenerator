package com.example.versegenerator.SelectionScreen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.LinearGradientShader
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.versegenerator.ViewModels.InputConfig
import com.example.versegenerator.ViewModels.StyleConfig
import com.example.versegenerator.ViewModels.ThemeConfig
import com.example.versegenerator.ViewModels.VerseViewModel
import com.example.versegenerator.data.Verse

@Composable
fun SelectionScreen(viewModel: VerseViewModel, modifier: Modifier) {
    // Collect all states in ONE place
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
    val isSaved by viewModel.isSaved.collectAsState()
    val shortcuts by viewModel.savedShortcuts.collectAsState()

    val stage by viewModel.stage
    val isQuick = inputState == InputConfig.ENABlED
    val isDark = themeState == ThemeConfig.DARK
    val isRandom = styleState == StyleConfig.RANDOM

    val focusManager = LocalFocusManager.current




    Column(modifier = Modifier
        .fillMaxSize()
        .border(width = 1.dp, color = Color.Gray)
        .background(color = MaterialTheme.colorScheme.surface)
        .pointerInput(Unit){
            detectTapGestures(onTap =
                { focusManager.clearFocus() }
            )
        })
    {
        Row(modifier = Modifier.height(75.dp).fillMaxWidth()) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(top = 15.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
//                elevation = CardDefaults.elevatedCardElevation(20.dp),
            ) {
                Row() {
                    SelectionMenu(
                        modifier.weight(0.20f), viewModel, books, book,
                        chapters, chapter, viewModel.difficultiesText,
                        difficulty, translation, isDark, isRandom, isQuick, versesOrder, stage
                    )
                    SelectionSearcher(
                        modifier.weight(0.80f),
                        viewModel,
                        book,
                        chapter,
                        isSaved,
                        shortcuts
                    )
                }
            }
        }
        HorizontalDivider(modifier = Modifier.height(1.dp), color = Color.Gray)

        Row(modifier = Modifier.weight(0.1f).fillMaxWidth()) {
            VerseDisplayContainer(
                viewModel = viewModel,
                versesOrder = versesOrder,
                currentIndex = currentIndex,
                isRandom = isRandom,
                isQuick = isQuick,
                difficulty = difficulty,
                translation = translation,
                book = book,
                chapter = chapter,
                reloadKey = reloadKey,
                stage = stage
            )
        }
    }
}

@Composable
fun VerseDisplayContainer(
    viewModel: VerseViewModel,
    versesOrder: List<Verse>, // Replace 'Verse' with your actual Model class name
    currentIndex: Int,
    isRandom: Boolean,
    isQuick: Boolean,
    difficulty: String,
    translation: String,
    book: String,
    chapter: Int,
    reloadKey: Any,
    stage: Int
) {
    val verses = remember(isRandom, versesOrder) {
        if (isRandom) versesOrder.shuffled() else versesOrder.toList()
    }

    val currentVerse = verses.getOrNull(currentIndex)
    val totalVerses = verses.size

    val progress = if (totalVerses > 1) {
        currentIndex.toFloat() / (totalVerses - 1)
    } else {
        0f
    }

    val isFirstVerse = currentIndex == 0

    if (currentVerse != null) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(modifier = Modifier.fillMaxWidth().weight(0.08f).background(color = MaterialTheme.colorScheme.surface)
                .padding(horizontal = 32.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically)
            {
                Text("${currentVerse.chapter}:${currentVerse.verse}",
                    fontSize = 14.sp,
                    color = Color(0xFF7298C7) )

                Spacer(modifier = Modifier.width(12.dp))

                LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .weight(1f) // takes remaining space
                    .height(6.dp)
                    .clip(RoundedCornerShape(50)),
                    color = if (isFirstVerse) Color.LightGray else Color(0xFF7298C7),
                    trackColor = if (isFirstVerse) Color.LightGray.copy(alpha = 0.3f) else Color.LightGray.copy(alpha = 0.5f) ,
//                    strokeCap = ProgressIndicatorDefaults.LinearStrokeCap,
                )
                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = "${currentVerse.verse - 1}/${totalVerses}",
                    fontSize = 14.sp,
                    color = Color(0xFF7298C7)
                )
            }
            HorizontalDivider(modifier = Modifier.height(1.dp), color = Color.Gray)

            Card(
                modifier = Modifier.fillMaxWidth().weight(0.92f),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
//                elevation = CardDefaults.elevatedCardElevation(20.dp),
            ) {
                // Header Logic
                if (isQuick) {
                    Row(modifier = Modifier.fillMaxWidth().padding(top = 20.dp, start = 20.dp), horizontalArrangement = Arrangement.Start) {
//                        Text("${currentVerse.chapter}:${currentVerse.verse}", style = MaterialTheme.typography.bodyLarge, color = Color(0xFF7298C7) )
                    }
                } else {
                    Row(modifier = Modifier.fillMaxWidth().padding(top = 20.dp), horizontalArrangement = Arrangement.Center) {
                        Text("${currentVerse.book} ${currentVerse.chapter}:${currentVerse.verse}", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimary)
                    }
                }

                // Content Logic
                Box(modifier = Modifier.fillMaxWidth().padding(5.dp)) {
                    if (isQuick) {
                        val verseData = remember(currentVerse, difficulty, reloadKey) {
                            ReplacingWordsIE(text = currentVerse.text, difficultyLevel = difficulty)
                        }
                        YourVerseIE(stage, verseData, viewModel, versesOrder)
                    } else {
                        val verseData = remember(currentVerse, difficulty, reloadKey) {
                            ReplacingWordsID(currentVerse.text, difficulty)
                        }
                        YourVerseID(stage, verseData.hiddenVerse, verseData.revealedVerse)
                    }
                }
            }


        }
    } else {
        Box(Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
            CircularProgressIndicator()
        }
    }
}