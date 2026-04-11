package com.example.versegenerator.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.versegenerator.ViewModels.ThemeConfig

// Paper Mode Colors

private val DarkColorScheme = darkColorScheme(
    primary = Color.DarkGray,
    onPrimary = Color.LightGray,
    background = Color.DarkGray,
    onBackground = Color.LightGray, // Soft Charcoal instead of Black
    surface = Color.DarkGray,
    onSurface = Color.LightGray,
    surfaceVariant = Color.DarkGray, // Perfect for your settings card
    onSurfaceVariant = Color.LightGray
)

private val LightColorScheme = lightColorScheme(
    primary = LightPaperPrimary,
    onPrimary = Color.Black,
    background = LightPaperBackground,
    onBackground = Color(0xFF2C2C2C), // Soft Charcoal instead of Black
    surface = LightPaperSurface,
    onSurface = Color(0xFF2C2C2C),
    surfaceVariant = Color(0xFFF2EFE4), // Perfect for your settings card
    onSurfaceVariant = Color(0xFF4E4E4E)
)

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */


@Composable
fun VerseGeneratorTheme(
    themeConfig: ThemeConfig,
    content: @Composable () -> Unit
) {

    val systemInDark = isSystemInDarkTheme()

    val darkTheme = when (themeConfig) {
        ThemeConfig.FOLLOW_SYSTEM -> isSystemInDarkTheme()
        ThemeConfig.LIGHT -> false
        ThemeConfig.DARK -> true
    }

    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}