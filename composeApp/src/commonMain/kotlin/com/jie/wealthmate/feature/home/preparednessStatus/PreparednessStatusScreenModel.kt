package com.jie.wealthmate.feature.home.preparednessStatus

import com.jie.wealthmate.base.BaseScreenModel
import com.jie.wealthmate.database.eneity.CategoryEntity
import com.jie.wealthmate.database.eneity.HistoryWithDetails
import com.jie.wealthmate.feature.home.StatusType
import com.jie.wealthmate.feature.menu.management.categoryManagement.component.LargeCategoryEnum
import com.jie.wealthmate.repository.BudgetRepository
import com.jie.wealthmate.repository.CategoryRepository
import com.jie.wealthmate.repository.HistoryRepository
import com.jie.wealthmate.utils.convertLocalDateToString
import com.jie.wealthmate.utils.firstDayOfMonth
import com.jie.wealthmate.utils.formatDateHyphenYM
import com.jie.wealthmate.utils.lastDayOfMonth
import com.jie.wealthmate.utils.toEpochMilliseconds
import com.jie.wealthmate.utils.today
import com.jie.wealthmate.vo.CategoryDiffInfoVo
import com.jie.wealthmate.vo.CategoryVo
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.minus
import kotlinx.datetime.plus

class PreparednessStatusScreenModel(
    private val historyRepository: HistoryRepository,
    private val categoryRepository: CategoryRepository,
    private val budgetRepository: BudgetRepository,
    initialStatusType: StatusType,
    initialLargeCategory: LargeCategoryEnum
) : BaseScreenModel<PreparednessStatusUiState>() {

    override val initialState: PreparednessStatusUiState = PreparednessStatusUiState(
        statusType = initialStatusType,
        largeCategory = initialLargeCategory
    )

    private val filterFlow = MutableStateFlow(initialStatusType to initialLargeCategory)

    init {
        observeData()
    }

    fun updateStatusType(statusType: StatusType) {
        filterFlow.value = statusType to filterFlow.value.second
    }

    fun updateLargeCategory(largeCategory: LargeCategoryEnum) {
        filterFlow.value = filterFlow.value.first to largeCategory
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeData() {
        filterFlow.flatMapLatest { (statusType, largeCategory) ->
            combine(
                historyRepository.getHistoriesByMonth(0, 0), // Dummy to trigger onStart
                categoryRepository.getCategoriesByLargeCategory(largeCategory.name)
            ) { _, _ -> }.onStart { showLoading(true) } // Show loading when filters change

            val periods = getPeriods(statusType)
            combine(
                historyRepository.getHistoriesByMonth(periods.currentStart, periods.currentEnd),
                historyRepository.getHistoriesByMonth(periods.lastStart, periods.lastEnd),
                categoryRepository.getCategoriesByLargeCategory(largeCategory.name),
                budgetRepository.getBudgetsByMonth(today.convertLocalDateToString(formatDateHyphenYM))
            ) { currentHistories, lastHistories, allCategories, budgets ->
                val filteredCurrent = currentHistories.filter { it.history.largeCategory == largeCategory.name }
                val filteredLast = lastHistories.filter { it.history.largeCategory == largeCategory.name }

                val currentAmount = filteredCurrent.sumOf { it.history.amount }
                val lastAmount = filteredLast.sumOf { it.history.amount }

                // categoryId가 null이거나 빈 값인 경우를 통일하여 그룹화
                val currentGrouped = filteredCurrent.groupBy { if (it.history.categoryId.isNullOrBlank()) null else it.history.categoryId }
                val lastGrouped = filteredLast.groupBy { if (it.history.categoryId.isNullOrBlank()) null else it.history.categoryId }

                val budgetAmount = if (largeCategory == LargeCategoryEnum.SAVING) {
                    val savingCategoryIds = allCategories.map { it.id }.toSet()
                    budgets.filter { it.categoryId in savingCategoryIds }.sumOf { it.amount }
                } else 0L

                // 모든 카테고리 타입(INCOME, EXPENSES, SAVING)에 대해 고정/변동 분리 적용
                val (variableComparisons, fixedComparisons) = run {
                    val (fixed, variable) = allCategories.partition { it.isFixed }
                    calculateCategoryComparisons(currentGrouped, lastGrouped, variable, currentAmount, true, largeCategory) to
                            calculateCategoryComparisons(currentGrouped, lastGrouped, fixed, currentAmount, false, largeCategory)
                }

                PreparednessStatusUiState(
                    statusType = statusType,
                    largeCategory = largeCategory,
                    currentAmount = currentAmount,
                    lastAmount = lastAmount,
                    budgetAmount = budgetAmount,
                    histories = filteredCurrent.sortedByDescending { it.history.date },
                    maxIncreaseCategory = variableComparisons.filter { it.diffAmount > 0 }.maxByOrNull { it.diffAmount },
                    maxDecreaseCategory = variableComparisons.filter { it.diffAmount < 0 }.minByOrNull { it.diffAmount },
                    categoryComparisons = variableComparisons,
                    fixedCategoryComparisons = fixedComparisons,
                    isLoading = false
                )
            }
        }.apiFlow { state ->
            reduceState { state }
        }
    }

    private fun calculateCategoryComparisons(
        currentGrouped: Map<String?, List<HistoryWithDetails>>,
        lastGrouped: Map<String?, List<HistoryWithDetails>>,
        targetCategories: List<CategoryEntity>,
        totalAmount: Long,
        includeUnset: Boolean,
        largeCategory: LargeCategoryEnum
    ): List<CategoryDiffInfoVo> {
        val comparisons = targetCategories.map { category ->
            val current = currentGrouped[category.id]?.sumOf { it.history.amount } ?: 0L
            val last = lastGrouped[category.id]?.sumOf { it.history.amount } ?: 0L

            CategoryDiffInfoVo(
                categoryId = category.id,
                categoryIcon = category.icon,
                categoryName = category.middleLabel,
                currentAmount = current,
                diffAmount = current - last,
                ratio = if (totalAmount > 0) current.toFloat() / totalAmount else 0f
            )
        }.toMutableList()

        if (includeUnset) {
            val unsetCurrent = currentGrouped[null]?.sumOf { it.history.amount } ?: 0L
            val unsetLast = lastGrouped[null]?.sumOf { it.history.amount } ?: 0L

            if (unsetCurrent > 0 || unsetLast > 0) {
                val unsetCategory = CategoryVo.unset(largeCategory)
                comparisons.add(
                    CategoryDiffInfoVo(
                        categoryId = null,
                        categoryIcon = unsetCategory.icon,
                        categoryName = unsetCategory.middleLabel,
                        currentAmount = unsetCurrent,
                        diffAmount = unsetCurrent - unsetLast,
                        ratio = if (totalAmount > 0) unsetCurrent.toFloat() / totalAmount else 0f
                    )
                )
            }
        }

        return comparisons.sortedByDescending { it.currentAmount }
    }

    private fun getPeriods(statusType: StatusType): Periods {
        val (currentRange, lastRange) = when (statusType) {
            StatusType.WEEK -> {
                val start = today.minus(today.dayOfWeek.isoDayNumber - 1, DateTimeUnit.DAY)
                (start to start.plus(6, DateTimeUnit.DAY)) to (start.minus(7, DateTimeUnit.DAY) to start.minus(1, DateTimeUnit.DAY))
            }
            StatusType.MONTH -> {
                val lastMonth = today.minus(1, DateTimeUnit.MONTH)
                (today.firstDayOfMonth() to today.lastDayOfMonth()) to (lastMonth.firstDayOfMonth() to lastMonth.lastDayOfMonth())
            }
            StatusType.YEAR -> {
                (LocalDate(today.year, 1, 1) to LocalDate(today.year, 12, 31)) to (LocalDate(today.year - 1, 1, 1) to LocalDate(today.year - 1, 12, 31))
            }
        }
        val endOffset = 86_399_999L
        return Periods(
            currentStart = currentRange.first.toEpochMilliseconds(),
            currentEnd = currentRange.second.toEpochMilliseconds() + endOffset,
            lastStart = lastRange.first.toEpochMilliseconds(),
            lastEnd = lastRange.second.toEpochMilliseconds() + endOffset
        )
    }

    data class Periods(
        val currentStart: Long,
        val currentEnd: Long,
        val lastStart: Long,
        val lastEnd: Long,
    )
}
