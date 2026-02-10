@file:OptIn(ExperimentalTime::class)

package com.jie.wealthmate.repository

import com.benasher44.uuid.uuid4
import com.jie.wealthmate.database.DatabaseProvider
import com.jie.wealthmate.database.eneity.HistoryEntity
import com.jie.wealthmate.database.eneity.HistoryWithDetails
import com.jie.wealthmate.utils.default
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlin.time.Clock
import kotlin.time.ExperimentalTime


class HistoryRepositoryImpl(private val databaseProvider: DatabaseProvider) : HistoryRepository {
    private val repoName = "HistoryRepository"
    private fun generateId(): String = uuid4().toString()
    private val dao get() = databaseProvider.database.historyDao()

    override fun getAllHistories(): Flow<List<HistoryEntity>> = loggedFlow(
        repositoryName = repoName,
        methodName = "getAllHistories",
        params = emptyMap()
    ) {
        dao.getAllHistories()
    }.flowOn(Dispatchers.Default)

    override fun getHistoriesWithDetails(): Flow<List<HistoryWithDetails>> = loggedFlow(
        repositoryName = repoName,
        methodName = "getHistoriesWithDetails",
        params = emptyMap()
    ) {
        dao.getHistoriesWithDetails()
    }.flowOn(Dispatchers.Default)

    override suspend fun getHistoryById(id: String): HistoryWithDetails? = withContext(Dispatchers.Default) {
        loggedCall(
            repositoryName = repoName,
            methodName = "getHistoryById",
            params = mapOf("id" to id)
        ) {
            dao.getHistoryById(id)
        }
    }

    override suspend fun insertHistory(history: HistoryEntity) = withContext(Dispatchers.Default) {
        loggedCall(
            repositoryName = repoName,
            methodName = "insertHistory",
            params = mapOf("history" to history)
        ) {
            val history = history.copy(
                id = generateId(),
                createdAt = Clock.System.now().toEpochMilliseconds(),
                updatedAt = Clock.System.now().toEpochMilliseconds(),
            )
            dao.insertHistory(history)
        }
    }

    override suspend fun insertHistories(histories: List<HistoryEntity>) = withContext(Dispatchers.Default) {
        loggedCall(
            repositoryName = repoName,
            methodName = "insertHistories",
            params = mapOf("histories" to histories)
        ) {
            val timestamp = Clock.System.now().toEpochMilliseconds()
            val historiesWithIds = histories.map {
                it.copy(
                    id = generateId(),
                    createdAt = timestamp,
                    updatedAt = timestamp,
                )
            }
            dao.insertHistories(historiesWithIds)
        }
    }

    override suspend fun updateHistory(history: HistoryEntity) = withContext(Dispatchers.Default) {
        loggedCall(
            repositoryName = repoName,
            methodName = "updateHistory",
            params = mapOf("history" to history)
        ) {
            dao.updateHistory(
                history.copy(updatedAt = Clock.System.now().toEpochMilliseconds())
            )
        }
    }

    override suspend fun updateHistories(histories: List<HistoryEntity>) = withContext(Dispatchers.Default) {
        loggedCall(
            repositoryName = repoName,
            methodName = "updateHistories",
            params = mapOf("histories" to histories)
        ) {
            val timestamp = Clock.System.now().toEpochMilliseconds()
            histories.forEach { history ->
                dao.updateHistory(history.copy(updatedAt = timestamp))
            }
        }
    }

    override suspend fun deleteHistory(id: String) = withContext(Dispatchers.Default) {
        loggedCall(
            repositoryName = repoName,
            methodName = "deleteHistory",
            params = mapOf("id" to id)
        ) {
            dao.softDeleteHistory(id)
        }
    }

    override suspend fun deleteHistoriesByInstallmentId(installmentId: String) = withContext(Dispatchers.Default) {
        loggedCall(
            repositoryName = repoName,
            methodName = "deleteHistoriesByInstallmentId",
            params = mapOf("installmentId" to installmentId)
        ) {
            val timestamp = Clock.System.now().toEpochMilliseconds()
            dao.getHistoriesByInstallmentId(installmentId).forEach {
                dao.softDeleteHistory(it.id, timestamp)
            }
        }
    }

    override fun getHistoriesByMonth(
        startDate: Long,
        endDate: Long,
    ): Flow<List<HistoryWithDetails>> =
        loggedFlow(
            repositoryName = repoName,
            methodName = "getHistoriesByMonth",
            params = mapOf("startDate" to startDate, "endDate" to endDate)
        ) {
            dao.getHistoriesByMonth(startDate, endDate)
        }.flowOn(Dispatchers.Default)

    override fun getSumByMonth(startDate: Long, endDate: Long, categoryType: String): Flow<Long> =
        loggedFlow(
            repositoryName = repoName,
            methodName = "getSumByMonth",
            params = mapOf("startDate" to startDate, "endDate" to endDate, "type" to categoryType)
        ) {
            dao.getSumByMonth(startDate, endDate, categoryType).map { it.default() }
        }.flowOn(Dispatchers.Default)

    override suspend fun getHistoriesByInstallmentId(installmentId: String): List<HistoryEntity> = withContext(Dispatchers.Default) {
        loggedCall(
            repositoryName = repoName,
            methodName = "getHistoriesByInstallmentId",
            params = mapOf("installmentId" to installmentId)
        ) {
            dao.getHistoriesByInstallmentId(installmentId)
        }
    }
}
