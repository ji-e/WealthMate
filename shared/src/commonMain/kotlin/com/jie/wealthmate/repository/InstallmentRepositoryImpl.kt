@file:OptIn(ExperimentalTime::class)

package com.jie.wealthmate.repository

import com.benasher44.uuid.uuid4
import com.jie.wealthmate.database.dao.InstallmentDao
import com.jie.wealthmate.database.eneity.InstallmentEntity
import kotlinx.coroutines.flow.Flow
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class InstallmentRepositoryImpl(private val dao: InstallmentDao) : InstallmentRepository {
    private val repoName = "InstallmentRepository"
    private fun generateId(): String = uuid4().toString()

    override suspend fun insertInstallment(installment: InstallmentEntity) = loggedCall(
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

    override suspend fun updateInstallment(installment: InstallmentEntity) = loggedCall(
        repositoryName = repoName,
        methodName = "updateInstallment",
        params = mapOf("installment" to installment)
    ) {
        val installment = installment.copy(
            updatedAt = Clock.System.now().toEpochMilliseconds(),
        )

        dao.update(installment)
    }

    override suspend fun deleteInstallment(installmentId: String) = loggedCall(
        repositoryName = repoName,
        methodName = "deactivateInstallment",
        params = mapOf("installmentId" to installmentId)
    ) {
        dao.delete(installmentId)
    }

    override fun getInstallments(): Flow<List<InstallmentEntity>> = loggedFlow(
        repositoryName = repoName,
        methodName = "getInstallments",
        params = mapOf()
    ) {
        dao.getInstallments()
    }

}