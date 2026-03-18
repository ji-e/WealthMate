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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.minus
import kotlinx.datetime.number
import kotlinx.datetime.plus

class HomeScreenModel(
    private val historyRepository: HistoryRepository,
    private val budgetRepository: BudgetRepository,
) : BaseScreenModel<HomeUiState>() {

    override val initialState: HomeUiState
        get() = HomeUiState()

    private val statusTypeFlow = MutableStateFlow(StatusType.MONTH)

    init {
        observeHomeData()
    }

    fun updateStatusType(statusType: StatusType) {
        statusTypeFlow.value = statusType
        reduceState { state ->
            state.copy(statusType = statusType)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeHomeData() {
        // "오늘" 섹션을 위한 이번 달 데이터 고정 추출
        val thisMonthStart = today.firstDayOfMonth().toEpochMilliseconds()
        val thisMonthEnd = today.lastDayOfMonth().toEpochMilliseconds() + 86_399_999L
        val yearMonth = today.convertLocalDateToString(formatDateHyphenYM)

        statusTypeFlow.flatMapLatest { statusType ->
            val (currentStart, currentEnd, lastStart, lastEnd) = getPeriods(statusType)

            combine(
                budgetRepository.getBudgetsByMonthWithDetails(yearMonth),
                historyRepository.getHistoriesByMonth(thisMonthStart, thisMonthEnd), // Today용
                historyRepository.getHistoriesByMonth(currentStart, currentEnd),    // 선택 기간용
                historyRepository.getHistoriesByMonth(lastStart, lastEnd)          // 이전 기간용
            ) { budgets, thisMonthHistories, currentHistories, lastHistories ->

                // 1. Today 섹션 데이터 (무조건 이번 달 기준)
                val thisMonthExpenses = thisMonthHistories
                    .filter { it.history.largeCategory == LargeCategoryEnum.EXPENSES.name }

                val thisMonthBudgetAmount = budgets
                    .filter { it.category?.largeCategory == LargeCategoryEnum.EXPENSES.name }
                    .sumOf { it.budget.amount }

                val todayStart = today.toEpochMilliseconds()
                val todayEnd = todayStart + 86_399_999L
                val todayAmount = thisMonthExpenses
                    .filter { it.history.date in todayStart..todayEnd }
                    .sumOf { it.history.amount }

                val thisMonthExpensesAmount = thisMonthExpenses.sumOf { it.history.amount }

                // 2. 선택된 기간(statusType) 데이터
                val currentGrouped = currentHistories.groupBy { it.history.largeCategory }
                val lastGrouped = lastHistories.groupBy { it.history.largeCategory }

                val currentExpenses = currentGrouped[LargeCategoryEnum.EXPENSES.name] ?: emptyList()
                val currentIncome = currentGrouped[LargeCategoryEnum.INCOME.name] ?: emptyList()
                val currentSaving = currentGrouped[LargeCategoryEnum.SAVING.name] ?: emptyList()

                val lastExpenses = lastGrouped[LargeCategoryEnum.EXPENSES.name] ?: emptyList()
                val lastIncome = lastGrouped[LargeCategoryEnum.INCOME.name] ?: emptyList()
                val lastSaving = lastGrouped[LargeCategoryEnum.SAVING.name] ?: emptyList()

                // 예산 (년 단위일 경우 연간 예산, 그 외 월간 예산)
                // (이 부분은 요구사항에 따라 조절 가능하나 현재는 statusType에 따른 예산 표시)
                val budgetAmount = if (statusType == StatusType.YEAR) {
                    // 년간 예산은 별도 로직이 필요할 수 있으나 일단 월간 기준으로 유지 또는 확장
                    thisMonthBudgetAmount
                } else {
                    thisMonthBudgetAmount
                }

                // 차트 데이터 (Category / Payment Method) - currentExpenses(선택기간) 기준
                val categorySegments = currentExpenses
                    .groupBy { it.category?.id }
                    .map { (_, histories) ->
                        CategorySegmentChartData(
                            category = histories.first().category.mapperToVo(LargeCategoryEnum.EXPENSES),
                            amount = histories.sumOf { it.history.amount }
                        )
                    }.sortedByDescending { it.amount }

                val paymentMethodSegments = currentExpenses
                    .groupBy { it.paymentMethod?.id }
                    .map { (_, histories) ->
                        PaymentMethodSegmentChartData(
                            paymentMethod = histories.first().paymentMethod?.mapperToVo(),
                            amount = histories.sumOf { it.history.amount }
                        )
                    }.sortedByDescending { it.amount }

                // 꺽은선 그래프 데이터 계산
                val currentList: List<Float?>
                val lastList: List<Float>

                when (statusType) {
                    StatusType.WEEK -> {
                        val currentDaily =
                            currentExpenses.groupBy { it.history.date.toLocalDate().dayOfWeek.isoDayNumber }
                        val lastDaily =
                            lastExpenses.groupBy { it.history.date.toLocalDate().dayOfWeek.isoDayNumber }
                        var currentCumulative = 0L
                        currentList = (1..7).map { day ->
                            if (day <= today.dayOfWeek.isoDayNumber) {
                                currentCumulative += currentDaily[day]?.sumOf { it.history.amount }
                                    ?: 0L
                                currentCumulative.toFloat()
                            } else null
                        }
                        var lastCumulative = 0L
                        lastList = (1..7).map { day ->
                            lastCumulative += lastDaily[day]?.sumOf { it.history.amount } ?: 0L
                            lastCumulative.toFloat()
                        }
                    }

                    StatusType.MONTH -> {
                        val daysInCurrentMonth = today.lastDayOfMonth().day
                        val daysInLastMonth =
                            today.minus(1, DateTimeUnit.MONTH).lastDayOfMonth().day
                        val maxDays = maxOf(daysInCurrentMonth, daysInLastMonth)
                        val currentDaily =
                            currentExpenses.groupBy { it.history.date.toLocalDate().day }
                        val lastDaily = lastExpenses.groupBy { it.history.date.toLocalDate().day }
                        var currentCumulative = 0L
                        currentList = (1..maxDays).map { day ->
                            if (day <= daysInCurrentMonth && day <= today.day) {
                                currentCumulative += currentDaily[day]?.sumOf { it.history.amount }
                                    ?: 0L
                                currentCumulative.toFloat()
                            } else null
                        }
                        var lastCumulative = 0L
                        lastList = (1..maxDays).map { day ->
                            if (day <= daysInLastMonth) {
                                lastCumulative += lastDaily[day]?.sumOf { it.history.amount } ?: 0L
                            }
                            lastCumulative.toFloat()
                        }
                    }

                    StatusType.YEAR -> {
                        val currentMonthly =
                            currentExpenses.groupBy { it.history.date.toLocalDate().month.number }
                        val lastMonthly =
                            lastExpenses.groupBy { it.history.date.toLocalDate().month.number }
                        var currentCumulative = 0L
                        currentList = (1..12).map { month ->
                            if (month <= today.month.number) {
                                currentCumulative += currentMonthly[month]?.sumOf { it.history.amount }
                                    ?: 0L
                                currentCumulative.toFloat()
                            } else null
                        }
                        var lastCumulative = 0L
                        lastList = (1..12).map { month ->
                            lastCumulative += lastMonthly[month]?.sumOf { it.history.amount } ?: 0L
                            lastCumulative.toFloat()
                        }
                    }
                }

                val maxAmount = maxOf(
                    currentList.filterNotNull().lastOrNull() ?: 0f,
                    lastList.lastOrNull() ?: 0f
                ).coerceAtLeast(1f)

                HomeUiState(
                    statusType = statusType,
                    todayAmount = todayAmount,
                    thisMonthBudgetAmount = thisMonthBudgetAmount,
                    thisMonthExpensesAmount = thisMonthExpensesAmount,
                    budgetAmount = budgetAmount,
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
            }
        }.apiFlow { state ->
            reduceState { state }
        }
    }

    private fun getPeriods(statusType: StatusType): Periods {
        return when (statusType) {
            StatusType.WEEK -> {
                val currentStart = today.minus(today.dayOfWeek.isoDayNumber - 1, DateTimeUnit.DAY)
                val currentEnd = currentStart.plus(6, DateTimeUnit.DAY)
                val lastStart = currentStart.minus(7, DateTimeUnit.DAY)
                val lastEnd = lastStart.plus(6, DateTimeUnit.DAY)
                Periods(
                    currentStart.toEpochMilliseconds(),
                    currentEnd.toEpochMilliseconds() + 86_399_999L,
                    lastStart.toEpochMilliseconds(),
                    lastEnd.toEpochMilliseconds() + 86_399_999L
                )
            }

            StatusType.MONTH -> {
                val currentStart = today.firstDayOfMonth()
                val currentEnd = today.lastDayOfMonth()
                val lastMonth = today.minus(1, DateTimeUnit.MONTH)
                val lastStart = lastMonth.firstDayOfMonth()
                val lastEnd = lastMonth.lastDayOfMonth()
                Periods(
                    currentStart.toEpochMilliseconds(),
                    currentEnd.toEpochMilliseconds() + 86_399_999L,
                    lastStart.toEpochMilliseconds(),
                    lastEnd.toEpochMilliseconds() + 86_399_999L
                )
            }

            StatusType.YEAR -> {
                val currentStart = LocalDate(today.year, 1, 1)
                val currentEnd = LocalDate(today.year, 12, 31)
                val lastStart = LocalDate(today.year - 1, 1, 1)
                val lastEnd = LocalDate(today.year - 1, 12, 31)
                Periods(
                    currentStart.toEpochMilliseconds(),
                    currentEnd.toEpochMilliseconds() + 86_399_999L,
                    lastStart.toEpochMilliseconds(),
                    lastEnd.toEpochMilliseconds() + 86_399_999L
                )
            }
        }
    }

    data class Periods(
        val currentStart: Long,
        val currentEnd: Long,
        val lastStart: Long,
        val lastEnd: Long,
    )
}
