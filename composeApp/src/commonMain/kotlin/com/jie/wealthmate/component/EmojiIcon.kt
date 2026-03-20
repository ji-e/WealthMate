package com.jie.wealthmate.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jie.wealthmate.theme.ColorRed
import com.jie.wealthmate.theme.WMTheme
import org.jetbrains.compose.resources.painterResource
import wealthmate.composeapp.generated.resources.Res
import wealthmate.composeapp.generated.resources.ic_push_pin

@Composable
fun EmojiIcon(
    icon: String?,
    color: Color,
    isFixed: Boolean = false,
    isFixedUsed: Boolean = false,
    modifier: Modifier = Modifier,
    size: EmojiIconSize = EmojiIconSize.MEDIUM,
) {
    Box(
        modifier = modifier.width(size.boxSize + if (isFixedUsed) (size.fixedIconSize / 3) else 0.dp),
        contentAlignment = Alignment.CenterEnd
    ) {
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .background(color)
                .size(size.boxSize),
            contentAlignment = Alignment.Center
        ) {
            WMText(
                text = icon ?: "❓",
                fontSize = size.iconSize
            )
        }

        if (isFixed) {
            Icon(
                painter = painterResource(Res.drawable.ic_push_pin),
                contentDescription = null,
                tint = ColorRed.Red_300,
                modifier = Modifier
                    .size(size.fixedIconSize)
                    .align(Alignment.TopStart)
            )
        }
    }
}

enum class EmojiIconSize(
    val boxSize: Dp,
    val iconSize: TextUnit,
    val fixedIconSize: Dp,
) {
    SMALL(
        boxSize = 20.dp,
        iconSize = 12.sp,
        fixedIconSize = 10.dp,
    ),
    MEDIUM(
        boxSize = 40.dp,
        iconSize = 24.sp,
        fixedIconSize = 20.dp,
    ),
    LARGE(
        boxSize = 80.dp,
        iconSize = 48.sp,
        fixedIconSize = 40.dp,
    )
}

@Preview
@Composable
private fun EmojiIconPreview() {
    WMTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                EmojiIcon(
                    icon = "💰",
                    color = ColorRed.Red_100,
                    size = EmojiIconSize.SMALL
                )
                EmojiIcon(
                    icon = "💰",
                    color = ColorRed.Red_100,
                    size = EmojiIconSize.MEDIUM
                )
                EmojiIcon(
                    icon = "💰",
                    color = ColorRed.Red_100,
                    size = EmojiIconSize.LARGE
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                EmojiIcon(
                    icon = "💰",
                    color = ColorRed.Red_100,
                    size = EmojiIconSize.SMALL,
                    isFixed = true
                )
                EmojiIcon(
                    icon = "💰",
                    color = ColorRed.Red_100,
                    size = EmojiIconSize.MEDIUM,
                    isFixed = true
                )
                EmojiIcon(
                    icon = "💰",
                    color = ColorRed.Red_100,
                    size = EmojiIconSize.LARGE,
                    isFixed = true
                )
            }
        }
    }
}
