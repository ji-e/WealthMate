package com.jie.wealthmate.feature.budget.budgetSetting

import com.jie.wealthmate.base.BaseScreenModel

class BudgetSettingScreenModel() : BaseScreenModel<BudgetSettingUiState>() {

    override val initialState: BudgetSettingUiState
        get() = BudgetSettingUiState()
}