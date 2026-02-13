package com.jie.wealthmate.feature.budget.addBudget

import com.jie.wealthmate.base.BaseScreenModel

class AddBudgetScreenModel() : BaseScreenModel<AddBudgetUiState>() {

    override val initialState: AddBudgetUiState
        get() = AddBudgetUiState()
}