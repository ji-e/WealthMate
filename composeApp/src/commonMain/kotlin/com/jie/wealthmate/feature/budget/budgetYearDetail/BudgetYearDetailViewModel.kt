@file:OptIn(ExperimentalCoroutinesApi::class, ExperimentalTime::class)

package com.jie.wealthmate.feature.budget.budgetYearDetail

import androidx.lifecycle.viewModelScope
import com.jie.wealthmate.base.BaseViewModel
import com.jie.wealthmate.feature.menu.management.categoryManagement.component.LargeCategoryEnum
import com.jie.wealthmate.repository.BudgetRepository
import com.jie.wealthmate.repository.HistoryRepository
import com.jie.wealthmate.repository.RepeatCycleRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

class BudgetYearDetailViewModel(
    private val selectedYear: String,
    private val budgetRepository: BudgetRepository,
    private val historyRepository: HistoryRepository,
    private val repeatCycleRepository: RepeatCycleRepository,
) : BaseViewModel<BudgetYearDetailUiState>() {

    override val initialState: BudgetYearDetailUiState =
        BudgetYearDetailUiState(selectedYear = selectedYear)

    init {
        loadYearlyData()
    }

    private fun loadYearlyData() {
        val year = selectedYear.toIntOrNull() ?: return
        val tz = TimeZone.currentSystemDefault()

        // 기간 설정 최적화 (LocalDate 계산 방식 개선)
        val startOfYear = LocalDate(year, 1, 1).atStartOfDayIn(tz).toEpochMilliseconds()
        val endOfYear = LocalDate(year + 1, 1, 1).atStartOfDayIn(tz).toEpochMilliseconds() - 1
        val startOfLastYear = LocalDate(year - 1, 1, 1).atStartOfDayIn(tz).toEpochMilliseconds()
        val endOfLastYear = LocalDate(year, 1, 1).atStartOfDayIn(tz).toEpochMilliseconds() - 1

        combine(
            budgetRepository.getBudgetsByYearWithDetails(selectedYear),
            historyRepository.getHistoriesByMonth(startOfYear, endOfYear),
            historyRepository.getSumByMonth(startOfLastYear, endOfLastYear, LargeCategoryEnum.INCOME.name),
            historyRepository.getSumByMonth(startOfLastYear, endOfLastYear, LargeCategoryEnum.EXPENSES.name),
            historyRepository.getSumByMonth(startOfLastYear, endOfLastYear, LargeCategoryEnum.SAVING.name)
        ) { budgets, yearlyHistories, lastIncome, lastExpense, lastSaving ->

            // 1. 예산 집계 (단일 순회)
            var totalBudgetIncome = 0L
            var totalBudgetExpense = 0L
            var totalBudgetSaving = 0L
            budgets.forEach { item ->
                when (item.category?.largeCategory) {
                    LargeCategoryEnum.INCOME.name -> totalBudgetIncome += item.budget.amount
                    LargeCategoryEnum.EXPENSES.name -> totalBudgetExpense += item.budget.amount
                    LargeCategoryEnum.SAVING.name -> totalBudgetSaving += item.budget.amount
                }
            }

            // 2. 실제 내역 처리 (단일 순회로 모든 데이터 집계: O(N))
            var totalActualIncome = 0L
            var totalActualExpense = 0L
            var totalActualSaving = 0L
            var totalVariableExpense = 0L

            val monthlySummary = Array(12) { MonthlyAcc() }
            val variableMap = mutableMapOf<String, CategoryAcc>()
            val fixedMap = mutableMapOf<String, CategoryAcc>()

            yearlyHistories.forEach { h ->
                val month = Instant.fromEpochMilliseconds(h.history.date).toLocalDateTime(tz).month.number
                val amount = h.history.amount
                val largeCategory = h.history.largeCategory
                val isFixed = h.history.repeatCycleId != null || h.history.installmentId != null
                val categoryLabel = h.category?.middleLabel ?: "카테고리 없음"
                val icon = h.category?.icon ?: "❓"
                val categoryId = h.history.categoryId ?: "unknown"

                val acc = monthlySummary[month - 1]
                when (largeCategory) {
                    LargeCategoryEnum.INCOME.name -> {
                        acc.income += amount
                        totalActualIncome += amount
                    }
                    LargeCategoryEnum.SAVING.name -> {
                        acc.saving += amount
                        totalActualSaving += amount
                    }
                    LargeCategoryEnum.EXPENSES.name -> {
                        totalActualExpense += amount
                        if (isFixed) {
                            acc.fixed += amount
                            val fixedAcc = fixedMap.getOrPut(categoryId) { CategoryAcc(categoryLabel, icon) }
                            fixedAcc.amount += amount
                        } else {
                            acc.variable += amount
                            totalVariableExpense += amount
                            val varAcc = variableMap.getOrPut(categoryId) { CategoryAcc(categoryLabel, icon) }
                            varAcc.amount += amount
                        }
                    }
                }
            }

            // 3. UI 모델 변환
            val monthlyData = monthlySummary.mapIndexed { index, acc ->
                MonthlyComparison(
                    month = index + 1,
                    income = acc.income,
                    expense = acc.variable,
                    fixedExpense = acc.fixed,
                    saving = acc.saving
                )
            }

            val topCategories = variableMap.values.map { acc ->
                CategoryExpense(
                    name = acc.name,
                    amount = acc.amount,
                    icon = acc.icon,
                    ratio = if (totalVariableExpense > 0) acc.amount.toFloat() / totalVariableExpense else 0f
                )
            }.sortedByDescending { it.amount }.take(5)

            val fixedExpenses = fixedMap.values.map { acc ->
                FixedExpense(
                    name = acc.name,
                    amount = acc.amount,
                    icon = acc.icon
                )
            }.sortedByDescending { it.amount }

            BudgetYearResult(
                totalBudgetIncome, totalBudgetExpense, totalBudgetSaving,
                totalActualIncome, totalActualExpense, totalActualSaving, totalVariableExpense,
                lastIncome, lastExpense, lastSaving,
                monthlyData, topCategories, fixedExpenses
            )
        }.onEach { result ->
            reduceState { state ->
                state.copy(
                    totalBudgetIncome = result.totalBudgetIncome,
                    totalBudgetExpense = result.totalBudgetExpense,
                    totalBudgetSaving = result.totalBudgetSaving,
                    totalActualIncome = result.totalActualIncome,
                    totalActualExpense = result.totalActualExpense,
                    totalActualSaving = result.totalActualSaving,
                    totalVariableExpense = result.totalVariableExpense,
                    lastActualIncome = result.lastActualIncome,
                    lastActualExpense = result.lastActualExpense,
                    lastActualSaving = result.lastActualSaving,
                    monthlyData = result.monthlyData,
                    topExpenseCategories = result.topExpenseCategories,
                    fixedExpenses = result.fixedExpenses
                )
            }
        }.launchIn(viewModelScope)
    }

    private data class BudgetYearResult(
        val totalBudgetIncome: Long,
        val totalBudgetExpense: Long,
        val totalBudgetSaving: Long,
        val totalActualIncome: Long,
        val totalActualExpense: Long,
        val totalActualSaving: Long,
        val totalVariableExpense: Long,
        val lastActualIncome: Long,
        val lastActualExpense: Long,
        val lastActualSaving: Long,
        val monthlyData: List<MonthlyComparison>,
        val topExpenseCategories: List<CategoryExpense>,
        val fixedExpenses: List<FixedExpense>
    )

    private class MonthlyAcc {
        var income = 0L
        var saving = 0L
        var fixed = 0L
        var variable = 0L
    }

    private class CategoryAcc(val name: String, val icon: String) {
        var amount = 0L
    }
}
