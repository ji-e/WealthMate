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

    fun getCategoryByIdFlow(categoryId: String): Flow<CategoryEntity?>

    fun getAllCategories(): Flow<List<CategoryEntity>>
    
    // ✅ 복원을 위해 추가
    suspend fun getAllCategoriesList(): List<CategoryEntity>
    suspend fun insertCategories(categories: List<CategoryEntity>)
    suspend fun deleteAllCategories()

    fun getCategoriesByLargeCategory(largeCategory: String): Flow<List<CategoryEntity>>

    // ✅ 공통 복원 로직
    suspend fun syncRemoteToLocal()
}
