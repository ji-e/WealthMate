@file:OptIn(ExperimentalTime::class)

package com.jie.wealthmate.repository

import com.benasher44.uuid.uuid4
import com.jie.wealthmate.database.dao.PaymentMethodDao
import com.jie.wealthmate.database.dao.PaymentMethodGroupDao
import com.jie.wealthmate.database.eneity.PaymentMethodEntity
import com.jie.wealthmate.database.eneity.PaymentMethodGroupEntity
import kotlinx.coroutines.flow.Flow
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class PaymentMethodRepositoryImpl(
    private val paymentMethodDao: PaymentMethodDao,
    private val paymentMethodGroupDao: PaymentMethodGroupDao,
) : PaymentMethodRepository {
    private val repoName = "PaymentMethodRepository"
    private fun generateId(): String = uuid4().toString()

    override suspend fun insertPaymentMethod(
        paymentMethodLabel: String,
        paymentMethodGroupId: String?,
        sort: Long,
    ) = loggedCall(
        repositoryName = repoName,
        methodName = "insertPaymentMethod",
        params = mapOf(
            "paymentMethodLabel" to paymentMethodLabel,
            "paymentMethodGroupId" to paymentMethodGroupId,
            "sort" to sort
        )
    ) {
        val paymentMethod = PaymentMethodEntity(
            id = generateId(),
            label = paymentMethodLabel,
            groupId = paymentMethodGroupId,
            updatedAt = Clock.System.now().toEpochMilliseconds(),
            sort = sort
        )
        paymentMethodDao.insert(paymentMethod)
    }

    override suspend fun updatePaymentMethod(paymentMethod: PaymentMethodEntity) =
        loggedCall(
            repositoryName = repoName,
            methodName = "updatePaymentMethod",
            params = mapOf("paymentMethod" to paymentMethod)
        ) {
            paymentMethodDao.update(
                paymentMethod.copy(updatedAt = Clock.System.now().toEpochMilliseconds())
            )
        }

    override suspend fun updatePaymentMethodSort(updates: List<Pair<String, Long>>) = loggedCall(
        repositoryName = repoName,
        methodName = "updatePaymentMethodSort",
        params = mapOf("updates" to updates)
    ) {
        paymentMethodDao.updatePaymentMethodSort(updates)
    }

    override suspend fun deletePaymentMethod(paymentMethodId: String) = loggedCall(
        repositoryName = repoName,
        methodName = "deletePaymentMethod",
        params = mapOf("paymentMethodId" to paymentMethodId)
    ) {
        paymentMethodDao.softDelete(paymentMethodId)
    }

    override suspend fun getPaymentMethodById(paymentMethodId: String): PaymentMethodEntity? =
        loggedCall(
            repositoryName = repoName,
            methodName = "getPaymentMethodById",
            params = mapOf("paymentMethodId" to paymentMethodId)
        ) {
            paymentMethodDao.getById(paymentMethodId)
        }

    override fun getPaymentMethods(): Flow<List<PaymentMethodEntity>> = loggedFlow(
        repositoryName = repoName,
        methodName = "getPaymentMethods",
        params = mapOf()
    ) {
        paymentMethodDao.getAllPaymentMethods()
    }

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

    override suspend fun updatePaymentMethodGroup(
        paymentMethodGroupId: String,
        paymentMethodGroupLabel: String,
    ) =
        loggedCall(
            repositoryName = repoName,
            methodName = "updatePaymentMethodGroup",
            params = mapOf("label" to paymentMethodGroupLabel)
        ) {
            paymentMethodGroupDao.update(
                PaymentMethodGroupEntity(
                    id = paymentMethodGroupId,
                    label = paymentMethodGroupLabel,
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