package com.example.versegenerator.SelectionScreen

import android.R
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowRight
import androidx.compose.material.icons.filled.ArrowRightAlt
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.outlined.ArrowRightAlt
import androidx.compose.material.icons.outlined.ChevronLeft
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.Sync
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.versegenerator.ViewModels.VerseViewModel
import com.example.versegenerator.data.Verse

@Composable
fun SelectionButton(clicked: () -> Unit, icon: ImageVector, tint: Color) {
    Card(
//        elevation = CardDefaults.elevatedCardElevation(15.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = CircleShape)
    {
        IconButton(
            onClick = clicked,
            modifier = Modifier
                .size(44.dp)
                .shadow(4.dp, RoundedCornerShape(14.dp))
                .clip(RoundedCornerShape(14.dp))
                .background(Color.White)
        ) {
            Icon(
                imageVector = icon,
                tint = tint,
                contentDescription = null
            )
        }
    }
}

@Composable
fun HintButton(selectedIndex: Int?, verseDisplayer: VerseDisplayIE,
               userInputs: SnapshotStateMap<Int, TextFieldValue>,
               focusRequesters: Map<Int, FocusRequester>
){
    Card(
//        elevation = CardDefaults.elevatedCardElevation(15.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = CircleShape) {
        IconButton(
            onClick = {
                val index = selectedIndex ?: return@IconButton

                val verseWord = verseDisplayer.wordList.firstOrNull { it.index == index }
                    ?: return@IconButton

                val answer = verseWord.word.filter { it.isLetter() }
                val current = userInputs[index]?.text ?: ""

                if (current.length < answer.length) {
                    val newText = answer.take(current.length + 1)

                    userInputs[index] = TextFieldValue(
                        text = newText,
                        selection = TextRange(newText.length)
                    )

                    focusRequesters[index]?.requestFocus()
                }
            },
            modifier = Modifier
            .size(44.dp)
            .shadow(4.dp, RoundedCornerShape(14.dp))
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White)
        ) {
            Icon(
                imageVector = Icons.Outlined.Lightbulb,
                contentDescription = null,
                tint = Color(0xFF7298C7),
                modifier = Modifier.size(26.dp)
            )
        }
    }
}

@Composable
fun ControlButtons(viewModel: VerseViewModel, versesOrder: List<Verse>,
                   selectedIndex: Int?, verseDisplayer: VerseDisplayIE,
                   userInputs: SnapshotStateMap<Int, TextFieldValue>,
                   focusRequesters: Map<Int, FocusRequester>) {
    var stage by viewModel.stage

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.surface)
            .height(75.dp)
            .padding(horizontal = 4.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    )
    {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(0.2f)) {
            SelectionButton(clicked = {
                stage = 1
                viewModel.reloadTrigger()
            }, icon = Icons.Outlined.Sync, tint = Color(0xFF7298C7))
        }

//        Spacer(modifier = Modifier.width(25.dp))

        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(0.6f)) {
            Button(
                onClick = {
                    if (stage == 1) {
                        stage = 2
                    } else {
                        viewModel.nextVerse(versesOrder.size)
                        stage = 1
                    }
                },
                modifier = Modifier
                    .border(1.dp, Color.Gray, RoundedCornerShape(18.dp))
                    .height(50.dp)
                    .width(150.dp),
                shape = RoundedCornerShape(18.dp),
                contentPadding = PaddingValues(0.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF7298C7),
                    contentColor = Color.White
                )
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .height(3.dp)
                            .offset(x = 25.dp)
                            .width(50.dp)
                            .background(Color.White)
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowRight,
                        contentDescription = null,
                        modifier = Modifier.size(50.dp)
                    )
                }
            }
        }
//        Spacer(modifier = Modifier.width(25.dp))

        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(0.2f)) {
            HintButton(
                selectedIndex, verseDisplayer,
                userInputs, focusRequesters
            )
        }
    }
}

//            SelectionButton(clicked = {
//                if (stage == 2) {
//                    stage = 1
//                } else {
//                    viewModel.previousVerse()
//                    stage = 1
//                }
//            }, icon = Icons.Outlined.Lightbulb,
//                tint = Color(0xFF7298C7))
//        }

//        Column(horizontalAlignment = Alignment.CenterHorizontally) {
//            SelectionButton(clicked = {
//                if (stage == 1) {
//                    stage = 2
//                } else {
//                    viewModel.nextVerse(versesOrder.size)
//                    stage = 1
//                }
//            }, icon =
//                Icons.Default.ArrowForward,
//                tint = Color(0xFF4A6572))
//            Spacer(modifier = Modifier.padding(5.dp))
//            Text("Next", fontSize = 12.sp)
//        }
//    }
