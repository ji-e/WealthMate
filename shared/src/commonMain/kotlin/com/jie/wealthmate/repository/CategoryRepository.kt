package com.jie.wealthmate.repository

import com.jie.wealthmate.database.eneity.CategoryEntity
import kotlinx.coroutines.flow.Flow


interface CategoryRepository {
    suspend fun insertCategory(
        icon: String,
        largeCategory: String,
        middleLabel: String,
        sort: Long,
        isFixed: Boolean,
        tagLabels: List<String>,
    )

    suspend fun updateCategory(category: CategoryEntity)

    suspend fun updateCategoriesSort(updates: List<Pair<String, Long>>)

    suspend fun deleteCategory(categoryId: String)

    suspend fun getCategoryById(categoryId: String): CategoryEntity?

    fun getAllCategories(): Flow<List<CategoryEntity>>

    fun getCategoriesByLargeCategory(largeCategory: String): Flow<List<CategoryEntity>>

}