package com.jie.wealthmate.feature.calendar

import com.jie.wealthmate.base.BaseUiState
import com.jie.wealthmate.utils.today
import com.jie.wealthmate.vo.HistoryVo
import kotlinx.datetime.LocalDate

data class CalendarUiState(
    val selectedMonth: LocalDate = today,
    val selectedDate: LocalDate = today,
    val histories: List<HistoryVo> = emptyList(),
    val filterOptions: Set<CalendarFilterOption> = CalendarFilterOption.entries.toSet()
) : BaseUiState

enum class CalendarFilterOption(val label: String, val group: FilterGroup) {
    // 수입 그룹
    SHOW_INCOME("수입 노출", FilterGroup.INCOME),
    INCLUDE_FIXED_INCOME("고정 카테고리 포함", FilterGroup.INCOME),
    
    // 지출 그룹
    SHOW_EXPENSES("지출 노출", FilterGroup.EXPENSES),
    INCLUDE_FIXED_EXPENSES("지출 고정 카테고리 포함", FilterGroup.EXPENSES),
    SHOW_SAVINGS("저축 포함", FilterGroup.EXPENSES),
    INCLUDE_FIXED_SAVINGS("저축 고정 카테고리 포함", FilterGroup.EXPENSES),
    
    // 기타 그룹
    SHOW_REPEAT("반복 내역 노출", FilterGroup.ETC)
}

enum class FilterGroup(val label: String) {
    INCOME("수입"),
    EXPENSES("지출"),
    ETC("기타")
}
