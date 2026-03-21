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

    override fun getHistoriesWithDetails(): Flow<List<HistoryWithDetails>> = d.flatFlow { it.getHistoriesWithDetails() }

    override suspend fun getHistoryById(id: String): HistoryWithDetails? = d.current.getHistoryById(id)

    override suspend fun insertHistory(history: HistoryEntity) = d.current.insertHistory(history)

    override suspend fun insertHistories(histories: List<HistoryEntity>) = d.current.insertHistories(histories)

    override suspend fun updateHistory(history: HistoryEntity) = d.current.updateHistory(history)

    override suspend fun updateHistories(histories: List<HistoryEntity>) = d.current.updateHistories(histories)

    override suspend fun deleteHistory(id: String) = d.current.deleteHistory(id)

    override suspend fun deleteHistoriesByInstallmentId(installmentId: String) = d.current.deleteHistoriesByInstallmentId(installmentId)

    override fun getHistoriesByMonth(startDate: Long, endDate: Long): Flow<List<HistoryWithDetails>> = d.flatFlow { it.getHistoriesByMonth(startDate, endDate) }

    override fun getSumByMonth(startDate: Long, endDate: Long, categoryType: String): Flow<Long> = d.flatFlow { it.getSumByMonth(startDate, endDate, categoryType) }

    override suspend fun getHistoriesByInstallmentId(installmentId: String): List<HistoryEntity> = d.current.getHistoriesByInstallmentId(installmentId)

    override suspend fun searchHistories(
        query: String,
        sortOrder: String,
        startDate: Long?,
        endDate: Long?,
        largeCategories: List<String>,
        categoryIds: List<String>,
        paymentMethodIds: List<String>,
        limit: Int,
        offset: Int
    ): List<HistoryWithDetails> = d.current.searchHistories(query, sortOrder, startDate, endDate, largeCategories, categoryIds, paymentMethodIds, limit, offset)

    override suspend fun getSearchSummary(
        query: String,
        startDate: Long?,
        endDate: Long?,
        largeCategories: List<String>,
        categoryIds: List<String>,
        paymentMethodIds: List<String>
    ): Map<String, Long> = d.current.getSearchSummary(query, startDate, endDate, largeCategories, categoryIds, paymentMethodIds)
}
