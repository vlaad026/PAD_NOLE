package com.example.nole.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val NoleLightColors = lightColorScheme(
    primary = NoleIndigo,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDBE4FF),
    onPrimaryContainer = NoleIndigo,
    secondary = NoleAmber,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFEF3C7),
    onSecondaryContainer = Color(0xFF92400E),
    tertiary = NoleGold,
    onTertiary = Color(0xFF1C1917),
    tertiaryContainer = NoleGoldBright,
    onTertiaryContainer = Color(0xFF1C1917),
    background = NoleBackground,
    onBackground = NoleInk,
    surface = Color.White,
    onSurface = NoleInk,
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = Color(0xFF475569),
    outline = NoleBorder,
    outlineVariant = Color(0xFFF1F5F9),
    error = Color(0xFFB91C1C),
    onError = Color.White,
)

@Composable
fun NoleTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = NoleLightColors,
        content = content,
    )
}

@Composable
fun noleTextFieldColors(): TextFieldColors {
    val colors = MaterialTheme.colorScheme
    return OutlinedTextFieldDefaults.colors(
        focusedBorderColor = colors.primary,
        unfocusedBorderColor = colors.outline,
        focusedLabelColor = colors.primary,
        unfocusedLabelColor = colors.onSurfaceVariant,
        focusedTextColor = colors.onSurface,
        unfocusedTextColor = colors.onSurface,
        cursorColor = colors.primary,
        focusedPlaceholderColor = colors.onSurfaceVariant,
        unfocusedPlaceholderColor = colors.onSurfaceVariant,
    )
}
