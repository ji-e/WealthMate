@file:OptIn(ExperimentalTime::class)

package com.jie.wealthmate.repository

import com.benasher44.uuid.uuid4
import com.jie.wealthmate.database.dao.HistoryDao
import com.jie.wealthmate.database.eneity.HistoryEntity
import com.jie.wealthmate.database.eneity.HistoryWithDetails
import com.jie.wealthmate.utils.default
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.time.Clock
import kotlin.time.ExperimentalTime


class HistoryRepositoryImpl(private val dao: HistoryDao) : HistoryRepository {
    private val repoName = "HistoryRepository"
    private fun generateId(): String = uuid4().toString()

    /**
     * 모든 내역 조회 (Flow)
     */
    override fun getAllHistories(): Flow<List<HistoryEntity>> = loggedFlow(
        repositoryName = repoName,
        methodName = "getAllHistories",
        params = emptyMap()
    ) {
        dao.getAllHistories()
    }

    /**
     * 상세 정보(카테고리, 결제수단 관계 포함) 조회
     */
    override fun getHistoriesWithDetails(): Flow<List<HistoryWithDetails>> = loggedFlow(
        repositoryName = repoName,
        methodName = "getHistoriesWithDetails",
        params = emptyMap()
    ) {
        dao.getHistoriesWithDetails()
    }

    /**
     * ID를 통한 단일 내역 조회
     */
    override suspend fun getHistoryById(id: String): HistoryWithDetails? = loggedCall(
        repositoryName = repoName,
        methodName = "getHistoryById",
        params = mapOf("id" to id)
    ) {
        dao.getHistoryById(id)
    }

    /**
     * 내역 추가 (Entity 생성 및 ID 발급 로직 포함)
     */
    override suspend fun insertHistory(history: HistoryEntity) = loggedCall(
        repositoryName = repoName,
        methodName = "insertHistory",
        params = mapOf("history" to history)
    ) {
        val history = history.copy(
            id = generateId(),
            updatedAt = Clock.System.now().toEpochMilliseconds(),
        )
        dao.insertHistory(history)
    }

    /**
     * 내역 업데이트
     */
    override suspend fun updateHistory(history: HistoryEntity) = loggedCall(
        repositoryName = repoName,
        methodName = "updateHistory",
        params = mapOf("history" to history)
    ) {
        dao.updateHistory(
            history.copy(updatedAt = Clock.System.now().toEpochMilliseconds())
        )
    }

    /**
     * 논리적 삭제 (Soft Delete)
     */
    override suspend fun deleteHistory(id: String) = loggedCall(
        repositoryName = repoName,
        methodName = "deleteHistory",
        params = mapOf("id" to id)
    ) {
        dao.softDeleteHistory(id)
    }

    /**
     * 월별 기간 조회
     */
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
        }

    /**
     * 월별 통계 (합계) - Null 처리 포함
     */
    override fun getSumByMonth(startDate: Long, endDate: Long, categoryType: String): Flow<Long> =
        loggedFlow(
            repositoryName = repoName,
            methodName = "getSumByMonth",
            params = mapOf("startDate" to startDate, "endDate" to endDate, "type" to categoryType)
        ) {
            // DAO에서 null이 반환될 경우 0L로 치환
            dao.getSumByMonth(startDate, endDate, categoryType).map { it.default() }
        }
}