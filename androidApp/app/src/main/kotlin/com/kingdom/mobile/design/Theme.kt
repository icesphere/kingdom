package com.kingdom.mobile.design

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val KingdomColors = lightColorScheme(
    primary = Color(0xFF2E5F43),
    onPrimary = Color.White,
    secondary = Color(0xFF7A5B20),
    tertiary = Color(0xFF7B334D),
    surface = Color(0xFFFCFCF7),
    surfaceVariant = Color(0xFFE8E2D2),
    background = Color(0xFFF7F3E8),
    error = Color(0xFF9B1B30)
)

@Composable
fun KingdomTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = KingdomColors,
        content = content
    )
}
