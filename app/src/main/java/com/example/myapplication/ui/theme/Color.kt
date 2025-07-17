package com.example.myapplication.ui.theme

import androidx.compose.ui.graphics.Color

// dark theme
val dark_backgroundColor: Color = Color(0xff404040)
val dark_buttonBackgroundColor: Color = Color(0xffb06000)
val dark_displayBackgroundColor: Color = Color(0xff505050)
val dark_textColor: Color = Color(0xffffffff)

// light theme
val light_backgroundColor: Color = Color(0xffffffff)
val light_buttonBackgroundColor: Color = Color(0xffffa040)
val light_displayBackgroundColor: Color = Color(0xfff0f0f0)
val light_textColor: Color = Color(0xff000000)

sealed class ThemeColors(
    val background: Color,
    val button: Color,
    val display: Color,
    val text: Color
) {
    data object Dark : ThemeColors(
        background = dark_backgroundColor,
        button = dark_buttonBackgroundColor,
        display = dark_displayBackgroundColor,
        text = dark_textColor
    )

    data object Light : ThemeColors(
        background = light_backgroundColor,
        button = light_buttonBackgroundColor,
        display = light_displayBackgroundColor,
        text = light_textColor
    )
}