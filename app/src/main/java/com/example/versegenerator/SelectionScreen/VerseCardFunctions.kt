package com.example.versegenerator.SelectionScreen

import android.util.Log
import android.widget.TextView
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowColumn
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import com.example.versegenerator.ViewModels.VerseViewModel
import com.example.versegenerator.data.Verse
import com.example.versegenerator.ui.theme.Garamond
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

import kotlin.math.roundToInt



// INPUT DISABLED
data class VerseDisplayID(
    val original: String,
    val hiddenVerse: AnnotatedString,
    val hiddenWords: List<String>,
    val revealedVerse: AnnotatedString
)
fun ReplacingWordsID(text: String, difficultyLevel: String = "Easy"): VerseDisplayID {

    val difficultiesMap = mapOf(
        "Easy" to 0.25,
        "Medium" to 0.50,
        "Hard" to 0.75
    )

    val difficulty = difficultiesMap.getOrDefault(difficultyLevel, 0.25)

    var splitWords = text
        .split(" ".toRegex())
        .filter { it.isNotBlank()}

    val splitEndices = splitWords.indices.filter { i ->
        val word = splitWords[i]
        word.length >= 3 && word.any {it.isLetter() }
    }
    val count = (splitEndices.size * difficulty).roundToInt().coerceAtLeast(1)
    val indicesToReplace = splitEndices.shuffled().take(count).toSet()

    val captureWords = mutableListOf<String>()

    val hiddenVerses = buildAnnotatedString(){
        splitWords.forEachIndexed { index, word ->
            if (indicesToReplace.contains(index)) {
                captureWords.add(word)
                withStyle(style = SpanStyle(
                    fontFamily = FontFamily.Monospace, fontWeight = FontWeight.ExtraBold, letterSpacing = (-2).sp)) {
                    append("_".repeat(word.length))
                }
            } else {
                append(word)
            }
            if (index < splitWords.size - 1) append(" ")
        }
    }

    val revealedVerses = buildAnnotatedString(){
        splitWords.forEachIndexed { index, word ->
            if (indicesToReplace.contains(index)) {
                withStyle(style = SpanStyle(fontWeight = FontWeight.ExtraBold, textDecoration = TextDecoration.Underline)) {
                    append(word)
                }
            }
                else {
                    append(word)
                }
            if (index < splitWords.size - 1) append(" ")
            }
        }
    return VerseDisplayID(original = text, hiddenVerse = hiddenVerses, hiddenWords = captureWords, revealedVerse = revealedVerses)
}
@Composable
fun YourVerseID(stage: Int, first: AnnotatedString, second: AnnotatedString): Unit {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(20.dp),)
    {
        Column(verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            if (stage == 1) {
                Text(
                    text = first,
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = 25.sp,
                    lineHeight = 30.sp,
                    fontFamily = FontFamily.Serif,
                    modifier = Modifier.padding(10.dp)
                )
            } else {
                Text(
                    text = second,
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = 25.sp,
                    lineHeight = 30.sp,
                    fontFamily = FontFamily.Serif,
                    modifier = Modifier.padding(10f .dp)
                )
            }
        }
    }
}


// INPUT ENABLED
data class VerseDisplayIE(
    val original: String,
    val wordList: List<VerseWord>,
    val hiddenWords: List<String>,
    val revealedVerse: AnnotatedString
)

data class VerseWord(
    val word: String,
    val isHidden: Boolean,
    val index: Int
)



fun ReplacingWordsIE(text: String, difficultyLevel: String = "Easy"): VerseDisplayIE {

    val difficultiesMap = mapOf(
        "Easy" to 0.25,
        "Medium" to 0.50,
        "Hard" to 0.75
    )

    val difficulty = difficultiesMap.getOrDefault(difficultyLevel, 0.25)

    var splitWords = text
        .split("[ —]".toRegex())
        .filter { it.isNotBlank()}

    val splitEndices = splitWords.indices.filter { i ->
        val word = splitWords[i]
        word.length >= 3 && word.any {it.isLetter() }
    }
    val count = (splitEndices.size * difficulty).roundToInt().coerceAtLeast(1)
    val indicesToReplace = splitEndices.shuffled().take(count).toSet()

    val captureWords = mutableListOf<String>()

    val wordList = splitWords.mapIndexed { index, word ->
        val isHidden = indicesToReplace.contains(index)
        if (isHidden) captureWords.add(word)

        VerseWord(
            word = word,
            isHidden = isHidden,
            index = index
        )
    }

    val revealedVerses = buildAnnotatedString(){
        splitWords.forEachIndexed { index, word ->
            if (indicesToReplace.contains(index)) {
                withStyle(style = SpanStyle(fontWeight = FontWeight.ExtraBold, textDecoration = TextDecoration.Underline)) {
                    append(word)
                }
            }
            else {
                append(word)
            }
            if (index < splitWords.size - 1) append(" ")
        }
    }
    return VerseDisplayIE(original = text, wordList = wordList, hiddenWords = captureWords, revealedVerse = revealedVerses)
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalFoundationApi::class,
    ExperimentalMaterial3Api::class
)
@Composable
fun YourVerseIE(stage: Int, verseDisplayer: VerseDisplayIE,
                viewModel: VerseViewModel, versesOrder: List<Verse>): Unit {

    var offsetX by remember { mutableFloatStateOf(0f) }
    var stage by viewModel.stage
    val userInputs = remember(verseDisplayer) { mutableStateMapOf<Int, TextFieldValue>() }
    var selectedIndex by remember { mutableStateOf<Int?>(null) }

    val focusRequesters = remember(verseDisplayer) {
        verseDisplayer.wordList
            .filter { it.isHidden }
            .associate { it.index to FocusRequester() }
    }

    val hiddenIndices = remember(verseDisplayer) { focusRequesters.keys.sorted() }

    val refreshState = rememberPullToRefreshState()
    var isRefreshing by remember { mutableStateOf(false)}
    val coroutineScope = rememberCoroutineScope()


    Card(modifier = Modifier.wrapContentHeight()
        .offset( { IntOffset(offsetX.roundToInt(), 0)})
        .pointerInput(Unit) {
            detectHorizontalDragGestures(onDragEnd = {
                if (offsetX > 200) {
                    if (stage == 2) {
                        stage = 1
                    } else {
                        viewModel.previousVerse()
                        stage = 1
                    }
                } else if (offsetX < -200) {
                    if (stage == 1) {
                        stage = 2
                    } else {
                        viewModel.nextVerse(versesOrder.size)
                        stage = 1
                    }
                }
                offsetX = 0f
            }) { change, dragAmount ->
                change.consume()
                offsetX += dragAmount
            } },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(20.dp))
    {
        PullToRefreshBox(isRefreshing = isRefreshing, onRefresh = {
            coroutineScope.launch {
                isRefreshing = true

                stage = 1
                viewModel.reloadTrigger()

                delay(250)

                isRefreshing = false
            }

        }, state = refreshState)
        {
            Column(
                modifier = Modifier.fillMaxSize()) {
                LazyColumn(modifier = Modifier.weight(1f).fillMaxWidth().padding(horizontal = 20.dp)) {
                    item {
                        FlowRow(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)

                        ) {
                            verseDisplayer.wordList.forEach { verseWord ->
                                if (verseWord.isHidden) {
                                    val currentInput =
                                        userInputs[verseWord.index] ?: TextFieldValue("")

                                    if (stage == 2) {
                                        val filterInput = currentInput.text.filter { it.isLetter() }
                                        val filterOriginal = verseWord.word.filter { it.isLetter() }

                                        val isCorrect = filterInput.equals(
                                            filterOriginal, ignoreCase = true
                                        )

                                        Text(
                                            text = "${verseWord.word}",
                                            color = if (isCorrect) Color(0xFF7298C7) else Color(
                                                0xFF80011F
                                            ),
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = Garamond,
                                            fontSize = 25.sp,
                                            lineHeight = 35.sp,
                                            textDecoration = TextDecoration.Underline
                                        )
                                    } else {
                                        val customSelectionColors = TextSelectionColors(
                                            handleColor = Color(0xFF7298C7).copy(alpha = 0.4f), // The color of the cursor handles
                                            backgroundColor = Color(0xFF7298C7).copy(alpha = 0.4f) // The highlight color
                                        )
                                        val charWidth =
                                            12.dp // Approximate width of a Serif char at 25sp
                                        val minWidth =
                                            (verseWord.word.length.coerceAtLeast(5) * charWidth)

                                        Box(
                                            modifier = Modifier
                                                .width(minWidth),
                                            contentAlignment = Alignment.BottomCenter
                                        ) {
                                            CompositionLocalProvider(LocalTextSelectionColors provides customSelectionColors) {
                                                BasicTextField(
                                                    value = currentInput,
                                                    onValueChange = { newValue ->
                                                        val filteredValue =
                                                            newValue.text.filter { it.isLetter() }
                                                        userInputs[verseWord.index] =
                                                            newValue.copy(text = filteredValue)
                                                    },
                                                    cursorBrush = SolidColor(Color(0xFF4A6572)),
                                                    keyboardOptions = KeyboardOptions(
                                                        keyboardType = KeyboardType.Text,
                                                        imeAction = ImeAction.Next
                                                    ),
                                                    keyboardActions = KeyboardActions(
                                                        onNext = {
                                                            val currentIndex =
                                                                hiddenIndices.indexOf(verseWord.index)
                                                            val nextIndex =
                                                                hiddenIndices.getOrNull(currentIndex + 1)
                                                            if (nextIndex != null) {
                                                                focusRequesters[nextIndex]?.requestFocus()
                                                            }
                                                            if (nextIndex == null) {
                                                                stage = 2
                                                            }
                                                        }
                                                    ),
                                                    modifier = Modifier
                                                        .then(focusRequesters[verseWord.index]?.let {
                                                            Modifier.focusRequester(it)
                                                        } ?: Modifier)
                                                        .onFocusChanged { focusState ->
                                                            if (focusState.isFocused) {
                                                                selectedIndex = verseWord.index
                                                            }
                                                        }
                                                        .width(IntrinsicSize.Min)
                                                        .align(Alignment.BottomCenter),
                                                    textStyle = MaterialTheme.typography.headlineSmall.copy(
                                                        textAlign = TextAlign.Center,
                                                        color = Color(0xFF4A6572),
                                                        fontFamily = Garamond,
                                                        fontSize = 25.sp,
                                                        lineHeight = 35.sp,
                                                        lineHeightStyle = LineHeightStyle(
                                                            alignment = LineHeightStyle.Alignment.Center,
                                                            trim = LineHeightStyle.Trim.Both
                                                        ),
                                                        platformStyle = PlatformTextStyle(
                                                            includeFontPadding = false
                                                        )
                                                    ),
                                                    decorationBox = { innerTextField ->

                                                        val isFocused =
                                                            remember { mutableStateOf(false) }

                                                        val lineColor =
                                                            if (currentInput.text.isNotEmpty()) {
                                                                Color(0xFF5D8FE7) // filled = blue
                                                            } else {
                                                                Color.LightGray
                                                            }

                                                        Column(
                                                            modifier = Modifier
                                                                .pointerInput(verseWord.index) {
                                                                    awaitEachGesture {
                                                                        val down =
                                                                            awaitFirstDown(pass = PointerEventPass.Initial)
                                                                        val secondDown =
                                                                            withTimeoutOrNull(
                                                                                viewConfiguration.doubleTapTimeoutMillis
                                                                            ) {
                                                                                awaitFirstDown(pass = PointerEventPass.Initial)
                                                                            }

                                                                        if (secondDown != null) {
                                                                            val text =
                                                                                userInputs[verseWord.index]?.text
                                                                                    ?: ""
                                                                            userInputs[verseWord.index] =
                                                                                TextFieldValue(
                                                                                    text = text,
                                                                                    selection = TextRange(
                                                                                        0,
                                                                                        text.length
                                                                                    )
                                                                                )
                                                                            focusRequesters[verseWord.index]?.requestFocus()
                                                                            secondDown.consume()
                                                                        }
                                                                    }
                                                                }
                                                                .widthIn(min = minWidth),
                                                            horizontalAlignment = Alignment.CenterHorizontally
                                                        ) {

                                                            Box(
                                                                contentAlignment = Alignment.Center
                                                            ) {
                                                                innerTextField()
                                                            }

                                                            // 🔥 CLEAN UNDERLINE
                                                            Box(
                                                                modifier = Modifier
                                                                    .fillMaxWidth()
                                                                    .height(if (currentInput.text.isNotEmpty()) 3.dp else 2.dp)
                                                                    .background(
                                                                        lineColor,
                                                                        RoundedCornerShape(50)
                                                                    )
                                                            )
                                                        }
                                                    }
                                                )
                                            }
                                        }
                                    }
                                } else {
                                    Text(
                                        text = verseWord.word,
                                        fontFamily = Garamond,
                                        fontSize = 25.sp,
                                        lineHeight = 35.sp
                                    )
                                }
                            }
                        }
                    }
                }
                HorizontalDivider(modifier = Modifier.height(1.dp).fillMaxWidth(), color = Color.Gray)
                ControlButtons(
                        viewModel = viewModel,
                        versesOrder = versesOrder,
                        selectedIndex = selectedIndex,
                        verseDisplayer = verseDisplayer,
                        userInputs = userInputs,
                        focusRequesters = focusRequesters
                )
            }
        }
    }
    }