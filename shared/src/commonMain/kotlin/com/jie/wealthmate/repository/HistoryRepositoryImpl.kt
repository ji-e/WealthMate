@file:OptIn(ExperimentalTime::class)

package com.jie.wealthmate.repository

import androidx.room.RoomRawQuery
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
            val historyWithId = history.copy(
                id = generateId(),
                createdAt = Clock.System.now().toEpochMilliseconds(),
                updatedAt = Clock.System.now().toEpochMilliseconds(),
            )
            dao.insertHistory(historyWithId)
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
    ): List<HistoryWithDetails> = withContext(Dispatchers.Default) {
        loggedCall(
            repositoryName = repoName,
            methodName = "searchHistories",
            params = mapOf(
                "query" to query,
                "sortOrder" to sortOrder,
                "startDate" to startDate,
                "endDate" to endDate,
                "largeCategories" to largeCategories,
                "categoryIds" to categoryIds,
                "paymentMethodIds" to paymentMethodIds,
                "limit" to limit,
                "offset" to offset
            )
        ) {
            val sql = StringBuilder("SELECT * FROM histories WHERE isDeleted = 0")
            val binds = mutableListOf<Any?>()

            if (query.isNotBlank()) {
                sql.append(" AND (content LIKE ? OR categoryId IN (SELECT id FROM categories WHERE middleLabel LIKE ?))")
                val searchParam = "%$query%"
                binds.add(searchParam)
                binds.add(searchParam)
            }

            when {
                startDate != null && endDate != null -> {
                    sql.append(" AND date BETWEEN ? AND ?")
                    binds.add(startDate)
                    binds.add(endDate)
                }
                startDate != null -> {
                    sql.append(" AND date >= ?")
                    binds.add(startDate)
                }
                endDate != null -> {
                    sql.append(" AND date <= ?")
                    binds.add(endDate)
                }
            }

            if (largeCategories.isNotEmpty()) {
                val placeholders = largeCategories.joinToString(",") { "?" }
                sql.append(" AND largeCategory IN ($placeholders)")
                binds.addAll(largeCategories)
            }

            if (categoryIds.isNotEmpty()) {
                val hasUnset = categoryIds.any { it.startsWith("unset") }
                val realCategoryIds = categoryIds.filter { !it.startsWith("unset") }

                if (hasUnset && realCategoryIds.isEmpty()) {
                    sql.append(" AND categoryId IS NULL")
                } else if (hasUnset && realCategoryIds.isNotEmpty()) {
                    val placeholders = realCategoryIds.joinToString(",") { "?" }
                    sql.append(" AND (categoryId IN ($placeholders) OR categoryId IS NULL)")
                    binds.addAll(realCategoryIds)
                } else {
                    val placeholders = categoryIds.joinToString(",") { "?" }
                    sql.append(" AND categoryId IN ($placeholders)")
                    binds.addAll(categoryIds)
                }
            }

            if (paymentMethodIds.isNotEmpty()) {
                val hasUnset = paymentMethodIds.any { it.startsWith("unset") }
                val realPaymentMethodIds = paymentMethodIds.filter { !it.startsWith("unset") }

                if (hasUnset && realPaymentMethodIds.isEmpty()) {
                    sql.append(" AND paymentMethodId IS NULL")
                } else if (hasUnset && realPaymentMethodIds.isNotEmpty()) {
                    val placeholders = realPaymentMethodIds.joinToString(",") { "?" }
                    sql.append(" AND (paymentMethodId IN ($placeholders) OR paymentMethodId IS NULL)")
                    binds.addAll(realPaymentMethodIds)
                } else {
                    val placeholders = paymentMethodIds.joinToString(",") { "?" }
                    sql.append(" AND paymentMethodId IN ($placeholders)")
                    binds.addAll(paymentMethodIds)
                }
            }

            val orderBy = when (sortOrder) {
                "LATEST" -> "date DESC, createdAt DESC"
                "HIGH_AMOUNT" -> "amount DESC"
                "LOW_AMOUNT" -> "amount ASC"
                else -> "date DESC, createdAt DESC"
            }
            sql.append(" ORDER BY $orderBy")

            sql.append(" LIMIT ? OFFSET ?")
            binds.add(limit.toLong())
            binds.add(offset.toLong())

            val rawQuery = RoomRawQuery(sql.toString()) { statement ->
                binds.forEachIndexed { index, value ->
                    val bindIndex = index + 1
                    when (value) {
                        is String -> statement.bindText(bindIndex, value)
                        is Long -> statement.bindLong(bindIndex, value)
                        is Int -> statement.bindLong(bindIndex, value.toLong())
                        is Double -> statement.bindDouble(bindIndex, value)
                        null -> statement.bindNull(bindIndex)
                        else -> statement.bindText(bindIndex, value.toString())
                    }
                }
            }
            dao.searchHistories(rawQuery)
        }
    }
}
