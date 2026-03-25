package com.jie.wealthmate.feature.calendar.component

import androidx.compose.runtime.Composable
import com.jie.wealthmate.component.WMListSelectionModalBottomSheet
import com.jie.wealthmate.utils.convertLocalDateToString
import com.jie.wealthmate.utils.formatDateKorYM
import com.jie.wealthmate.utils.today
import kotlinx.datetime.LocalDate

/**
 * 월 선택 ModalBottomSheet
 */
@Composable
fun SelectedCalendarModalBottomSheet(
    monthItem: List<LocalDate>,
    selectedMonth: LocalDate = today,
    onMonthChange: (LocalDate) -> Unit = {},
    onDismissRequest: () -> Unit = {},
) {
    WMListSelectionModalBottomSheet(
        title = "월 선택",
        items = monthItem,
        selectedItem = monthItem.find { it.year == selectedMonth.year && it.month == selectedMonth.month },
        itemLabel = { it.convertLocalDateToString(formatDateKorYM) },
        onItemSelected = onMonthChange,
        onDismissRequest = onDismissRequest
    )
}
