package com.jie.wealthmate.feature.menu.management.repeatHistoryManagement

import androidx.lifecycle.viewModelScope
import com.jie.wealthmate.base.BaseViewModel
import com.jie.wealthmate.database.eneity.RepeatCycleEntity
import com.jie.wealthmate.feature.menu.management.categoryManagement.component.LargeCategoryEnum
import com.jie.wealthmate.repository.RepeatCycleRepository
import kotlinx.coroutines.launch

class RepeatHistoryManagementViewModel(
    private val repeatCycleRepository: RepeatCycleRepository,
) : BaseViewModel<RepeatHistoryManagementUiState>() {

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
        viewModelScope.launch {
            showLoading(true)
            try {
                repeatCycleRepository.updateRepeatCycle(
                    repeatCycle.copy(
                        isActive = isActive
                    )
                )
                showSnackbar("반복 정보가 수정되었습니다.")
            } catch (e: Exception) {
                showSnackbar(e.message ?: "오류가 발생했습니다.")
            } finally {
                showLoading(false)
            }
        }
    }
}
