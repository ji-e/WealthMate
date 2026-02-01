package com.jie.wealthmate.feature.calendar.historyDetail

import com.jie.wealthmate.base.BaseScreenModel
import com.jie.wealthmate.repository.CategoryRepository
import com.jie.wealthmate.repository.HistoryRepository
import com.jie.wealthmate.repository.PaymentMethodRepository
import com.jie.wealthmate.usecase.HistorySaveUseCase

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

    fun getHistory(historyId: String) {
        launchSafe(
            block = {
                historyRepository.getHistoryById(historyId)
            }
        ) {
            println(it)
        }
    }

    fun saveHistory() {

    }

}