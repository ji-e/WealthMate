package com.jie.wealthmate.feature.calendar.component.monthCalendar

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jie.wealthmate.component.WMText
import com.jie.wealthmate.utils.convertDate
import com.jie.wealthmate.utils.formatDateKorYM
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import wealthmate.composeapp.generated.resources.Res
import wealthmate.composeapp.generated.resources.ic_arrow_drop_down
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
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        MonthCalendarHeader(selectedMonth)
    }
}

@Composable
private fun MonthCalendarHeader(
    selectedMonth: String,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically
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
            )
        }

        WMText(
            text = "오늘",
            style = Typography().labelLarge
        )

        Spacer(modifier = Modifier.weight(1f))

        Icon(
            painter = painterResource(Res.drawable.ic_more_vert),
            contentDescription = "더보기",
        )
    }
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