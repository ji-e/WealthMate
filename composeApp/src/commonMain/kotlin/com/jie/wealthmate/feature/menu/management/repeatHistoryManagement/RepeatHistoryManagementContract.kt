package com.jie.wealthmate.feature.menu.management.repeatHistoryManagement

import com.jie.wealthmate.base.BaseUiState
import com.jie.wealthmate.vo.RepeatCycleVo

data class RepeatHistoryManagementUiState(
    val repeatHistoryItems: List<RepeatCycleVo> = emptyList(),
) : BaseUiState {

}
