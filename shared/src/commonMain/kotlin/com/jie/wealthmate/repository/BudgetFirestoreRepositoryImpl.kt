package com.jie.wealthmate.repository

import com.jie.wealthmate.database.eneity.BudgetEntity
import com.jie.wealthmate.database.eneity.BudgetWithDetails
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

class BudgetFirestoreRepositoryImpl(
    private val authRepository: AuthRepository,
    private val categoryRepository: CategoryRepository
) : BudgetRepository {
    private val firestore = Firebase.firestore

    private fun getUserId(): String = authRepository.getUserName() ?: "anonymous"
    private fun getBudgetCollection() = firestore
        .collection("users")
        .document(getUserId())
        .collection("budgets")

    override suspend fun saveBudgets(yearMonth: String, budgets: List<BudgetEntity>) = withContext(Dispatchers.Default) {
        val now = Clock.System.now().toEpochMilliseconds()
        val existing = getBudgetCollection().where { "yearMonth" equalTo yearMonth }.get()
        
        firestore.runTransaction {
            existing.documents.forEach { doc ->
                val current = doc.data<BudgetEntity>()
                set(doc.reference, current.copy(isDeleted = true, updatedAt = now), encodeDefaults = true)
            }
            budgets.forEach { budget ->
                val id = budget.id.ifBlank { "${yearMonth}_${budget.categoryId}" }
                set(getBudgetCollection().document(id), budget.copy(id = id, updatedAt = now), encodeDefaults = true)
            }
        }
    }

    override suspend fun updateBudget(budget: BudgetEntity) = withContext(Dispatchers.Default) {
        val updated = budget.copy(updatedAt = Clock.System.now().toEpochMilliseconds())
        getBudgetCollection().document(budget.id).set(updated, encodeDefaults = true)
    }

    override fun getBudgetsByMonth(yearMonth: String): Flow<List<BudgetEntity>> =
        getBudgetCollection()
            .where { "yearMonth" equalTo yearMonth }
            .where { "isDeleted" equalTo false }
            .snapshots
            .map { snapshot -> snapshot.documents.map { it.data<BudgetEntity>() } }
            .flowOn(Dispatchers.Default)

    override fun getBudgetsByMonthWithDetails(yearMonth: String): Flow<List<BudgetWithDetails>> =
        combine(
            getBudgetsByMonth(yearMonth),
            categoryRepository.getAllCategories()
        ) { budgets, categories ->
            budgets.map { budget ->
                BudgetWithDetails(
                    budget = budget,
                    category = categories.find { it.id == budget.categoryId }
                )
            }
        }.flowOn(Dispatchers.Default)

    override fun getBudgetsByYearWithDetails(year: String): Flow<List<BudgetWithDetails>> =
        combine(
            getBudgetCollection()
                .where { "yearMonth" greaterThanOrEqualTo "$year-01" }
                .where { "yearMonth" lessThanOrEqualTo "$year-12" }
                .where { "isDeleted" equalTo false }
                .snapshots
                .map { snapshot -> snapshot.documents.map { it.data<BudgetEntity>() } },
            categoryRepository.getAllCategories()
        ) { budgets, categories ->
            budgets.map { budget ->
                BudgetWithDetails(
                    budget = budget,
                    category = categories.find { it.id == budget.categoryId }
                )
            }
        }.flowOn(Dispatchers.Default)

    override fun getAllYearMonths(): Flow<List<String>> =
        getBudgetCollection()
            .where { "isDeleted" equalTo false }
            .snapshots
            .map { snapshot -> 
                snapshot.documents.map { it.data<BudgetEntity>().yearMonth }.distinct().sortedDescending()
            }.flowOn(Dispatchers.Default)

    override suspend fun deleteBudgetsByMonth(yearMonth: String) = withContext(Dispatchers.Default) {
        val now = Clock.System.now().toEpochMilliseconds()
        val existing = getBudgetCollection().where { "yearMonth" equalTo yearMonth }.get()
        firestore.runTransaction {
            existing.documents.forEach { doc ->
                val current = doc.data<BudgetEntity>()
                set(doc.reference, current.copy(isDeleted = true, updatedAt = now), encodeDefaults = true)
            }
        }
    }

    // ✅ 인터페이스 미구현 오류 해결 (복원용 메서드 추가)
    override suspend fun getAllBudgetsList(): List<BudgetEntity> = withContext(Dispatchers.Default) {
        getBudgetCollection().where { "isDeleted" equalTo false }.get().documents.map { it.data() }
    }

    override suspend fun insertBudgets(budgets: List<BudgetEntity>) {
        budgets.forEach { getBudgetCollection().document(it.id).set(it, encodeDefaults = true) }
    }

    override suspend fun deleteAllBudgets() {
        getBudgetCollection().get().documents.forEach { it.reference.delete() }
    }

    override suspend fun syncRemoteToLocal() {
        // Delegate에서 비즈니스 로직 처리
    }

    override suspend fun syncLocalToRemote() {
        // Delegate에서 비즈니스 로직 처리
    }
}
