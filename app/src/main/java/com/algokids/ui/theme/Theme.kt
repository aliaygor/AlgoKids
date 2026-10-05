package com.algokids.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val AlgoColors = lightColorScheme(
    primary=Color(0xFF315DB0), onPrimary=Color.White,
    secondary=Color(0xFF308263), onSecondary=Color.White,
    tertiary=Color(0xFF8256A3), background=Color(0xFFF4F6FC),
    surface=Color.White, onSurface=Color(0xFF172B4D), onBackground=Color(0xFF172B4D)
)

@Composable
fun AlgoKidsTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme=AlgoColors,typography=Typography,content=content)
}
