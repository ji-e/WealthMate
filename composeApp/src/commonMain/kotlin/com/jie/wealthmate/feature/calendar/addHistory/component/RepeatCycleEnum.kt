package com.jie.wealthmate.feature.calendar.addHistory.component

import com.jie.wealthmate.feature.calendar.component.monthCalendar.WeekEnum
import com.jie.wealthmate.utils.convertLocalDateToString
import com.jie.wealthmate.utils.formatDateKorMD
import kotlinx.datetime.LocalDate
import kotlinx.datetime.isoDayNumber

enum class RepeatCycleEnum(val label: String, val description: String) {
    DAILY(label = "매일", description = "매일 반복돼요."),
    WEEKDAY(label = "평일", description = "매주 월요일부터 금요일까지 반복돼요."),
    WEEKEND(label = "주말", description = "매주 토요일과 일요일에 반복돼요."),
    WEEKLY(label = "매주", description = "매주 같은 요일에 반복돼요."),
    MONTHLY(label = "매달", description = "매월 같은 날짜에 반복돼요."),
    MONTH_END(label = "월말", description = "매월 마지막 날에 반복돼요."),
    YEARLY(label = "매년", description = "매년 같은 날짜에 반복돼요."),
    ;

    companion object {
        fun RepeatCycleEnum.formattedDescription(data: LocalDate): String {
            return when (this) {
                RepeatCycleEnum.WEEKLY -> {
                    "매주 ${WeekEnum.creator(data.dayOfWeek.isoDayNumber).korDisplayName}요일에 반복돼요."
                }

                RepeatCycleEnum.MONTHLY -> {
                    "매월 ${data.day}일에 반복돼요."
                }

                RepeatCycleEnum.YEARLY -> {
                    "매년 ${data.convertLocalDateToString(formatDateKorMD)}에 반복돼요."
                }

                else -> description
            }
        }
    }
}