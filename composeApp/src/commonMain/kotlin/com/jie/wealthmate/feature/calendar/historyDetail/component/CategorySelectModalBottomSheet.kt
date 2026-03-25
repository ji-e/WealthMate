package com.jie.wealthmate.feature.calendar.historyDetail.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.jie.wealthmate.component.ButtonSize
import com.jie.wealthmate.component.ButtonStyle
import com.jie.wealthmate.component.WMButton
import com.jie.wealthmate.component.WMModalBottomSheet
import com.jie.wealthmate.component.WMSpacer
import com.jie.wealthmate.feature.calendar.addHistory.component.CategorySelectionRow
import com.jie.wealthmate.feature.menu.management.categoryManagement.component.LargeCategoryEnum
import com.jie.wealthmate.theme.Padding
import com.jie.wealthmate.vo.CategoryTagVo
import com.jie.wealthmate.vo.CategoryVo

@Composable
fun CategorySelectModalBottomSheet(
    categoryItems: List<CategoryVo>,
    selectedLargeCategory: LargeCategoryEnum,
    selectedCategory: CategoryVo?,
    selectedCategoryTag: CategoryTagVo?,
    onConfirmClick: (CategoryVo?, CategoryTagVo?) -> Unit,
    onDismissRequest: () -> Unit,
) {
    var tempSelectedCategory by remember { mutableStateOf(selectedCategory) }
    var tempSelectedCategoryTag by remember { mutableStateOf(selectedCategoryTag) }

    WMModalBottomSheet(
        title = "카테고리 수정",
        onDismissRequest = onDismissRequest
    ) {
        CategorySelectionRow(
            modifier = Modifier.padding(horizontal = Padding.BackgroundHorizontal),
            title = null,
            categoryItems = categoryItems,
            selectedLargeCategory = selectedLargeCategory,
            selectedCategory = tempSelectedCategory,
            selectedCategoryTag = tempSelectedCategoryTag,
            onCategoryClick = {
                tempSelectedCategory = it
                tempSelectedCategoryTag = null
            },
            onCategoryTagClick = { tempSelectedCategoryTag = it }
        )

        WMSpacer()

        WMButton(
            text = "확인",
            buttonStyle = ButtonStyle.FILLED,
            buttonSize = ButtonSize.LARGE,
            modifier = Modifier
                .padding(horizontal = Padding.BackgroundHorizontal)
                .padding(bottom = Padding.BackgroundBottom)
                .fillMaxWidth(),
            onClick = {
                onDismissRequest()
                onConfirmClick(tempSelectedCategory, tempSelectedCategoryTag)
            }
        )
    }
}