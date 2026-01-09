package com.jie.wealthmate.feature.calendar.component.monthCalendar

import androidx.compose.ui.graphics.Color
import com.jie.wealthmate.theme.ColorBlue
import com.jie.wealthmate.theme.ColorGray
import com.jie.wealthmate.theme.ColorRed


enum class WeekEnum(val color: Color, val korDisplayName: String) {
    SUN(
        color = ColorRed.Red_300,
        korDisplayName = "일"
    ),
    MON(
        color = ColorGray.Gray_500,
        korDisplayName = "월"
    ),
    TUE(
        color = ColorGray.Gray_500,
        korDisplayName = "화"
    ),
    WED(
        color = ColorGray.Gray_500,
        korDisplayName = "수"
    ),
    THU(
        color = ColorGray.Gray_500,
        korDisplayName = "목"
    ),
    FRI(
        color = ColorGray.Gray_500,
        korDisplayName = "금"
    ),
    SAT(
        color = ColorBlue.Blue_300,
        korDisplayName = "토"
    );
}
