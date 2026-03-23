package com.jie.wealthmate.feature.menu.management.categoryManagement.editCategory.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.jie.wealthmate.component.CategoryIconEnum
import com.jie.wealthmate.component.WMModalBottomSheet
import com.jie.wealthmate.theme.ColorSetting
import com.jie.wealthmate.theme.Padding
import com.jie.wealthmate.theme.WMTheme


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
            onIconChange = {
                onIconChange(it)
                onDismissRequest()
            },
        )
    }
}

@Composable
@Preview
private fun CategoryIconModalBottomSheetPreview() {
    WMTheme {
        Column(
            modifier = Modifier
                .background(ColorSetting.Info)
                .padding(Padding.SpacerM)
        ) {
            CategoryIconModalBottomSheet(
                selectedCategoryIcon = CategoryIconEnum.defaultCategoryIcon,
                onIconChange = {},
                onDismissRequest = {}
            )
        }
    }
}
