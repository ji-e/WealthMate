package com.jie.wealthmate.feature.menu.management.repeatHistoryManagement

import com.jie.wealthmate.base.BaseScreenModel
import com.jie.wealthmate.database.eneity.RepeatCycleEntity
import com.jie.wealthmate.feature.menu.management.categoryManagement.component.LargeCategoryEnum
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

    fun changeTab(tab: LargeCategoryEnum) {
        reduceState { state ->
            state.copy(currentTab = tab)
        }
    }

    fun modifyRepeatCycle(repeatCycle: RepeatCycleEntity, isActive: Boolean) {
        launchSafe(
            block = {
                repeatCycleRepository.updateRepeatCycle(
                    repeatCycle.copy(
                        isActive = isActive
                    )
                )
            }
        ) {
            showSnackbar("반복 정보가 수정되었습니다.")
        }
    }
}
