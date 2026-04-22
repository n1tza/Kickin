package com.nnita.kickin.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary              = VibrantGreen,
    onPrimary            = White,
    primaryContainer     = GreenContainerDark,
    onPrimaryContainer   = VibrantGreen,
    secondary            = ForestGreen,
    onSecondary          = White,
    secondaryContainer   = Gray700,
    onSecondaryContainer = Gray100,
    background           = Gray900,
    onBackground         = Gray100,
    surface              = Gray800,
    onSurface            = Gray100,
    surfaceVariant       = Gray700,
    onSurfaceVariant     = Gray400,
    outline              = Gray500,
    outlineVariant       = Gray700,
    error                = LiveRed,
    onError              = White
)

private val LightColorScheme = lightColorScheme(
    primary              = ForestGreen,
    onPrimary            = White,
    primaryContainer     = GreenContainerLight,
    onPrimaryContainer   = Black,
    secondary            = VibrantGreen,
    onSecondary          = White,
    secondaryContainer   = Gray100,
    onSecondaryContainer = Black,
    background           = Gray50,
    onBackground         = Black,
    surface              = White,
    onSurface            = Black,
    surfaceVariant       = Gray100,
    onSurfaceVariant     = Gray600,
    outline              = Gray200,
    outlineVariant       = Gray100,
    error                = LiveRed,
    onError              = White
)

@Composable
fun KickinTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = Typography,
        content = content
    )
}
