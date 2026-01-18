package com.jie.wealthmate.repository

import com.jie.wealthmate.database.Category
import com.jie.wealthmate.database.DatabaseDriverFactory
import com.jie.wealthmate.database.WMDatabase
import com.jie.wealthmate.entity.CategoryEntity
import com.jie.wealthmate.entity.CategoryTagEntity
import com.jie.wealthmate.utils.transformBoolean
import com.jie.wealthmate.utils.trasnformLong
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class CategoryRepositoryImpl(databaseDriverFactory: DatabaseDriverFactory) : CategoryRepository {
    private val database = WMDatabase.Companion(driver = databaseDriverFactory.createDriver())
    private val dbQuery = database.categoryQueries

    /**
     * 카테고리 추가
     * @return 추가된 카테고리의 ID
     */
    override suspend fun addCategory(
        icon: String,
        largeCategory: String,
        middleLabel: String,
        tagIds: List<Long>,
        sort: Long,
        isFixed: Boolean,
    ): Long = withContext(Dispatchers.IO) {
        println("addCategory called\n icon: $icon, largeCategory: $largeCategory, middleLabel: $middleLabel, tagIds: $tagIds, sort: $sort, isFixed: $isFixed")

        database.transactionWithResult {
            // 1. 카테고리 추가
            dbQuery.insertCategory(icon, largeCategory, middleLabel, sort, isFixed.trasnformLong())

            // 2. 추가된 카테고리 ID 가져오기
            val categoryId = dbQuery.lastInsertRowId().executeAsOne()

            // 3. 태그 관계 추가
            tagIds.forEach { tagId ->
                dbQuery.insertCategoryTagRelation(categoryId, tagId)
            }

            categoryId
        }
    }

    override suspend fun updateCategorySorts(updates: List<Pair<Long, Long>>) {
        println("updateCategorySorts called\n updates: $updates")

        database.transaction {
            updates.forEach { (id, sort) ->
                dbQuery.updateCategorySort(sort = sort, id = id)
            }
        }
    }

    /**
     * 카테고리 삭제 (관련 태그 관계도 CASCADE로 자동 삭제)
     */
    override suspend fun deleteCategory(categoryId: Long): Unit {
        println("deleteCategory called\n categoryId: $categoryId")

        removeAllTagsFromCategory(categoryId)
        withContext(Dispatchers.IO) {
            dbQuery.deleteCategory(categoryId)
        }
    }

    /**
     * 카테고리 조회
     */
    override suspend fun getCategoryById(categoryId: Long): Category? {
        val response = withContext(Dispatchers.IO) {
            dbQuery.selectCategoryById(categoryId).executeAsOneOrNull()
        }

        println("getCategoryById called\n $response")

        return response
    }

    /**
     * 카테고리와 태그 함께 조회
     */
    override suspend fun getCategoryWithTags(categoryId: Long): CategoryEntity? {
        val response = withContext(Dispatchers.IO) {
            val results = dbQuery.selectCategoryWithTags(categoryId).executeAsList()

            if (results.isEmpty()) return@withContext null

            val first = results.first()
            CategoryEntity(
                id = first.category_id,
                icon = first.icon,
                largeCategory = first.largeCategory,
                middleLabel = first.middleLabel,
                sort = first.sort,
                fixed = first.isFixed.transformBoolean(),
                tags = results.mapNotNull { row ->
                    row.tag_id?.let {
                        CategoryTagEntity(it, row.tagLabel ?: "")
                    }
                }
            )
        }

        println("getCategoryWithTags called\n $response")

        return response
    }

    /**
     * 모든 카테고리와 태그 조회
     */
    override suspend fun getAllCategoriesWithTags(): List<CategoryEntity> {
        val response = withContext(Dispatchers.IO) {
            dbQuery.selectAllCategoriesWithTags()
                .executeAsList()
                .groupBy { it.category_id }
                .map { (_, rows) ->
                    val first = rows.first()
                    CategoryEntity(
                        id = first.category_id,
                        icon = first.icon,
                        largeCategory = first.largeCategory,
                        middleLabel = first.middleLabel,
                        sort = first.sort,
                        fixed = first.isFixed.transformBoolean(),
                        tags = rows.mapNotNull { row ->
                            row.tag_id?.let {
                                CategoryTagEntity(it, row.tagLabel ?: "")
                            }
                        }
                    )
                }
        }
        println("getAllCategoriesWithTags called\n $response")

        return response
    }

    /**
     * 태그 추가
     * @return 추가된 태그의 ID
     */
    override suspend fun addTag(
        largeCategory: String,
        middleLabel: String,
        tagLabel: String,
    ): Long = withContext(Dispatchers.IO) {
        dbQuery.insertTag(largeCategory, middleLabel, tagLabel)
        dbQuery.lastInsertRowId().executeAsOne()
    }

    /**
     * 태그 삭제 (관련된 카테고리 관계도 CASCADE로 자동 삭제)
     */
    override suspend fun deleteTag(tagId: Long): Unit = withContext(Dispatchers.IO) {
        dbQuery.deleteTag(tagId)
    }

    /**
     * 카테고리에 태그 추가
     */
    override suspend fun addTagToCategory(categoryId: Long, tagId: Long): Unit =
        withContext(Dispatchers.IO) {
            dbQuery.insertCategoryTagRelation(categoryId, tagId)
        }

    /**
     * 카테고리에서 태그 제거
     */
    override suspend fun removeTagFromCategory(categoryId: Long, tagId: Long): Unit =
        withContext(Dispatchers.IO) {
            dbQuery.deleteCategoryTagRelation(categoryId, tagId)
        }

    /**
     * 카테고리의 모든 태그 제거
     */
    override suspend fun removeAllTagsFromCategory(categoryId: Long): Unit =
        withContext(Dispatchers.IO) {
            dbQuery.deleteAllTagsFromCategory(categoryId)
        }

    /**
     * 카테고리의 태그 업데이트 (기존 태그 모두 삭제 후 새로 추가)
     */
    override suspend fun updateCategoryTags(categoryId: Long, tagIds: List<Long>): Unit =
        withContext(Dispatchers.IO) {
            database.transaction {
                // 기존 태그 관계 모두 삭제
                dbQuery.deleteAllTagsFromCategory(categoryId)

                // 새 태그 관계 추가
                tagIds.forEach { tagId ->
                    dbQuery.insertCategoryTagRelation(categoryId, tagId)
                }
            }
        }
}