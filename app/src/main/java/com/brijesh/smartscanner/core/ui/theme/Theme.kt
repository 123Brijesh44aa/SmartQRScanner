package com.brijesh.smartscanner.core.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = ScannerGreen,
    onPrimary = BackgroundDark,

    secondary = ScannerGreenDark,
    onSecondary = BackgroundDark,

    background = BackgroundDark,
    onBackground = TextPrimaryDark,

    surface = SurfaceDark,
    onSurface = TextPrimaryDark,

    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = TextSecondaryDark,

    error = ErrorRed,
    onError = BackgroundDark
)

private val LightColorScheme = lightColorScheme(
    primary = ScannerGreenDark,
    onPrimary = BackgroundLight,

    secondary = ScannerGreen,
    onSecondary = BackgroundDark,

    background = BackgroundLight,
    onBackground = TextPrimaryLight,

    surface = SurfaceLight,
    onSurface = TextPrimaryLight,

    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = TextSecondaryLight,

    error = ErrorRed,
    onError = BackgroundDark
)

@Composable
fun SmartScannerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        DarkColorScheme
    } else {
        LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}