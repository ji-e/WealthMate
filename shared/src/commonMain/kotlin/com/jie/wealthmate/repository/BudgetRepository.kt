package com.jie.wealthmate.repository

import com.jie.wealthmate.database.eneity.BudgetEntity
import kotlinx.coroutines.flow.Flow

interface BudgetRepository {
    suspend fun saveBudgets(yearMonth: String, budgets: List<BudgetEntity>)
    suspend fun updateBudget(budget: BudgetEntity)
    fun getBudgetsByMonth(yearMonth: String): Flow<List<BudgetEntity>>
    suspend fun deleteBudgetsByMonth(yearMonth: String)
}
