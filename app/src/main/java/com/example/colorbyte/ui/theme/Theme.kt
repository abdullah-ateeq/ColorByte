package com.example.colorbyte.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.example.colorbyte.data.model.ThemeMode

private val DarkColorScheme = darkColorScheme(
    primary = NeutralAccentDark,
    onPrimary = CanvasDark,
    primaryContainer = SurfaceVariantDark,
    onPrimaryContainer = SoftWhiteText,
    secondary = SoftWhiteTextSecondary,
    onSecondary = CanvasDark,
    background = CanvasDark,
    onBackground = SoftWhiteText,
    surface = SurfaceDark,
    onSurface = SoftWhiteText,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = SoftWhiteTextSecondary,
    outline = DarkBorder,
    outlineVariant = Color(0xFF23262E),
    error = ErrorRed,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = NeutralAccentLight,
    onPrimary = Color.White,
    primaryContainer = SurfaceVariantLight,
    onPrimaryContainer = CharcoalText,
    secondary = CharcoalTextSecondary,
    onSecondary = Color.White,
    background = CanvasWhite,
    onBackground = CharcoalText,
    surface = SurfaceLight,
    onSurface = CharcoalText,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = CharcoalTextSecondary,
    outline = CharcoalBorder,
    outlineVariant = Color(0xFFECEEF2),
    error = ErrorRed,
    onError = Color.White
)

@Composable
fun ColorByteTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val darkTheme = when (themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }

    val context = LocalContext.current
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}