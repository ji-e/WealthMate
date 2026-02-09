package com.jie.wealthmate.feature.menu.management.repeatHistoryManagement.addRepeatHistory

import com.jie.wealthmate.base.BaseScreenModel
import com.jie.wealthmate.database.eneity.RepeatCycleEntity
import com.jie.wealthmate.repository.RepeatCycleRepository

class AddRepeatHistoryScreenModel(
    private val repeatCycleRepository: RepeatCycleRepository,
) : BaseScreenModel<AddRepeatHistoryUiState>() {

    override val initialState: AddRepeatHistoryUiState
        get() = AddRepeatHistoryUiState()

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
