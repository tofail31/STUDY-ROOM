package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val WarmLightColorScheme = lightColorScheme(
    primary = StoneInkPrimary,
    onPrimary = StoneSurface,
    primaryContainer = StoneAccentSoft,
    onPrimaryContainer = StoneInkPrimary,
    secondary = StoneAccent,
    onSecondary = NeumorphicHighlight,
    secondaryContainer = StoneSurface,
    onSecondaryContainer = StoneInkPrimary,
    tertiary = StoneSuccess,
    background = StoneBackground,
    onBackground = StoneInkPrimary,
    surface = StoneSurface,
    onSurface = StoneInkPrimary,
    surfaceVariant = StonePanel,
    onSurfaceVariant = StoneInkSecondary,
    outline = StoneDivider
)

private val WarmDarkColorScheme = darkColorScheme(
    primary = DarkStoneInkPrimary,
    onPrimary = DarkStoneBg,
    primaryContainer = StoneAccent,
    onPrimaryContainer = DarkStoneInkPrimary,
    secondary = StoneAccentSoft,
    onSecondary = DarkStoneBg,
    background = DarkStoneBg,
    onBackground = DarkStoneInkPrimary,
    surface = DarkStoneSurface,
    onSurface = DarkStoneInkPrimary,
    surfaceVariant = DarkStonePanel,
    onSurfaceVariant = DarkStoneInkSecondary
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) WarmDarkColorScheme else WarmLightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}
