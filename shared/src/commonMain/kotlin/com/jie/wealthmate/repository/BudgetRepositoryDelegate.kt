package com.jie.wealthmate.repository

import com.jie.wealthmate.account.AccountAwareDelegate
import com.jie.wealthmate.account.AccountProvider
import com.jie.wealthmate.database.eneity.BudgetEntity

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

    override suspend fun saveBudgets(yearMonth: String, budgets: List<BudgetEntity>) =
        d.dualCall { it.saveBudgets(yearMonth, budgets) }

    override suspend fun updateBudget(budget: BudgetEntity) =
        d.dualCall { it.updateBudget(budget) }

    override suspend fun deleteBudgetsByMonth(yearMonth: String) =
        d.dualCall { it.deleteBudgetsByMonth(yearMonth) }

    override fun getBudgetsByMonth(yearMonth: String) =
        d.flatFlow { it.getBudgetsByMonth(yearMonth) }

    override fun getBudgetsByMonthWithDetails(yearMonth: String) =
        d.flatFlow { it.getBudgetsByMonthWithDetails(yearMonth) }

    override fun getBudgetsByYearWithDetails(year: String) =
        d.flatFlow { it.getBudgetsByYearWithDetails(year) }

    override fun getAllYearMonths() =
        d.flatFlow { it.getAllYearMonths() }

    // ✅ 복원용 메서드들
    override suspend fun getAllBudgetsList(): List<BudgetEntity> = d.call { it.getAllBudgetsList() }
    override suspend fun insertBudgets(budgets: List<BudgetEntity>) = d.dualCall { it.insertBudgets(budgets) }
    override suspend fun deleteAllBudgets() = d.dualCall { it.deleteAllBudgets() }

    // ✅ 핵심 복원 로직: Firestore -> Local
    override suspend fun syncRemoteToLocal() {
        d.restore { special, normal ->
            val remoteData = special.getAllBudgetsList()
            if (remoteData.isNotEmpty()) {
                normal.deleteAllBudgets()
                normal.insertBudgets(remoteData)
            }
        }
    }

    // ✅ 핵심 복원 로직: Local -> Firestore
    override suspend fun syncLocalToRemote() {
        d.restore { special, normal ->
            val localData = normal.getAllBudgetsList()
            if (localData.isNotEmpty()) {
                special.deleteAllBudgets()
                special.insertBudgets(localData)
            }
        }
    }
}
