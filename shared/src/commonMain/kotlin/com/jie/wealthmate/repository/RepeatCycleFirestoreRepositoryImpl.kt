package com.jie.wealthmate.repository

import com.jie.wealthmate.database.eneity.RepeatCycleEntity
import com.jie.wealthmate.database.eneity.RepeatCycleWithDetails
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
import dev.gitlive.firebase.firestore.where
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlin.time.Clock

class RepeatCycleFirestoreRepositoryImpl(
    private val authRepository: AuthRepository,
    private val categoryRepository: CategoryRepository,
    private val paymentMethodRepository: PaymentMethodRepository,
) : RepeatCycleRepository {
    private val repoName = "RepeatCycleFirestoreRepository"
    private val firestore = Firebase.firestore

    private fun getUserId(): String = authRepository.getUserName() ?: "anonymous"
    private fun getRepeatCycleCollection() = firestore
        .collection("users")
        .document(getUserId())
        .collection("repeat_cycles")

    override suspend fun insertRepeatCycle(repeatCycle: RepeatCycleEntity) =
        withContext(Dispatchers.Default) {
            val now = Clock.System.now().toEpochMilliseconds()
            getRepeatCycleCollection().document(repeatCycle.id)
                .set(repeatCycle.copy(updatedAt = now), encodeDefaults = true)
        }

    override suspend fun updateRepeatCycle(repeatCycle: RepeatCycleEntity) =
        withContext(Dispatchers.Default) {
            val now = Clock.System.now().toEpochMilliseconds()
            getRepeatCycleCollection().document(repeatCycle.id)
                .set(repeatCycle.copy(updatedAt = now), encodeDefaults = true)
        }

    override suspend fun deactivateRepeatCycle(repeatCycleId: String) =
        withContext(Dispatchers.Default) {
            val docRef = getRepeatCycleCollection().document(repeatCycleId)
            val snapshot = docRef.get()
            if (snapshot.exists) {
                val current = snapshot.data<RepeatCycleEntity>()
                docRef.set(
                    current.copy(
                        isActive = false,
                        updatedAt = Clock.System.now().toEpochMilliseconds()
                    ), encodeDefaults = true
                )
            }
        }

    override suspend fun deleteRepeatCycle(repeatCycleId: String) =
        withContext(Dispatchers.Default) {
            val docRef = getRepeatCycleCollection().document(repeatCycleId)
            val snapshot = docRef.get()
            if (snapshot.exists) {
                val current = snapshot.data<RepeatCycleEntity>()
                docRef.set(
                    current.copy(
                        isDeleted = true,
                        updatedAt = Clock.System.now().toEpochMilliseconds()
                    ), encodeDefaults = true
                )
            }
        }

    override fun getRepeatCycles(): Flow<List<RepeatCycleEntity>> =
        getRepeatCycleCollection()
            .where { "isDeleted" equalTo false }
            .snapshots
            .map { snapshot -> snapshot.documents.map { it.data<RepeatCycleEntity>() } }
            .flowOn(Dispatchers.Default)

    override fun getRepeatCycleWithDetails(): Flow<List<RepeatCycleWithDetails>> =
        combine(
            getRepeatCycles(),
            categoryRepository.getAllCategories(),
            paymentMethodRepository.getPaymentMethods()
        ) { repeats, categories, paymentMethods ->
            repeats.map { repeat ->
                RepeatCycleWithDetails(
                    repeatCycle = repeat,
                    category = categories.find { it.id == repeat.categoryId },
                    paymentMethod = paymentMethods.find { it.paymentMethod.id == repeat.paymentMethodId }?.paymentMethod
                )
            }
        }.flowOn(Dispatchers.Default)

    override suspend fun getRepeatCycleById(repeatCycleId: String): RepeatCycleWithDetails? =
        withContext(Dispatchers.Default) {
            val snapshot = getRepeatCycleCollection().document(repeatCycleId).get()
            if (!snapshot.exists) return@withContext null
            val repeat = snapshot.data<RepeatCycleEntity>()
            RepeatCycleWithDetails(
                repeatCycle = repeat,
                category = repeat.categoryId?.let { categoryRepository.getCategoryById(it) },
                paymentMethod = repeat.paymentMethodId?.let {
                    paymentMethodRepository.getPaymentMethodById(it)?.paymentMethod
                }
            )
        }

    // ✅ 복원용 메서드들 구현
    override suspend fun getAllRepeatCyclesList(): List<RepeatCycleEntity> = withContext(Dispatchers.Default) {
        getRepeatCycleCollection().where { "isDeleted" equalTo false }.get().documents.map { it.data() }
    }

    override suspend fun insertRepeatCycles(repeatCycles: List<RepeatCycleEntity>) {
        repeatCycles.forEach { getRepeatCycleCollection().document(it.id).set(it, encodeDefaults = true) }
    }

    override suspend fun deleteAllRepeatCycles() {
        getRepeatCycleCollection().get().documents.forEach { it.reference.delete() }
    }

    override suspend fun syncRemoteToLocal() {
        // Delegate에서 비즈니스 로직 처리
    }
}
