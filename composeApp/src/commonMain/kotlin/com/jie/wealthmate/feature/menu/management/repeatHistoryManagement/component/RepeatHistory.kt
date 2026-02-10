package com.jie.wealthmate.feature.menu.management.repeatHistoryManagement.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jie.wealthmate.component.SwitchSize
import com.jie.wealthmate.component.WMSwitch
import com.jie.wealthmate.component.WMText
import com.jie.wealthmate.database.eneity.RepeatCycleEntity
import com.jie.wealthmate.database.eneity.RepeatCycleWithDetails
import com.jie.wealthmate.feature.calendar.addHistory.component.RepeatCycleEnum
import com.jie.wealthmate.feature.calendar.addHistory.component.RepeatCycleEnum.Companion.formattedShortDescription
import com.jie.wealthmate.feature.menu.management.categoryManagement.component.LargeCategoryEnum
import com.jie.wealthmate.theme.ColorGray
import com.jie.wealthmate.theme.ColorRed
import com.jie.wealthmate.utils.default
import com.jie.wealthmate.utils.formatWithCommas
import com.jie.wealthmate.utils.toLocalDate
import org.jetbrains.compose.resources.painterResource
import wealthmate.composeapp.generated.resources.Res
import wealthmate.composeapp.generated.resources.ic_push_pin

@Composable
fun ColumnScope.RepeatHistoryList(
    modifier: Modifier = Modifier,
    repeatHistoryItems: List<RepeatCycleWithDetails>,
    onItemClick: (RepeatCycleWithDetails) -> Unit,
    onIsActiveChange: (RepeatCycleEntity, Boolean) -> Unit,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 20.dp)
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
            .padding(start = 20.dp, end = 28.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(modifier = Modifier.width(60.dp)) {
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(LargeCategoryEnum.creator(data.repeatCycle.largeCategory).backgroundColor)
                    .size(40.dp)
                    .align(Alignment.Center),
                contentAlignment = Alignment.Center
            ) {
                WMText(
                    text = category?.icon ?: "?",
                    style = typography.titleLarge
                )
            }
            if (category?.isFixed.default()) {
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

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .padding(end = 12.dp)
                    .weight(1f)
            ) {
                WMText(
                    text = "${formatWithCommas(repeatCycle.amount.toString())}원",
                    style = typography.titleMedium.copy(fontWeight = FontWeight.Medium),
                    maxLines = 1,
                )
                WMText(
                    text = repeatCycleInfoText(data.repeatCycle),
                    style = typography.bodySmall.copy(color = ColorGray.Gray_500),
                    maxLines = 2,
                )
            }

            WMSwitch(
                checked = data.repeatCycle.isActive,
                switchSize = SwitchSize.X_SMALL,
                onCheckedChange = { onIsActiveChange(data.repeatCycle, it) }
            )
        }
    }
}

private fun repeatCycleInfoText(data: RepeatCycleEntity): String {
    val content = data.content
    val repeatCycle = RepeatCycleEnum.create(data.repeatCycle)
        .formattedShortDescription(data.date.toLocalDate())

    val parts = listOfNotNull(
        content.takeIf { it.isNullOrBlank().not() },
        repeatCycle,
    )

    return parts.joinToString(" | ")

}
