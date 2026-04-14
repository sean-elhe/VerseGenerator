package com.example.versegenerator.SelectionScreen

import android.text.style.UnderlineSpan
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.versegenerator.ViewModels.InputConfig
import com.example.versegenerator.ViewModels.StyleConfig
import com.example.versegenerator.ViewModels.ThemeConfig
import com.example.versegenerator.ViewModels.VerseViewModel

import kotlin.math.roundToInt

@Composable
fun SelectionMenu(viewModel: VerseViewModel, books: List<String>, book: String,
                  chapters: List<String>, chapter: Int, difficulties: List<String>,
                  difficulty: String, translation: String, isDark: Boolean,
                  isRandom: Boolean, isInput: Boolean, stage: Int
){
    var menuExpanded by remember { mutableStateOf(false) }
    var stage by viewModel.stage

    Box(modifier = Modifier.padding(15.dp)) {

        IconButton(onClick = { menuExpanded = true }) {
            Icon(Icons.Default.Settings, contentDescription = "Open Settings")
        }

        // The actual Dropdown Container
        DropdownMenu(
            expanded = menuExpanded,
            onDismissRequest = { menuExpanded = false },
            modifier = Modifier.width(300.dp),
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            // We wrap your Column in a Column with padding inside the Menu
            Column(modifier = Modifier.padding(5.dp)) {

                // --- YOUR EXISTING UI ---
                SelectionDropdown(
                    label = "Translation",
                    options = viewModel.translations,
                    selectedOption = translation,
                    onOptionSelected = { newTranslation ->
                        viewModel.updateTranslation(newTranslation)
                        stage = 1
                    }
                )

                SelectionDropdown(
                    label = "Difficulty",
                    options = difficulties,
                    selectedOption = difficulty,
                    onOptionSelected = { newDifficulty ->
                        viewModel.updateDifficulty(newDifficulty)
                        stage = 1
                    }
                )

                SelectionDropdown(
                    label = "Book",
                    options = books,
                    selectedOption = book,
                    onOptionSelected = { newBook ->
                        viewModel.updateBook(newBook)
                        viewModel.updateChapter(1)
                        viewModel.resetIndex()
                        stage = 1
                    }
                )

                SelectionDropdown(
                    label = "Chapter",
                    options = chapters,
                    selectedOption = chapter.toString(),
                    onOptionSelected = { newChapter ->
                        viewModel.updateChapter(newChapter.toInt())
                        viewModel.resetIndex()
                        stage = 1

                    }
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth().padding(15.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(if (isInput) "Input" else "Auto", style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.padding(10.dp))
                    Switch(
                        checked = isInput,
                        onCheckedChange = { isChecked ->
                            val newStyle = if (isChecked) InputConfig.ENABlED else InputConfig.DISABLED
                            viewModel.updateInput(newStyle)
                            viewModel.resetIndex()
                        }
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth().padding(15.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(if (isRandom) "Random" else "Order", style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.padding(10.dp))
                    Switch(
                        checked = isRandom,
                        onCheckedChange = { isChecked ->
                            val newStyle = if (isChecked) StyleConfig.RANDOM else StyleConfig.ORDER
                            viewModel.updateStyle(newStyle)
                            viewModel.resetIndex()
                        }
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth().padding(15.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(if (isDark) "Dark" else "Light", style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.padding(10.dp))
                    Switch(
                        checked = isDark,
                        onCheckedChange = { isChecked ->
                            val newTheme = if (isChecked) ThemeConfig.DARK else ThemeConfig.LIGHT
                            viewModel.updateTheme(newTheme)
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectionDropdown(
    label: String, options: List<String>, selectedOption: String, onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedOption,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth().padding(horizontal = 50.dp),
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun SelectionButton(clicked: () -> Unit, icon: ImageVector) {
    OutlinedIconButton(
        onClick = clicked,
        modifier = Modifier.padding(5.dp),
//        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(width = 2.dp, color = Color.DarkGray)
    ) {
        Icon(
            imageVector = icon,
            modifier = Modifier.size(100.dp).padding(5.dp),
            contentDescription = null
        )
    }
}

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

data class ComparisonResult(
    val correctCount: Int,
    val incorrectCount: Int,
    val totalCount: Int
)

fun ReplacingWordsIE(text: String, difficultyLevel: String = "Easy"): VerseDisplayIE {

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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun YourVerseIE(stage: Int, verseDisplayer: VerseDisplayIE): Unit {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(20.dp))
    {
        val userInputs = remember(verseDisplayer) { mutableStateMapOf<Int, String>() }

        val focusRequesters = remember(verseDisplayer) {
            verseDisplayer.wordList
                .filter { it.isHidden }
                .associate { it.index to FocusRequester() }
        }
        val hiddenIndices = remember(verseDisplayer) { focusRequesters.keys.sorted() }

        FlowRow(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            verseDisplayer.wordList.forEach { verseWord ->
                if (verseWord.isHidden) {
                    val currentInput = userInputs[verseWord.index] ?: ""

                    if (stage == 2) {
                        val filterInput = currentInput.filter { it.isLetter() }
                        val filterOriginal = verseWord.word.filter { it.isLetter() }

                        val isCorrect = filterInput.equals(
                            filterOriginal, ignoreCase = true)

                        Text(
                            text = verseWord.word,
                            color = if (isCorrect) Color.Green else Color.Red,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = FontFamily.Serif,
                            fontSize = 20.sp,
                            lineHeight = 25.sp,
                            textDecoration = TextDecoration.Underline
                        )
                    } else {
                        BasicTextField(
                            value = currentInput,
                            onValueChange = { newValue ->
                                val filteredValue = newValue.filter { it.isLetter() }
                                userInputs[verseWord.index] = filteredValue
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Next
                            ),
                            keyboardActions = KeyboardActions(
                                onNext = {
                                    val currentIndex = hiddenIndices.indexOf(verseWord.index)
                                    val nextIndex = hiddenIndices.getOrNull(currentIndex + 1)
                                    if (nextIndex != null) {
                                        focusRequesters[nextIndex]?.requestFocus()
                                    }
                                }
                            ),
                            modifier = Modifier
                                .then(focusRequesters[verseWord.index]?.let
                                { Modifier.focusRequester(it) } ?: Modifier)
                                .width(IntrinsicSize.Min)
                                .widthIn(min = (verseWord.word.length * 12).dp),
                            decorationBox = { innerTextField ->
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    innerTextField()
                                    Box(Modifier.fillMaxWidth().height(2.dp).background(Color.Black))
                                }
                            },
                            textStyle = TextStyle(
                                textAlign = TextAlign.Center,
                                fontFamily = FontFamily.Serif,
                                fontSize = 20.sp,
                                lineHeight = 35.sp,
                            ))
                    }
                } else {
                    Text(
                        text = verseWord.word,
                        fontFamily = FontFamily.Serif,
                        fontSize = 20.sp,
                        lineHeight = 25.sp,)
                }
            }
        }

    }
}

fun CompareWords(wordList: List<VerseWord>, userInputs: Map<Int, String>
): ComparisonResult {
    val totalCount = wordList.filter { it.isHidden }
    var correct = 0
    var incorrect = 0

    totalCount.forEach { verseWord ->
        val userInput = userInputs[verseWord.index]?.trim() ?: ""
        val original = verseWord.word.trim()

        if (userInput.equals(original, ignoreCase = true)) {
            correct++
        } else {
            incorrect++
        }
    }
    return ComparisonResult(
        correctCount = correct,
        incorrectCount = incorrect,
        totalCount = totalCount.size
    )
}
