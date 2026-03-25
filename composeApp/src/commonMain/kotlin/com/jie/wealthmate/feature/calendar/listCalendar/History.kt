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
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jie.wealthmate.component.EmojiIcon
import com.jie.wealthmate.component.EmojiIconSize
import com.jie.wealthmate.component.InfoText
import com.jie.wealthmate.component.SpacerSize
import com.jie.wealthmate.component.WMSpacer
import com.jie.wealthmate.component.WMText
import com.jie.wealthmate.feature.menu.management.categoryManagement.component.LargeCategoryEnum
import com.jie.wealthmate.theme.ColorGray
import com.jie.wealthmate.theme.ColorSetting
import com.jie.wealthmate.theme.Padding
import com.jie.wealthmate.theme.WMTheme
import com.jie.wealthmate.utils.default
import com.jie.wealthmate.utils.formatWithCommas
import com.jie.wealthmate.utils.today
import com.jie.wealthmate.vo.CategoryVo
import com.jie.wealthmate.vo.HistoryVo
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.painterResource
import wealthmate.composeapp.generated.resources.Res
import wealthmate.composeapp.generated.resources.ic_percent_on
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
            .padding(vertical = Padding.SpacerXS)
            .padding(start = 20.dp, end = Padding.BackgroundHorizontal),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 아이콘 및 배지 영역
        Box(
            modifier = Modifier.width(
                EmojiIconSize.MEDIUM.boxSize + (EmojiIconSize.MEDIUM.fixedIconSize / 3) * 2
            )
        ) {
            EmojiIcon(
                icon = category?.icon,
                color = history.largeCategory.backgroundColor,
                isFixed = category?.isFixed.default(),
                isFixedUsed = true
            )

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
        Column(
            modifier = Modifier
                .padding(start = Padding.SpacerXS)
                .weight(1f)
        ) {
            val (contentDisplay, isPlaceholder) = remember(history.content, history.categoryInfo) {
                val display = history.content?.takeIf { it.isNotBlank() }
                    ?: history.categoryInfo.takeIf { it.isNotBlank() }
                    ?: "내용 미입력"
                val placeholder = history.content.isNullOrBlank() && history.categoryInfo.isBlank()
                display to placeholder
            }

            WMText(
                text = contentDisplay,
                style = typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = if (isPlaceholder) ColorGray.Gray_300 else ColorGray.Gray_700,
                maxLines = 1
            )

            if (history.historyInfo.isNotBlank()) {
                WMSpacer(size = SpacerSize.XX_SMALL)
                InfoText(
                    text = history.historyInfo,
                    maxLines = 1
                )
            }
        }

        // 금액 영역
        val mark = when (history.largeCategory) {
            LargeCategoryEnum.INCOME -> "+"
            LargeCategoryEnum.EXPENSES, LargeCategoryEnum.SAVING -> "-"
        }

        WMText(
            text = "$mark${formatWithCommas(history.amount.toString())}원",
            style = typography.titleMedium,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = history.largeCategory.accentColor,
            maxLines = 1,
            textAlign = TextAlign.End,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

@Preview
@Composable
private fun HistoryItemPreview() {
    val mockHistory = HistoryVo(
        id = "1",
        largeCategory = LargeCategoryEnum.EXPENSES,
        date = today,
        amount = 12500,
        category = CategoryVo.unset(LargeCategoryEnum.EXPENSES).copy(
            icon = "🍱",
            middleLabel = "식비"
        ),
        content = "점심 돈까스"
    )

    WMTheme {
        Column(modifier = Modifier.background(Color.White)) {
            DateHeader(date = today)
            HistoryItem(
                history = mockHistory,
                onItemClick = {}
            )
            HistoryItem(
                history = mockHistory.copy(
                    largeCategory = LargeCategoryEnum.INCOME,
                    amount = 50000,
                    content = "용돈",
                    category = CategoryVo.unset(LargeCategoryEnum.INCOME).copy(icon = "💰")
                ),
                onItemClick = {}
            )
            HistoryItem(
                history = mockHistory.copy(
                    content = null,
                    category = CategoryVo.unset(LargeCategoryEnum.EXPENSES).copy(
                        icon = "☕",
                        middleLabel = "카페/간식"
                    )
                ),
                onItemClick = {}
            )
        }
    }
}
