@file:OptIn(ExperimentalCoroutinesApi::class)

package com.jie.wealthmate.feature.budget.budgetYearDetail

import com.jie.wealthmate.base.BaseScreenModel
import com.jie.wealthmate.repository.BudgetRepository
import com.jie.wealthmate.repository.HistoryRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi

class BudgetYearDetailScreenModel(
    private val selectedYear: String,
    private val budgetRepository: BudgetRepository,
    private val historyRepository: HistoryRepository,
) : BaseScreenModel<BudgetYearDetailUiState>() {

    override val initialState: BudgetYearDetailUiState =
        BudgetYearDetailUiState(selectedYear = selectedYear)

}
