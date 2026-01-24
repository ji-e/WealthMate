@file:OptIn(ExperimentalTime::class)

package com.jie.wealthmate.repository

import com.benasher44.uuid.uuid4
import com.jie.wealthmate.database.dao.CategoryDao
import com.jie.wealthmate.database.eneity.CategoryEntity
import com.jie.wealthmate.database.eneity.CategoryTagEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class CategoryRepositoryImpl(private val dao: CategoryDao) : CategoryRepository {
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

        println("insertCategory called\n $category")

        dao.insert(category)
    }

    override suspend fun updateCategory(category: CategoryEntity) {
        println("updateCategory called\n $category")

        dao.update(category.copy(updatedAt = Clock.System.now().toEpochMilliseconds()))
    }

    override suspend fun updateCategoriesSort(updates: List<Pair<String, Long>>) {
        println("updateCategoriesSort called\n updates: $updates")

        dao.updateCategoriesSort(updates)
    }

    /**
     * 카테고리 삭제
     */
    override suspend fun deleteCategory(categoryId: String): Unit {
        println("deleteCategory called\n categoryId: $categoryId")

        dao.softDelete(categoryId)
    }

    /**
     * 카테고리 조회
     */
    override suspend fun getCategoryById(categoryId: String): CategoryEntity? {
        return dao.getById(categoryId).apply {
            println("getCategoryById called\n $this")
        }
    }

    /**
     * largeCategory에 해당하는 모든 카테고리 조회
     */
    override fun getCategoriesByLargeCategory(largeCategory: String): Flow<List<CategoryEntity>> {
        println("getCategoriesByLargeCategory called\n largeCategory: $largeCategory")

        return dao.getByLargeCategory(largeCategory).onEach { categories ->
            println("Categories loaded: ${categories.size} items")
            categories.forEach {
                println(it)
            }
        }
    }
}