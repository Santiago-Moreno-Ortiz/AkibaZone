package com.example.akibazone.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.example.akibazone.data.preferences.ThemePreference

private val DarkColorScheme = darkColorScheme(
    primary = DarkAkibaZoneColors.primary,
    secondary = DarkAkibaZoneColors.secondary,
    tertiary = DarkAkibaZoneColors.favorite,
    background = DarkAkibaZoneColors.background,
    surface = DarkAkibaZoneColors.surface,
    onPrimary = DarkAkibaZoneColors.onPrimary,
    onSecondary = DarkAkibaZoneColors.onSecondary,
    onTertiary = DarkAkibaZoneColors.onSecondary,
    onBackground = DarkAkibaZoneColors.textPrimary,
    onSurface = DarkAkibaZoneColors.textPrimary,
    surfaceVariant = DarkAkibaZoneColors.card,
    onSurfaceVariant = DarkAkibaZoneColors.textSecondary,
    error = DarkAkibaZoneColors.error
)

private val LightColorScheme = lightColorScheme(
    primary = LightAkibaZoneColors.primary,
    secondary = LightAkibaZoneColors.secondary,
    tertiary = LightAkibaZoneColors.favorite,
    background = LightAkibaZoneColors.background,
    surface = LightAkibaZoneColors.surface,
    onPrimary = LightAkibaZoneColors.onPrimary,
    onSecondary = LightAkibaZoneColors.onSecondary,
    onTertiary = LightAkibaZoneColors.onSecondary,
    onBackground = LightAkibaZoneColors.textPrimary,
    onSurface = LightAkibaZoneColors.textPrimary,
    surfaceVariant = LightAkibaZoneColors.card,
    onSurfaceVariant = LightAkibaZoneColors.textSecondary,
    error = LightAkibaZoneColors.error
)

@Composable
fun AkibaZoneTheme(
    themePreference: ThemePreference = ThemePreference.SYSTEM,
    content: @Composable () -> Unit
) {
    val systemIsDark = isSystemInDarkTheme()
    val isDark = when (themePreference) {
        ThemePreference.SYSTEM -> systemIsDark
        ThemePreference.LIGHT -> false
        ThemePreference.DARK -> true
    }
    val colorScheme = if (isDark) DarkColorScheme else LightColorScheme
    val akibaColors = if (isDark) DarkAkibaZoneColors else LightAkibaZoneColors

    CompositionLocalProvider(LocalAkibaZoneColors provides akibaColors) {
        val view = LocalView.current
        if (!view.isInEditMode) {
            SideEffect {
                val window = (view.context as Activity).window
                window.statusBarColor = akibaColors.background.toArgb()
                window.navigationBarColor = akibaColors.background.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !isDark
            }
        }

        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
