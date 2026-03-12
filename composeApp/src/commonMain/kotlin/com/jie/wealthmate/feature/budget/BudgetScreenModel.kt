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
                    // 예산 상단 헤더용: 지출 합계만 포함 (수입, 저축 제외)
                    val totalBudget = budgetsWithDetails
                        .filter {
                            it.category?.largeCategory == LargeCategoryEnum.EXPENSES.name
                        }
                        .sumOf { it.budget.amount }

                    val expenses = historiesWithDetails
                        .filter {
                            it.history.largeCategory == LargeCategoryEnum.EXPENSES.name
                        }

                    val totalUsed = expenses.sumOf { it.history.amount }

                    val topExpenses = expenses
                        .filter { it.category?.isFixed != true }
                        .sortedByDescending { it.history.amount }
                        .take(3)
                        .map { it.mapperToVo() }
                        .toImmutableList()

                    // 카테고리별 예산 초과 아이템 계산 (지출 카테고리만 대상)
                    val overItems = budgetsWithDetails
                        .filter { it.category?.largeCategory == LargeCategoryEnum.EXPENSES.name }
                        .mapNotNull { budgetWithDetail ->
                            val categorySpent = historiesWithDetails
                                .filter { it.history.categoryId == budgetWithDetail.budget.categoryId }
                                .sumOf { it.history.amount }

                            if (categorySpent > budgetWithDetail.budget.amount) {
                                val categoryHistories = historiesWithDetails
                                    .filter { it.history.categoryId == budgetWithDetail.budget.categoryId }

                                val topExpense = categoryHistories.maxByOrNull { it.history.amount }

                                BudgetOverUsageVo(
                                    category = budgetWithDetail.category.mapperToVo(),
                                    spentAmount = categorySpent,
                                    budgetAmount = budgetWithDetail.budget.amount,
                                    overAmount = categorySpent - budgetWithDetail.budget.amount,
                                    transactionCount = categoryHistories.size,
                                    topExpenseTitle = topExpense?.history?.content,
                                    topExpenseAmount = topExpense?.history?.amount
                                )
                            } else null
                        }.toImmutableList()

                    // 요약 정보 계산 (수입, 지출, 저축 각각 표시)
                    val summaryItems = LargeCategoryEnum.entries
                        .map { largeCategory ->
                            val categoryBudget = budgetsWithDetails
                                .filter { it.category?.largeCategory == largeCategory.name }
                                .sumOf { it.budget.amount }

                            val categorySpent = historiesWithDetails
                                .filter { it.history.largeCategory == largeCategory.name }
                                .sumOf { it.history.amount }

                            BudgetSummaryVo(
                                largeCategory = largeCategory,
                                icon = when (largeCategory) {
                                    LargeCategoryEnum.INCOME -> "💰"
                                    LargeCategoryEnum.EXPENSES -> "💸"
                                    LargeCategoryEnum.SAVING -> "🏦"
                                },
                                budgetAmount = categoryBudget,
                                currentAmount = categorySpent
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
