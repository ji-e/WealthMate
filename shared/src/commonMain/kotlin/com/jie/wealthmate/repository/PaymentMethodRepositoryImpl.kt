@file:OptIn(ExperimentalTime::class)

package com.jie.wealthmate.repository

import com.benasher44.uuid.uuid4
import com.jie.wealthmate.database.DatabaseProvider
import com.jie.wealthmate.database.eneity.PaymentMethodEntity
import com.jie.wealthmate.database.eneity.PaymentMethodGroupEntity
import com.jie.wealthmate.database.eneity.PaymentMethodWithGroupEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class PaymentMethodRepositoryImpl(
    private val databaseProvider: DatabaseProvider,
) : PaymentMethodRepository {
    private val repoName = "PaymentMethodRepository"
    private fun generateId(): String = uuid4().toString()
    private val paymentMethodDao get() = databaseProvider.database.paymentMethodDao()
    private val paymentMethodGroupDao get() = databaseProvider.database.paymentMethodGroupDao()

    override suspend fun insertPaymentMethod(
        paymentMethodLabel: String,
        paymentMethodGroupId: String?,
        paymentMethodGroupLabel: String?,
        sort: Long,
    ) = withContext(Dispatchers.Default) {
        loggedCall(
            repositoryName = repoName,
            methodName = "insertPaymentMethod",
            params = mapOf(
                "paymentMethodLabel" to paymentMethodLabel,
                "paymentMethodGroupId" to paymentMethodGroupId,
                "paymentMethodGroupLabel" to paymentMethodGroupLabel,
                "sort" to sort
            )
        ) {
            val paymentMethod = PaymentMethodEntity(
                id = generateId(),
                label = paymentMethodLabel,
                groupId = paymentMethodGroupId,
                groupLabel = paymentMethodGroupLabel,
                updatedAt = Clock.System.now().toEpochMilliseconds(),
                sort = sort
            )
            paymentMethodDao.insert(paymentMethod)
        }
    }

    override suspend fun updatePaymentMethod(paymentMethod: PaymentMethodEntity) = withContext(Dispatchers.Default) {
        loggedCall(
            repositoryName = repoName,
            methodName = "updatePaymentMethod",
            params = mapOf("paymentMethod" to paymentMethod)
        ) {
            paymentMethodDao.update(
                paymentMethod.copy(updatedAt = Clock.System.now().toEpochMilliseconds())
            )
        }
    }

    override suspend fun updatePaymentMethodSort(updates: List<Pair<String, Long>>) = withContext(Dispatchers.Default) {
        loggedCall(
            repositoryName = repoName,
            methodName = "updatePaymentMethodSort",
            params = mapOf("updates" to updates)
        ) {
            paymentMethodDao.updatePaymentMethodSort(updates)
        }
    }

    override suspend fun deletePaymentMethod(paymentMethodId: String) = withContext(Dispatchers.Default) {
        loggedCall(
            repositoryName = repoName,
            methodName = "deletePaymentMethod",
            params = mapOf("paymentMethodId" to paymentMethodId)
        ) {
            paymentMethodDao.softDelete(paymentMethodId)
        }
    }

    override suspend fun getPaymentMethodById(paymentMethodId: String): PaymentMethodWithGroupEntity? = withContext(Dispatchers.Default) {
        loggedCall(
            repositoryName = repoName,
            methodName = "getPaymentMethodById",
            params = mapOf("paymentMethodId" to paymentMethodId)
        ) {
            paymentMethodDao.getPaymentMethodById(paymentMethodId)
        }
    }

    override fun getPaymentMethods(): Flow<List<PaymentMethodWithGroupEntity>> = loggedFlow(
        repositoryName = repoName,
        methodName = "getPaymentMethods",
        params = mapOf()
    ) {
        paymentMethodDao.getAllPaymentMethodsWithGroup()
    }.flowOn(Dispatchers.Default)

    override suspend fun insertPaymentMethodGroup(label: String) = withContext(Dispatchers.Default) {
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
    }

    override suspend fun updatePaymentMethodGroup(
        paymentMethodGroupId: String,
        paymentMethodGroupLabel: String,
    ) = withContext(Dispatchers.Default) {
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
    }

    override suspend fun deletePaymentMethodGroup(paymentMethodGroupId: String) = withContext(Dispatchers.Default) {
        loggedCall(
            repositoryName = repoName,
            methodName = "deletePaymentMethodGroup",
            params = mapOf("paymentMethodGroupId" to paymentMethodGroupId)
        ) {
            paymentMethodGroupDao.softDelete(paymentMethodGroupId)
        }
    }

    override fun getPaymentMethodGroups(): Flow<List<PaymentMethodGroupEntity>> = loggedFlow(
        repositoryName = repoName,
        methodName = "getPaymentMethodGroups",
        params = mapOf()
    ) {
        paymentMethodGroupDao.getAll()
    }.flowOn(Dispatchers.Default)
}
