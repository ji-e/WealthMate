package com.jie.wealthmate.feature.menu.management.categoryManagement.addCategory.component

import androidx.compose.runtime.Composable
import com.jie.wealthmate.component.CategoryIconEnum
import com.jie.wealthmate.component.WMModalBottomSheet


@Composable
fun CategoryIconModalBottomSheet(
    selectedCategoryIcon: CategoryIconEnum,
    onIconChange: (CategoryIconEnum) -> Unit = {},
    onDismissRequest: () -> Unit,
) {
    WMModalBottomSheet(
        onDismissRequest = onDismissRequest,
    ) {
        CategoryIconGrid(
            selectedCategoryIcon = selectedCategoryIcon,
            onIconChange = onIconChange,
        )
    }
}