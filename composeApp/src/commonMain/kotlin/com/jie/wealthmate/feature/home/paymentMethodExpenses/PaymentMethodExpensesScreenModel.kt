package com.jie.wealthmate.feature.home.paymentMethodExpenses

import com.jie.wealthmate.base.BaseScreenModel
import com.jie.wealthmate.feature.home.StatusType
import com.jie.wealthmate.feature.menu.management.categoryManagement.component.LargeCategoryEnum
import com.jie.wealthmate.repository.HistoryRepository
import com.jie.wealthmate.repository.PaymentMethodRepository
import com.jie.wealthmate.utils.firstDayOfMonth
import com.jie.wealthmate.utils.lastDayOfMonth
import com.jie.wealthmate.utils.toEpochMilliseconds
import com.jie.wealthmate.utils.toLocalDate
import com.jie.wealthmate.utils.today
import com.jie.wealthmate.vo.PaymentMethodVo
import com.jie.wealthmate.vo.PaymentMethodVo.Companion.mapperToVo
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.minus
import kotlinx.datetime.plus

class PaymentMethodExpensesScreenModel(
    private val historyRepository: HistoryRepository,
    private val paymentMethodRepository: PaymentMethodRepository,
    private val initialStatusType: StatusType,
    private val initialLargeCategory: LargeCategoryEnum,
    private val paymentMethodId: String?
) : BaseScreenModel<PaymentMethodExpensesUiState>() {

    override val initialState: PaymentMethodExpensesUiState = PaymentMethodExpensesUiState(
        statusType = initialStatusType,
        largeCategory = initialLargeCategory
    )

    private val filterFlow = MutableStateFlow(initialStatusType to initialLargeCategory)

    init {
        observeData()
    }

    fun updateStatusType(statusType: StatusType) {
        if (container.uiState.value.statusType == statusType) return
        filterFlow.value = statusType to filterFlow.value.second
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeData() {
        filterFlow.flatMapLatest { (statusType, largeCategory) ->
            val periods = getPeriods(statusType)
            val isUnsetSearch = paymentMethodId.isNullOrBlank() || paymentMethodId == PaymentMethodVo.UNSET.id
            val effectivePaymentMethodId = if (isUnsetSearch) null else paymentMethodId

            combine(
                historyRepository.getHistoriesByMonth(periods.currentStart, periods.currentEnd),
                historyRepository.getHistoriesByMonth(periods.lastStart, periods.lastEnd),
                flow {
                    val entity = if (effectivePaymentMethodId != null) {
                        paymentMethodRepository.getPaymentMethodById(effectivePaymentMethodId)
                    } else null
                    emit(entity)
                }
            ) { currentHistories, lastHistories, paymentMethodWithGroup ->
                val paymentMethodVo = paymentMethodWithGroup?.paymentMethod?.mapperToVo() ?: PaymentMethodVo.UNSET
                
                val filteredCurrent = currentHistories.filter { 
                    it.history.largeCategory == largeCategory.name && 
                    if (isUnsetSearch) it.history.paymentMethodId.isNullOrBlank() else it.history.paymentMethodId == effectivePaymentMethodId
                }
                val filteredLast = lastHistories.filter { 
                    it.history.largeCategory == largeCategory.name && 
                    if (isUnsetSearch) it.history.paymentMethodId.isNullOrBlank() else it.history.paymentMethodId == effectivePaymentMethodId
                }

                val histories = filteredCurrent.sortedByDescending { it.history.date }
                val groupedHistories = histories
                    .groupBy { it.history.date.toLocalDate() }
                    .toList()
                    .sortedByDescending { it.first }

                PaymentMethodExpensesUiState(
                    statusType = statusType,
                    largeCategory = largeCategory,
                    paymentMethod = paymentMethodVo,
                    totalAmount = filteredCurrent.sumOf { it.history.amount },
                    lastTotalAmount = filteredLast.sumOf { it.history.amount },
                    histories = histories,
                    groupedHistories = groupedHistories
                )
            }
        }.apiFlow { newState ->
            reduceState { newState }
        }
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
