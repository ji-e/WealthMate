package com.jie.wealthmate.feature.budget.budgetSetting.component

import androidx.compose.runtime.Composable
import com.jie.wealthmate.component.WMListSelectionModalBottomSheet
import com.jie.wealthmate.utils.convertDate
import com.jie.wealthmate.utils.formatDateKorYM

@Composable
fun MonthBudgetMoreModalBottomSheet(
    targetYearMonth: String,
    hasBudgetByTarget: Boolean,
    onItemSelected: (BudgetMoreMenu) -> Unit,
    onDismissRequest: () -> Unit,
) {
    val formattedDate = targetYearMonth.convertDate(formatDateKorYM)
    val title = if (formattedDate.isNotEmpty()) "$formattedDate 예산 관리" else "예산 관리"

    val menuItems = if (hasBudgetByTarget) {
        listOf(BudgetMoreMenu.MODIFY, BudgetMoreMenu.COPY, BudgetMoreMenu.DELETE)
    } else {
        listOf(BudgetMoreMenu.ADD)
    }

    WMListSelectionModalBottomSheet(
        title = title,
        items = menuItems,
        selectedItem = null,
        itemLabel = { it.label },
        onItemSelected = onItemSelected,
        onDismissRequest = onDismissRequest
    )
}

enum class BudgetMoreMenu(val label: String) {
    ADD("추가"),
    MODIFY("수정"),
    COPY("복사"),
    DELETE("삭제")
}