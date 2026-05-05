package com.example.versegenerator.SelectionScreen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
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
import com.example.versegenerator.ui.theme.PlayFair
import kotlin.collections.forEach
import kotlin.compareTo



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

    Box(
        modifier = modifier
            .height(48.dp),
        contentAlignment = Alignment.Center
    ) {
        BasicTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier
                .focusRequester(focusRequester)
                .onFocusChanged {
                    if (it.isFocused && query.isNotEmpty()) onExpandedChange(true)
                }
                .fillMaxWidth()
                .padding(horizontal = 5.dp),
            singleLine = true,
            keyboardOptions = keyboardOptions,
            keyboardActions = KeyboardActions(onDone = { onDone() }),
            textStyle = TextStyle(
                fontFamily = FontFamily.SansSerif,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 0.5.sp,
                color = MaterialTheme.colorScheme.onPrimary,
            ),
            decorationBox = { innerTextField ->
                    Box(modifier = Modifier
                            .fillMaxWidth()
                            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {
                                focusRequester.requestFocus()
                                onExpandedChange(true) }) {
                        if (query.isEmpty()) {
                            Text(
                                text = hintText,
                                modifier = Modifier.fillMaxWidth(),
                                fontFamily = FontFamily.SansSerif,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
//                                color = Color(0xFF7298C7),
                                letterSpacing = 0.5.sp
                            )
                        }
                        innerTextField()
                    }
                }
        )

        // The Tiny Anchor for the Dropdown
        Box(Modifier
            .align(Alignment.BottomStart)
            .fillMaxWidth()
            .height(0.dp)) {
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
    chapter: Int,
    isFavorite: Boolean,
    shortcuts: List<VerseViewModel.VerseShortcut>
) {

    val focusManager = LocalFocusManager.current


    val bookFocusRequester = remember { FocusRequester() }
    val chapterFocusRequester = remember { FocusRequester() }

    val books by viewModel.filteredBooks.collectAsStateWithLifecycle()
    val books2 by viewModel.finalBooksToDisplay.collectAsStateWithLifecycle()
    val chapters by viewModel.filteredChapters.collectAsStateWithLifecycle()

    var firstExpanded by remember { mutableStateOf(false) }
    var secondExpanded by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxWidth()) {
        Row(horizontalArrangement = Arrangement.Center) {
            Row(modifier = Modifier.weight(0.7f).padding(start = 60.dp),
                horizontalArrangement = Arrangement.Center) {

                // --- BOOK SELECTOR ---
                SearchableSelector(
                    focusRequester = bookFocusRequester,
                    modifier = Modifier
                        .width(IntrinsicSize.Max)
                        .onFocusChanged(
                        { viewModel.bookQuery = "" }
                    ),
                    query = viewModel.bookQuery,
                    onQueryChange = {
                        viewModel.bookChange(it)
                        firstExpanded = true
                    },
                    onDone = {
                        val match = books.find { it.equals(viewModel.bookQuery, ignoreCase = true) }
                            ?: books.firstOrNull()
                        if (match != null) {
                            viewModel.updateBook(match)
                            viewModel.bookChange("")
                            viewModel.resetIndex()
                            firstExpanded = false

                            chapterFocusRequester.requestFocus()
                            secondExpanded = true
                        }
                    },
                    hintText = book,
                    expanded = firstExpanded,
                    onExpandedChange = { firstExpanded = it },
                    onDismissRequest = {
                        firstExpanded = false
                    },
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
                                viewModel.resetIndex()
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
                    modifier = Modifier.weight(0.2f)
                        .onFocusChanged( {
                            viewModel.chapterQuery = ""
                        }),
                    query = viewModel.chapterQuery,
                    onQueryChange = {
                        if (it.length <= 2) {
                            viewModel.chapterChange(it)
                            secondExpanded = true
                        }
                    },
                    onDone = {
                        val inputInt = viewModel.chapterQuery.toIntOrNull()
                        if (inputInt != null && chapters.contains(inputInt)) {
                            viewModel.updateChapter(inputInt)
                            viewModel.chapterChange("")
                            secondExpanded = false
                            viewModel.resetIndex()
                            focusManager.clearFocus()
                        }
                    },
                    hintText = "$chapter",
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
                                    fontFamily = FontFamily.Serif,
                                )
                            },
                            onClick = {
                                viewModel.updateChapter(chapterNum)
                                viewModel.chapterChange("")
                                secondExpanded = false

                                viewModel.resetIndex()
                                focusManager.clearFocus()
                            }
                        )
                    }
                }
            }
            SelectionFavorites(
                modifier = Modifier.weight(0.3f),
                isFavorite = isFavorite,
                onToggle = { viewModel.toggleSaved() },
                shortcuts = shortcuts,
                onShortcutClicked = {
                        shortcut ->
                    viewModel.jumpToShortcut(shortcut)
                    viewModel.resetIndex()
                }
            )
        }
    }
}


@Composable
fun SelectionFavorites(
    modifier: Modifier = Modifier,
    isFavorite: Boolean,
    onToggle: () -> Unit,
    shortcuts: List<VerseViewModel.VerseShortcut>,
    onShortcutClicked: (VerseViewModel.VerseShortcut) -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }
    val haptic = LocalHapticFeedback.current

    Box(
        modifier
            .fillMaxWidth().padding(end = 15.dp),
        contentAlignment = Alignment.CenterEnd)
    {
        LongClickIconButton(
            icon = if (isFavorite) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
            contentDescription = "Saved chapters!",
            tint = if (isFavorite) Color(0xFF7298C7) else Color.Gray,
            onClick = { menuExpanded = true },
            onLongClick = {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onToggle() }
        )

        DropdownMenu(
            expanded = menuExpanded,
            onDismissRequest = { menuExpanded = false }
        ) {
            if (shortcuts.isEmpty()) {
                DropdownMenuItem(
                    text = {
                        Text(
                            "No saved chapters",
                            style = MaterialTheme.typography.bodySmall
                        )
                    },
                    onClick = { menuExpanded = false },
                    enabled = false
                )
            } else {
                shortcuts.forEach { shortcut ->
                    DropdownMenuItem(
                        text = { Text("${shortcut.book} ${shortcut.chapter} (${shortcut.translation})") },
                        onClick = {
                            onShortcutClicked(shortcut)
                            menuExpanded = false
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LongClickIconButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    tint: Color = LocalContentColor.current
) {
    Box(
        modifier = Modifier
            .minimumInteractiveComponentSize() // Ensures 48dp touch target
            .size(48.dp) // Standard IconButton size
            .clip(CircleShape) // For the ripple effect shape
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = tint
        )
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
        .fillMaxWidth().padding(start = 15.dp)) {
        IconButton(onClick = { menuExpanded = true }) {
            Icon(Icons.Outlined.Menu, contentDescription = "Open Settings",
                tint = Color(0xFF4A6572))
        }

        DropdownMenu(
            expanded = menuExpanded,
            onDismissRequest = { menuExpanded = false },
            modifier = Modifier
                .width(300.dp)
                .border(1.dp, Color.Gray, RoundedCornerShape(8.dp)),
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

//                SelectionDropdown(
//                    label = "Book",
//                    options = books,
//                    selectedOption = book,
//                    onOptionSelected = { newBook ->
//                        viewModel.updateBook(newBook)
//                        viewModel.updateChapter(1)
//                        viewModel.resetIndex()
//                        stage = 1
//                    }
//                )
//
//                SelectionDropdown(
//                    label = "Chapter",
//                    options = chapters,
//                    selectedOption = chapter.toString(),
//                    onOptionSelected = { newChapter ->
//                        viewModel.updateChapter(newChapter.toInt())
//                        viewModel.resetIndex()
//                        stage = 1
//
//                    }
//                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 15.dp, vertical = 5.dp),
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
                        .padding(horizontal = 15.dp, vertical = 5.dp),
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
                        .padding(horizontal = 15.dp, vertical = 5.dp),
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
