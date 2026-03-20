package com.jie.wealthmate.feature.home

import com.jie.wealthmate.base.BaseUiState
import com.jie.wealthmate.feature.home.component.vo.AmountVo
import com.jie.wealthmate.feature.home.component.vo.CategorySegmentedChartVo
import com.jie.wealthmate.feature.home.component.vo.PaymentMethodSegmentedChartVo
import com.jie.wealthmate.feature.home.component.vo.RecurringHistoryVo
import com.jie.wealthmate.feature.menu.management.categoryManagement.component.LargeCategoryEnum
import com.jie.wealthmate.utils.default
import com.jie.wealthmate.vo.CategoryTagVo
import com.jie.wealthmate.vo.CategoryVo
import com.jie.wealthmate.vo.PaymentMethodVo
import kotlinx.collections.immutable.toImmutableList

data class HomeUiState(
    val statusType: StatusType = StatusType.MONTH,
    val todayAmount: Long = 0,
    val thisMonthBudgetAmount: Long = 0,
    val thisMonthExpensesAmount: Long = 0,
    val budgetAmount: Long = 0,
    val currentAmount: AmountVo? = null,
    val lastAmount: AmountVo? = null,
    val categorySegment: List<CategorySegmentedChartVo> = emptyList(),
    val paymentMethodSegment: List<PaymentMethodSegmentedChartVo> = emptyList(),
    val currentExpensesData: List<Float?> = emptyList(),
    val lastExpensesData: List<Float> = emptyList(),
    val recurringHistories: List<RecurringHistoryVo> = emptyList(),
) : BaseUiState {
    val categorySegmentChartItems: List<CategorySegmentedChartVo>
        get() {
            val totalExpenses = currentAmount?.expensesAmount.default()
            if (totalExpenses <= 0L || categorySegment.isEmpty()) return emptyList()

            val limit = 5
            if (categorySegment.size <= limit) return categorySegment

            val topItems = categorySegment.take(limit)
            val othersCount = categorySegment.size - limit
            val othersAmount = totalExpenses - topItems.sumOf { it.amount }

            if (othersAmount <= 0L) return topItems

            val othersItem = CategorySegmentedChartVo(
                category = CategoryVo(
                    id = "others",
                    icon = "•••",
                    largeCategory = LargeCategoryEnum.EXPENSES,
                    middleLabel = "그 외 ${othersCount}개",
                    sort = 0,
                    isFixed = false,
                    tags = emptyList<CategoryTagVo>().toImmutableList(),
                ),
                amount = othersAmount
            )
            return topItems + othersItem
        }

    val paymentMethodSegmentChartItems: List<PaymentMethodSegmentedChartVo>
        get() {
            val totalExpenses = currentAmount?.expensesAmount.default()
            if (totalExpenses <= 0L || paymentMethodSegment.isEmpty()) return emptyList()

            val limit = 3
            if (paymentMethodSegment.size <= limit) return paymentMethodSegment

            val topItems = paymentMethodSegment.take(limit)
            val othersCount = paymentMethodSegment.size - limit
            val othersAmount = totalExpenses - topItems.sumOf { it.amount }

            if (othersAmount <= 0L) return topItems

            val othersItem = PaymentMethodSegmentedChartVo(
                paymentMethod = PaymentMethodVo(
                    id = "others",
                    label = "그 외 ${othersCount}개",
                    groupId = "",
                    groupLabel = "",
                    assetId = "",
                    sort = 0,
                ),
                amount = othersAmount
            )
            return topItems + othersItem
        }

    val passedRecurringAmount: Long
        get() = recurringHistories
            .filter { it.isPassed || it.isToday }
            .sumOf { it.monthlyTotalAmount }

    val totalRecurringAmount: Long
        get() = recurringHistories.sumOf { it.monthlyTotalAmount }

    val sortedRecurringHistories: List<RecurringHistoryVo>
        get() = recurringHistories.sortedWith(
            compareBy<RecurringHistoryVo> { it.isPassed }
                .thenByDescending { it.isToday }
                .thenBy { it.sortOrder }
        )
}

enum class StatusType(val label: String, val lastLabel: String) {
    WEEK(
        label = "이번 주",
        lastLabel = "지난 주"
    ),
    MONTH(
        label = "이번 달",
        lastLabel = "지난 달"
    ),
    YEAR(
        label = "올 해",
        lastLabel = "지난 해"
    )
}
