package com.jie.wealthmate.feature.calendar.component.vo

import androidx.compose.runtime.Immutable
import com.jie.wealthmate.feature.calendar.monthCalendar.MonthPeriodEnum
import com.jie.wealthmate.vo.HistoryVo
import kotlinx.datetime.LocalDate

/**
 * 일별 요약 데이터를 담는 불변 데이터 클래스
 */
@Immutable
internal data class DayItemDataVo(
    val date: LocalDate,
    val period: MonthPeriodEnum,
    val incomeAmount: Long,
    val expenseAmount: Long,
    val repeatItems: List<HistoryVo>,
    val rowIndex: Int,
)