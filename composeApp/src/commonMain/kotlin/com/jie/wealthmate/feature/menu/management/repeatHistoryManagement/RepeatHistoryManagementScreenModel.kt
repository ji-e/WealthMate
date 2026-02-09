package com.jie.wealthmate.feature.menu.management.repeatHistoryManagement

import com.jie.wealthmate.base.BaseScreenModel
import com.jie.wealthmate.repository.RepeatCycleRepository

class RepeatHistoryManagementScreenModel(
    private val repeatCycleRepository: RepeatCycleRepository,
) : BaseScreenModel<RepeatHistoryManagementUiState>() {

    override val initialState: RepeatHistoryManagementUiState
        get() = RepeatHistoryManagementUiState()

    init {
        getRepeatHistory()
    }

    private fun getRepeatHistory() {
        repeatCycleRepository.getRepeatCycleWithDetails()
            .apiFlow { response ->
                reduceState { state ->
                    state.copy(
                        repeatHistoryItems = response
                    )
                }
            }
    }
}
