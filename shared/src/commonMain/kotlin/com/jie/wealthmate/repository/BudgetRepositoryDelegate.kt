package com.jie.wealthmate.repository

import com.jie.wealthmate.account.AccountAwareDelegate
import com.jie.wealthmate.account.AccountProvider
import com.jie.wealthmate.database.eneity.BudgetEntity
import com.jie.wealthmate.database.eneity.BudgetWithDetails
import kotlinx.coroutines.flow.Flow

class BudgetRepositoryDelegate(
    accountProvider: AccountProvider,
    private val localRepository: BudgetRepository,
    private val firestoreRepository: BudgetRepository,
) : BudgetRepository {
    private val d = AccountAwareDelegate(
        special = firestoreRepository,
        normal = localRepository,
        accountProvider = accountProvider,
    )

    override suspend fun saveBudgets(yearMonth: String, budgets: List<BudgetEntity>) = d.current.saveBudgets(yearMonth, budgets)

    override suspend fun updateBudget(budget: BudgetEntity) = d.current.updateBudget(budget)

    override fun getBudgetsByMonth(yearMonth: String): Flow<List<BudgetEntity>> = d.flatFlow { it.getBudgetsByMonth(yearMonth) }

    override fun getBudgetsByMonthWithDetails(yearMonth: String): Flow<List<BudgetWithDetails>> = d.flatFlow { it.getBudgetsByMonthWithDetails(yearMonth) }

    override fun getBudgetsByYearWithDetails(year: String): Flow<List<BudgetWithDetails>> = d.flatFlow { it.getBudgetsByYearWithDetails(year) }

    override fun getAllYearMonths(): Flow<List<String>> = d.flatFlow { it.getAllYearMonths() }

    override suspend fun deleteBudgetsByMonth(yearMonth: String) = d.current.deleteBudgetsByMonth(yearMonth)
}
