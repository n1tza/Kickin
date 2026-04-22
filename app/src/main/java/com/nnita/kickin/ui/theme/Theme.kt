package com.nnita.kickin.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary              = NeonGreen,
    onPrimary            = CharcoalBlack,
    primaryContainer     = NeonGreenContainerDark,
    onPrimaryContainer   = NeonGreen,
    secondary            = NeonGreen,
    onSecondary          = CharcoalBlack,
    secondaryContainer   = Gray700,
    onSecondaryContainer = Gray100,
    background           = CharcoalBlack,
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
    primary              = NeonGreen,
    onPrimary            = CharcoalBlack,
    primaryContainer     = NeonGreenContainerLight,
    onPrimaryContainer   = CharcoalBlack,
    secondary            = CharcoalBlack,
    onSecondary          = White,
    secondaryContainer   = Gray100,
    onSecondaryContainer = CharcoalBlack,
    background           = Gray50,
    onBackground         = CharcoalBlack,
    surface              = White,
    onSurface            = CharcoalBlack,
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
