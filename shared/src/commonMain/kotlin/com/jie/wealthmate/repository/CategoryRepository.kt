package com.jie.wealthmate.repository

import com.jie.wealthmate.database.Category
import com.jie.wealthmate.entity.CategoryEntity


interface CategoryRepository {
    suspend fun addCategory(
        icon: String,
        largeCategory: String,
        middleLabel: String,
        tagIds: List<Long> = emptyList(),
        sort: Long,
        isFixed: Boolean,
    ): Long

    suspend fun deleteCategory(categoryId: Long)

    suspend fun getCategoryById(categoryId: Long): Category?

    suspend fun getCategoryWithTags(categoryId: Long): CategoryEntity?

    suspend fun getAllCategoriesWithTags(): List<CategoryEntity>

    suspend fun addTag(
        largeCategory: String,
        middleLabel: String,
        tagLabel: String,
    ): Long

    suspend fun deleteTag(tagId: Long)
    suspend fun addTagToCategory(
        categoryId: Long,
        tagId: Long,
    )

    suspend fun removeTagFromCategory(
        categoryId: Long,
        tagId: Long,
    )

    suspend fun removeAllTagsFromCategory(categoryId: Long)
    suspend fun updateCategoryTags(
        categoryId: Long,
        tagIds: List<Long>,
    )
}