package com.jie.wealthmate.feature.home

import com.jie.wealthmate.base.BaseUiState
import com.jie.wealthmate.feature.home.component.CategorySegmentChartData
import com.jie.wealthmate.feature.home.component.PaymentMethodSegmentChartData
import com.jie.wealthmate.feature.menu.management.categoryManagement.component.LargeCategoryEnum
import com.jie.wealthmate.utils.default
import com.jie.wealthmate.vo.CategoryTagVo
import com.jie.wealthmate.vo.CategoryVo
import com.jie.wealthmate.vo.PaymentMethodVo
import kotlinx.collections.immutable.toImmutableList

data class HomeUiState(
    val statusType: StatusType = StatusType.MONTH,
    val todayAmount: Long = 120000,
    val budgetAmount: Long = 7000000,
    val currentAmount: Amount? = null,
    val lastAmount: Amount? = null,
    val categorySegment: List<CategorySegmentChartData> = emptyList(),
    val paymentMethodSegment: List<PaymentMethodSegmentChartData> = emptyList(),
) : BaseUiState {
    val ectCategorySegmentChartData = CategorySegmentChartData(
        category = CategoryVo(
            id = "",
            icon = "•••",
            largeCategory = LargeCategoryEnum.EXPENSES,
            middleLabel = "그 외",
            sort = 0,
            isFixed = false,
            tags = emptyList<CategoryTagVo>().toImmutableList(),
        ),
        amount = currentAmount?.expensesAmount.default() - categorySegment.sumOf { it.amount }
    )

    val ectPaymentMethodSegmentChartData = PaymentMethodSegmentChartData(
        paymentMethod = PaymentMethodVo(
            id = "",
            label = "그 외",
            groupId = "",
            groupLabel = "",
            assetId = "",
            sort = 0,
        ),
        amount = currentAmount?.expensesAmount.default() - paymentMethodSegment.sumOf { it.amount }
    )

    // temp
    val categorySegmentChartItems: List<CategorySegmentChartData>
        get() {
            return if (categorySegment.size >= 5) {
                (categorySegment.take(5).toMutableList() +
                        ectCategorySegmentChartData).sortedByDescending { it.amount }
            } else {
                categorySegment
            }
        }

    val paymentMethodSegmentChartItems: List<PaymentMethodSegmentChartData>
        get() {
            return if (paymentMethodSegment.size >= 3) {
                (paymentMethodSegment.take(3).toMutableList() +
                        ectPaymentMethodSegmentChartData).sortedByDescending { it.amount }
            } else {
                paymentMethodSegment
            }
        }
}

data class Amount(
    val expensesAmount: Long,
    val incomeAmount: Long,
    val savingAmount: Long,
)

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

