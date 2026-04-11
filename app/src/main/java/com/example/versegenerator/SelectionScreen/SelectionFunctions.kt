package com.example.versegenerator.SelectionScreen

import android.graphics.drawable.Icon
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.Card
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
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.versegenerator.ViewModels.StyleConfig
import com.example.versegenerator.ViewModels.ThemeConfig
import com.example.versegenerator.ViewModels.VerseViewModel
import kotlinx.coroutines.flow.StateFlow

import kotlin.math.roundToInt

data class VerseDisplay(
    val original: String,
    val hiddenVerse: AnnotatedString,
    val hiddenWords: List<String>,
    val revealedVerse: AnnotatedString
)

@Composable
fun SelectionMenu(viewModel: VerseViewModel,
                  books: List<String>,
                  book: String,
                  chapters: List<String>,
                  chapter: Int,
                  difficulties: List<String>,
                  difficulty: String,
                  translation: String,
                  isDark: Boolean,
                  isRandom: Boolean,
                  currentIndex: Int){
    var menuExpanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.padding(15.dp)) {

        IconButton(onClick = { menuExpanded = true }) {
            Icon(Icons.Default.Settings, contentDescription = "Open Settings")
        }

        // The actual Dropdown Container
        DropdownMenu(
            expanded = menuExpanded,
            onDismissRequest = { menuExpanded = false },
            modifier = Modifier.width(300.dp) // Set a width so it looks consistent
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
                    }
                )

                SelectionDropdown(
                    label = "Difficulty",
                    options = difficulties,
                    selectedOption = difficulty,
                    onOptionSelected = { newDifficulty ->
                        viewModel.updateDifficulty(newDifficulty)
                    }
                )

                SelectionDropdown(
                    label = "Book",
                    options = books,
                    selectedOption = book,
                    onOptionSelected = { newBook ->
                        viewModel.updateBook(newBook)
                    }
                )

                SelectionDropdown(
                    label = "Chapter",
                    options = chapters,
                    selectedOption = chapter.toString(),
                    onOptionSelected = { newChapter ->
                        viewModel.updateChapter(newChapter.toInt())
                    }
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

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
    label: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
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

fun ReplacingWords(text: String, difficultyLevel: String = "Easy"): VerseDisplay {

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
                withStyle(style = SpanStyle(fontWeight = FontWeight.ExtraBold)) {
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
    return VerseDisplay(original = text, hiddenVerse = hiddenVerses, hiddenWords = captureWords, revealedVerse = revealedVerses)
}

@Composable
fun YourVerse(stage: Int, first: AnnotatedString, second: AnnotatedString): Unit {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
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
