package com.jie.wealthmate.repository

import com.jie.wealthmate.database.eneity.HistoryEntity
import com.jie.wealthmate.database.eneity.HistoryWithDetails
import kotlinx.coroutines.flow.Flow


interface HistoryRepository {
    fun getAllHistories(): Flow<List<HistoryEntity>>

    fun getHistoriesWithDetails(): Flow<List<HistoryWithDetails>>

    suspend fun getHistoryById(id: String): HistoryEntity?

    suspend fun insertHistory(
        largeCategory: String,
        date: Long,
        amount: Long,
        installment: Long? = null,
        categoryId: String? = null,
        categoryTagId: String? = null,
        paymentMethodId: String? = null,
        content: String? = null,
    )

    suspend fun updateHistory(history: HistoryEntity)

    suspend fun deleteHistory(id: String)

    fun getHistoriesByMonth(startDate: Long, endDate: Long): Flow<List<HistoryWithDetails>>

    fun getSumByMonth(startDate: Long, endDate: Long, categoryType: String): Flow<Long>
}