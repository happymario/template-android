package com.victoria.bleled.app.recent.compose.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable

private val DarkColorPalette = MyColors(
    isDark = true
)

private val LightColorPalette = MyColors(
    isDark = false
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    ProvideMyColors(colors = colors) {
        MaterialTheme(
            colors = debugColors(darkTheme = darkTheme),
            typography = Typography,
            shapes = Shapes,
            content = content
        )
    }
}

object MyApplicationTheme {
    val colors: MyColors
        @Composable
        get() = LocalMyColors.current
}