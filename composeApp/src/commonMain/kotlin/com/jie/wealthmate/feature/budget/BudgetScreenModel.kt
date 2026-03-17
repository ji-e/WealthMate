package com.jie.wealthmate.feature.budget

import cafe.adriel.voyager.core.model.screenModelScope
import com.jie.wealthmate.base.BaseScreenModel
import com.jie.wealthmate.feature.budget.component.BudgetOverUsageVo
import com.jie.wealthmate.feature.budget.component.BudgetSummaryVo
import com.jie.wealthmate.feature.menu.management.categoryManagement.component.LargeCategoryEnum
import com.jie.wealthmate.repository.BudgetRepository
import com.jie.wealthmate.repository.HistoryRepository
import com.jie.wealthmate.utils.convertLocalDateToString
import com.jie.wealthmate.utils.firstDayOfMonth
import com.jie.wealthmate.utils.formatDateHyphenYM
import com.jie.wealthmate.utils.lastDayOfMonth
import com.jie.wealthmate.utils.toEpochMilliseconds
import com.jie.wealthmate.utils.today
import com.jie.wealthmate.vo.CategoryVo.Companion.mapperToVo
import com.jie.wealthmate.vo.HistoryVo
import com.jie.wealthmate.vo.HistoryVo.Companion.mapperToVo
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.datetime.LocalDate

class BudgetScreenModel(
    private val budgetRepository: BudgetRepository,
    private val historyRepository: HistoryRepository,
) : BaseScreenModel<BudgetUiState>() {
    override val initialState: BudgetUiState
        get() = BudgetUiState()

    init {
        observeBudgetData()
    }

    private fun observeBudgetData() {
        container.uiState
            .map { it.selectedMonth }
            .distinctUntilChanged()
            .flatMapLatest { selectedMonth ->
                val monthStr = selectedMonth.convertLocalDateToString(formatDateHyphenYM)
                val start = selectedMonth.firstDayOfMonth().toEpochMilliseconds()
                val end = selectedMonth.lastDayOfMonth().toEpochMilliseconds()

                combine(
                    budgetRepository.getBudgetsByMonthWithDetails(monthStr),
                    historyRepository.getHistoriesByMonth(start, end)
                ) { budgetsWithDetails, historiesWithDetails ->
                    // 1. 데이터 그룹화하여 중복 순회 최소화
                    val historiesByCategoryId = historiesWithDetails.groupBy { it.history.categoryId }
                    val historiesByLargeCategory = historiesWithDetails.groupBy { it.history.largeCategory }
                    val budgetsByLargeCategory = budgetsWithDetails.groupBy { it.category?.largeCategory }

                    // 2. 지출 관련 데이터 추출
                    val expenseKey = LargeCategoryEnum.EXPENSES.name
                    val expenseHistories = historiesByLargeCategory[expenseKey] ?: emptyList()
                    val expenseBudgets = budgetsByLargeCategory[expenseKey] ?: emptyList()

                    val totalBudget = expenseBudgets.sumOf { it.budget.amount }
                    val totalUsed = expenseHistories.sumOf { it.history.amount }

                    // 3. 상위 지출 항목 (고정 지출 제외)
                    val topExpenses = expenseHistories
                        .filter { it.category?.isFixed != true }
                        .sortedByDescending { it.history.amount }
                        .take(3)
                        .map { it.mapperToVo() }
                        .toImmutableList()

                    // 4. 카테고리별 예산 초과 아이템 계산
                    val overItems = expenseBudgets
                        .groupBy { it.budget.categoryId }
                        .mapNotNull { (categoryId, budgets) ->
                            val totalCategoryBudget = budgets.sumOf { it.budget.amount }
                            val categoryHistories = historiesByCategoryId[categoryId] ?: emptyList()
                            val categorySpent = categoryHistories.sumOf { it.history.amount }

                            if (categorySpent > totalCategoryBudget) {
                                val firstBudgetWithDetail = budgets.first()
                                val topExpense = categoryHistories.maxByOrNull { it.history.amount }

                                BudgetOverUsageVo(
                                    category = firstBudgetWithDetail.category.mapperToVo(),
                                    spentAmount = categorySpent,
                                    budgetAmount = totalCategoryBudget,
                                    overAmount = categorySpent - totalCategoryBudget,
                                    transactionCount = categoryHistories.size,
                                    topExpenseTitle = topExpense?.history?.content,
                                    topExpenseAmount = topExpense?.history?.amount
                                )
                            } else null
                        }.toImmutableList()

                    // 5. 대분류별 요약 정보 정보 계산
                    val summaryItems = LargeCategoryEnum.entries
                        .map { largeCategory ->
                            val categoryName = largeCategory.name
                            BudgetSummaryVo(
                                largeCategory = largeCategory,
                                icon = when (largeCategory) {
                                    LargeCategoryEnum.INCOME -> "💰"
                                    LargeCategoryEnum.EXPENSES -> "💸"
                                    LargeCategoryEnum.SAVING -> "🏦"
                                },
                                budgetAmount = budgetsByLargeCategory[categoryName]?.sumOf { it.budget.amount } ?: 0L,
                                currentAmount = historiesByLargeCategory[categoryName]?.sumOf { it.history.amount } ?: 0L
                            )
                        }.toImmutableList()

                    BudgetResult(totalBudget, totalUsed, overItems, summaryItems, topExpenses)
                }
            }.onEach { result ->
                reduceState { state ->
                    state.copy(
                        totalBudgetAmount = result.totalBudget,
                        usedAmount = result.totalUsed,
                        budgetOverItems = result.overItems,
                        budgetSummaryItems = result.summaryItems,
                        topExpenses = result.topExpenses
                    )
                }
            }.launchIn(screenModelScope)
    }

    private data class BudgetResult(
        val totalBudget: Long,
        val totalUsed: Long,
        val overItems: ImmutableList<BudgetOverUsageVo>,
        val summaryItems: ImmutableList<BudgetSummaryVo>,
        val topExpenses: ImmutableList<HistoryVo>
    )

    fun updateSelectedMonth(month: LocalDate = today) {
        reduceState { state ->
            if (state.selectedMonth == month) return@reduceState state
            state.copy(selectedMonth = month)
        }
    }
}
