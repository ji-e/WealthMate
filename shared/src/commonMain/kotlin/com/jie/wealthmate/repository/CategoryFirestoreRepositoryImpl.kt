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
import kotlinx.coroutines.flow.onStart
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
        Napier.d("[$repoName] insertCategory called")
        loggedCall(
            repositoryName = repoName,
            methodName = "insertCategory",
            params = mapOf(
                "icon" to icon,
                "largeCategory" to largeCategory,
                "middleLabel" to middleLabel,
                "sort" to sort,
                "isFixed" to isFixed,
                "tagLabels" to tagLabels
            )
        ) {
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
    }

    override suspend fun updateCategory(category: CategoryEntity) =
        withContext(Dispatchers.Default) {
            Napier.d("[$repoName] updateCategory called")
            loggedCall(
                repositoryName = repoName,
                methodName = "updateCategory",
                params = mapOf("category" to category)
            ) {
                val updatedCategory =
                    category.copy(updatedAt = Clock.System.now().toEpochMilliseconds())
                getCategoryCollection().document(category.id)
                    .set(updatedCategory, encodeDefaults = true)
            }
        }

    override suspend fun updateCategoriesSort(updates: List<Pair<String, Long>>) =
        withContext(Dispatchers.Default) {
            loggedCall(
                repositoryName = repoName,
                methodName = "updateCategoriesSort",
                params = mapOf("updates" to updates)
            ) {
                firestore.runTransaction {
                    updates.forEach { (id, newSort) ->
                        val docRef = getCategoryCollection().document(id)
                        val snapshot = get(docRef)
                        if (snapshot.exists) {
                            val current = snapshot.data<CategoryEntity>()
                            set(
                                docRef,
                                current.copy(
                                    sort = newSort,
                                    updatedAt = Clock.System.now().toEpochMilliseconds()
                                )
                            )
                        }
                    }
                }
            }
        }

    override suspend fun deleteCategory(categoryId: String) = withContext(Dispatchers.Default) {
        loggedCall(
            repositoryName = repoName,
            methodName = "deleteCategory",
            params = mapOf("categoryId" to categoryId)
        ) {
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
    }

    override suspend fun getCategoryById(categoryId: String): CategoryEntity? =
        withContext(Dispatchers.Default) {
            loggedCall(
                repositoryName = repoName,
                methodName = "getCategoryById",
                params = mapOf("categoryId" to categoryId)
            ) {
                val snapshot = getCategoryCollection().document(categoryId).get()
                if (snapshot.exists) snapshot.data<CategoryEntity>() else null
            }
        }

    override fun getCategoryByIdFlow(categoryId: String): Flow<CategoryEntity?> = flow {
        val docFlow = getCategoryCollection().document(categoryId).snapshots.map {
            if (it.exists) it.data<CategoryEntity>() else null
        }
        emitAll(docFlow)
    }.flowOn(Dispatchers.Default)

    override fun getAllCategories(): Flow<List<CategoryEntity>> =
        loggedFlow<List<CategoryEntity>>(
            repositoryName = repoName,
            methodName = "getAllCategories",
            params = emptyMap()
        ) {
            getCategoryCollection()
                .where { "isDeleted" equalTo false }
                .orderBy("sort", Direction.ASCENDING)
                .snapshots
                .map { snapshot -> snapshot.documents.map { it.data<CategoryEntity>() } }
        }.onStart { Napier.d("[$repoName] getAllCategories flow started") }
        .flowOn(Dispatchers.Default)

    override fun getCategoriesByLargeCategory(largeCategory: String): Flow<List<CategoryEntity>> =
        loggedFlow<List<CategoryEntity>>(
            repositoryName = repoName,
            methodName = "getCategoriesByLargeCategory",
            params = mapOf("largeCategory" to largeCategory)
        ) {
            getCategoryCollection()
                .where { "largeCategory" equalTo largeCategory }
                .where { "isDeleted" equalTo false }
                .orderBy("sort", Direction.ASCENDING)
                .snapshots
                .map { snapshot -> snapshot.documents.map { it.data<CategoryEntity>() } }
        }.flowOn(Dispatchers.Default)
}
