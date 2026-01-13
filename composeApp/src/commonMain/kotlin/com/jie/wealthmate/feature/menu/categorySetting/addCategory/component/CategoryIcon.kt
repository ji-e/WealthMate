package com.jie.wealthmate.feature.menu.categorySetting.addCategory.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jie.wealthmate.component.WMIconButton
import com.jie.wealthmate.feature.menu.categorySetting.component.LargeCategoryEnum
import com.jie.wealthmate.theme.ColorGray
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import wealthmate.composeapp.generated.resources.Res
import wealthmate.composeapp.generated.resources.emoji_u1f4b0
import wealthmate.composeapp.generated.resources.ic_exchange

@Composable
fun CategoryIcon(
    modifier: Modifier = Modifier,
    largeCategory: LargeCategoryEnum,
    selectedCategoryIcon: DrawableResource = Res.drawable.emoji_u1f4b0,
    onClickChange: () -> Unit,
) {
    Box(modifier = modifier) {
        Box(
            modifier = Modifier
                .background(
                    color = largeCategory.backgroundColor,
                    shape = CircleShape
                )
                .size(100.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(selectedCategoryIcon),
                contentDescription = "카테고리 아이콘",
                modifier = Modifier.size(80.dp)
            )
        }
        Box(
            modifier = Modifier
                .background(
                    color = ColorGray.Gray_100,
                    shape = CircleShape
                )
                .border(
                    width = 2.dp,
                    color = ColorGray.White_80,
                    shape = CircleShape
                )
                .size(30.dp)
                .align(Alignment.BottomEnd),
            contentAlignment = Alignment.Center
        ) {
            WMIconButton(
                modifier = Modifier.size(12.dp),
                iconRes = Res.drawable.ic_exchange,
                onClick = onClickChange,
            )
        }
    }
}
