@file:OptIn(ExperimentalTime::class)

package com.jie.wealthmate.repository

import com.benasher44.uuid.uuid4
import com.jie.wealthmate.database.DatabaseProvider
import com.jie.wealthmate.database.eneity.CategoryEntity
import com.jie.wealthmate.database.eneity.CategoryTagEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class CategoryRepositoryImpl(private val databaseProvider: DatabaseProvider) : CategoryRepository {
    private val repoName = "CategoryRepository"
    private fun generateId(): String = uuid4().toString()
    private val dao get() = databaseProvider.database.categoryDao()

    override suspend fun insertCategory(
        icon: String,
        largeCategory: String,
        middleLabel: String,
        sort: Long,
        isFixed: Boolean,
        tagLabels: List<String>,
    ) = withContext(Dispatchers.Default) {
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
            val tags = tagLabels.map { tagLabel ->
                CategoryTagEntity(
                    id = generateId(),
                    tagLabel = tagLabel,
                    updatedAt = Clock.System.now().toEpochMilliseconds(),
                    isDeleted = false
                )
            }

            val category = CategoryEntity(
                id = generateId(),
                icon = icon,
                largeCategory = largeCategory,
                middleLabel = middleLabel,
                sort = sort,
                isFixed = isFixed,
                tags = tags,
                updatedAt = Clock.System.now().toEpochMilliseconds(),
                isDeleted = false
            )
            dao.insert(category)
        }
    }

    override suspend fun updateCategory(category: CategoryEntity) =
        withContext(Dispatchers.Default) {
            loggedCall(
                repositoryName = repoName,
                methodName = "updateCategory",
                params = mapOf("category" to category)
            ) {
                dao.update(category.copy(updatedAt = Clock.System.now().toEpochMilliseconds()))
            }
        }

    override suspend fun updateCategoriesSort(updates: List<Pair<String, Long>>) =
        withContext(Dispatchers.Default) {
            loggedCall(
                repositoryName = repoName,
                methodName = "updateCategoriesSort",
                params = mapOf("updates" to updates)
            ) {
                dao.updateCategoriesSort(updates)
            }
        }

    override suspend fun deleteCategory(categoryId: String) = withContext(Dispatchers.Default) {
        loggedCall(
            repositoryName = repoName,
            methodName = "deleteCategory",
            params = mapOf("categoryId" to categoryId)
        ) {
            dao.softDelete(categoryId)
        }
    }

    override suspend fun getCategoryById(categoryId: String): CategoryEntity? =
        withContext(Dispatchers.Default) {
            loggedCall(
                repositoryName = repoName,
                methodName = "getCategoryById",
                params = mapOf("categoryId" to categoryId)
            ) {
                dao.getById(categoryId)
            }
        }

    override fun getAllCategories(): Flow<List<CategoryEntity>> =
        loggedFlow(
            repositoryName = repoName,
            methodName = "getCategories",
            params = mapOf()
        ) {
            dao.getAll()
        }.flowOn(Dispatchers.Default) // DB 조회 및 로그 처리를 백그라운드 스레드에서 실행


    override fun getCategoriesByLargeCategory(largeCategory: String): Flow<List<CategoryEntity>> =
        loggedFlow(
            repositoryName = repoName,
            methodName = "getCategoriesByLargeCategory",
            params = mapOf("largeCategory" to largeCategory)
        ) {
            dao.getByLargeCategory(largeCategory)
        }.flowOn(Dispatchers.Default) // DB 조회 및 로그 처리를 백그라운드 스레드에서 실행
}
