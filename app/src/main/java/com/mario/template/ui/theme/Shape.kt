package com.mario.template.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.dp

// Card등 둥근 모서리 리용
data class CustomShapes(
    val small: RoundedCornerShape = RoundedCornerShape(2.dp),
    val medium: RoundedCornerShape = RoundedCornerShape(4.dp),
    val large: RoundedCornerShape = RoundedCornerShape(6.dp),
    val extraLarge: RoundedCornerShape = RoundedCornerShape(8.dp),
    val indicator: RoundedCornerShape = RoundedCornerShape(3.dp),
)

//val Shapes = Shapes(
//    extraSmall = RoundedCornerShape(3.dp),
//    small = RoundedCornerShape(4.dp),
//    medium = RoundedCornerShape(4.dp),
//    large = RoundedCornerShape(0.dp)
//)

val LocalShapes = staticCompositionLocalOf { CustomShapes() }