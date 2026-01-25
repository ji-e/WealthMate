@file:OptIn(ExperimentalTime::class)

package com.jie.wealthmate.repository

import com.benasher44.uuid.uuid4
import com.jie.wealthmate.database.dao.PaymentMethodGroupDao
import com.jie.wealthmate.database.eneity.PaymentMethodGroupEntity
import kotlinx.coroutines.flow.Flow
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class PaymentMethodRepositoryImpl(
    private val paymentMethodGroupDao: PaymentMethodGroupDao,
) : PaymentMethodRepository {
    private val repoName = "PaymentMethodRepository"
    private fun generateId(): String = uuid4().toString()

    override suspend fun insertPaymentMethodGroup(label: String) =
        loggedCall(
            repositoryName = repoName,
            methodName = "insertPaymentMethodGroup",
            params = mapOf("label" to label)
        ) {
            val paymentMethodGroup = PaymentMethodGroupEntity(
                id = generateId(),
                label = label,
                updatedAt = Clock.System.now().toEpochMilliseconds(),
                isDeleted = false
            )
            paymentMethodGroupDao.insert(paymentMethodGroup)
        }

    override suspend fun updatePaymentMethodGroup(paymentMethodGroupId: String, label: String) =
        loggedCall(
            repositoryName = repoName,
            methodName = "updatePaymentMethodGroup",
            params = mapOf("label" to label)
        ) {
            paymentMethodGroupDao.update(
                PaymentMethodGroupEntity(
                    id = paymentMethodGroupId,
                    label = label,
                    updatedAt = Clock.System.now().toEpochMilliseconds()
                )
            )
        }

    override suspend fun deletePaymentMethodGroup(paymentMethodGroupId: String) =
        loggedCall(
            repositoryName = repoName,
            methodName = "deletePaymentMethodGroup",
            params = mapOf("paymentMethodGroupId" to paymentMethodGroupId)
        ) {
            paymentMethodGroupDao.softDelete(paymentMethodGroupId)
        }

    override fun getPaymentMethodGroups(): Flow<List<PaymentMethodGroupEntity>> = loggedFlow(
        repositoryName = repoName,
        methodName = "getPaymentMethodGroups",
        params = mapOf()
    ) {
        paymentMethodGroupDao.getAll()
    }
}