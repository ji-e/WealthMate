package com.jie.wealthmate.feature.calendar.historyDetail.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jie.wealthmate.component.LabelText
import com.jie.wealthmate.component.WMText
import com.jie.wealthmate.theme.ColorGray
import com.jie.wealthmate.theme.ColorRed
import com.jie.wealthmate.vo.CategoryTagVo
import com.jie.wealthmate.vo.CategoryVo
import org.jetbrains.compose.resources.painterResource
import wealthmate.composeapp.generated.resources.Res
import wealthmate.composeapp.generated.resources.ic_push_pin

@Composable
fun Category(
    modifier: Modifier = Modifier,
    category: CategoryVo?,
    categoryTag: CategoryTagVo?,
    onCategoryClick: () -> Unit,
) {
    Column(modifier = modifier) {
        LabelText(text = "카테고리")

        Row(
            modifier = Modifier.fillMaxWidth()
                .padding(top = 12.dp)
                .height(60.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(ColorGray.Gray_50)
                .clickable { onCategoryClick() }
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (category == null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    contentAlignment = Alignment.Center
                ) {
                    WMText(
                        text = "카테고리 없음",
                        style = Typography().titleSmall.copy(color = ColorGray.Gray_300),
                    )
                }
                return@Row
            }
            Box(modifier = Modifier.width(60.dp)) {
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(category.largeCategory.backgroundColor)
                        .size(40.dp)
                        .align(Alignment.Center),
                    contentAlignment = Alignment.Center
                ) {
                    WMText(
                        text = category.icon,
                        style = Typography().titleLarge
                    )
                }
                if (category.isFixed) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_push_pin),
                        contentDescription = null,
                        tint = ColorRed.Red_300,
                        modifier = Modifier
                            .size(24.dp)
                            .align(Alignment.TopStart)
                    )
                }
            }

            WMText(
                text = category.middleLabel,
                style = Typography().titleMedium,
                maxLines = 1
            )

            categoryTag?.let {
                WMText(
                    text = " > ${it.label}",
                    style = Typography().titleMedium,
                    maxLines = 1
                )
            }
        }
    }
}