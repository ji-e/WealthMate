package com.jie.wealthmate.repository

import com.jie.wealthmate.database.DatabaseProvider
import com.jie.wealthmate.database.eneity.BudgetEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import kotlin.time.Clock

class BudgetRepositoryImpl(private val databaseProvider: DatabaseProvider) : BudgetRepository {
    private val repoName = "BudgetRepository"
    private val dao get() = databaseProvider.database.budgetDao()

    override suspend fun saveBudgets(yearMonth: String, budgets: List<BudgetEntity>) = withContext(Dispatchers.Default) {
        loggedCall(
            repositoryName = repoName,
            methodName = "saveBudgets",
            params = mapOf("yearMonth" to yearMonth, "budgetsCount" to budgets.size)
        ) {
            val now = Clock.System.now().toEpochMilliseconds()
            dao.softDeleteByMonth(yearMonth, now)
            dao.insertAll(budgets.map { it.copy(updatedAt = now) })
        }
    }

    override suspend fun updateBudget(budget: BudgetEntity) = withContext(Dispatchers.Default) {
        loggedCall(
            repositoryName = repoName,
            methodName = "updateBudget",
            params = mapOf("budget" to budget)
        ) {
            dao.update(budget.copy(updatedAt = Clock.System.now().toEpochMilliseconds()))
        }
    }

    override fun getBudgetsByMonth(yearMonth: String): Flow<List<BudgetEntity>> =
        loggedFlow(
            repositoryName = repoName,
            methodName = "getBudgetsByMonth",
            params = mapOf("yearMonth" to yearMonth)
        ) {
            dao.getBudgetsByMonth(yearMonth)
        }.flowOn(Dispatchers.Default)

    override suspend fun deleteBudgetsByMonth(yearMonth: String) = withContext(Dispatchers.Default) {
        loggedCall(
            repositoryName = repoName,
            methodName = "deleteBudgetsByMonth",
            params = mapOf("yearMonth" to yearMonth)
        ) {
            dao.softDeleteByMonth(yearMonth, Clock.System.now().toEpochMilliseconds())
        }
    }
}
