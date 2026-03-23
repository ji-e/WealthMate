@file:OptIn(ExperimentalTime::class)

package com.jie.wealthmate.repository

import com.benasher44.uuid.uuid4
import com.jie.wealthmate.database.DatabaseProvider
import com.jie.wealthmate.database.eneity.InstallmentEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class InstallmentRepositoryImpl(private val databaseProvider: DatabaseProvider) :
    InstallmentRepository {
    private val repoName = "InstallmentRepository"
    private fun generateId(): String = uuid4().toString()
    private val dao get() = databaseProvider.database.installmentDao()

    override suspend fun insertInstallment(installment: InstallmentEntity) = withContext(Dispatchers.Default) {
        loggedCall(
            repositoryName = repoName,
            methodName = "insertInstallment",
            params = mapOf("installment" to installment)
        ) {
            val installment = installment.copy(
                id = installment.id.ifEmpty { generateId() },
                updatedAt = Clock.System.now().toEpochMilliseconds(),
            )

            dao.insert(installment)
        }
    }

    override suspend fun updateInstallment(installment: InstallmentEntity) = withContext(Dispatchers.Default) {
        loggedCall(
            repositoryName = repoName,
            methodName = "updateInstallment",
            params = mapOf("installment" to installment)
        ) {
            val installment = installment.copy(
                updatedAt = Clock.System.now().toEpochMilliseconds(),
            )

            dao.update(installment)
        }
    }

    override suspend fun deleteInstallment(installmentId: String) = withContext(Dispatchers.Default) {
        loggedCall(
            repositoryName = repoName,
            methodName = "deactivateInstallment",
            params = mapOf("installmentId" to installmentId)
        ) {
            dao.delete(installmentId)
        }
    }

    override fun getInstallments(): Flow<List<InstallmentEntity>> = loggedFlow(
        repositoryName = repoName,
        methodName = "getInstallments",
        params = mapOf()
    ) {
        dao.getInstallments()
    }.flowOn(Dispatchers.Default)

    // ✅ 복원용 추가 구현
    override suspend fun getAllInstallmentsList(): List<InstallmentEntity> = withContext(Dispatchers.Default) {
        dao.getAllList()
    }

    override suspend fun insertInstallments(installments: List<InstallmentEntity>) = withContext(Dispatchers.Default) {
        dao.upsertAll(installments)
    }

    override suspend fun deleteAllInstallments() = withContext(Dispatchers.Default) {
        dao.deleteAll()
    }

    override suspend fun syncRemoteToLocal() {
        // Delegate에서 처리
    }
}
