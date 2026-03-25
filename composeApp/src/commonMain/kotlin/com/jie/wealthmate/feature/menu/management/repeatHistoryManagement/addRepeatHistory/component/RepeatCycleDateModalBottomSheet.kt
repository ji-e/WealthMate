package com.jie.wealthmate.feature.menu.management.repeatHistoryManagement.addRepeatHistory.component

import androidx.compose.runtime.Composable
import com.jie.wealthmate.component.WMListSelectionModalBottomSheet

@Composable
fun RepeatCycleDateModalBottomSheet(
    repeatCycleDate: Int?,
    repeatCycleDateItems: List<Pair<Int, String>>,
    onConfirmClick: (Int?) -> Unit,
    onDismissRequest: () -> Unit,
) {
    WMListSelectionModalBottomSheet(
        title = "반복 날짜",
        items = repeatCycleDateItems,
        selectedItem = repeatCycleDateItems.find { it.first == repeatCycleDate },
        itemLabel = { it.second },
        onItemSelected = {
            onConfirmClick(it.first)
        },
        onDismissRequest = onDismissRequest
    )
}
