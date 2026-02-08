package com.jie.wealthmate.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.jie.wealthmate.database.eneity.CategoryEntity
import kotlinx.coroutines.flow.Flow
import kotlin.time.Clock

@Dao
interface CategoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(category: CategoryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(categories: List<CategoryEntity>)

    @Update
    suspend fun update(category: CategoryEntity)

    @Query("UPDATE categories SET sort = :sort, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateSort(id: String, sort: Long, updatedAt: Long = Clock.System.now().toEpochMilliseconds())

    @Transaction
    suspend fun updateCategoriesSort(sortUpdates: List<Pair<String, Long>>) {
        val now = Clock.System.now().toEpochMilliseconds()
        sortUpdates.forEach { (id, sort) ->
            updateSort(id, sort, now)
        }
    }

    @Delete
    suspend fun delete(category: CategoryEntity)

    // Soft delete (isDeleted 플래그 사용)
    @Query("UPDATE categories SET isDeleted = 1, updatedAt = :updatedAt WHERE id = :id")
    suspend fun softDelete(id: String, updatedAt: Long = Clock.System.now().toEpochMilliseconds())

    // 전체 조회 (삭제되지 않은 항목만)
    @Query("SELECT * FROM categories WHERE isDeleted = 0 ORDER BY sort ASC")
    fun getAll(): Flow<List<CategoryEntity>>

    // 전체 조회 (삭제된 항목 포함)
    @Query("SELECT * FROM categories ORDER BY sort ASC")
    fun getAllIncludingDeleted(): Flow<List<CategoryEntity>>

    // 단건 조회
    @Query("SELECT * FROM categories WHERE id = :id AND isDeleted = 0 LIMIT 1")
    suspend fun getById(id: String): CategoryEntity?

    // 대분류로 조회
    @Query("SELECT * FROM categories WHERE largeCategory = :largeCategory AND isDeleted = 0 ORDER BY sort ASC")
    fun getByLargeCategory(largeCategory: String): Flow<List<CategoryEntity>>

    // 중분류로 조회
    @Query("SELECT * FROM categories WHERE middleLabel = :middleLabel AND isDeleted = 0 ORDER BY sort ASC")
    fun getByMiddleLabel(middleLabel: String): Flow<List<CategoryEntity>>

    /**
     * 특정 시점 이후에 변경된 모든 카테고리 조회 (삭제된 항목 포함)
     */
    @Query("SELECT * FROM categories WHERE updatedAt > :lastSync")
    suspend fun getChangesSince(lastSync: Long): List<CategoryEntity>

    /**
     * 클라우드 데이터를 로컬에 병합 (ID가 같으면 덮어쓰기)
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(categories: List<CategoryEntity>)

    /**
     * 모든 카테고리 삭제 (백업 복원 시 사용)
     */
    @Query("DELETE FROM categories")
    suspend fun deleteAll()
}
