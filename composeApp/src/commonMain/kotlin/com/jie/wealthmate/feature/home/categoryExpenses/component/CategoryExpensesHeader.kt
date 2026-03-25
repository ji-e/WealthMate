package com.jie.wealthmate.feature.home.categoryExpenses.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jie.wealthmate.component.EmojiIcon
import com.jie.wealthmate.component.EmojiIconSize
import com.jie.wealthmate.component.SpacerSize
import com.jie.wealthmate.component.WMSpacer
import com.jie.wealthmate.component.WMText
import com.jie.wealthmate.feature.home.StatusType
import com.jie.wealthmate.theme.ColorBlue
import com.jie.wealthmate.theme.ColorGray
import com.jie.wealthmate.theme.ColorRed
import com.jie.wealthmate.utils.formatWithCommas
import com.jie.wealthmate.vo.CategoryVo

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
            modifier = Modifier.width(EmojiIconSize.LARGE.boxSize + (EmojiIconSize.LARGE.fixedIconSize / 3) * 2),
        ) {
            EmojiIcon(
                icon = category.icon,
                color = category.largeCategory.backgroundColor,
                size = EmojiIconSize.LARGE,
                isFixed = category.isFixed,
                isFixedUsed = true
            )
        }

        WMSpacer(size = SpacerSize.SMALL)

        WMText(
            text = category.middleLabel.ifEmpty { "카테고리 없음" },
            style = typography.titleMedium,
            color = ColorGray.Gray_500,
            fontWeight = FontWeight.SemiBold

        )
        WMSpacer(size = SpacerSize.X_SMALL)
        WMText(
            text = "${totalAmount.formatWithCommas()}원",
            style = typography.headlineMedium,
            fontWeight = FontWeight.SemiBold
        )

        val diffColor = when {
            diffAmount > 0 -> ColorRed.Red_300
            diffAmount < 0 -> ColorBlue.Blue_300
            else -> ColorGray.Gray_400
        }
        val sign = if (diffAmount > 0) "+" else ""

        WMSpacer(size = SpacerSize.XX_SMALL)
        WMText(
            text = "${statusType.lastLabel} 대비 $sign${diffAmount.formatWithCommas()}원",
            style = typography.bodySmall,
            color = diffColor
        )
    }
}