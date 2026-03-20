package com.jie.wealthmate.feature.home.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jie.wealthmate.component.EmojiIcon
import com.jie.wealthmate.component.EmojiIconSize
import com.jie.wealthmate.component.EmptyBoxView
import com.jie.wealthmate.component.HorizontalBar
import com.jie.wealthmate.component.SpacerSize
import com.jie.wealthmate.component.WMSpacer
import com.jie.wealthmate.component.WMText
import com.jie.wealthmate.feature.home.RecurringHistoryUiModel
import com.jie.wealthmate.feature.menu.management.categoryManagement.component.LargeCategoryEnum
import com.jie.wealthmate.theme.ColorGray
import com.jie.wealthmate.theme.ColorRed
import com.jie.wealthmate.theme.ColorSetting
import com.jie.wealthmate.theme.Padding
import com.jie.wealthmate.theme.Shapes
import com.jie.wealthmate.theme.WMTheme
import com.jie.wealthmate.theme.noRippleClickable
import com.jie.wealthmate.utils.formatWithCommas
import org.jetbrains.compose.resources.painterResource
import wealthmate.composeapp.generated.resources.Res
import wealthmate.composeapp.generated.resources.ic_keyboard_arrow_right

@Composable
fun RecurringHistory(
    recurringHistories: List<RecurringHistoryUiModel>,
    totalAmount: Long,
    passedAmount: Long,
    modifier: Modifier = Modifier,
    onHeaderClick: () -> Unit = {},
    onItemClick: (String) -> Unit = {},
) {
    Column(modifier = modifier.fillMaxWidth()) {
        RecurringHistoryHeader(onClick = onHeaderClick)

        WMSpacer(size = SpacerSize.SMALL)

        if (recurringHistories.isEmpty()) {
            EmptyBoxView(
                modifier = Modifier.padding(horizontal = Padding.BackgroundHorizontal),
                contentText = "등록된 반복 지출 내역이 없습니다.",
            )
        } else {
            RecurringStatusCard(
                passedAmount = passedAmount,
                totalAmount = totalAmount
            )

            WMSpacer(size = SpacerSize.X_SMALL)

            recurringHistories.forEach { item ->
                key(item.id) {
                    RecurringHistoryItem(
                        item = item,
                        onClick = { onItemClick(item.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun RecurringHistoryHeader(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .padding(horizontal = Padding.BackgroundHorizontal)
            .noRippleClickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        WMText(
            text = "이번 달 반복 지출",
            style = MaterialTheme.typography.titleSmall.copy(color = ColorSetting.Info),
        )

        Icon(
            painter = painterResource(Res.drawable.ic_keyboard_arrow_right),
            contentDescription = null,
            tint = ColorSetting.Info,
            modifier = Modifier.size(16.dp)
        )
    }
}

@Composable
private fun RecurringStatusCard(
    passedAmount: Long,
    totalAmount: Long,
    modifier: Modifier = Modifier,
) {
    val typography = MaterialTheme.typography
    Column(
        modifier = modifier
            .padding(horizontal = Padding.BackgroundHorizontal)
            .fillMaxWidth()
            .background(ColorGray.Gray_50, Shapes.medium)
            .padding(
                horizontal = Padding.ContainerHorizontal,
                vertical = Padding.ContainerVertical
            )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            WMText(
                text = "이번 달 반복 지출 현황",
                style = typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            WMText(
                text = "${passedAmount.formatWithCommas()} / ${totalAmount.formatWithCommas()}원",
                style = typography.bodySmall
            )
        }

        WMSpacer(size = SpacerSize.X_SMALL)

        HorizontalBar(
            spent = passedAmount,
            total = totalAmount,
            firstBarColor = LargeCategoryEnum.EXPENSES.middleColor
        )

        WMSpacer(size = SpacerSize.XX_SMALL)
    }
}

@Composable
private fun RecurringHistoryItem(
    item: RecurringHistoryUiModel,
    onClick: () -> Unit,
) {
    val typography = MaterialTheme.typography

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = Padding.SpacerXS)
            .padding(
                start = Padding.BackgroundHorizontal - EmojiIconSize.MEDIUM.fixedIconSize / 3,
                end = Padding.BackgroundHorizontal
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon Section
        EmojiIcon(
            icon = item.categoryIcon,
            color = item.largeCategory.backgroundColor,
            isFixed = item.isFixed,
            isFixedUsed = true
        )

        // Content Section
        Column(
            modifier = Modifier
                .padding(horizontal = Padding.SpacerXS)
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(Padding.SpacerXXS)
        ) {
            WMText(
                text = item.content,
                style = typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                WMText(
                    text = item.recurringDateText,
                    style = typography.bodySmall,
                    color = ColorSetting.Info
                )

                val statusInfo = when {
                    item.isPassed -> "지났음" to ColorSetting.Info
                    item.isToday -> "오늘" to ColorRed.Red_300
                    else -> null
                }

                statusInfo?.let { (text, color) ->
                    WMText(
                        text = " | ",
                        style = typography.bodySmall,
                        color = ColorSetting.Info
                    )
                    WMText(
                        text = text,
                        style = typography.bodySmall,
                        color = color,
                    )
                }
            }
        }

        // Amount Section
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(Padding.SpacerXXS)
        ) {
            WMText(
                text = "${item.singleAmount.formatWithCommas()}원",
                style = typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            WMText(
                text = "/ ${item.monthlyTotalAmount.formatWithCommas()}원",
                style = typography.bodySmall,
                color = ColorSetting.Info
            )
        }
    }
}

@Preview(name = "Data Exist", showBackground = true)
@Composable
private fun RecurringHistoryPreview() {
    WMTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(vertical = 16.dp)
        ) {
            RecurringHistory(
                recurringHistories = listOf(
                    RecurringHistoryUiModel(
                        id = "1",
                        categoryIcon = "🍔",
                        largeCategory = LargeCategoryEnum.EXPENSES,
                        content = "점심 식비가 얼마일까요?????!!?!?!?!?",
                        singleAmount = 10000,
                        monthlyTotalAmount = 200000,
                        recurringDateText = "매월 10일",
                        isPassed = true,
                        isFixed = true
                    ),
                    RecurringHistoryUiModel(
                        id = "2",
                        categoryIcon = "🏠",
                        largeCategory = LargeCategoryEnum.EXPENSES,
                        content = "월세",
                        singleAmount = 500000,
                        monthlyTotalAmount = 500000,
                        recurringDateText = "매월 25일",
                        isPassed = false,
                        isToday = true,
                        isFixed = false
                    )
                ),
                totalAmount = 700000,
                passedAmount = 200000
            )
        }
    }
}

@Preview(name = "Data Empty", showBackground = true)
@Composable
private fun RecurringHistoryEmptyPreview() {
    WMTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(vertical = 16.dp)
        ) {
            RecurringHistory(
                recurringHistories = emptyList(),
                totalAmount = 0,
                passedAmount = 0
            )
        }
    }
}
