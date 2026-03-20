package com.jie.wealthmate.feature.calendar.listCalendar

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jie.wealthmate.component.InfoText
import com.jie.wealthmate.component.WMText
import com.jie.wealthmate.feature.menu.management.categoryManagement.component.LargeCategoryEnum
import com.jie.wealthmate.theme.ColorGray
import com.jie.wealthmate.theme.ColorRed
import com.jie.wealthmate.theme.ColorSetting
import com.jie.wealthmate.theme.Padding
import com.jie.wealthmate.theme.WMTheme
import com.jie.wealthmate.utils.formatWithCommas
import com.jie.wealthmate.utils.today
import com.jie.wealthmate.vo.HistoryVo
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.painterResource
import wealthmate.composeapp.generated.resources.Res
import wealthmate.composeapp.generated.resources.ic_percent_on
import wealthmate.composeapp.generated.resources.ic_push_pin
import wealthmate.composeapp.generated.resources.ic_repeat_on

@Composable
fun DateHeader(
    date: LocalDate,
    modifier: Modifier = Modifier,
) {
    WMText(
        text = "${date.day}일",
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.SemiBold,
        color = ColorSetting.Info,
        modifier = modifier
            .fillMaxWidth()
            .background(color = ColorGray.White)
            .padding(horizontal = Padding.BackgroundHorizontal)
            .padding(top = Padding.ContainerVertical, bottom = Padding.SpacerXXS)
    )
}

@Composable
fun HistoryItem(
    history: HistoryVo,
    onItemClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val typography = MaterialTheme.typography
    val category = history.category

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onItemClick() }
            .padding(vertical =Padding.SpacerXS)
            .padding(start = 20.dp, end = Padding.BackgroundHorizontal),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 아이콘 및 배지 영역
        Box(modifier = Modifier.width(60.dp)) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(history.largeCategory.backgroundColor)
                    .align(Alignment.Center),
                contentAlignment = Alignment.Center
            ) {
                WMText(
                    text = category?.icon ?: "❓",
                    style = typography.titleLarge,
                )
            }

            // 고정 지출 핀 배지
            if (category?.isFixed == true) {
                Icon(
                    painter = painterResource(Res.drawable.ic_push_pin),
                    contentDescription = "고정",
                    tint = ColorRed.Red_300,
                    modifier = Modifier
                        .size(20.dp)
                        .align(Alignment.TopStart)
                )
            }

            // 반복 또는 할부 배지
            val badgeRes = when {
                history.repeatCycle != null -> Res.drawable.ic_repeat_on
                history.installment != null -> Res.drawable.ic_percent_on
                else -> null
            }

            badgeRes?.let {
                Image(
                    painter = painterResource(it),
                    contentDescription = null,
                    modifier = Modifier
                        .size(20.dp)
                        .align(Alignment.BottomEnd)
                )
            }
        }

        // 내용 영역
        Column(modifier = Modifier.weight(1f)) {
            val contentDisplay = remember(history.content, history.categoryInfo) {
                history.content?.takeIf { it.isNotBlank() }
                    ?: history.categoryInfo.takeIf { it.isNotBlank() }
                    ?: "내용 미입력"
            }
            val isPlaceholder = history.content.isNullOrBlank() && history.categoryInfo.isBlank()

            WMText(
                text = contentDisplay,
                style = typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = if (isPlaceholder) ColorGray.Gray_300 else ColorGray.Gray_700,
                maxLines = 1
            )

            if (history.historyInfo.isNotBlank()) {
                InfoText(
                    text = history.historyInfo,
                    maxLines = 1
                )
            }
        }

        // 금액 영역
        val amountColor = when (history.largeCategory) {
            LargeCategoryEnum.INCOME -> LargeCategoryEnum.INCOME.accentColor
            LargeCategoryEnum.EXPENSES -> LargeCategoryEnum.EXPENSES.accentColor
            LargeCategoryEnum.SAVING -> LargeCategoryEnum.SAVING.accentColor
            else -> ColorGray.Gray_700
        }

        val mark = when (history.largeCategory) {
            LargeCategoryEnum.INCOME -> "+"
            LargeCategoryEnum.EXPENSES -> "-"
            LargeCategoryEnum.SAVING -> "-"
            else -> ""
        }

        WMText(
            text = "$mark${formatWithCommas(history.amount.toString())}원",
            style = typography.titleMedium.copy(fontSize = 18.sp),
            fontWeight = FontWeight.SemiBold,
            color = amountColor,
            maxLines = 1,
            textAlign = TextAlign.End,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HistoryItemPreview() {
    WMTheme {
        Column(modifier = Modifier.background(Color.White)) {
            DateHeader(date = today)
            // Preview check with mock data or empty state
        }
    }
}
