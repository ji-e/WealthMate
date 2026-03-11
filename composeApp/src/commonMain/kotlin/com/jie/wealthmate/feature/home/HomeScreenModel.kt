package com.jie.wealthmate.feature.home

import com.jie.wealthmate.base.BaseScreenModel
import com.jie.wealthmate.feature.home.component.CategorySegmentChartData
import com.jie.wealthmate.feature.home.component.PaymentMethodSegmentChartData
import com.jie.wealthmate.feature.menu.management.categoryManagement.component.LargeCategoryEnum
import com.jie.wealthmate.repository.BudgetRepository
import com.jie.wealthmate.repository.HistoryRepository
import com.jie.wealthmate.utils.convertLocalDateToString
import com.jie.wealthmate.utils.firstDayOfMonth
import com.jie.wealthmate.utils.formatDateHyphenYM
import com.jie.wealthmate.utils.lastDayOfMonth
import com.jie.wealthmate.utils.toEpochMilliseconds
import com.jie.wealthmate.utils.toLocalDate
import com.jie.wealthmate.utils.today
import com.jie.wealthmate.vo.CategoryVo.Companion.mapperToVo
import com.jie.wealthmate.vo.PaymentMethodVo.Companion.mapperToVo
import kotlinx.coroutines.flow.combine
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.minus

class HomeScreenModel(
    private val historyRepository: HistoryRepository,
    private val budgetRepository: BudgetRepository,
) : BaseScreenModel<HomeUiState>() {

    override val initialState: HomeUiState
        get() = HomeUiState()

    init {
        loadHomeData()
    }

    fun updateStatusType(statusType: StatusType) {
        reduceState { state ->
            state.copy(statusType = statusType)
        }
    }

    private fun loadHomeData() {
        val currentMonthStart = today.firstDayOfMonth().toEpochMilliseconds()
        val currentMonthEnd = today.lastDayOfMonth().toEpochMilliseconds()
        
        val lastMonthDate = today.minus(1, DateTimeUnit.MONTH)
        val lastMonthStart = lastMonthDate.firstDayOfMonth().toEpochMilliseconds()
        val lastMonthEnd = lastMonthDate.lastDayOfMonth().toEpochMilliseconds()

        val yearMonth = today.convertLocalDateToString(formatDateHyphenYM)

        combine(
            budgetRepository.getBudgetsByMonthWithDetails(yearMonth),
            historyRepository.getHistoriesByMonth(currentMonthStart, currentMonthEnd),
            historyRepository.getHistoriesByMonth(lastMonthStart, lastMonthEnd)
        ) { budgets, currentHistories, lastHistories ->
            // 1. 카테고리별 그룹화 (최적화: 반복적인 filter 대신 한 번의 grouping 사용)
            val currentGrouped = currentHistories.groupBy { it.history.largeCategory }
            val lastGrouped = lastHistories.groupBy { it.history.largeCategory }

            val currentExpenses = currentGrouped[LargeCategoryEnum.EXPENSES.name] ?: emptyList()
            val currentIncome = currentGrouped[LargeCategoryEnum.INCOME.name] ?: emptyList()
            val currentSaving = currentGrouped[LargeCategoryEnum.SAVING.name] ?: emptyList()

            val lastExpenses = lastGrouped[LargeCategoryEnum.EXPENSES.name] ?: emptyList()
            val lastIncome = lastGrouped[LargeCategoryEnum.INCOME.name] ?: emptyList()
            val lastSaving = lastGrouped[LargeCategoryEnum.SAVING.name] ?: emptyList()

            // 2. 예산 계산 (지출만)
            val budgetAmount = budgets
                .filter { it.category?.largeCategory == LargeCategoryEnum.EXPENSES.name }
                .sumOf { it.budget.amount }

            // 3. 오늘 지출 계산
            val todayStart = today.toEpochMilliseconds()
            val todayEnd = todayStart + 86_399_999L // 24 * 60 * 60 * 1000 - 1
            val todayAmount = currentExpenses
                .filter { it.history.date in todayStart..todayEnd }
                .sumOf { it.history.amount }

            // 4. 차트 데이터 (Category / Payment Method)
            val categorySegments = currentExpenses
                .mapNotNull { it.category?.let { cat -> it.history.amount to cat } }
                .groupBy({ it.second.id }, { it.first })
                .map { (id, amounts) ->
                    val firstHistoryWithCat = currentExpenses.first { it.category?.id == id }
                    CategorySegmentChartData(
                        category = firstHistoryWithCat.category!!.mapperToVo(),
                        amount = amounts.sum()
                    )
                }.sortedByDescending { it.amount }

            val paymentMethodSegments = currentExpenses
                .mapNotNull { it.paymentMethod?.let { pm -> it.history.amount to pm } }
                .groupBy({ it.second.id }, { it.first })
                .map { (id, amounts) ->
                    val firstHistoryWithPm = currentExpenses.first { it.paymentMethod?.id == id }
                    PaymentMethodSegmentChartData(
                        paymentMethod = firstHistoryWithPm.paymentMethod!!.mapperToVo(),
                        amount = amounts.sum()
                    )
                }.sortedByDescending { it.amount }

            // 5. 누적 꺽은선 그래프 데이터 계산
            val daysInCurrentMonth = today.lastDayOfMonth().day
            val daysInLastMonth = lastMonthDate.lastDayOfMonth().day
            val maxDays = maxOf(daysInCurrentMonth, daysInLastMonth)

            val currentDailyTotals = currentExpenses.groupBy { it.history.date.toLocalDate().day }
                .mapValues { it.value.sumOf { h -> h.history.amount } }

            val lastDailyTotals = lastExpenses.groupBy { it.history.date.toLocalDate().day }
                .mapValues { it.value.sumOf { h -> h.history.amount } }

            var currentCumulative = 0L
            val currentList = (1..maxDays).map { day ->
                if (day <= daysInCurrentMonth && day <= today.day) {
                    currentCumulative += currentDailyTotals[day] ?: 0L
                    currentCumulative.toFloat()
                } else null
            }

            var lastCumulative = 0L
            val lastList = (1..maxDays).map { day ->
                if (day <= daysInLastMonth) {
                    lastCumulative += lastDailyTotals[day] ?: 0L
                }
                lastCumulative.toFloat()
            }

            val maxAmount = maxOf(currentCumulative, lastCumulative).coerceAtLeast(1L).toFloat()

            HomeUiState(
                budgetAmount = budgetAmount,
                todayAmount = todayAmount,
                currentAmount = Amount(
                    expensesAmount = currentExpenses.sumOf { it.history.amount },
                    incomeAmount = currentIncome.sumOf { it.history.amount },
                    savingAmount = currentSaving.sumOf { it.history.amount }
                ),
                lastAmount = Amount(
                    expensesAmount = lastExpenses.sumOf { it.history.amount },
                    incomeAmount = lastIncome.sumOf { it.history.amount },
                    savingAmount = lastSaving.sumOf { it.history.amount }
                ),
                categorySegment = categorySegments,
                paymentMethodSegment = paymentMethodSegments,
                currentExpensesData = currentList.map { it?.div(maxAmount) },
                lastExpensesData = lastList.map { it / maxAmount }
            )
        }.apiFlow { state ->
            reduceState { state }
        }
    }
}
