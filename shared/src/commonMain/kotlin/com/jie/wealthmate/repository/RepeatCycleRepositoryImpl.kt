@file:OptIn(ExperimentalTime::class)

package com.jie.wealthmate.repository

import com.benasher44.uuid.uuid4
import com.jie.wealthmate.database.DatabaseProvider
import com.jie.wealthmate.database.eneity.RepeatCycleEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class RepeatCycleRepositoryImpl(private val databaseProvider: DatabaseProvider) :
    RepeatCycleRepository {
    private val repoName = "RepeatCycleRepository"
    private fun generateId(): String = uuid4().toString()
    private val dao get() = databaseProvider.database.repeatCycleDao()

    override suspend fun insertRepeatCycle(repeatCycle: RepeatCycleEntity) = withContext(Dispatchers.Default) {
        loggedCall(
            repositoryName = repoName,
            methodName = "insertRepeatCycle",
            params = mapOf("repeatCycle" to repeatCycle)
        ) {
            val repeatCycle = repeatCycle.copy(
                id = repeatCycle.id.ifEmpty { generateId() },
                updatedAt = Clock.System.now().toEpochMilliseconds(),
            )

            dao.insert(repeatCycle)
        }
    }

    override suspend fun updateRepeatCycle(repeatCycle: RepeatCycleEntity) = withContext(Dispatchers.Default) {
        loggedCall(
            repositoryName = repoName,
            methodName = "updateRepeatCycle",
            params = mapOf("repeatCycle" to repeatCycle)
        ) {
            val repeatCycle = repeatCycle.copy(
                updatedAt = Clock.System.now().toEpochMilliseconds(),
            )

            dao.update(repeatCycle)
        }
    }

    override suspend fun deactivateRepeatCycle(repeatCycleId: String) = withContext(Dispatchers.Default) {
        loggedCall(
            repositoryName = repoName,
            methodName = "deactivateRepeatCycle",
            params = mapOf("repeatCycleId" to repeatCycleId)
        ) {
            dao.deactivate(repeatCycleId)
        }
    }

    override fun getRepeatCycles(): Flow<List<RepeatCycleEntity>> = loggedFlow(
        repositoryName = repoName,
        methodName = "getRepeatCycles",
        params = mapOf()
    ) {
        dao.getRepeatCycles()
    }.flowOn(Dispatchers.Default)

}
