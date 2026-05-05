package com.example.versegenerator.SelectionScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowColumn
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.versegenerator.ViewModels.InputConfig
import com.example.versegenerator.ViewModels.StyleConfig
import com.example.versegenerator.ViewModels.ThemeConfig
import com.example.versegenerator.ViewModels.VerseViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ScoreScreen(viewModel: VerseViewModel){

    LaunchedEffect(Unit) {
            viewModel.resetIndex()
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.resetHints()
            viewModel.resetScores()
        }
    }

    val hintsCount by viewModel.hintsUsed.collectAsStateWithLifecycle()
    val themeState by viewModel.themeConfig.collectAsStateWithLifecycle()

    val difficulty by viewModel.selectedDifficulty.collectAsState("Easy")
    val translation by viewModel.selectedTranslation.collectAsState("NIV")
    val book by viewModel.selectedBook.collectAsState("Genesis")
    val chapter by viewModel.selectedChapter.collectAsState(1)

    val score = viewModel.quizScore

    if (score == null) {
        Text("No score yet")
        return
    }

    LazyColumn (modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp)
    ) {
        item {
            val progress =
                if (score.totalWords == 0) 0f
                else score.totalCorrect.toFloat() / score.totalWords

            Column(
                modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "${book} ${chapter}", textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 20.dp),
                    fontSize = 25.sp, fontWeight = FontWeight.Bold
                )

                Text("Translation: ${translation}")
                Text("Difficulty: ${difficulty}")
                Text("Hints used: ${hintsCount}")
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    "${score.totalCorrect}/${score.totalWords} | " +
                            "(${String.format("%.1f", score.percentage)}%) | " +
                            "${viewModel.formatTime(score.totalTime)}",
                    fontSize = 20.sp, fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.padding(vertical = 12.dp))
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp),
                    color = Color(0xFF7298C7),
                    trackColor = Color(0xFF7298C7).copy(alpha = 0.5f),
                    gapSize = - 5.dp,          // Removes the gap between active and inactive parts
                    drawStopIndicator = {}    // Removes the little "stop" square at the end
                )
            }
        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
    }

        items(score.verseScores.sortedBy { it.verseNumber })  { verseScore ->

                val progress =
                    if (verseScore.total == 0) 0f
                    else verseScore.correct.toFloat() / verseScore.total

                Column(modifier = Modifier.padding(vertical = 12.dp)) {
                    Text(
                        "${chapter}:${verseScore.verseNumber} — " +
                                "${verseScore.correct}/${verseScore.total} | " +
                                "(${(progress * 100).toInt()}%) | " +
                                "${viewModel.formatTime(verseScore.timeMillis)}"
                    )

                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp),
                        color = Color(0xFF7298C7),
                        trackColor = Color(0xFF7298C7).copy(alpha = 0.5f),
                        strokeCap = StrokeCap.Round,
                        gapSize = - 5.dp,          // Removes the gap between active and inactive parts
                        drawStopIndicator = {}    // Removes the little "stop" square at the end
                    )
                }
            }
        }
    }
