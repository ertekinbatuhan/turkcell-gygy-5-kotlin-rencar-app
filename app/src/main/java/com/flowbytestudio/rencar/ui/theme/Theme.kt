package com.flowbytestudio.rencar.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = Primary,
    background = Background,
    surface = Surface,
    onPrimary = Surface,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
)

@Composable
fun RencarTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}
