package com.example.nole.core.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val NoleLightColors = lightColorScheme(
    primary = NoleIndigo,
    onPrimary = NoleSurface,
    primaryContainer = Color(0xFFD6E0F5),
    onPrimaryContainer = NoleIndigo,
    secondary = NoleAmber,
    onSecondary = NoleSurface,
    secondaryContainer = Color(0xFFF3E6C8),
    onSecondaryContainer = Color(0xFF92400E),
    tertiary = NoleGold,
    onTertiary = Color(0xFF1C1917),
    tertiaryContainer = NoleGoldBright,
    onTertiaryContainer = Color(0xFF1C1917),
    background = NoleBackground,
    onBackground = NoleInk,
    surface = NoleSurface,
    onSurface = NoleInk,
    surfaceVariant = NoleSurfaceVariant,
    onSurfaceVariant = NoleSlate,
    outline = NoleBorder,
    outlineVariant = Color(0xFFD5DEE8),
    error = Color(0xFFB91C1C),
    onError = NoleSurface,
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
        focusedContainerColor = colors.surface,
        unfocusedContainerColor = colors.surfaceVariant,
        focusedPlaceholderColor = colors.onSurfaceVariant,
        unfocusedPlaceholderColor = colors.onSurfaceVariant,
    )
}
