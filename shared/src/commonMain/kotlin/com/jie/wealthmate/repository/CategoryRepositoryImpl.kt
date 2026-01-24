@file:OptIn(ExperimentalTime::class)

package com.jie.wealthmate.repository

import com.benasher44.uuid.uuid4
import com.jie.wealthmate.database.dao.CategoryDao
import com.jie.wealthmate.database.eneity.CategoryEntity
import com.jie.wealthmate.database.eneity.CategoryTagEntity
import kotlinx.coroutines.flow.Flow
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class CategoryRepositoryImpl(private val dao: CategoryDao) : CategoryRepository {
    private val repoName = "CategoryRepository"
    private fun generateId(): String = uuid4().toString()

    /**
     * 카테고리 추가
     */
    override suspend fun insertCategory(
        icon: String,
        largeCategory: String,
        middleLabel: String,
        sort: Long,
        isFixed: Boolean,
        tagLabels: List<String>,
    ) = loggedCall(
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
                id = generateId(), // 자동 생성
                tagLabel = tagLabel,
                updatedAt = Clock.System.now().toEpochMilliseconds(),
                isDeleted = false
            )
        }

        val category = CategoryEntity(
            id = generateId(), // 자동 생성
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

    override suspend fun updateCategory(category: CategoryEntity) = loggedCall(
        repositoryName = repoName,
        methodName = "updateCategory",
        params = mapOf(
            "category" to category
        )
    ) {
        dao.update(category.copy(updatedAt = Clock.System.now().toEpochMilliseconds()))
    }

    override suspend fun updateCategoriesSort(updates: List<Pair<String, Long>>) = loggedCall(
        repositoryName = repoName,
        methodName = "updateCategoriesSort",
        params = mapOf(
            "updates" to updates
        )
    ) {
        dao.updateCategoriesSort(updates)
    }

    /**
     * 카테고리 삭제
     */
    override suspend fun deleteCategory(categoryId: String) = loggedCall(
        repositoryName = repoName,
        methodName = "deleteCategory",
        params = mapOf(
            "categoryId" to categoryId
        )
    ) {
        dao.softDelete(categoryId)
    }

    /**
     * 카테고리 조회
     */
    override suspend fun getCategoryById(categoryId: String): CategoryEntity? = loggedCall(
        repositoryName = repoName,
        methodName = "getCategoryById",
        params = mapOf(
            "categoryId" to categoryId
        )
    )
    {
        dao.getById(categoryId)
    }

    /**
     * largeCategory에 해당하는 모든 카테고리 조회
     */
    override fun getCategoriesByLargeCategory(largeCategory: String): Flow<List<CategoryEntity>> =
        loggedFlow(
            repositoryName = repoName,
            methodName = "getCategoriesByLargeCategory",
            params = mapOf("largeCategory" to largeCategory)
        ) {
            dao.getByLargeCategory(largeCategory)
        }
}