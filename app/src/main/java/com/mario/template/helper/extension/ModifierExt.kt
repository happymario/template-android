/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mario.template.helper.extension

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.layout.AlignmentLine
import androidx.compose.ui.layout.FirstBaseline
import androidx.compose.ui.layout.LastBaseline
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Support wide screen by making the content width max 840dp, centered horizontally.
 */
fun Modifier.supportWideScreen() = this
    .fillMaxWidth()
    .wrapContentWidth(align = Alignment.CenterHorizontally)
    .widthIn(max = 840.dp)

fun Modifier.baselinePadding(
    firstBaselineToTop: Dp,
    lastBaselineToBottom: Dp
) = layout { measurable, constraints ->
    val placeable = measurable.measure(constraints)

    check(placeable[FirstBaseline] != AlignmentLine.Unspecified)
    val firstBaseline = placeable[FirstBaseline]

    check(placeable[LastBaseline] != AlignmentLine.Unspecified)
    val lastBaseline = placeable[LastBaseline]

    val lastBaselineToBottomHeight = placeable.height - lastBaseline

    val lastBaselineToBottomDelta = lastBaselineToBottom.roundToPx() - lastBaselineToBottomHeight

    val totalHeight = placeable.height +
            (firstBaselineToTop.roundToPx() - firstBaseline)

    val placeableY = totalHeight - placeable.height
    layout(placeable.width, totalHeight + lastBaselineToBottomDelta) {
        placeable.placeRelative(0, placeableY)
    }
}


inline fun Modifier.noRippleClickable(crossinline onClick: () -> Unit): Modifier = composed {
    clickable(indication = null,
        interactionSource = remember { MutableInteractionSource() }) {
        onClick()
    }
}