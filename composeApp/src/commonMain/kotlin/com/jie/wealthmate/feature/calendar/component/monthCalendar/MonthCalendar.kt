package com.jie.wealthmate.feature.calendar.component.monthCalendar

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TextButton
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jie.wealthmate.component.WMIconButton
import com.jie.wealthmate.component.WMText
import com.jie.wealthmate.theme.ColorGray
import com.jie.wealthmate.utils.convertLocalDateToString
import com.jie.wealthmate.utils.formatDateKorYM
import com.jie.wealthmate.utils.today
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import wealthmate.composeapp.generated.resources.Res
import wealthmate.composeapp.generated.resources.ic_arrow_drop_down
import wealthmate.composeapp.generated.resources.ic_calendar_today
import wealthmate.composeapp.generated.resources.ic_more_vert

/**
 * MonthCalendar Composable to display a month view calendar.
 * @param selectedMonth 선택된 월 (예: "2024-06")
 * @param selectedDate 선택된 날짜 (예: "2024-06-10")
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MonthCalendar(
    modifier: Modifier = Modifier,
    selectedMonth: LocalDate,
    selectedDate: LocalDate,
    onClickToday: () -> Unit = {},
    onClickSelectedMonth: () -> Unit = {},
    onClickDate: (LocalDate) -> Unit = {},
) {
    val anchoredState = remember {
        AnchoredDraggableState(
            initialValue = CalendarState.Normal,
            anchors = DraggableAnchors {
                CalendarState.Maximized at 0f
                CalendarState.Normal at -1f
                CalendarState.Minimized at -2f
            },
        )
    }

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        MonthCalendarHeader(
            today = today,
            selectedMonth = selectedMonth,
            onClickToday = onClickToday,
            onClickSelectedMonth = onClickSelectedMonth,
        )

        MonthCalendarContent(
            modifier = Modifier.weight(1f),
            anchoredState = anchoredState,
            today = today,
            selectedDate = selectedDate,
            selectedMonth = selectedMonth,
            onClickDate = onClickDate
        )
    }
}

@Composable
private fun MonthCalendarHeader(
    today: LocalDate,
    selectedMonth: LocalDate,
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
                    text = selectedMonth.convertLocalDateToString(formatDateKorYM),
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
                    text = today.day.toString(),
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun MonthCalendarContent(
    modifier: Modifier = Modifier,
    anchoredState: AnchoredDraggableState<CalendarState>,
    today: LocalDate,
    selectedDate: LocalDate,
    selectedMonth: LocalDate,
    onClickDate: (LocalDate) -> Unit,
) {
    Column(modifier = modifier) {
        WeekHeader()
        DayGrid(
            today = today,
            selectedDate = selectedDate,
            selectedMonth = selectedMonth,
            anchoredState = anchoredState,
            onClickDate = onClickDate
        )
    }
}

@Composable
@Preview(showBackground = true)
private fun MonthCalendarPreview() {
    MonthCalendar(
        modifier = Modifier.fillMaxHeight(),
        selectedMonth = today,
        selectedDate = today,
    )
}
