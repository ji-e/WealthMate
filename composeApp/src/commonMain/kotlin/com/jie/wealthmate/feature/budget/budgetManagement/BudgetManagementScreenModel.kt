package com.jie.wealthmate.feature.budget.budgetManagement

import com.jie.wealthmate.base.BaseScreenModel

class BudgetManagementScreenModel() : BaseScreenModel<BudgetManagementUiState>() {

    override val initialState: BudgetManagementUiState
        get() = BudgetManagementUiState()
}