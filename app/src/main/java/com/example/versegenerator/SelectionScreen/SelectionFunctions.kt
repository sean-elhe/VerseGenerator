package com.example.versegenerator.SelectionScreen

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Sync
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.PopupProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.versegenerator.ViewModels.InputConfig
import com.example.versegenerator.ViewModels.StyleConfig
import com.example.versegenerator.ViewModels.ThemeConfig
import com.example.versegenerator.ViewModels.VerseViewModel
import com.example.versegenerator.data.Verse
import kotlin.math.max

import kotlin.math.roundToInt

@Composable
fun SearchableSelector(
    query: String,
    onQueryChange: (String) -> Unit,
    onDone: () -> Unit,
    hintText: String,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    focusRequester: FocusRequester = remember { FocusRequester() },
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    items: @Composable ColumnScope.() -> Unit
) {
//    val focusRequester = remember { FocusRequester() }

    Box(
        modifier = modifier.fillMaxHeight(),
        contentAlignment = Alignment.CenterStart
    ) {
        BasicTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier
                .focusRequester(focusRequester)
                .onFocusChanged {
                    // This logic allows the parent to control
                    // if focus should trigger an expansion
                    if (it.isFocused && query.isNotEmpty()) onExpandedChange(true)
                }
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            singleLine = true,
            keyboardOptions = keyboardOptions,
            keyboardActions = KeyboardActions(onDone = { onDone() }),
            textStyle = TextStyle(
                fontFamily = FontFamily.Serif,
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.onPrimary
            ),
            decorationBox = { innerTextField ->
                Box(
                    contentAlignment = Alignment.CenterStart,
                    modifier = modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        focusRequester.requestFocus()
                        onExpandedChange(true)
                    }) {
                    if (query.isEmpty()) {
                        Text(
                            text = hintText,
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f)
                        )
                    }
                    innerTextField()
                }
            }
        )

        // The Tiny Anchor for the Dropdown
        Box(Modifier.align(Alignment.BottomStart).fillMaxWidth().height(0.dp)) {
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = onDismissRequest,
                properties = PopupProperties(focusable = false),
                offset = DpOffset(x = 10.dp, y = 5.dp),
                modifier = Modifier
                    .widthIn(min = 150.dp)
                    .heightIn(max = 250.dp)
                    .background(Color.White)
            ) {
                items()
            }
        }
    }
}

@Composable
fun SelectionSearcher(
    modifier: Modifier = Modifier,
    viewModel: VerseViewModel,
    book: String,
    chapter: Int
) {
    val focusManager = LocalFocusManager.current

    val bookFocusRequester = remember { FocusRequester() }
    val chapterFocusRequester = remember { FocusRequester() }

    val books by viewModel.filteredBooks.collectAsStateWithLifecycle()
    val chapters by viewModel.filteredChapters.collectAsStateWithLifecycle()

    var firstExpanded by remember { mutableStateOf(false) }
    var secondExpanded by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 5.dp, end = 25.dp)
                .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(8.dp))
                .border(2.dp, Color.Black, RoundedCornerShape(8.dp))
                .height(50.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // --- BOOK SELECTOR ---
            SearchableSelector(
                focusRequester = bookFocusRequester,
                modifier = Modifier.weight(0.6f),
                query = viewModel.bookQuery,
                onQueryChange = {
                    viewModel.bookChange(it)
                    // Only expand the book list when the user is actually typing
                    firstExpanded = it.isNotEmpty()
                },
                onDone = {
                    val match = books.find { it.equals(viewModel.bookQuery, ignoreCase = true) }
                        ?: books.firstOrNull()
                    if (match != null) {
                        viewModel.updateBook(match)
                        viewModel.bookChange("")
                        firstExpanded = false

                        chapterFocusRequester.requestFocus()
                        secondExpanded = true
                    }
                },
                hintText = book,
                expanded = firstExpanded,
                onExpandedChange = {
                    if (viewModel.bookQuery.isNotEmpty()) firstExpanded = it },
                onDismissRequest = { firstExpanded = false },
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Done
                )
            ) {
                books.forEach { bookName ->
                    DropdownMenuItem(
                        text = { Text(bookName, color = Color.Black) },
                        onClick = {
                            viewModel.updateBook(bookName)
                            viewModel.updateChapter(1)
                            viewModel.bookChange("")
                            firstExpanded = false

                            chapterFocusRequester.requestFocus()
                            secondExpanded = true
                        }
                    )
                }
            }

            // --- CHAPTER SELECTOR ---
            SearchableSelector(
                focusRequester = chapterFocusRequester,
                modifier = Modifier.weight(0.4f),
                query = viewModel.chapterQuery,
                onQueryChange = {
                    if (it.length <= 2) {
                        viewModel.chapterChange(it)
                        // Chapters expand immediately on any change
                        secondExpanded = true
                    }
                },
                onDone = {
                    val inputInt = viewModel.chapterQuery.toIntOrNull()
                    if (inputInt != null && chapters.contains(inputInt)) {
                        viewModel.updateChapter(inputInt)
                        secondExpanded = false
                        viewModel.chapterChange("")
                        focusManager.clearFocus()
                    }
                },
                hintText = "[$chapter]",
                expanded = secondExpanded,
                // For chapters, we allow the dropdown to trigger on focus too
                onExpandedChange = { secondExpanded = it },
                onDismissRequest = { secondExpanded = false },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                )
            ) {
                chapters.forEach { chapterNum ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = chapterNum.toString(),
                                color = Color.Black,
                                fontFamily = FontFamily.Serif
                            )
                        },
                        onClick = {
                            viewModel.updateChapter(chapterNum)
                            viewModel.chapterChange("")
                            secondExpanded = false
                            focusManager.clearFocus()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun SelectionFavorites(modifier: Modifier = Modifier){
    var menuExpanded by remember { mutableStateOf(false) }

    Box(modifier
        .fillMaxWidth()
        .padding(15.dp)) {
        IconButton(onClick = { menuExpanded = true}) {
            Icon(Icons.Default.Favorite, contentDescription = "Open Favourites")
        }
    }
}

@Composable
fun SelectionMenu(modifier: Modifier = Modifier, viewModel: VerseViewModel, books: List<String>, book: String,
                  chapters: List<String>, chapter: Int, difficulties: List<String>,
                  difficulty: String, translation: String, isDark: Boolean,
                  isRandom: Boolean, isInput: Boolean, verseOrder: List<Verse>, stage: Int
){
    var menuExpanded by remember { mutableStateOf(false) }
    var stage by viewModel.stage

    Box(modifier
        .fillMaxWidth()
        .padding(15.dp)
        .padding(start = 25.dp)) {

        IconButton(onClick = { menuExpanded = true }) {
            Icon(Icons.Default.Settings, contentDescription = "Open Settings")
        }

        DropdownMenu(
            expanded = menuExpanded,
            onDismissRequest = { menuExpanded = false },
            modifier = Modifier
                .width(300.dp)
                .border(2.dp, Color.Black, RoundedCornerShape(8.dp)),
            containerColor = MaterialTheme.colorScheme.surface
        ) {
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(15.dp),
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(15.dp),
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(15.dp),
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
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
                .padding(horizontal = 50.dp),
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
            modifier = Modifier
                .size(100.dp)
                .padding(5.dp),
            contentDescription = null
        )
    }
}

@Composable
fun SelectionButtons(viewModel: VerseViewModel, versesOrder: List<Verse>){
    var stage by viewModel.stage

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(25.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    )
    {
        SelectionButton(clicked = {
            if (stage == 2) {
                stage = 1
            } else {
                viewModel.previousVerse()
                stage = 1
            }
        }, icon = Icons.Filled.KeyboardArrowLeft)
        SelectionButton(clicked = {
            stage = 1
            viewModel.reloadTrigger()
        }, icon = Icons.Filled.Sync)
        SelectionButton(clicked = {
            if (stage == 1) {
                stage = 2
            } else {
                viewModel.nextVerse(versesOrder.size)
                stage = 1
            }
        }, icon = Icons.Filled.KeyboardArrowRight)
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
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
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
                            color = if (isCorrect) Color(0xFFA1E1BC) else Color(0xFF80011F),
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = FontFamily.Serif,
                            fontSize = 20.sp,
                            lineHeight = 20.sp,
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
                                .then(
                                    focusRequesters[verseWord.index]?.let
                                { Modifier.focusRequester(it) } ?: Modifier)
                                .width(IntrinsicSize.Min)
                                .widthIn(min = (verseWord.word.length * 13).dp),
                            decorationBox = { innerTextField ->
                                Box(
                                    modifier = Modifier
                                        .padding(bottom = 2.dp)
                                        .drawBehind {
                                            val strokeWidth = 2.dp.toPx()
                                            val y = size.height - 3.dp.toPx()
                                            drawLine(
                                                color = Color.Black,
                                                start = Offset(0f, y),
                                                end = Offset(size.width, y),
                                                strokeWidth = strokeWidth
                                            )
                                        }
                                ) {
                                    innerTextField()
                                }
                            },
                            textStyle = TextStyle(
                                textAlign = TextAlign.Center,
                                fontFamily = FontFamily.Serif,
                                fontSize = 20.sp,
                                lineHeight = 20.sp,
                                lineHeightStyle = LineHeightStyle(
                                    alignment = LineHeightStyle.Alignment.Center,
                                    trim = LineHeightStyle.Trim.Both
                                ),
                                platformStyle = PlatformTextStyle(
                                    includeFontPadding = false
                                )
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
