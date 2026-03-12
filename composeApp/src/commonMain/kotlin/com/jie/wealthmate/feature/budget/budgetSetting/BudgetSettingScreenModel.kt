@file:OptIn(ExperimentalCoroutinesApi::class)

package com.jie.wealthmate.feature.budget.budgetSetting

import com.jie.wealthmate.base.BaseScreenModel
import com.jie.wealthmate.feature.menu.management.categoryManagement.component.LargeCategoryEnum
import com.jie.wealthmate.repository.BudgetRepository
import com.jie.wealthmate.repository.HistoryRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

class BudgetSettingScreenModel(
    private val budgetRepository: BudgetRepository,
    private val historyRepository: HistoryRepository,
) : BaseScreenModel<BudgetSettingUiState>() {

    override val initialState: BudgetSettingUiState = BudgetSettingUiState()

    private val selectedYearFlow = MutableStateFlow("")

    init {
        observeYearList()
        observeBudgets()
    }

    private fun observeYearList() {
        budgetRepository.getAllYearMonths()
            .onEach { yearMonths ->
                val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
                val currentYearStr = now.year.toString()

                val years = (yearMonths.map { it.split("-")[0] } + currentYearStr)
                    .distinct()
                    .sortedDescending()

                reduceState { state ->
                    val nextYear = state.selectedYear.ifEmpty { currentYearStr }
                    if (selectedYearFlow.value.isEmpty()) {
                        selectedYearFlow.value = nextYear
                    }
                    state.copy(
                        yearList = years,
                        selectedYear = nextYear
                    )
                }
            }
            .launchIn(ioScope)
    }

    private fun observeBudgets() {
        selectedYearFlow
            .flatMapLatest { year ->
                if (year.isEmpty()) return@flatMapLatest flowOf(emptyList<MonthBudgetGroup>())

                budgetRepository.getBudgetsByYearWithDetails(year)
                    .flatMapLatest { allBudgets ->
                        val now =
                            Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
                        val currentYear = now.year
                        val currentMonth = now.month.number
                        val targetYear = year.toIntOrNull() ?: currentYear
                        val timeZone = TimeZone.currentSystemDefault()

                        val budgetMonths = allBudgets.mapNotNull {
                            it.budget.yearMonth.split("-").lastOrNull()?.toIntOrNull()
                        }.toSet()

                        val requiredMonths = when {
                            targetYear < currentYear -> (1..12).toSet()
                            targetYear == currentYear -> (1..currentMonth).toSet()
                            else -> emptySet()
                        }

                        val allTargetMonths = (budgetMonths + requiredMonths).sortedDescending()
                        if (allTargetMonths.isEmpty()) return@flatMapLatest flowOf(emptyList<MonthBudgetGroup>())

                        val budgetsByMonth = allBudgets.groupBy { it.budget.yearMonth }
                        val flows = allTargetMonths.map { m ->
                            val yearMonth = "$year-${m.toString().padStart(2, '0')}"
                            val budgets = budgetsByMonth[yearMonth] ?: emptyList()

                            val start = LocalDate(targetYear, Month(m), 1).atStartOfDayIn(timeZone)
                                .toEpochMilliseconds()
                            val end = if (m == 12) {
                                LocalDate(targetYear + 1, 1, 1).atStartOfDayIn(timeZone)
                                    .toEpochMilliseconds()
                            } else {
                                LocalDate(targetYear, Month(m + 1), 1).atStartOfDayIn(timeZone)
                                    .toEpochMilliseconds()
                            }

                            combine(
                                historyRepository.getSumByMonth(
                                    start,
                                    end,
                                    LargeCategoryEnum.INCOME.name
                                ),
                                historyRepository.getSumByMonth(
                                    start,
                                    end,
                                    LargeCategoryEnum.SAVING.name
                                ),
                                historyRepository.getSumByMonth(
                                    start,
                                    end,
                                    LargeCategoryEnum.EXPENSES.name
                                )
                            ) { income, saving, expense ->
                                MonthBudgetGroup(yearMonth, budgets, income, saving, expense)
                            }
                        }

                        combine(flows) { it.toList() }
                    }
            }
            .onEach { grouped ->
                reduceState {
                    it.copy(
                        monthBudgets = grouped,
                        yearlySummary = calculateYearlySummary(grouped)
                    )
                }
            }
            .launchIn(ioScope)
    }

    private fun calculateYearlySummary(monthBudgets: List<MonthBudgetGroup>): YearlySummary {
        var income = 0L to 0L // actual to target
        var saving = 0L to 0L
        var expense = 0L to 0L

        monthBudgets.forEach { group ->
            income = (income.first + group.actualIncome) to income.second
            saving = (saving.first + group.actualSaving) to saving.second
            expense = (expense.first + group.actualExpense) to expense.second

            group.budgets.forEach { item ->
                val amount = item.budget.amount
                when (item.category?.largeCategory) {
                    LargeCategoryEnum.INCOME.name -> income =
                        income.first to (income.second + amount)

                    LargeCategoryEnum.SAVING.name -> saving =
                        saving.first to (saving.second + amount)

                    LargeCategoryEnum.EXPENSES.name -> expense =
                        expense.first to (expense.second + amount)
                }
            }
        }

        return YearlySummary(
            actualIncome = income.first, targetIncome = income.second,
            actualSaving = saving.first, targetSaving = saving.second,
            actualExpense = expense.first, targetExpense = expense.second
        )
    }

    fun onYearSelected(year: String) {
        selectedYearFlow.value = year
        reduceState { it.copy(selectedYear = year) }
    }

    fun deleteBudget(yearMonth: String) {
        launchSafe(
            block = { budgetRepository.deleteBudgetsByMonth(yearMonth) },
            onSuccess = { showSnackbar("삭제되었습니다.") }
        )
    }
}
