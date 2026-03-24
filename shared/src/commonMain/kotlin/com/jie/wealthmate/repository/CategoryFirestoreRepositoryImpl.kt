@file:OptIn(ExperimentalTime::class)

package com.jie.wealthmate.repository

import com.benasher44.uuid.uuid4
import com.jie.wealthmate.database.eneity.CategoryEntity
import com.jie.wealthmate.database.eneity.CategoryTagEntity
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.Direction
import dev.gitlive.firebase.firestore.firestore
import dev.gitlive.firebase.firestore.where
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class CategoryFirestoreRepositoryImpl(
    private val authRepository: AuthRepository,
) : CategoryRepository {
    private val repoName = "CategoryFirestoreRepository"
    private val firestore = Firebase.firestore

    init {
        Napier.d("[$repoName] Initialized for user: ${authRepository.getUserName()}")
    }

    private fun generateId(): String = uuid4().toString()

    private fun getUserId(): String = authRepository.getUserName() ?: "anonymous"

    private fun getCategoryCollection() = firestore
        .collection("users")
        .document(getUserId())
        .collection("categories")

    override suspend fun insertCategory(
        icon: String,
        largeCategory: String,
        middleLabel: String,
        sort: Long,
        isFixed: Boolean,
        tagLabels: List<String>,
    ) = withContext(Dispatchers.Default) {
        val id = generateId()
        val now = Clock.System.now().toEpochMilliseconds()
        val tags = tagLabels.map { label ->
            CategoryTagEntity(
                id = generateId(),
                tagLabel = label,
                updatedAt = now,
                isDeleted = false
            )
        }
        val category = CategoryEntity(
            id = id,
            icon = icon,
            largeCategory = largeCategory,
            middleLabel = middleLabel,
            sort = sort,
            isFixed = isFixed,
            tags = tags,
            updatedAt = now,
            isDeleted = false
        )
        getCategoryCollection().document(id).set(category, encodeDefaults = true)
    }

    override suspend fun updateCategory(category: CategoryEntity) =
        withContext(Dispatchers.Default) {
            val updatedCategory =
                category.copy(updatedAt = Clock.System.now().toEpochMilliseconds())
            getCategoryCollection().document(category.id)
                .set(updatedCategory, encodeDefaults = true)
        }

    override suspend fun updateCategoriesSort(updates: List<Pair<String, Long>>) =
        withContext(Dispatchers.Default) {
            firestore.runTransaction {
                val snapshotsWithNewSort = updates.map { (id, newSort) ->
                    val docRef = getCategoryCollection().document(id)
                    Triple(docRef, get(docRef), newSort)
                }
                val now = Clock.System.now().toEpochMilliseconds()
                snapshotsWithNewSort.forEach { (docRef, snapshot, newSort) ->
                    if (snapshot.exists) {
                        val current = snapshot.data<CategoryEntity>()
                        set(
                            docRef,
                            current.copy(
                                sort = newSort,
                                updatedAt = now
                            ),
                            encodeDefaults = true
                        )
                    }
                }
            }
        }

    override suspend fun deleteCategory(categoryId: String) = withContext(Dispatchers.Default) {
        val docRef = getCategoryCollection().document(categoryId)
        val snapshot = docRef.get()
        if (snapshot.exists) {
            val current = snapshot.data<CategoryEntity>()
            docRef.set(
                current.copy(
                    isDeleted = true,
                    updatedAt = Clock.System.now().toEpochMilliseconds()
                ), encodeDefaults = true
            )
        }
    }

    override suspend fun getCategoryById(categoryId: String): CategoryEntity? =
        withContext(Dispatchers.Default) {
            val snapshot = getCategoryCollection().document(categoryId).get()
            if (snapshot.exists) snapshot.data<CategoryEntity>() else null
        }

    override fun getCategoryByIdFlow(categoryId: String): Flow<CategoryEntity?> = flow {
        val docFlow = getCategoryCollection().document(categoryId).snapshots.map {
            if (it.exists) it.data<CategoryEntity>() else null
        }
        emitAll(docFlow)
    }.flowOn(Dispatchers.Default)

    override fun getAllCategories(): Flow<List<CategoryEntity>> =
        getCategoryCollection()
            .where { "isDeleted" equalTo false }
            .orderBy("sort", Direction.ASCENDING)
            .snapshots
            .map { snapshot -> snapshot.documents.map { it.data<CategoryEntity>() } }
            .flowOn(Dispatchers.Default)

    override fun getCategoriesByLargeCategory(largeCategory: String): Flow<List<CategoryEntity>> =
        getCategoryCollection()
            .where { "largeCategory" equalTo largeCategory }
            .where { "isDeleted" equalTo false }
            .orderBy("sort", Direction.ASCENDING)
            .snapshots
            .map { snapshot -> snapshot.documents.map { it.data<CategoryEntity>() } }
            .flowOn(Dispatchers.Default)

    // ✅ 복원용 추가 구현
    override suspend fun getAllCategoriesList(): List<CategoryEntity> = withContext(Dispatchers.Default) {
        val snapshot = getCategoryCollection().where { "isDeleted" equalTo false }.get()
        snapshot.documents.map { it.data() }
    }

    override suspend fun insertCategories(categories: List<CategoryEntity>) {
        // Firestore 대량 insert는 보통 writeBatch 사용 (여기서는 단순 구현)
        categories.forEach { category ->
            getCategoryCollection().document(category.id).set(category, encodeDefaults = true)
        }
    }

    override suspend fun deleteAllCategories() {
        // Firestore는 collection 전체 삭제 API가 없으므로 document 하나씩 삭제해야 함
        val snapshot = getCategoryCollection().get()
        snapshot.documents.forEach { doc -> doc.reference.delete() }
    }

    override suspend fun syncRemoteToLocal() {
        // Delegate에서 처리
    }

    override suspend fun syncLocalToRemote() {
        // Delegate에서 처리
    }
}
