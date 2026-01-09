package com.jie.wealthmate.feature.calendar.component.monthCalendar

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TextButton
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jie.wealthmate.component.WMIconButton
import com.jie.wealthmate.component.WMText
import com.jie.wealthmate.theme.ColorGray
import com.jie.wealthmate.utils.convertDate
import com.jie.wealthmate.utils.convertDateToLocalDate
import com.jie.wealthmate.utils.formatDateKorYM
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import wealthmate.composeapp.generated.resources.Res
import wealthmate.composeapp.generated.resources.ic_arrow_drop_down
import wealthmate.composeapp.generated.resources.ic_calendar_today
import wealthmate.composeapp.generated.resources.ic_more_vert


/**
 * MonthCalendar Composable to display a month view calendar.
 * @param today 오늘 날짜 (예: "2024-06-15")
 * @param selectedMonth 선택된 월 (예: "2024-06")
 * @param selectedDate 선택된 날짜 (예: "2024-06-10")
 */
@Composable
fun MonthCalendar(
    today: String,
    selectedMonth: String,
    selectedDate: String,
    onClickToday: () -> Unit = {},
    onClickSelectedMonth: () -> Unit = {},
    onClickDay: (Int) -> Unit = {},
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        MonthCalendarHeader(
            today = today,
            selectedMonth = selectedMonth,
            onClickToday = onClickToday,
            onClickSelectedMonth = onClickSelectedMonth,
        )

        MonthCalendarContent(
            today = today,
            selectedMonth = selectedMonth,
            onClickDay = onClickDay
        )
    }
}

@Composable
private fun MonthCalendarHeader(
    today: String,
    selectedMonth: String,
    onClickToday: () -> Unit,
    onClickSelectedMonth: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 12.dp, end = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextButton(
            contentPadding = PaddingValues(horizontal = 12.dp),
            onClick = onClickSelectedMonth
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                WMText(
                    text = selectedMonth.convertDate(formatDateKorYM),
                    style = Typography().titleMedium.copy(fontWeight = FontWeight.SemiBold)
                )
                Icon(
                    painter = painterResource(Res.drawable.ic_arrow_drop_down),
                    contentDescription = "년 월 선택",
                    tint = ColorGray.Gray_700
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))


        IconButton(
            onClick = onClickToday
        ) {
            Box(
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_calendar_today),
                    modifier = Modifier.size(24.dp),
                    contentDescription = "오늘"
                )

                WMText(
                    text = today.convertDateToLocalDate()?.day.toString(),
                    style = Typography().labelSmall.copy(fontWeight = FontWeight.SemiBold),
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }

        WMIconButton(
            iconRes = Res.drawable.ic_more_vert,
            contentDescription = "더보기",
            onClick = {}
        )
    }
}

@Composable
private fun MonthCalendarContent(
    today: String,
    selectedMonth: String,
    onClickDay: (Int) -> Unit,
) {
    Week()
}

@Composable
@Preview(showBackground = true)
private fun MonthCalendarPreview() {
    MonthCalendar(
        today = "2024-06-15",
        selectedMonth = "2024-06",
        selectedDate = "2024-06-10",
    )
}