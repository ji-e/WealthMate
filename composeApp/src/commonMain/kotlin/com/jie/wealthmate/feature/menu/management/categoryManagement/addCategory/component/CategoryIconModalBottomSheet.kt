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
        title = "카테고리 아이콘",
        onDismissRequest = onDismissRequest,
    ) {
        CategoryIconGrid(
            selectedCategoryIcon = selectedCategoryIcon,
            onIconChange = onIconChange,
        )
    }
}