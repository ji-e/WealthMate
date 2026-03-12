package com.jie.wealthmate.feature.budget.budgetDetail

import cafe.adriel.voyager.core.model.screenModelScope
import com.jie.wealthmate.base.BaseScreenModel
import com.jie.wealthmate.feature.menu.management.categoryManagement.component.LargeCategoryEnum
import com.jie.wealthmate.repository.BudgetRepository
import com.jie.wealthmate.repository.HistoryRepository
import com.jie.wealthmate.utils.convertLocalDateToString
import com.jie.wealthmate.utils.firstDayOfMonth
import com.jie.wealthmate.utils.formatDateHyphenYM
import com.jie.wealthmate.utils.lastDayOfMonth
import com.jie.wealthmate.utils.toEpochMilliseconds
import com.jie.wealthmate.vo.CategoryVo.Companion.mapperToVo
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.datetime.LocalDate

class BudgetDetailScreenModel(
    private val budgetRepository: BudgetRepository,
    private val historyRepository: HistoryRepository,
    private val initialMonth: LocalDate,
) : BaseScreenModel<BudgetDetailUiState>() {
    override val initialState: BudgetDetailUiState
        get() = BudgetDetailUiState(selectedMonth = initialMonth)

    init {
        observeData()
    }

    private fun observeData() {
        val monthStr = initialMonth.convertLocalDateToString(formatDateHyphenYM)
        val start = initialMonth.firstDayOfMonth().toEpochMilliseconds()
        val end = initialMonth.lastDayOfMonth().toEpochMilliseconds()

        combine(
            budgetRepository.getBudgetsByMonthWithDetails(monthStr),
            historyRepository.getHistoriesByMonth(start, end)
        ) { budgets, histories ->
            val historiesByLargeCategory = histories.groupBy { it.history.largeCategory }
            val historiesByCategoryId = histories.groupBy { it.history.categoryId }
            val budgetsByLargeCategory = budgets.groupBy { it.category?.largeCategory }

            val sections = LargeCategoryEnum.entries.map { largeCategory ->
                val categoryName = largeCategory.name
                val filteredBudgets = budgetsByLargeCategory[categoryName] ?: emptyList()
                
                val categoryBudgets = filteredBudgets.map { budgetWithDetail ->
                    val usedAmount = historiesByCategoryId[budgetWithDetail.budget.categoryId]?.sumOf { it.history.amount } ?: 0L
                    val percentage = if (budgetWithDetail.budget.amount > 0) {
                        (usedAmount.toDouble() / budgetWithDetail.budget.amount * 100).toInt()
                    } else 0
                    
                    CategoryBudgetVo(
                        category = budgetWithDetail.category.mapperToVo(largeCategory),
                        budgetAmount = budgetWithDetail.budget.amount,
                        usedAmount = usedAmount,
                        percentage = percentage
                    )
                }.sortedByDescending { it.usedAmount }.toImmutableList()

                BudgetSectionVo(
                    largeCategory = largeCategory,
                    totalBudget = filteredBudgets.sumOf { it.budget.amount },
                    totalUsed = (historiesByLargeCategory[categoryName] ?: emptyList()).sumOf { it.history.amount },
                    items = categoryBudgets
                )
            }.toImmutableList()

            reduceState { state ->
                state.copy(
                    totalIncome = sections.find { it.largeCategory == LargeCategoryEnum.INCOME }?.totalUsed ?: 0L,
                    totalExpense = sections.find { it.largeCategory == LargeCategoryEnum.EXPENSES }?.totalUsed ?: 0L,
                    totalSaving = sections.find { it.largeCategory == LargeCategoryEnum.SAVING }?.totalUsed ?: 0L,
                    sections = sections
                )
            }
        }.launchIn(screenModelScope)
    }
}
