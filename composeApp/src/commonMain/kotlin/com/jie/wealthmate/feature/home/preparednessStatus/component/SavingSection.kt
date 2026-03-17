package com.jie.wealthmate.feature.home.preparednessStatus.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jie.wealthmate.feature.budget.budgetYearDetail.component.SavingGoal
import com.jie.wealthmate.feature.home.StatusType
import com.jie.wealthmate.feature.menu.management.categoryManagement.component.LargeCategoryEnum
import com.jie.wealthmate.vo.CategoryDiffInfoVo

@Composable
fun SavingSection(
    modifier: Modifier = Modifier,
    statusType: StatusType,
    totalAmount: Long,
    budgetAmount: Long,
    categoryComparisons: List<CategoryDiffInfoVo> = emptyList(),
    fixedCategoryComparisons: List<CategoryDiffInfoVo> = emptyList(),
) {
    Column(modifier = modifier.fillMaxWidth()) {
        if (statusType == StatusType.MONTH && budgetAmount > 0) {
            SavingGoal(
                budgetSaving = budgetAmount,
                actualSaving = totalAmount,
            )
        }

        // 비중 도넛 차트 섹션
        StatusDonutChartSection(
            modifier = Modifier.padding(top = 24.dp),
            title = "카테고리별 저축 비중",
            totalLabel = "저축",
            categories = categoryComparisons + fixedCategoryComparisons
        )

        if (categoryComparisons.isNotEmpty()) {
            // 변동 저축 카테고리별 비교 섹션
            StatusCategoryComparisonSection(
                modifier = Modifier.padding(top = 24.dp),
                title = "변동저축 카테고리별 비교",
                statusType = statusType,
                largeCategory = LargeCategoryEnum.SAVING,
                comparisons = categoryComparisons
            )
        }

        if (fixedCategoryComparisons.isNotEmpty()) {
            // 고정 저축 카테고리별 비교 섹션
            StatusFixedCategoryComparisonSection(
                modifier = Modifier.padding(top = 24.dp),
                title = "고정저축 카테고리별 비교",
                statusType = statusType,
                largeCategory = LargeCategoryEnum.SAVING,
                comparisons = fixedCategoryComparisons
            )
        }
    }
}
