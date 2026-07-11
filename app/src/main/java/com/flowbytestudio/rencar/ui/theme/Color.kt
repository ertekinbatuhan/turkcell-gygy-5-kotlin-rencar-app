package com.flowbytestudio.rencar.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color

/** Semantic color tokens used across Rencar screens, provided per light/dark theme. */
data class RencarColorScheme(
    val primary: Color,
    val primaryVariant: Color,
    val primaryLight: Color,
    val bgLight: Color,
    val success: Color,
    val successLight: Color,
    val danger: Color,
    val dangerLight: Color,
    val background: Color,
    val surface: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val divider: Color,
    val borderLight: Color,
    val borderColor: Color,
)

val LightRencarColors = RencarColorScheme(
    primary = Color(0xFF0B6BCB),
    primaryVariant = Color(0xFF1E7FE0),
    primaryLight = Color(0xFFEEF3FF),
    bgLight = Color(0xFFF1F4F8),
    success = Color(0xFF22C55E),
    successLight = Color(0xFFDCFCE7),
    danger = Color(0xFFEF4444),
    dangerLight = Color(0xFFFFEDED),
    // Sayfa zemini kartlardan ayrışsın diye gri; kartlar surface (beyaz) kullanır.
    background = Color(0xFFF1F4F8),
    surface = Color(0xFFFFFFFF),
    textPrimary = Color(0xFF111827),
    textSecondary = Color(0xFF6B7280),
    divider = Color(0xFFF3F4F6),
    borderLight = Color(0xFFE5E7EB),
    borderColor = Color(0xFFE3E8EF),
)

val DarkRencarColors = RencarColorScheme(
    primary = Color(0xFF4C95F0),
    primaryVariant = Color(0xFF3B8EF0),
    primaryLight = Color(0xFF14233A),
    bgLight = Color(0xFF171C24),
    success = Color(0xFF34C98A),
    successLight = Color(0xFF173726),
    danger = Color(0xFFF0575B),
    dangerLight = Color(0xFF2E1A1B),
    background = Color(0xFF0C0F14),
    surface = Color(0xFF171C24),
    textPrimary = Color(0xFFF3F6FA),
    textSecondary = Color(0xFF98A2B0),
    divider = Color(0xFF232A33),
    borderLight = Color(0xFF2A313B),
    borderColor = Color(0xFF2A313B),
)

val LocalRencarColors = compositionLocalOf { LightRencarColors }

// Theme-aware accessors — kept as the same names/call-sites used throughout the app so
// screens written against "Background", "TextPrimary", etc. automatically follow the
// active theme without needing to be rewritten to read a CompositionLocal directly.
val Primary: Color @Composable get() = LocalRencarColors.current.primary
val PrimaryVariant: Color @Composable get() = LocalRencarColors.current.primaryVariant
val PrimaryLight: Color @Composable get() = LocalRencarColors.current.primaryLight
val BgLight: Color @Composable get() = LocalRencarColors.current.bgLight
val Success: Color @Composable get() = LocalRencarColors.current.success
val SuccessLight: Color @Composable get() = LocalRencarColors.current.successLight
val Danger: Color @Composable get() = LocalRencarColors.current.danger
val DangerLight: Color @Composable get() = LocalRencarColors.current.dangerLight
val Background: Color @Composable get() = LocalRencarColors.current.background
val Surface: Color @Composable get() = LocalRencarColors.current.surface
val TextPrimary: Color @Composable get() = LocalRencarColors.current.textPrimary
val TextSecondary: Color @Composable get() = LocalRencarColors.current.textSecondary
val Divider: Color @Composable get() = LocalRencarColors.current.divider
val BorderLight: Color @Composable get() = LocalRencarColors.current.borderLight
val BorderColor: Color @Composable get() = LocalRencarColors.current.borderColor
