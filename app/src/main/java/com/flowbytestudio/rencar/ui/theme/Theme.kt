package com.flowbytestudio.rencar.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.flowbytestudio.rencar.data.settings.ThemeController
import com.flowbytestudio.rencar.data.settings.ThemeMode

private val LightColorScheme = lightColorScheme(
    primary = LightRencarColors.primary,
    background = LightRencarColors.background,
    surface = LightRencarColors.surface,
    onPrimary = LightRencarColors.surface,
    onBackground = LightRencarColors.textPrimary,
    onSurface = LightRencarColors.textPrimary,
)

private val DarkColorScheme = darkColorScheme(
    primary = DarkRencarColors.primary,
    background = DarkRencarColors.background,
    surface = DarkRencarColors.surface,
    onPrimary = DarkRencarColors.background,
    onBackground = DarkRencarColors.textPrimary,
    onSurface = DarkRencarColors.textPrimary,
)

@Composable
fun RencarTheme(content: @Composable () -> Unit) {
    val themeMode by ThemeController.themeMode.collectAsState()
    val darkTheme = when (themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }

    CompositionLocalProvider(
        LocalRencarColors provides if (darkTheme) DarkRencarColors else LightRencarColors,
    ) {
        MaterialTheme(
            colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
            typography = Typography,
            content = content,
        )
    }
}
