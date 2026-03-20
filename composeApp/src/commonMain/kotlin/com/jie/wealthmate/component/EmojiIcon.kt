package com.jie.wealthmate.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun EmojiIcon(
    icon: String,
    color: Color,
    modifier: Modifier = Modifier,
    size: EmojiIconSize = EmojiIconSize.MEDIUM,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.End
    ) {
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .background(color)
                .size(size.boxSize),
            contentAlignment = Alignment.Center
        ) {
            WMText(
                text = icon,
                fontSize = size.iconSize
            )
        }
    }
}

enum class EmojiIconSize(
    val boxSize: Dp,
    val iconSize: TextUnit,
) {
    SMALL(
        boxSize = 20.dp,
        iconSize = 12.sp
    ),
    MEDIUM(
        boxSize = 40.dp,
        iconSize = 24.sp
    ),
    LARGE(
        boxSize = 80.dp,
        iconSize = 48.sp
    )
}