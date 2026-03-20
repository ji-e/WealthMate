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
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jie.wealthmate.component.InfoText
import com.jie.wealthmate.component.WMText
import com.jie.wealthmate.feature.menu.management.categoryManagement.component.LargeCategoryEnum
import com.jie.wealthmate.theme.ColorGray
import com.jie.wealthmate.theme.ColorRed
import com.jie.wealthmate.utils.default
import com.jie.wealthmate.utils.formatWithCommas
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
) {
    WMText(
        text = "${date.day}일",
        style = Typography().titleSmall.copy(
            fontWeight = FontWeight.SemiBold,
            color = ColorGray.Gray_500
        ),
        modifier = Modifier
            .fillMaxWidth()
            .background(color = ColorGray.White)
            .padding(horizontal = 28.dp)
            .padding(top = 12.dp, bottom = 4.dp)
    )
}

@Composable
fun HistoryItem(
    history: HistoryVo,
    onItemClick: () -> Unit,
) {
    val category = history.category
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onItemClick() }
            .padding(vertical = 8.dp)
            .padding(start = 20.dp, end = 28.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.width(60.dp)) {
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(history.largeCategory.backgroundColor)
                    .size(40.dp)
                    .align(Alignment.Center),
                contentAlignment = Alignment.Center
            ) {
                WMText(
                    text = category?.icon ?: "❓",
                    style = Typography().titleLarge,
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
            history.repeatCycle?.let {
                Image(
                    painter = painterResource(Res.drawable.ic_repeat_on),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(end = 4.dp)
                        .size(24.dp)
                        .align(Alignment.BottomEnd)
                )
            }
            history.installment?.let {
                Image(
                    painter = painterResource(Res.drawable.ic_percent_on),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(end = 4.dp)
                        .size(24.dp)
                        .align(Alignment.BottomEnd)
                )
            }
        }

        val mark = when (history.largeCategory) {
            LargeCategoryEnum.INCOME -> "+"
            LargeCategoryEnum.EXPENSES -> "-"
            else -> ""
        }

        Column {
            val content = history.content.default()
                .ifEmpty { history.categoryInfo.default() }
            WMText(
                text = content.ifEmpty { "내용 미입력" },
                style = Typography().bodyLarge.copy(
                    fontWeight = FontWeight.Medium,
                    color = if (content.isBlank()) ColorGray.Gray_300 else ColorGray.Gray_700
                ),
                maxLines = 1
            )

            if (history.historyInfo.isNotBlank()) {
                InfoText(
                    text = history.historyInfo,
                    maxLines = 2
                )
            }
        }
        WMText(
            text = "$mark${formatWithCommas(history.amount.toString())}원",
            style = Typography().titleMedium.copy(
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            ),
            maxLines = 1,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.End
        )
    }
}
