package com.mario.template.ui.theme

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

val SplashGradient1 = Color(0xFFF2F4F7)
val SplashGradient2 = Color(0xFFBCC8D6)
val SplashGradient = listOf(SplashGradient1, SplashGradient2)
val TutoGradient1 = Color(0xFF484B5B)
val TutoGradient2 = Color(0xFF2C2D35)
val TutoGradient = listOf(TutoGradient1, TutoGradient2)


val Title1 = Color(0xFF0A0A22)
val SubTitle1 = Color(0xFF8B95A2)
val BtnTitle1 = Color.White
val IndicatorSelected = Color(0xFF0A0A22)
val IndicatorUnSelected = Color(0xFFFFFFFF)

// dark를 이용할때 recomposition이 되어야 하므로 mutableStateOf가 되어야 함.
class CustomColors(
    primary: Color = Purple80,
    splash_gradient: List<Color> = SplashGradient,
    title1: Color = Title1,
    subtitle1: Color = SubTitle1,
    tuto_gradient: List<Color> = SplashGradient,
    btn_title1: Color = BtnTitle1,
    indicator_selected: Color = IndicatorSelected,
    indicator_unselected: Color = IndicatorUnSelected,
    isDark: Boolean
) {
    var primary by mutableStateOf(Purple80)
        private set
    var splash_gradient by mutableStateOf(SplashGradient)
        private set
    var title1 by mutableStateOf(Title1)
        private set
    var subtitle1 by mutableStateOf(SubTitle1)
        private set
    var tuto_gradient by mutableStateOf(TutoGradient)
        private set
    var btn_title1 by mutableStateOf(BtnTitle1)
        private set
    var indicator_selected by mutableStateOf(IndicatorSelected)
        private set
    var indicator_unselected by mutableStateOf(IndicatorUnSelected)
        private set
    var isDark by mutableStateOf(isDark)
        private set

    fun update(other: CustomColors) {
        primary = other.primary
        splash_gradient = other.splash_gradient
        title1 = other.title1
        subtitle1 = other.subtitle1
        tuto_gradient = other.tuto_gradient
        btn_title1 = other.btn_title1
        indicator_selected = other.indicator_selected
        indicator_unselected = other.indicator_unselected
        isDark = other.isDark
    }

    fun copy(): CustomColors = CustomColors(
        primary = primary,
        splash_gradient = splash_gradient,
        title1 = title1,
        subtitle1 = subtitle1,
        tuto_gradient = tuto_gradient,
        btn_title1 = btn_title1,
        indicator_selected = indicator_selected,
        indicator_unselected = indicator_unselected,
        isDark = isDark
    )
}

val DarkColorPalette = CustomColors(
    isDark = true
)

val LightColorPalette = CustomColors(
    isDark = false
)

val LocalColors = staticCompositionLocalOf { LightColorPalette }