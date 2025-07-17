package com.example.myapplication.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.viewmodel.ThemeViewModel

private val DarkColorScheme = darkColorScheme(
    background = ThemeColors.Dark.background,
    primary = ThemeColors.Dark.text,
    secondary = ThemeColors.Dark.button,
    tertiary = ThemeColors.Dark.display
)

private val LightColorScheme = lightColorScheme(
    background = ThemeColors.Light.background,
    primary = ThemeColors.Light.text,
    secondary = ThemeColors.Light.button,
    tertiary = ThemeColors.Light.display
)

@Composable
fun MyApplicationTheme(
    viewModel: ThemeViewModel = viewModel(),
    content: @Composable () -> Unit
) {
    val themeState by viewModel.themeState.collectAsStateWithLifecycle();

    MaterialTheme(
        colorScheme = if (themeState.isDark) DarkColorScheme else LightColorScheme,
        typography = Typography,
        content = content
    )
}