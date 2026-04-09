package com.example.versegenerator.SelectionScreen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.versegenerator.ViewModels.ThemeConfig
import com.example.versegenerator.ViewModels.VerseViewModel


@Composable
 fun SelectionScreen(viewModel: VerseViewModel, modifier: Modifier) {
    val books by viewModel.booksList.collectAsStateWithLifecycle()
    val chapters by viewModel.chaptersList.collectAsStateWithLifecycle()
    val verses by viewModel.currentChapter.collectAsStateWithLifecycle()
    val currentIndex by viewModel.currentVerseIndex.collectAsStateWithLifecycle()
    var stage by remember { mutableIntStateOf(1) }
    val difficulties = viewModel.difficultiesText
    val difficulty = viewModel.selectedDifficulty

    val themeState by viewModel.themeConfig.collectAsStateWithLifecycle()
    val isDark = themeState == ThemeConfig.DARK

    var menuExpanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {

         Box(modifier = Modifier.fillMaxWidth().padding(bottom = 50.dp, top = 25.dp),
             contentAlignment = Alignment.Center)
         {
             val context = LocalContext.current
             Column()
             {
                 SelectionMenu(viewModel, books, chapters, difficulties, isDark)

                 if (verses.isNotEmpty()) {
                     val verse = verses[currentIndex]
                     var verseData = ReplacingWords(verse.text, difficulty)
                     Column() {
                         Card(
                             modifier = Modifier.fillMaxWidth().padding(25.dp),
                             elevation = CardDefaults.elevatedCardElevation(10.dp),
                             border = BorderStroke(width = 2.dp, Color.DarkGray)
                         ) {
                             Row(
                                 modifier = Modifier.fillMaxWidth().padding(top = 50.dp),
                                 horizontalArrangement = Arrangement.Center
                             )
                             {
                                 Text(
                                     text = "${verse.book} ${verse.chapter}:${verse.verse}",
                                     style = MaterialTheme.typography.headlineMedium,
                                     fontWeight = FontWeight.Bold,
                                     color = MaterialTheme.colorScheme.onBackground
                                 )
                             }
                             Box(
                                 modifier = Modifier
                                     .fillMaxWidth()
                                     .padding(10.dp)
                             )
                             {
                                 if (stage == 1) {
                                     YourVerse(stage, verseData.hiddenVerse, verseData.revealedVerse)
                                 } else {
                                     YourVerse(stage, verseData.hiddenVerse, verseData.revealedVerse)
                                 }
                             }
                             Row(modifier = Modifier.fillMaxWidth().padding(25.dp),
                                 horizontalArrangement = Arrangement.End)
                             {
//                                 IconButton(onClick = {
//                                   verseData = ReplacingWords(verse.text, difficulty)
//                                 }) {
//                                     Icon(
//                                         imageVector = Icons.Rounded.Refresh,
//                                         modifier = Modifier.size(50.dp),
//                                         contentDescription = null
//                                     )
//                                 }
                                 IconButton(onClick = {
                                     if (stage == 1) {
                                         stage = 2
                                     } else {
                                         viewModel.nextVerse(verses.size)
                                         stage--
                                     }
                                 }) {
                                     Icon(
                                         imageVector = Icons.Rounded.ArrowForward,
                                         modifier = Modifier.size(50.dp),
                                         contentDescription = null
                                     )
                                 }
                             }
                         }
                     }
                 }
             }
         }

     }
 }