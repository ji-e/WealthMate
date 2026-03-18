package com.jie.wealthmate.feature.home.component

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jie.wealthmate.component.WMText
import com.jie.wealthmate.feature.home.RecurringHistoryUiModel
import com.jie.wealthmate.theme.ColorGray
import com.jie.wealthmate.theme.ColorRed
import com.jie.wealthmate.theme.noRippleClickable
import com.jie.wealthmate.utils.formatWithCommas
import org.jetbrains.compose.resources.painterResource
import wealthmate.composeapp.generated.resources.Res
import wealthmate.composeapp.generated.resources.ic_keyboard_arrow_right
import wealthmate.composeapp.generated.resources.ic_push_pin

@Composable
fun RecurringHistory(
    modifier: Modifier = Modifier,
    recurringHistories: List<RecurringHistoryUiModel>,
    totalAmount: Long,
    passedAmount: Long,
    onHeaderClick: () -> Unit = {},
    onItemClick: (String) -> Unit = {},
) {
    val typography = MaterialTheme.typography
    var isStarted by remember { mutableStateOf(false) }

    val progress = remember(totalAmount, passedAmount) {
        if (totalAmount == 0L) 0f else passedAmount.toFloat() / totalAmount.toFloat()
    }

    val animProgress by animateFloatAsState(
        targetValue = if (isStarted) progress else 0f,
        animationSpec = tween(1000, easing = FastOutSlowInEasing),
        label = "RecurringProgressAnimation"
    )

    LaunchedEffect(Unit) { isStarted = true }

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .padding(bottom = 12.dp)
                .padding(horizontal = 28.dp)
                .noRippleClickable(onClick = onHeaderClick),
            verticalAlignment = Alignment.CenterVertically
        ) {
            WMText(
                text = "이번 달 반복 지출",
                style = typography.titleSmall.copy(color = ColorGray.Gray_500),
            )

            Icon(
                painter = painterResource(Res.drawable.ic_keyboard_arrow_right),
                contentDescription = null,
                tint = ColorGray.Gray_500,
                modifier = Modifier.size(16.dp)
            )
        }

        if (recurringHistories.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .background(color = ColorGray.Gray_50, shape = RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                WMText(
                    text = "등록된 반복 지출 내역이 없습니다.",
                    style = typography.bodySmall.copy(color = ColorGray.Gray_400)
                )
            }
            return
        }

        // Graph Section
        Column(
            modifier = Modifier
                .padding(horizontal = 28.dp)
                .fillMaxWidth()
                .background(ColorGray.Gray_50, RoundedCornerShape(8.dp))
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                WMText(
                    text = "이번 달 반복 지출 현황",
                    style = typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
                )
                WMText(
                    text = "${formatWithCommas(passedAmount.toString())} " +
                            "/ ${formatWithCommas(totalAmount.toString())}원",
                    style = typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(CircleShape)
                    .background(ColorGray.Gray_200)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(animProgress)
                        .fillMaxHeight()
                        .background(ColorRed.Red_300)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))
        }

        Spacer(modifier = Modifier.height(12.dp))

        // List Section
        recurringHistories.forEach { item ->
            RecurringHistoryItem(
                item = item,
                onClick = { onItemClick(item.id) }
            )
        }
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
            .padding(vertical = 8.dp)
            .padding(start = 8.dp, end = 28.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.width(60.dp)) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(item.largeCategory.backgroundColor)
                    .align(Alignment.CenterEnd),
                contentAlignment = Alignment.Center
            ) {
                WMText(
                    text = item.categoryIcon,
                    style = typography.titleLarge
                )
            }
            if (item.isFixed) {
                Icon(
                    painter = painterResource(Res.drawable.ic_push_pin),
                    contentDescription = null,
                    tint = ColorRed.Red_300,
                    modifier = Modifier
                        .padding(start = 10.dp)
                        .size(24.dp)
                        .align(Alignment.TopStart)
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            WMText(
                text = item.content,
                style = typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                maxLines = 1
            )
            Row {
                WMText(
                    text = item.recurringDateText,
                    style = typography.bodySmall.copy(color = ColorGray.Gray_500)
                )
                if (item.isPassed) {
                    WMText(
                        text = " | ",
                        style = typography.bodySmall.copy(color = ColorGray.Gray_500)
                    )
                    WMText(
                        text = "지났음",
                        style = typography.labelSmall.copy(
                            color = ColorGray.Gray_500,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                } else if (item.isToday) {
                    WMText(
                        text = " | ",
                        style = typography.bodySmall.copy(color = ColorGray.Gray_500)
                    )
                    WMText(
                        text = "오늘",
                        style = typography.labelSmall.copy(
                            color = ColorRed.Red_300,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            }
        }

        Column(horizontalAlignment = Alignment.End) {
            WMText(
                text = "${formatWithCommas(item.singleAmount.toString())}원",
                style = typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
            )
            WMText(
                text = "/ ${formatWithCommas(item.monthlyTotalAmount.toString())}원",
                style = typography.bodySmall.copy(color = ColorGray.Gray_500)
            )
        }
    }
}
