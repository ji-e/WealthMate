package com.jie.wealthmate.repository

import com.jie.wealthmate.account.AccountAwareDelegate
import com.jie.wealthmate.account.AccountProvider
import com.jie.wealthmate.database.eneity.HistoryEntity
import com.jie.wealthmate.database.eneity.HistoryWithDetails
import kotlinx.coroutines.flow.Flow

class HistoryRepositoryDelegate(
    accountProvider: AccountProvider,
    private val localRepository: HistoryRepository,
    private val firestoreRepository: HistoryRepository,
) : HistoryRepository {
    private val d = AccountAwareDelegate(
        special = firestoreRepository,
        normal = localRepository,
        accountProvider = accountProvider,
    )

    override fun getAllHistories(): Flow<List<HistoryEntity>> = d.flatFlow { it.getAllHistories() }

    override fun getHistoriesWithDetails(): Flow<List<HistoryWithDetails>> =
        d.flatFlow { it.getHistoriesWithDetails() }

    override suspend fun getHistoryById(id: String): HistoryWithDetails? =
        d.call { it.getHistoryById(id) }

    override suspend fun insertHistory(history: HistoryEntity) =
        d.dualCall { it.insertHistory(history) }

    override suspend fun insertHistories(histories: List<HistoryEntity>) =
        d.dualCall { it.insertHistories(histories) }

    override suspend fun updateHistory(history: HistoryEntity) =
        d.dualCall { it.updateHistory(history) }

    override suspend fun updateHistories(histories: List<HistoryEntity>) =
        d.dualCall { it.updateHistories(histories) }

    override suspend fun deleteHistory(id: String) = d.dualCall { it.deleteHistory(id) }

    override suspend fun deleteHistoriesByInstallmentId(installmentId: String) =
        d.dualCall { it.deleteHistoriesByInstallmentId(installmentId) }

    override fun getHistoriesByMonth(
        startDate: Long,
        endDate: Long,
    ): Flow<List<HistoryWithDetails>> = d.flatFlow { it.getHistoriesByMonth(startDate, endDate) }

    override suspend fun getHistoriesByMonthWithDeleted(
        startDate: Long,
        endDate: Long
    ): List<HistoryWithDetails> = d.call {
        it.getHistoriesByMonthWithDeleted(startDate, endDate)
    }

    override fun getSumByMonth(startDate: Long, endDate: Long, categoryType: String): Flow<Long> =
        d.flatFlow { it.getSumByMonth(startDate, endDate, categoryType) }

    override suspend fun getHistoriesByInstallmentId(installmentId: String): List<HistoryEntity> =
        d.call { it.getHistoriesByInstallmentId(installmentId) }

    override suspend fun searchHistories(
        query: String,
        sortOrder: String,
        startDate: Long?,
        endDate: Long?,
        largeCategories: List<String>,
        categoryIds: List<String>,
        paymentMethodIds: List<String>,
        limit: Int,
        offset: Int,
    ): List<HistoryWithDetails> = d.call {
        it.searchHistories(
            query,
            sortOrder,
            startDate,
            endDate,
            largeCategories,
            categoryIds,
            paymentMethodIds,
            limit,
            offset
        )
    }

    override suspend fun getSearchSummary(
        query: String,
        startDate: Long?,
        endDate: Long?,
        largeCategories: List<String>,
        categoryIds: List<String>,
        paymentMethodIds: List<String>,
    ): Map<String, Long> = d.call {
        it.getSearchSummary(
            query,
            startDate,
            endDate,
            largeCategories,
            categoryIds,
            paymentMethodIds
        )
    }

    // ✅ 복원용 메서드들
    override suspend fun getAllHistoriesList(): List<HistoryEntity> = d.call { it.getAllHistoriesList() }
    override suspend fun deleteAllHistories() = d.dualCall { it.deleteAllHistories() }

    // ✅ 핵심 복원 로직: Firestore -> Local
    override suspend fun syncRemoteToLocal() {
        d.restore { special, normal ->
            val remoteData = special.getAllHistoriesList()
            if (remoteData.isNotEmpty()) {
                normal.deleteAllHistories()
                normal.insertHistories(remoteData)
            }
        }
    }

    // ✅ 핵심 복원 로직: Local -> Firestore
    override suspend fun syncLocalToRemote() {
        d.restore { special, normal ->
            val localData = normal.getAllHistoriesList()
            if (localData.isNotEmpty()) {
                special.deleteAllHistories()
                special.insertHistories(localData)
            }
        }
    }
}
