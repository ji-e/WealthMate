package com.jie.wealthmate.feature.menu.management.repeatHistoryManagement.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jie.wealthmate.component.EmojiIcon
import com.jie.wealthmate.component.EmojiIconSize
import com.jie.wealthmate.component.SwitchSize
import com.jie.wealthmate.component.WMSwitch
import com.jie.wealthmate.component.WMText
import com.jie.wealthmate.database.eneity.RepeatCycleEntity
import com.jie.wealthmate.database.eneity.RepeatCycleWithDetails
import com.jie.wealthmate.feature.calendar.addHistory.component.RepeatCycleEnum
import com.jie.wealthmate.feature.calendar.addHistory.component.formattedShortDescription
import com.jie.wealthmate.feature.menu.management.categoryManagement.component.LargeCategoryEnum
import com.jie.wealthmate.theme.ColorGray
import com.jie.wealthmate.theme.ColorSetting
import com.jie.wealthmate.theme.Padding
import com.jie.wealthmate.utils.default
import com.jie.wealthmate.utils.formatWithCommas
import com.jie.wealthmate.utils.toLocalDate

@Composable
fun ColumnScope.RepeatHistoryList(
    modifier: Modifier = Modifier,
    repeatHistoryItems: List<RepeatCycleWithDetails>,
    onItemClick: (RepeatCycleWithDetails) -> Unit,
    onIsActiveChange: (RepeatCycleEntity, Boolean) -> Unit,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(top = Padding.SpacerXS, bottom = Padding.BackgroundBottom)
    ) {
        items(
            count = repeatHistoryItems.size,
            key = { index -> repeatHistoryItems[index].repeatCycle.id }
        ) { index ->
            val repeatHistory = repeatHistoryItems[index]

            RepeatHistoryItem(
                modifier = Modifier.clickable { onItemClick(repeatHistory) },
                data = repeatHistory,
                onIsActiveChange = onIsActiveChange
            )
        }
    }
}

@Composable
private fun RepeatHistoryItem(
    modifier: Modifier = Modifier,
    data: RepeatCycleWithDetails,
    onIsActiveChange: (RepeatCycleEntity, Boolean) -> Unit,
) {
    val typography = MaterialTheme.typography
    val repeatCycle = data.repeatCycle
    val category = data.category

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(ColorGray.White)
            .padding(
                start = Padding.BackgroundHorizontal - EmojiIconSize.MEDIUM.fixedIconSize / 3,
                end = Padding.BackgroundHorizontal
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        EmojiIcon(
            icon = category?.icon,
            color = LargeCategoryEnum.creator(data.repeatCycle.largeCategory).backgroundColor,
            isFixedUsed = true,
            isFixed = category?.isFixed.default()
        )

        Row(
            modifier = Modifier
                .padding(start = Padding.SpacerXS)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .padding(end = 12.dp)
                    .weight(1f)
            ) {
                WMText(
                    text = "${repeatCycle.amount.formatWithCommas()}원",
                    style = typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                )
                WMText(
                    text = repeatCycleInfoText(data.repeatCycle),
                    style = typography.bodySmall,
                    color = ColorSetting.Info,
                    maxLines = 2,
                )
            }

            WMSwitch(
                checked = data.repeatCycle.isActive,
                switchSize = SwitchSize.SMALL,
                onCheckedChange = { onIsActiveChange(data.repeatCycle, it) }
            )
        }
    }
}

private fun repeatCycleInfoText(data: RepeatCycleEntity): String {
    val content = data.content
    val repeatDescription =
        when (val repeatCycleEnum = RepeatCycleEnum.create(data.repeatCycle)) {
            RepeatCycleEnum.WEEKLY -> {
                repeatCycleEnum.formattedShortDescription(data.dayOfWeek ?: 1)
            }

            RepeatCycleEnum.MONTHLY -> {
                repeatCycleEnum.formattedShortDescription(data.dayOfMonth ?: 1)
            }

            else -> {
                repeatCycleEnum.formattedShortDescription(data.date.toLocalDate())
            }
        }

    val parts = listOfNotNull(
        content.takeIf { it.isNullOrBlank().not() },
        repeatDescription,
    )

    return parts.joinToString(" | ")

}
