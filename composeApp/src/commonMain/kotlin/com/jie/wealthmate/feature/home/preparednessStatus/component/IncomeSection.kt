package com.jie.wealthmate.feature.home.preparednessStatus.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jie.wealthmate.feature.home.StatusType
import com.jie.wealthmate.feature.menu.management.categoryManagement.component.LargeCategoryEnum
import com.jie.wealthmate.vo.CategoryDiffInfoVo

@Composable
fun IncomeSection(
    modifier: Modifier = Modifier,
    statusType: StatusType,
    totalAmount: Long,
    categoryComparisons: List<CategoryDiffInfoVo> = emptyList(),
    fixedCategoryComparisons: List<CategoryDiffInfoVo> = emptyList(),
) {
    Column(modifier = modifier.fillMaxWidth()) {
        if (totalAmount > 0) {
            StatusDonutChartSection(
                title = "카테고리별 수입 비중",
                totalLabel = "수입",
                categories = categoryComparisons + fixedCategoryComparisons
            )
        }

        if (categoryComparisons.isNotEmpty()) {
            StatusCategoryComparisonSection(
                modifier = Modifier.padding(top = 24.dp),
                title = "변동수입 카테고리별 비교",
                statusType = statusType,
                largeCategory = LargeCategoryEnum.INCOME,
                comparisons = categoryComparisons
            )
        }

        if (fixedCategoryComparisons.isNotEmpty()) {
            StatusFixedCategoryComparisonSection(
                modifier = Modifier.padding(top = 24.dp),
                title = "고정수입 카테고리별 비교",
                statusType = statusType,
                largeCategory = LargeCategoryEnum.INCOME,
                comparisons = fixedCategoryComparisons
            )
        }
    }
}
