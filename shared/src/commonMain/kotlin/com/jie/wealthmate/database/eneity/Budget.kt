package com.jie.wealthmate.database.eneity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.benasher44.uuid.uuid4
import kotlinx.serialization.Serializable
import kotlin.time.Clock

@Serializable
@Entity(tableName = "budgets")
data class BudgetEntity(
    @PrimaryKey val id: String = uuid4().toString(),
    val yearMonth: String, // "yyyy-MM"
    val categoryId: String,
    val categoryTagId: String? = null,
    val amount: Long,
    val updatedAt: Long = Clock.System.now().toEpochMilliseconds(),
    val isDeleted: Boolean = false,
)
