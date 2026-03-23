package com.jie.wealthmate.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.jie.wealthmate.database.eneity.BudgetEntity
import com.jie.wealthmate.database.eneity.BudgetWithDetails
import kotlinx.coroutines.flow.Flow

@Dao
interface BudgetDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(budgets: List<BudgetEntity>)

    @Update
    suspend fun update(budget: BudgetEntity)

    @Query("SELECT * FROM budgets WHERE yearMonth = :yearMonth AND isDeleted = 0")
    fun getBudgetsByMonth(yearMonth: String): Flow<List<BudgetEntity>>

    // ✅ 복원용 전체 리스트 조회 추가
    @Query("SELECT * FROM budgets WHERE isDeleted = 0")
    suspend fun getAllList(): List<BudgetEntity>

    @Transaction
    @Query("SELECT * FROM budgets WHERE yearMonth = :yearMonth AND isDeleted = 0")
    fun getBudgetsByMonthWithDetails(yearMonth: String): Flow<List<BudgetWithDetails>>

    @Transaction
    @Query("SELECT * FROM budgets WHERE yearMonth LIKE :year || '-%' AND isDeleted = 0")
    fun getBudgetsByYearWithDetails(year: String): Flow<List<BudgetWithDetails>>

    @Query("SELECT DISTINCT yearMonth FROM budgets WHERE isDeleted = 0")
    fun getAllYearMonths(): Flow<List<String>>

    @Query("UPDATE budgets SET isDeleted = 1, updatedAt = :updatedAt WHERE yearMonth = :yearMonth")
    suspend fun softDeleteByMonth(yearMonth: String, updatedAt: Long)

    @Query("SELECT * FROM budgets WHERE updatedAt > :lastSync")
    suspend fun getChangesSince(lastSync: Long): List<BudgetEntity>

    @Query("DELETE FROM budgets")
    suspend fun deleteAll()
}
