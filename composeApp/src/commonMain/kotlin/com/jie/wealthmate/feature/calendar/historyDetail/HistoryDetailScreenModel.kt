package com.jie.wealthmate.feature.calendar.historyDetail

import androidx.compose.ui.text.input.TextFieldValue
import com.jie.wealthmate.base.BaseScreenModel
import com.jie.wealthmate.feature.calendar.addHistory.component.RepeatCycleEnum
import com.jie.wealthmate.repository.CategoryRepository
import com.jie.wealthmate.repository.HistoryRepository
import com.jie.wealthmate.repository.PaymentMethodRepository
import com.jie.wealthmate.usecase.HistorySaveUseCase
import com.jie.wealthmate.utils.default
import com.jie.wealthmate.vo.CategoryTagVo
import com.jie.wealthmate.vo.CategoryVo
import com.jie.wealthmate.vo.HistoryVo.Companion.mapperToVo
import com.jie.wealthmate.vo.PaymentMethodVo
import kotlinx.datetime.LocalDate

class HistoryDetailScreenModel(
    private val categoryRepository: CategoryRepository,
    private val paymentMethodRepository: PaymentMethodRepository,
    private val historyRepository: HistoryRepository,
    private val historySaveUseCase: HistorySaveUseCase,
) : BaseScreenModel<HistoryDetailUiState>() {

    override val initialState: HistoryDetailUiState
        get() = HistoryDetailUiState()


    fun updateInit(historyId: String) {
        getHistory(historyId)
    }

    fun updateDate(date: LocalDate) {
        reduceState { state ->
            state.copy(
                isDataChanged = true,
                date = date
            )
        }
    }

    fun updateRepeatCycle(repeatCycle: RepeatCycleEnum?) {
        reduceState { state ->
            state.copy(
                isDataChanged = true,
                repeatCycle = repeatCycle
            )
        }
    }

    fun updateInstallmentCount(installmentCount: Int?) {
        reduceState { state ->
            state.copy(
                isDataChanged = true,
                installmentCount = installmentCount
            )
        }
    }

    fun updateAmount(amount: TextFieldValue) {
        reduceState { state ->
            state.copy(
                isDataChanged = true,
                amount = amount
            )
        }
    }

    fun updateCategory(category: CategoryVo) {
        reduceState { state ->
            state.copy(
                isDataChanged = true,
                category = category
            )
        }
    }

    fun updateCategoryTag(categoryTag: CategoryTagVo) {
        reduceState { state ->
            state.copy(
                isDataChanged = true,
                categoryTag = categoryTag
            )
        }
    }

    fun updatePaymentMethod(paymentMethod: PaymentMethodVo?) {
        reduceState { state ->
            state.copy(
                isDataChanged = true,
                paymentMethod = paymentMethod
            )
        }
    }

    fun updateContent(content: TextFieldValue) {
        reduceState { state ->
            state.copy(
                isDataChanged = true,
                content = content
            )
        }
    }

    fun getHistory(historyId: String) {
        launchSafe(
            block = {
                historyRepository.getHistoryById(historyId)
            }
        ) { response ->
            val history = response.mapperToVo()
            reduceState { state ->
                state.copy(
                    date = history.date,
                    amount = TextFieldValue(history.amount.toString()),
                    category = history.category,
                    categoryTag = history.categoryTag,
                    paymentMethod = history.paymentMethod,
                    content = TextFieldValue(history.content.default()),
                )
            }
            println(response)
        }
    }

    fun saveHistory() {

    }

}