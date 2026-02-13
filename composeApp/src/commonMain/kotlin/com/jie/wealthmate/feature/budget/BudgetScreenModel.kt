package com.jie.wealthmate.feature.budget

import com.jie.wealthmate.base.BaseScreenModel

class BudgetScreenModel() : BaseScreenModel<BudgetUiState>() {
    override val initialState: BudgetUiState
        get() = BudgetUiState()
}