package com.jie.wealthmate.repository

import com.jie.wealthmate.database.eneity.BudgetEntity
import com.jie.wealthmate.database.eneity.BudgetWithDetails
import kotlinx.coroutines.flow.Flow

interface BudgetRepository {
    suspend fun saveBudgets(yearMonth: String, budgets: List<BudgetEntity>)
    suspend fun updateBudget(budget: BudgetEntity)
    fun getBudgetsByMonth(yearMonth: String): Flow<List<BudgetEntity>>
    fun getBudgetsByMonthWithDetails(yearMonth: String): Flow<List<BudgetWithDetails>>
    fun getBudgetsByYearWithDetails(year: String): Flow<List<BudgetWithDetails>>
    fun getAllYearMonths(): Flow<List<String>>
    suspend fun deleteBudgetsByMonth(yearMonth: String)
}
