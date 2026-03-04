package com.jie.wealthmate.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.jie.wealthmate.database.eneity.BudgetEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BudgetDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(budgets: List<BudgetEntity>)

    @Update
    suspend fun update(budget: BudgetEntity)

    @Query("SELECT * FROM budgets WHERE yearMonth = :yearMonth AND isDeleted = 0")
    fun getBudgetsByMonth(yearMonth: String): Flow<List<BudgetEntity>>

    @Query("UPDATE budgets SET isDeleted = 1, updatedAt = :updatedAt WHERE yearMonth = :yearMonth")
    suspend fun softDeleteByMonth(yearMonth: String, updatedAt: Long)

    @Query("SELECT * FROM budgets WHERE updatedAt > :lastSync")
    suspend fun getChangesSince(lastSync: Long): List<BudgetEntity>

    @Query("DELETE FROM budgets")
    suspend fun deleteAll()
}
