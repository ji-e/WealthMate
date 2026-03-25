package com.jie.wealthmate.feature.budget.budgetDetail

import androidx.lifecycle.viewModelScope
import com.jie.wealthmate.base.BaseViewModel
import com.jie.wealthmate.feature.menu.management.categoryManagement.component.LargeCategoryEnum
import com.jie.wealthmate.repository.BudgetRepository
import com.jie.wealthmate.repository.CategoryRepository
import com.jie.wealthmate.repository.HistoryRepository
import com.jie.wealthmate.utils.convertLocalDateToString
import com.jie.wealthmate.utils.firstDayOfMonth
import com.jie.wealthmate.utils.formatDateHyphenYM
import com.jie.wealthmate.utils.lastDayOfMonth
import com.jie.wealthmate.utils.toEpochMilliseconds
import com.jie.wealthmate.vo.CategoryTagVo
import com.jie.wealthmate.vo.CategoryVo
import com.jie.wealthmate.vo.CategoryVo.Companion.mapperToVo
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toPersistentMap
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus

class BudgetDetailViewModel(
    private val budgetRepository: BudgetRepository,
    private val historyRepository: HistoryRepository,
    private val categoryRepository: CategoryRepository,
    private val initialMonth: LocalDate,
) : BaseViewModel<BudgetDetailUiState>() {
    override val initialState: BudgetDetailUiState
        get() = BudgetDetailUiState(selectedMonth = initialMonth)

    init {
        observeData()
    }

    fun toggleSection(largeCategory: LargeCategoryEnum) {
        reduceState { state ->
            val current = state.expandedStates[largeCategory] ?: false
            state.copy(
                expandedStates = state.expandedStates.toMutableMap().apply {
                    put(largeCategory, !current)
                }.toPersistentMap()
            )
        }
    }

    private fun observeData() {
        val monthStr = initialMonth.convertLocalDateToString(formatDateHyphenYM)
        val start = initialMonth.firstDayOfMonth().toEpochMilliseconds()
        val end = initialMonth.lastDayOfMonth().toEpochMilliseconds()

        val lastMonth = initialMonth.minus(1, DateTimeUnit.MONTH)
        val lastStart = lastMonth.firstDayOfMonth().toEpochMilliseconds()
        val lastEnd = lastMonth.lastDayOfMonth().toEpochMilliseconds()

        val lastMonthSumsFlow = combine(
            historyRepository.getSumByMonth(lastStart, lastEnd, LargeCategoryEnum.INCOME.name),
            historyRepository.getSumByMonth(lastStart, lastEnd, LargeCategoryEnum.EXPENSES.name),
            historyRepository.getSumByMonth(lastStart, lastEnd, LargeCategoryEnum.SAVING.name)
        ) { income, expense, saving -> Triple(income, expense, saving) }

        combine(
            budgetRepository.getBudgetsByMonthWithDetails(monthStr),
            historyRepository.getHistoriesByMonth(start, end),
            categoryRepository.getAllCategories(),
            lastMonthSumsFlow
        ) { budgets, histories, allCategories, lastMonthSums ->
            val (lastIncome, lastExpense, lastSaving) = lastMonthSums
            val budgetsByLargeCategory = budgets.groupBy { it.category?.largeCategory }
            val categoriesByLargeCategory = allCategories.groupBy { it.largeCategory }

            val sections = LargeCategoryEnum.entries.map { largeCategory ->
                val categoryName = largeCategory.name
                val filteredBudgets = budgetsByLargeCategory[categoryName] ?: emptyList()
                val filteredHistories = histories.filter { it.history.largeCategory == categoryName }
                val filteredCategories = categoriesByLargeCategory[categoryName] ?: emptyList()

                val budgetsWithVo = filteredBudgets.map { it to it.category.mapperToVo(largeCategory) }
                val historiesWithVo = filteredHistories.map { it to it.category.mapperToVo(largeCategory) }
                val allCategoriesVo = filteredCategories.map { it.mapperToVo(largeCategory) }

                val allCategoryIds = (allCategoriesVo.map { it.id } + budgetsWithVo.map { it.second.id } + historiesWithVo.map { it.second.id }).distinct()

                val groups = allCategoryIds.map { categoryId ->
                    val budgetsInCategory = budgetsWithVo.filter { it.second.id == categoryId }
                    val historiesInCategory = historiesWithVo.filter { it.second.id == categoryId }

                    val categoryVo = allCategoriesVo.find { it.id == categoryId }
                        ?: budgetsInCategory.firstOrNull()?.second
                        ?: historiesInCategory.firstOrNull()?.second
                        ?: CategoryVo.unset(largeCategory)

                    val totalCategoryBudget = budgetsInCategory.sumOf { it.first.budget.amount }
                    val totalCategoryUsed = historiesInCategory.sumOf { it.first.history.amount }

                    val totalPercentage = if (totalCategoryBudget > 0) {
                        (totalCategoryUsed.toDouble() / totalCategoryBudget * 100).toInt()
                    } else if (totalCategoryUsed > 0) 100 else 0

                    val tagBudgets = (categoryVo.tags.map { tagVo ->
                        val budgetForTag = budgetsInCategory.find { it.first.budget.categoryTagId == tagVo.id }?.first?.budget?.amount ?: 0L
                        val usedAmountForTag = historiesInCategory
                            .filter { it.first.history.categoryTagId == tagVo.id }
                            .sumOf { it.first.history.amount }

                        val percentage = if (budgetForTag > 0) {
                            (usedAmountForTag.toDouble() / budgetForTag * 100).toInt()
                        } else if (usedAmountForTag > 0) 100 else 0

                        CategoryBudgetVo(
                            category = categoryVo,
                            tag = tagVo,
                            budgetAmount = budgetForTag,
                            usedAmount = usedAmountForTag,
                            percentage = percentage
                        )
                    } + run {
                        if (categoryVo.tags.isNotEmpty()) {
                            val budgetForNoTag = budgetsInCategory
                                .filter { b -> b.first.budget.categoryTagId.isNullOrEmpty() || categoryVo.tags.none { it.id == b.first.budget.categoryTagId } }
                                .sumOf { it.first.budget.amount }
                            val usedAmountForNoTag = historiesInCategory
                                .filter { h -> h.first.history.categoryTagId.isNullOrEmpty() || categoryVo.tags.none { it.id == h.first.history.categoryTagId } }
                                .sumOf { it.first.history.amount }

                            if (budgetForNoTag > 0 || usedAmountForNoTag > 0) {
                                val percentage = if (budgetForNoTag > 0) {
                                    (usedAmountForNoTag.toDouble() / budgetForNoTag * 100).toInt()
                                } else if (usedAmountForNoTag > 0) 100 else 0
                                listOf(
                                    CategoryBudgetVo(
                                        category = categoryVo,
                                        tag = CategoryTagVo(id = null, label = "태그 없음"),
                                        budgetAmount = budgetForNoTag,
                                        usedAmount = usedAmountForNoTag,
                                        percentage = percentage
                                    )
                                )
                            } else emptyList()
                        } else emptyList()
                    }).sortedByDescending { it.usedAmount }.toImmutableList()

                    CategoryBudgetGroupVo(
                        category = categoryVo,
                        totalBudget = totalCategoryBudget,
                        totalUsed = totalCategoryUsed,
                        percentage = totalPercentage,
                        tagBudgets = tagBudgets
                    )
                }.sortedBy { it.category.sort }.toImmutableList()

                BudgetSectionVo(
                    largeCategory = largeCategory,
                    totalBudget = filteredBudgets.sumOf { it.budget.amount },
                    totalUsed = filteredHistories.sumOf { it.history.amount },
                    groups = groups
                )
            }.toImmutableList()

            reduceState { state ->
                state.copy(
                    totalIncome = sections.find { it.largeCategory == LargeCategoryEnum.INCOME }?.totalUsed ?: 0L,
                    totalExpense = sections.find { it.largeCategory == LargeCategoryEnum.EXPENSES }?.totalUsed ?: 0L,
                    totalSaving = sections.find { it.largeCategory == LargeCategoryEnum.SAVING }?.totalUsed ?: 0L,
                    lastTotalIncome = lastIncome,
                    lastTotalExpense = lastExpense,
                    lastTotalSaving = lastSaving,
                    sections = sections
                )
            }
        }.launchIn(viewModelScope)
    }
}
