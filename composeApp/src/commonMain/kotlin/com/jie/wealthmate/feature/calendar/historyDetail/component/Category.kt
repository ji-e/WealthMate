package com.jie.wealthmate.feature.calendar.historyDetail.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
) {
    Column(modifier = modifier) {
        WMText(
            text = "카테고리",
            style = Typography().titleSmall.copy(fontWeight = FontWeight.SemiBold)
        )

        Row(
            modifier = Modifier.fillMaxWidth()
                .padding(top = 12.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(ColorGray.Gray_50)
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (category == null) {
                WMText(
                    text = "카테고리 없음",
                    style = Typography().titleSmall.copy(color = ColorGray.Gray_300),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                return@Row
            }
            Box(modifier = Modifier.width(60.dp)) {
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(category.largeCategory.backgroundColor)
                        .size(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    WMText(
                        text = category.icon,
                        style = Typography().bodyLarge.copy(fontSize = 20.sp)
                    )
                }
                if (category.isFixed) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_push_pin),
                        contentDescription = null,
                        tint = ColorRed.Red_300,
                        modifier = Modifier
                            .padding(start = 24.dp)
                            .size(24.dp)
                            .align(Alignment.TopStart)
                    )
                }
            }

            WMText(
                text = category.middleLabel,
                style = Typography().titleSmall,
//                modifier = Modifier.padding(top = 2.dp, end = 12.dp)
            )

            categoryTag?.let {
                WMText(
                    text = " > ${it.label}",
                    style = Typography().titleSmall,
//                modifier = Modifier.padding(top = 2.dp, end = 12.dp)
                )
            }
        }
    }
}