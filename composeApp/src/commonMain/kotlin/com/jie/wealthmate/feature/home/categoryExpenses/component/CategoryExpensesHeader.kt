package com.jie.wealthmate.feature.home.categoryExpenses.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jie.wealthmate.component.WMText
import com.jie.wealthmate.feature.home.StatusType
import com.jie.wealthmate.theme.ColorBlue
import com.jie.wealthmate.theme.ColorGray
import com.jie.wealthmate.theme.ColorRed
import com.jie.wealthmate.utils.formatWithCommas
import com.jie.wealthmate.vo.CategoryVo
import org.jetbrains.compose.resources.painterResource
import wealthmate.composeapp.generated.resources.Res
import wealthmate.composeapp.generated.resources.ic_push_pin

@Composable
fun CategoryExpensesHeader(
    statusType: StatusType,
    category: CategoryVo?,
    totalAmount: Long,
    diffAmount: Long,
) {
    category ?: return
    val typography = MaterialTheme.typography
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.width(104.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(color = category.largeCategory.backgroundColor)
                    .size(80.dp),
                contentAlignment = Alignment.Center
            ) {

                WMText(
                    text = category.icon.ifEmpty { "❓" },
                    style = Typography().displayMedium
                )
            }

            if (category.isFixed) {
                Icon(
                    painter = painterResource(Res.drawable.ic_push_pin),
                    contentDescription = null,
                    tint = ColorRed.Red_300,
                    modifier = Modifier
                        .size(40.dp)
                        .align(Alignment.TopStart)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        WMText(
            text = category.middleLabel.ifEmpty { "카테고리 없음" },
            style = typography.titleMedium.copy(
                color = ColorGray.Gray_500,
                fontWeight = FontWeight.SemiBold
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        WMText(
            text = "${totalAmount.formatWithCommas()}원",
            style = typography.headlineMedium.copy(fontWeight = FontWeight.SemiBold)
        )

        val diffColor = when {
            diffAmount > 0 -> ColorRed.Red_300
            diffAmount < 0 -> ColorBlue.Blue_300
            else -> ColorGray.Gray_400
        }
        val sign = if (diffAmount > 0) "+" else ""

        WMText(
            text = "${statusType.lastLabel} 대비 $sign${diffAmount.formatWithCommas()}원",
            style = typography.bodySmall.copy(color = diffColor)
        )
    }
}