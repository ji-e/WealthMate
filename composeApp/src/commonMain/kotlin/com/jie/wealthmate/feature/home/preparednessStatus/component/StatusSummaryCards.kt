package com.jie.wealthmate.feature.home.preparednessStatus.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jie.wealthmate.component.WMText
import com.jie.wealthmate.feature.menu.management.categoryManagement.component.LargeCategoryEnum
import com.jie.wealthmate.theme.ColorBlue
import com.jie.wealthmate.theme.ColorGray
import com.jie.wealthmate.theme.ColorRed
import com.jie.wealthmate.utils.formatWithCommas
import com.jie.wealthmate.vo.CategoryDiffInfoVo

/**
 * 최대 증가/감소 카테고리를 보여주는 상단 카드 섹션
 */
@Composable
fun StatusSummaryCards(
    modifier: Modifier = Modifier,
    maxIncreaseCategory: CategoryDiffInfoVo?,
    maxDecreaseCategory: CategoryDiffInfoVo?,
    largeCategory: LargeCategoryEnum,
) {
    if (maxIncreaseCategory != null || maxDecreaseCategory != null) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min) // VerticalDivider가 보이기 위해 높이 최소화 설정 필요
                .clip(RoundedCornerShape(8.dp))
                .background(ColorGray.White)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            maxIncreaseCategory?.let {
                StatusSummaryCard(
                    label = "${largeCategory.label} 최대 증가 📈",
                    info = it,
                    diffColor = ColorRed.Red_300,
                    iconBackgroundColor = largeCategory.backgroundColor
                )
            }

            if (maxIncreaseCategory != null && maxDecreaseCategory != null) {
                VerticalDivider(
                    modifier = Modifier
                        .padding(horizontal = 6.dp)
                        .fillMaxHeight(),
                    color = ColorGray.Gray_100,
                    thickness = 1.dp
                )
            }

            maxDecreaseCategory?.let {
                StatusSummaryCard(
                    label = "${largeCategory.label} 최대 감소 📉",
                    info = it,
                    diffColor = ColorBlue.Blue_300,
                    iconBackgroundColor = largeCategory.backgroundColor
                )
            }
        }
    }
}

@Composable
private fun RowScope.StatusSummaryCard(
    label: String,
    info: CategoryDiffInfoVo,
    diffColor: Color,
    iconBackgroundColor: Color,
) {
    val typography = MaterialTheme.typography
    Column(
        modifier = Modifier.weight(1f)
    ) {
        WMText(
            text = label,
            style = typography.titleSmall.copy(
                color = ColorGray.Gray_500,
                fontWeight = FontWeight.SemiBold
            ),
            modifier = Modifier.padding(bottom = 12.dp)
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            StatusCategoryIcon(icon = info.categoryIcon, backgroundColor = iconBackgroundColor)
            Spacer(modifier = Modifier.width(8.dp))
            Column(verticalArrangement = Arrangement.SpaceBetween) {
                WMText(
                    text = info.categoryName,
                    style = typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
                )
                val sign = if (info.diffAmount > 0) "+" else ""
                WMText(
                    text = "$sign${info.diffAmount.formatWithCommas()}원",
                    style = typography.labelMedium.copy(color = diffColor)
                )
            }
        }
    }
}
