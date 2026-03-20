package com.jie.wealthmate.feature.home

import com.jie.wealthmate.base.BaseViewModel
import com.jie.wealthmate.base.DAY_END_MILLIS_OFFSET
import com.jie.wealthmate.base.DEFAULT_CATEGORY_ICON
import com.jie.wealthmate.base.DEFAULT_RECURRING_CONTENT
import com.jie.wealthmate.database.eneity.BudgetWithDetails
import com.jie.wealthmate.database.eneity.HistoryWithDetails
import com.jie.wealthmate.database.eneity.RepeatCycleWithDetails
import com.jie.wealthmate.feature.calendar.addHistory.component.RepeatCycleEnum
import com.jie.wealthmate.feature.calendar.addHistory.component.formattedShortDescription
import com.jie.wealthmate.feature.home.component.vo.AmountVo
import com.jie.wealthmate.feature.home.component.vo.CategorySegmentedChartVo
import com.jie.wealthmate.feature.home.component.vo.PaymentMethodSegmentedChartVo
import com.jie.wealthmate.feature.home.component.vo.PeriodsVo
import com.jie.wealthmate.feature.home.component.vo.RecurringHistoryVo
import com.jie.wealthmate.feature.home.component.vo.RecurringInfoVo
import com.jie.wealthmate.feature.menu.management.categoryManagement.component.LargeCategoryEnum
import com.jie.wealthmate.repository.BudgetRepository
import com.jie.wealthmate.repository.HistoryRepository
import com.jie.wealthmate.repository.RepeatCycleRepository
import com.jie.wealthmate.utils.convertLocalDateToString
import com.jie.wealthmate.utils.default
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

class HomeViewModel(
    private val historyRepository: HistoryRepository,
    private val budgetRepository: BudgetRepository,
    private val repeatCycleRepository: RepeatCycleRepository,
) : BaseViewModel<HomeUiState>() {

    override val initialState: HomeUiState = HomeUiState()

    // 현재 선택된 기간 타입 (주/월/년)
    private val statusTypeFlow = MutableStateFlow(StatusType.MONTH)

    init {
        observeHomeData()
    }

    /**
     * 기간 타입을 업데이트합니다. (UI에서 호출)
     */
    fun updateStatusType(statusType: StatusType) {
        statusTypeFlow.value = statusType
    }

    /**
     * 홈 화면에 필요한 데이터를 관찰하고 상태를 업데이트합니다.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeHomeData() {
        // 오늘/이번 달 섹션을 위한 고정 기간 설정 (이번 달 1일 ~ 마지막 날)
        val thisMonthStart = today.firstDayOfMonth().toEpochMilliseconds()
        val thisMonthEnd = today.lastDayOfMonth().toEpochMilliseconds() + DAY_END_MILLIS_OFFSET
        val yearMonth = today.convertLocalDateToString(formatDateHyphenYM)

        statusTypeFlow.flatMapLatest { statusType ->
            val periods = getPeriods(statusType)

            combine(
                budgetRepository.getBudgetsByMonthWithDetails(yearMonth),
                historyRepository.getHistoriesByMonth(thisMonthStart, thisMonthEnd),                // Today 섹션용 (이번 달 고정)
                historyRepository.getHistoriesByMonth(periods.currentStart, periods.currentEnd),    // 선택된 현재 기간
                historyRepository.getHistoriesByMonth(periods.lastStart, periods.lastEnd),          // 비교 대상인 지난 기간
                repeatCycleRepository.getRepeatCycleWithDetails()
            ) { budgets, thisMonthHistories, currentHistories, lastHistories, repeatCycles ->
                mapToUiState(
                    statusType = statusType,
                    budgets = budgets,
                    thisMonthHistories = thisMonthHistories,
                    currentHistories = currentHistories,
                    lastHistories = lastHistories,
                    repeatCycles = repeatCycles
                )
            }
        }.apiFlow { state ->
            reduceState { state }
            // DB Flow는 스트림이 종료되지 않아 onCompletion이 호출되지 않으므로, 첫 데이터 수신 시 로딩을 강제로 해제합니다.
            showLoading(false)
        }
    }

    /**
     * 수집된 데이터를 UI 상태(HomeUiState)로 변환합니다.
     */
    private fun mapToUiState(
        statusType: StatusType,
        budgets: List<BudgetWithDetails>,
        thisMonthHistories: List<HistoryWithDetails>,
        currentHistories: List<HistoryWithDetails>,
        lastHistories: List<HistoryWithDetails>,
        repeatCycles: List<RepeatCycleWithDetails>,
    ): HomeUiState {
        // 1. 오늘 및 이번 달 요약 데이터 계산
        val thisMonthExpenses =
            thisMonthHistories.filter { it.history.largeCategory == LargeCategoryEnum.EXPENSES.name }
        val thisMonthExpensesAmount = thisMonthExpenses.sumOf { it.history.amount }
        val thisMonthBudgetAmount = budgets
            .filter { it.category?.largeCategory == LargeCategoryEnum.EXPENSES.name }
            .sumOf { it.budget.amount }

        val todayStart = today.toEpochMilliseconds()
        val todayEnd = todayStart + DAY_END_MILLIS_OFFSET
        val todayAmount = thisMonthExpenses
            .filter { it.history.date in todayStart..todayEnd }
            .sumOf { it.history.amount }

        // 2. 현재 기간 vs 지난 기간 금액 비교
        val currentGrouped = currentHistories.groupBy { it.history.largeCategory }
        val lastGrouped = lastHistories.groupBy { it.history.largeCategory }

        val currentExpenses = currentGrouped[LargeCategoryEnum.EXPENSES.name].default()
        val lastExpenses = lastGrouped[LargeCategoryEnum.EXPENSES.name].default()

        val currentAmount = AmountVo(
            expensesAmount = currentExpenses.sumOf { it.history.amount },
            incomeAmount = currentGrouped[LargeCategoryEnum.INCOME.name]
                ?.sumOf { it.history.amount }
                .default(),
            savingAmount = currentGrouped[LargeCategoryEnum.SAVING.name]
                ?.sumOf { it.history.amount }
                .default()
        )
        val lastAmount = AmountVo(
            expensesAmount = lastExpenses.sumOf { it.history.amount },
            incomeAmount = lastGrouped[LargeCategoryEnum.INCOME.name]
                ?.sumOf { it.history.amount }
                .default(),
            savingAmount = lastGrouped[LargeCategoryEnum.SAVING.name]
                ?.sumOf { it.history.amount }
                .default()
        )

        // 3. 차트용 데이터 가공 (카테고리별 / 결제수단별)
        val categorySegments = calculateCategorySegments(currentExpenses)
        val paymentMethodSegments = calculatePaymentMethodSegments(currentExpenses)

        // 4. 누적 지출 그래프 데이터 계산
        val (currentList, lastList) = calculateGraphData(statusType, currentExpenses, lastExpenses)
        val maxAmount = maxOf(
            currentList.filterNotNull().lastOrNull().default(),
            lastList.lastOrNull().default()
        ).coerceAtLeast(1f)

        // 5. 고정 지출(반복 내역) 상태 계산
        val recurringHistories = calculateRecurringHistories(thisMonthHistories, repeatCycles)

        return HomeUiState(
            statusType = statusType,
            todayAmount = todayAmount,
            thisMonthBudgetAmount = thisMonthBudgetAmount,
            thisMonthExpensesAmount = thisMonthExpensesAmount,
            budgetAmount = thisMonthBudgetAmount, // 년 단위일 경우 별도 로직이 필요할 수 있으나 현재는 이번 달 예산 기준
            currentAmount = currentAmount,
            lastAmount = lastAmount,
            categorySegment = categorySegments,
            paymentMethodSegment = paymentMethodSegments,
            currentExpensesData = currentList.map { it?.div(maxAmount) },
            lastExpensesData = lastList.map { it / maxAmount },
            recurringHistories = recurringHistories
        )
    }

    /**
     * 카테고리별 지출 세그먼트를 계산합니다.
     */
    private fun calculateCategorySegments(expenses: List<HistoryWithDetails>): List<CategorySegmentedChartVo> {
        return expenses.groupBy { it.category?.id }
            .map { (_, histories) ->
                CategorySegmentedChartVo(
                    category = histories.first().category.mapperToVo(LargeCategoryEnum.EXPENSES),
                    amount = histories.sumOf { it.history.amount }
                )
            }.sortedByDescending { it.amount }
    }

    /**
     * 결제 수단별 지출 세그먼트를 계산합니다.
     */
    private fun calculatePaymentMethodSegments(expenses: List<HistoryWithDetails>): List<PaymentMethodSegmentedChartVo> {
        return expenses.groupBy { it.paymentMethod?.id }
            .map { (_, histories) ->
                PaymentMethodSegmentedChartVo(
                    paymentMethod = histories.first().paymentMethod?.mapperToVo(),
                    amount = histories.sumOf { it.history.amount }
                )
            }.sortedByDescending { it.amount }
    }

    /**
     * 기간 타입(주/월/년)에 따른 누적 지출 그래프 데이터를 생성합니다.
     */
    private fun calculateGraphData(
        statusType: StatusType,
        currentExpenses: List<HistoryWithDetails>,
        lastExpenses: List<HistoryWithDetails>,
    ): Pair<List<Float?>, List<Float>> {
        return when (statusType) {
            StatusType.WEEK -> {
                val current = calculateCumulative(
                    histories = currentExpenses,
                    maxUnit = 7,
                    unitSelector = { it.dayOfWeek.isoDayNumber },
                    currentLimit = today.dayOfWeek.isoDayNumber
                )
                val last = calculateCumulative(
                    histories = lastExpenses,
                    maxUnit = 7,
                    unitSelector = { it.dayOfWeek.isoDayNumber }).filterNotNull()
                current to last
            }

            StatusType.MONTH -> {
                val daysInCurrentMonth = today.lastDayOfMonth().day
                val daysInLastMonth = today.minus(1, DateTimeUnit.MONTH).lastDayOfMonth().day
                val maxDays = maxOf(daysInCurrentMonth, daysInLastMonth)
                val current = calculateCumulative(
                    histories = currentExpenses,
                    maxUnit = maxDays,
                    unitSelector = { it.day },
                    currentLimit = today.day
                )
                val last = calculateCumulative(
                    histories = lastExpenses,
                    maxUnit = maxDays,
                    unitSelector = { it.day },
                    currentLimit = daysInLastMonth
                ).filterNotNull()
                current to last
            }

            StatusType.YEAR -> {
                val current = calculateCumulative(
                    histories = currentExpenses,
                    maxUnit = 12,
                    unitSelector = { it.month.number },
                    currentLimit = today.month.number
                )
                val last =
                    calculateCumulative(
                        histories = lastExpenses,
                        maxUnit = 12,
                        unitSelector = { it.month.number }).filterNotNull()
                current to last
            }
        }
    }

    /**
     * 시간 단위(일/월 등)별 누적 합계를 계산하는 공통 로직입니다.
     */
    private fun calculateCumulative(
        histories: List<HistoryWithDetails>,
        maxUnit: Int,
        unitSelector: (LocalDate) -> Int,
        currentLimit: Int? = null,
    ): List<Float?> {
        val grouped = histories.groupBy { unitSelector(it.history.date.toLocalDate()) }
        var cumulative = 0L
        return (1..maxUnit).map { unit ->
            if (currentLimit == null || unit <= currentLimit) {
                cumulative += grouped[unit]?.sumOf { it.history.amount }.default()
                cumulative.toFloat()
            } else {
                null
            }
        }
    }

    /**
     * 이번 달의 실제 내역과 반복 설정 데이터를 비교하여 고정 지출 상태를 계산합니다.
     */
    private fun calculateRecurringHistories(
        thisMonthHistories: List<HistoryWithDetails>,
        repeatCycles: List<RepeatCycleWithDetails>,
    ): List<RecurringHistoryVo> {
        val historiesByRepeatId = thisMonthHistories.groupBy { it.history.repeatCycleId }

        return repeatCycles
            .filter { it.repeatCycle.isActive && it.repeatCycle.largeCategory == LargeCategoryEnum.EXPENSES.name }
            .mapNotNull { item ->
                val repeatCycle = item.repeatCycle
                val repeatCycleEnum = RepeatCycleEnum.create(repeatCycle.repeatCycle)
                val actualHistories = historiesByRepeatId[repeatCycle.id].default()

                val info =
                    getRecurringInfo(repeatCycleEnum, repeatCycle.dayOfMonth, repeatCycle.dayOfWeek)
                        ?: return@mapNotNull null

                // 실제 내역이 존재하면 이를 우선하여 '지남' 또는 '오늘' 여부를 판단
                val finalPassed =
                    if (actualHistories.isNotEmpty()) {
                        actualHistories.any { it.history.date.toLocalDate() >= today }.not()
                    } else {
                        info.isPassed
                    }

                val finalToday =
                    if (actualHistories.isNotEmpty()) {
                        actualHistories.any { it.history.date.toLocalDate() == today }
                    } else {
                        info.isToday
                    }

                RecurringHistoryVo(
                    id = repeatCycle.id,
                    categoryIcon = item.category?.icon ?: DEFAULT_CATEGORY_ICON,
                    largeCategory = LargeCategoryEnum.creator(repeatCycle.largeCategory),
                    content = repeatCycle.content ?: item.category?.middleLabel
                    ?: DEFAULT_RECURRING_CONTENT,
                    singleAmount = repeatCycle.amount,
                    monthlyTotalAmount = if (actualHistories.isNotEmpty()) actualHistories.sumOf { it.history.amount } else repeatCycle.amount,
                    recurringDateText = info.dateText,
                    isPassed = finalPassed,
                    isToday = finalToday,
                    isFixed = item.category?.isFixed.default(),
                    sortOrder = info.sortOrder
                )
            }
    }

    /**
     * 반복 주기와 날짜 정보를 기반으로 표시용 텍스트 및 상태 정보를 생성합니다.
     */
    private fun getRecurringInfo(
        enum: RepeatCycleEnum,
        dayOfMonth: Int?,
        dayOfWeek: Int?,
    ): RecurringInfoVo? {
        return when (enum) {
            RepeatCycleEnum.MONTHLY -> {
                val day = dayOfMonth ?: 1
                RecurringInfoVo(
                    isPassed = today.day > day,
                    isToday = today.day == day,
                    dateText = enum.formattedShortDescription(day),
                    sortOrder = day
                )
            }

            RepeatCycleEnum.WEEKLY -> {
                val day = dayOfWeek ?: 1
                RecurringInfoVo(
                    isPassed = today.dayOfWeek.isoDayNumber > day,
                    isToday = today.dayOfWeek.isoDayNumber == day,
                    dateText = enum.formattedShortDescription(day),
                    sortOrder = day
                )
            }

            RepeatCycleEnum.DAILY, RepeatCycleEnum.WEEKDAY, RepeatCycleEnum.WEEKEND -> {
                RecurringInfoVo(
                    isPassed = false,
                    isToday = true,
                    dateText = enum.shortDescription,
                    sortOrder = 0
                )
            }

            RepeatCycleEnum.MONTH_END -> {
                val lastDay = today.lastDayOfMonth()
                RecurringInfoVo(
                    isPassed = today > lastDay,
                    isToday = today == lastDay,
                    dateText = enum.shortDescription,
                    sortOrder = 32
                )
            }

            else -> null
        }
    }

    /**
     * 기간 타입(주/월/년)에 맞춰 현재 및 비교 대상 기간의 시작/종료 밀리초를 계산합니다.
     */
    private fun getPeriods(statusType: StatusType): PeriodsVo {
        val (currentStart, currentEnd) = when (statusType) {
            StatusType.WEEK -> {
                val start = today.minus(today.dayOfWeek.isoDayNumber - 1, DateTimeUnit.DAY)
                start to start.plus(6, DateTimeUnit.DAY)
            }

            StatusType.MONTH -> {
                today.firstDayOfMonth() to today.lastDayOfMonth()
            }
            StatusType.YEAR -> {
                LocalDate(today.year, 1, 1) to LocalDate(today.year, 12, 31)
            }
        }

        val (lastStart, lastEnd) = when (statusType) {
            StatusType.WEEK -> {
                val start = currentStart.minus(7, DateTimeUnit.DAY)
                start to start.plus(6, DateTimeUnit.DAY)
            }

            StatusType.MONTH -> {
                val lastMonth = today.minus(1, DateTimeUnit.MONTH)
                lastMonth.firstDayOfMonth() to lastMonth.lastDayOfMonth()
            }

            StatusType.YEAR -> {
                LocalDate(today.year - 1, 1, 1) to LocalDate(today.year - 1, 12, 31)
            }
        }

        return PeriodsVo(
            currentStart = currentStart.toEpochMilliseconds(),
            currentEnd = currentEnd.toEpochMilliseconds() + DAY_END_MILLIS_OFFSET,
            lastStart = lastStart.toEpochMilliseconds(),
            lastEnd = lastEnd.toEpochMilliseconds() + DAY_END_MILLIS_OFFSET
        )
    }
}
