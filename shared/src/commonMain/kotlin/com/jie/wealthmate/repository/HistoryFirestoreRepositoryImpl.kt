@file:OptIn(ExperimentalTime::class)

package com.jie.wealthmate.repository

import com.benasher44.uuid.uuid4
import com.jie.wealthmate.database.eneity.HistoryEntity
import com.jie.wealthmate.database.eneity.HistoryWithDetails
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.Direction
import dev.gitlive.firebase.firestore.firestore
import dev.gitlive.firebase.firestore.where
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class HistoryFirestoreRepositoryImpl(
    private val authRepository: AuthRepository,
    private val categoryRepository: CategoryRepository,
    private val paymentMethodRepository: PaymentMethodRepository,
    private val repeatCycleRepository: RepeatCycleRepository,
    private val installmentRepository: InstallmentRepository,
) : HistoryRepository {
    private val firestore = Firebase.firestore

    private fun generateId(): String = uuid4().toString()
    private fun getUserId(): String = authRepository.getUserName() ?: "anonymous"
    private fun getHistoryCollection() = firestore
        .collection("users")
        .document(getUserId())
        .collection("histories")

    override fun getAllHistories(): Flow<List<HistoryEntity>> =
        getHistoryCollection()
            .where { "isDeleted" equalTo false }
            .snapshots
            .map { snapshot -> snapshot.documents.map { it.data<HistoryEntity>() } }
            .flowOn(Dispatchers.Default)

    override fun getHistoriesWithDetails(): Flow<List<HistoryWithDetails>> =
        combine(
            getAllHistories(),
            categoryRepository.getAllCategories(),
            paymentMethodRepository.getPaymentMethods(),
            repeatCycleRepository.getRepeatCycles(),
            installmentRepository.getInstallments()
        ) { histories, categories, paymentMethods, repeats, installments ->
            histories.map { history ->
                HistoryWithDetails(
                    history = history,
                    category = categories.find { it.id == history.categoryId },
                    paymentMethod = paymentMethods.find { it.paymentMethod.id == history.paymentMethodId }?.paymentMethod,
                    repeatCycle = repeats.find { it.id == history.repeatCycleId },
                    installment = installments.find { it.id == history.installmentId }
                )
            }
        }.flowOn(Dispatchers.Default)

    override suspend fun getHistoryById(id: String): HistoryWithDetails? = withContext(Dispatchers.Default) {
        val snapshot = getHistoryCollection().document(id).get()
        if (!snapshot.exists) return@withContext null
        val history = snapshot.data<HistoryEntity>()
        
        HistoryWithDetails(
            history = history,
            category = history.categoryId?.let { categoryRepository.getCategoryById(it) },
            paymentMethod = history.paymentMethodId?.let { paymentMethodRepository.getPaymentMethodById(it)?.paymentMethod },
            repeatCycle = history.repeatCycleId?.let { repeatCycleRepository.getRepeatCycleById(it)?.repeatCycle },
            installment = history.installmentId?.let { installmentRepository.getAllInstallmentsList().find { i -> i.id == it } }
        )
    }

    override suspend fun insertHistory(history: HistoryEntity) = withContext(Dispatchers.Default) {
        val id = history.id.ifBlank { generateId() }
        val now = Clock.System.now().toEpochMilliseconds()
        val historyWithId = history.copy(
            id = id,
            createdAt = if (history.createdAt == 0L) now else history.createdAt,
            updatedAt = now
        )
        getHistoryCollection().document(id).set(historyWithId, encodeDefaults = true)
    }

    override suspend fun insertHistories(histories: List<HistoryEntity>) = withContext(Dispatchers.Default) {
        val now = Clock.System.now().toEpochMilliseconds()
        firestore.runTransaction {
            histories.forEach { history ->
                val id = history.id.ifBlank { generateId() }
                set(getHistoryCollection().document(id), history.copy(id = id, createdAt = if (history.createdAt == 0L) now else history.createdAt, updatedAt = now), encodeDefaults = true)
            }
        }
    }

    override suspend fun updateHistory(history: HistoryEntity) = withContext(Dispatchers.Default) {
        val updated = history.copy(updatedAt = Clock.System.now().toEpochMilliseconds())
        getHistoryCollection().document(history.id).set(updated, encodeDefaults = true)
    }

    override suspend fun updateHistories(histories: List<HistoryEntity>) = withContext(Dispatchers.Default) {
        val now = Clock.System.now().toEpochMilliseconds()
        firestore.runTransaction {
            histories.forEach { history ->
                set(getHistoryCollection().document(history.id), history.copy(updatedAt = now), encodeDefaults = true)
            }
        }
    }

    override suspend fun deleteHistory(id: String) = withContext(Dispatchers.Default) {
        val docRef = getHistoryCollection().document(id)
        val snapshot = docRef.get()
        if (snapshot.exists) {
            val current = snapshot.data<HistoryEntity>()
            docRef.set(current.copy(isDeleted = true, updatedAt = Clock.System.now().toEpochMilliseconds()), encodeDefaults = true)
        }
    }

    override suspend fun deleteHistoriesByInstallmentId(installmentId: String) = withContext(Dispatchers.Default) {
        val now = Clock.System.now().toEpochMilliseconds()
        val snapshots = getHistoryCollection().where { "installmentId" equalTo installmentId }.get()
        firestore.runTransaction {
            snapshots.documents.forEach { doc ->
                val current = doc.data<HistoryEntity>()
                set(doc.reference, current.copy(isDeleted = true, updatedAt = now), encodeDefaults = true)
            }
        }
    }

    override fun getHistoriesByMonth(startDate: Long, endDate: Long): Flow<List<HistoryWithDetails>> =
        combine(
            getHistoryCollection()
                .where { "date" greaterThanOrEqualTo startDate }
                .where { "date" lessThanOrEqualTo endDate }
                .where { "isDeleted" equalTo false }
                .orderBy("date", Direction.DESCENDING)
                .snapshots
                .map { snapshot -> snapshot.documents.map { it.data<HistoryEntity>() } },
            categoryRepository.getAllCategories(),
            paymentMethodRepository.getPaymentMethods(),
            repeatCycleRepository.getRepeatCycles(),
            installmentRepository.getInstallments()
        ) { histories, categories, paymentMethods, repeats, installments ->
            histories.map { history ->
                HistoryWithDetails(
                    history = history,
                    category = categories.find { it.id == history.categoryId },
                    paymentMethod = paymentMethods.find { it.paymentMethod.id == history.paymentMethodId }?.paymentMethod,
                    repeatCycle = repeats.find { it.id == history.repeatCycleId },
                    installment = installments.find { it.id == history.installmentId }
                )
            }
        }.flowOn(Dispatchers.Default)

    override suspend fun getHistoriesByMonthWithDeleted(startDate: Long, endDate: Long): List<HistoryWithDetails> = withContext(Dispatchers.Default) {
        val snapshots = getHistoryCollection()
            .where { "date" greaterThanOrEqualTo startDate }
            .where { "date" lessThanOrEqualTo endDate }
            .get()

        val histories = snapshots.documents.map { it.data<HistoryEntity>() }
        val categories = categoryRepository.getAllCategoriesList()
        val paymentMethods = paymentMethodRepository.getAllPaymentMethodsList()
        val repeats = repeatCycleRepository.getAllRepeatCyclesList()
        val installments = installmentRepository.getAllInstallmentsList()

        histories.map { history ->
            HistoryWithDetails(
                history = history,
                category = categories.find { it.id == history.categoryId },
                paymentMethod = paymentMethods.find { it.id == history.paymentMethodId },
                repeatCycle = repeats.find { it.id == history.repeatCycleId },
                installment = installments.find { it.id == history.installmentId }
            )
        }
    }

    override fun getSumByMonth(startDate: Long, endDate: Long, categoryType: String): Flow<Long> =
        getHistoryCollection()
            .where { "date" greaterThanOrEqualTo startDate }
            .where { "date" lessThanOrEqualTo endDate }
            .where { "largeCategory" equalTo categoryType }
            .where { "isDeleted" equalTo false }
            .snapshots
            .map { it.documents.sumOf { doc -> doc.data<HistoryEntity>().amount } }
            .flowOn(Dispatchers.Default)

    override suspend fun getHistoriesByInstallmentId(installmentId: String): List<HistoryEntity> = withContext(Dispatchers.Default) {
        getHistoryCollection()
            .where { "installmentId" equalTo installmentId }
            .where { "isDeleted" equalTo false }
            .get()
            .documents.map { it.data<HistoryEntity>() }
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
        var firestoreQuery = getHistoryCollection().where { "isDeleted" equalTo false }
        
        startDate?.let { firestoreQuery = firestoreQuery.where { "date" greaterThanOrEqualTo it } }
        endDate?.let { firestoreQuery = firestoreQuery.where { "date" lessThanOrEqualTo it } }
        
        val snapshots = firestoreQuery.get()
        val allHistories = snapshots.documents.map { it.data<HistoryEntity>() }
        
        val filtered = allHistories.filter { history ->
            val matchesQuery = query.isBlank() || history.content?.contains(query, ignoreCase = true) == true
            val matchesLargeCategory = largeCategories.isEmpty() || largeCategories.contains(history.largeCategory)
            val matchesCategory = categoryIds.isEmpty() || categoryIds.contains(history.categoryId)
            val matchesPayment = paymentMethodIds.isEmpty() || paymentMethodIds.contains(history.paymentMethodId)
            matchesQuery && matchesLargeCategory && matchesCategory && matchesPayment
        }
        
        val categories = categoryRepository.getAllCategoriesList()
        val paymentMethods = paymentMethodRepository.getAllPaymentMethodsList()
        val repeats = repeatCycleRepository.getAllRepeatCyclesList()
        val installments = installmentRepository.getAllInstallmentsList()

        filtered.drop(offset).take(limit).map { history ->
            HistoryWithDetails(
                history = history,
                category = categories.find { it.id == history.categoryId },
                paymentMethod = paymentMethods.find { it.id == history.paymentMethodId },
                repeatCycle = repeats.find { it.id == history.repeatCycleId },
                installment = installments.find { it.id == history.installmentId }
            )
        }
    }

    override suspend fun getSearchSummary(
        query: String,
        startDate: Long?,
        endDate: Long?,
        largeCategories: List<String>,
        categoryIds: List<String>,
        paymentMethodIds: List<String>
    ): Map<String, Long> = withContext(Dispatchers.Default) {
        val histories = searchHistories(query, "LATEST", startDate, endDate, largeCategories, categoryIds, paymentMethodIds, Int.MAX_VALUE, 0)
        histories.groupBy { it.history.largeCategory }
            .mapValues { it.value.sumOf { item -> item.history.amount } }
    }

    override suspend fun getAllHistoriesList(): List<HistoryEntity> = withContext(Dispatchers.Default) {
        getHistoryCollection().where { "isDeleted" equalTo false }.get().documents.map { it.data<HistoryEntity>() }
    }

    override suspend fun deleteAllHistories() {
        val snapshot = getHistoryCollection().get()
        snapshot.documents.forEach { it.reference.delete() }
    }

    override suspend fun syncRemoteToLocal() {
        // Delegate에서 처리
    }

    override suspend fun syncLocalToRemote() {
        // Delegate에서 처리
    }
}
