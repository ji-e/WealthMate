package com.jie.wealthmate.feature.calendar.component.monthCalendar

import androidx.compose.ui.graphics.Color
import com.jie.wealthmate.theme.ColorBlue
import com.jie.wealthmate.theme.ColorGray
import com.jie.wealthmate.theme.ColorRed


enum class WeekEnum(val color: Color, val korDisplayName: String, val isoDayNumber: Int) {
    SUN(
        color = ColorRed.Red_300,
        korDisplayName = "일",
        isoDayNumber = 7
    ),
    MON(
        color = ColorGray.Gray_500,
        korDisplayName = "월",
        isoDayNumber = 1
    ),
    TUE(
        color = ColorGray.Gray_500,
        korDisplayName = "화",
        isoDayNumber = 2
    ),
    WED(
        color = ColorGray.Gray_500,
        korDisplayName = "수",
        isoDayNumber = 3
    ),
    THU(
        color = ColorGray.Gray_500,
        korDisplayName = "목",
        isoDayNumber = 4
    ),
    FRI(
        color = ColorGray.Gray_500,
        korDisplayName = "금",
        isoDayNumber = 5
    ),
    SAT(
        color = ColorBlue.Blue_300,
        korDisplayName = "토",
        isoDayNumber = 6
    );

    companion object {
        fun creator(isoDayNumber: Int): WeekEnum =
            WeekEnum.entries.find { it.isoDayNumber == isoDayNumber } ?: MON
    }

}
