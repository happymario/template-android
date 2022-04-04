package com.victoria.bleled.app.recent.compose.theme

import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color

val Purple200 = Color(0xFFBB86FC)
val Purple500 = Color(0xFF6200EE)
val Purple700 = Color(0xFF3700B3)
val Teal200 = Color(0xFF03DAC5)
val Navy = Color(0xFF03D111)
val Error = Color(0xFFFF0000)
val Gradient1 = listOf(Purple200, Purple500, Purple700, Navy)

@Stable
class MyColors(
    purple200: Color = Purple200,
    purple500: Color = Purple500,
    purple700: Color = Purple700,
    teal200: Color = Teal200,
    gradient_1: List<Color> = Gradient1,
    navy: Color = Navy,
    interactive: List<Color> = gradient_1, // gradient
    error: Color = Error,
    isDark: Boolean,
) {
    var purple200 by mutableStateOf(Purple200)
        private set
    var purple500 by mutableStateOf(Purple500)
        private set
    var purple700 by mutableStateOf(Purple700)
        private set
    var teal200 by mutableStateOf(Teal200)
        private set
    var gradient_1 by mutableStateOf(Gradient1)
        private set
    var navy by mutableStateOf(Navy)
        private set
    var error by mutableStateOf(Error)
        private set
    var isDark by mutableStateOf(isDark)
        private set

    fun update(other: MyColors) {
        purple200 = other.purple200
        purple500 = other.purple500
        purple700 = other.purple700
        teal200 = other.teal200
        gradient_1 = other.gradient_1
        navy = other.navy
        error = other.error
        isDark = other.isDark
    }

    fun copy(): MyColors = MyColors(
        purple200 = purple200,
        purple500 = purple500,
        purple700 = purple700,
        teal200 = teal200,
        gradient_1 = gradient_1,
        navy = navy,
        error = error,
        isDark = isDark
    )
}

val LocalMyColors = staticCompositionLocalOf<MyColors> {
    error("No LocalMyColorsPalette provided")
}

@Composable
fun ProvideMyColors(
    colors: MyColors,
    content: @Composable () -> Unit,
) {
    val colorPalette = remember {
        colors.copy()
    }
    colorPalette.update(colors)
    CompositionLocalProvider(LocalMyColors provides colorPalette, content = content)
}

/**
 * A Material [Colors] implementation which sets all colors to [debugColor] to discourage usage of
 * [MaterialTheme.colors] in preference to [JetsnackTheme.colors].
 */
fun debugColors(
    darkTheme: Boolean,
    debugColor: Color = Color.Magenta,
) = Colors(
    primary = debugColor,
    primaryVariant = debugColor,
    secondary = debugColor,
    secondaryVariant = debugColor,
    background = debugColor,
    surface = debugColor,
    error = debugColor,
    onPrimary = debugColor,
    onSecondary = debugColor,
    onBackground = debugColor,
    onSurface = debugColor,
    onError = debugColor,
    isLight = !darkTheme
)
