package com.example.akibazone.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

data class AkibaZoneColors(
    val background: Color,
    val backgroundSecondary: Color,
    val card: Color,
    val surface: Color,
    val primary: Color,
    val primaryLight: Color,
    val primaryDark: Color,
    val secondary: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textDisabled: Color,
    val favorite: Color,
    val success: Color,
    val warning: Color,
    val error: Color,
    val onPrimary: Color,
    val onSecondary: Color
)

val DarkAkibaZoneColors = AkibaZoneColors(
    background = Color(0xFF0B0D14),
    backgroundSecondary = Color(0xFF111522),
    card = Color(0xFF171B2A),
    surface = Color(0xFF1E2435),
    primary = Color(0xFF8B5CF6),
    primaryLight = Color(0xFFA78BFA),
    primaryDark = Color(0xFF6D3FE0),
    secondary = Color(0xFF38BDF8),
    textPrimary = Color(0xFFF1F5F9),
    textSecondary = Color(0xFF94A3B8),
    textDisabled = Color(0xFF64748B),
    favorite = Color(0xFFF472B6),
    success = Color(0xFF34D399),
    warning = Color(0xFFFBBF24),
    error = Color(0xFFF87171),
    onPrimary = Color(0xFFFFFFFF),
    onSecondary = Color(0xFF0B0D14)
)

val LightAkibaZoneColors = AkibaZoneColors(
    background = Color(0xFFF8FAFC),
    backgroundSecondary = Color(0xFFE2E8F0),
    card = Color(0xFFFFFFFF),
    surface = Color(0xFFF1F5F9),
    primary = Color(0xFF6D3FE0),
    primaryLight = Color(0xFF8B5CF6),
    primaryDark = Color(0xFF5125B5),
    secondary = Color(0xFF0284C7),
    textPrimary = Color(0xFF0F172A),
    textSecondary = Color(0xFF475569),
    textDisabled = Color(0xFF94A3B8),
    favorite = Color(0xFFDB2777),
    success = Color(0xFF059669),
    warning = Color(0xFFD97706),
    error = Color(0xFFDC2626),
    onPrimary = Color(0xFFFFFFFF),
    onSecondary = Color(0xFFFFFFFF)
)

val LocalAkibaZoneColors = staticCompositionLocalOf { DarkAkibaZoneColors }

val Background: Color
    @Composable get() = LocalAkibaZoneColors.current.background
val BackgroundSecondary: Color
    @Composable get() = LocalAkibaZoneColors.current.backgroundSecondary
val CardColor: Color
    @Composable get() = LocalAkibaZoneColors.current.card
val SurfaceColor: Color
    @Composable get() = LocalAkibaZoneColors.current.surface
val Primary: Color
    @Composable get() = LocalAkibaZoneColors.current.primary
val PrimaryLight: Color
    @Composable get() = LocalAkibaZoneColors.current.primaryLight
val PrimaryDark: Color
    @Composable get() = LocalAkibaZoneColors.current.primaryDark
val Secondary: Color
    @Composable get() = LocalAkibaZoneColors.current.secondary
val TextPrimary: Color
    @Composable get() = LocalAkibaZoneColors.current.textPrimary
val TextSecondary: Color
    @Composable get() = LocalAkibaZoneColors.current.textSecondary
val TextDisabled: Color
    @Composable get() = LocalAkibaZoneColors.current.textDisabled
val Favorite: Color
    @Composable get() = LocalAkibaZoneColors.current.favorite
val Success: Color
    @Composable get() = LocalAkibaZoneColors.current.success
val Warning: Color
    @Composable get() = LocalAkibaZoneColors.current.warning
val Error: Color
    @Composable get() = LocalAkibaZoneColors.current.error
