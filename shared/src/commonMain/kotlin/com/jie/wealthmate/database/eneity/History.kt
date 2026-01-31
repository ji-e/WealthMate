package com.jie.wealthmate.database.eneity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import kotlin.time.Clock

@Entity(tableName = "histories")
data class HistoryEntity(
    @PrimaryKey val id: String,
    val largeCategory: String,
    val date: Long,
    val amount: Long,
    val installment: Long?,
    val categoryId: String?,
    val categoryTagId: String?,
    val paymentMethodId: String?,
    val content: String?,
    val updatedAt: Long = Clock.System.now().toEpochMilliseconds(),
    val isDeleted: Boolean = false,
)

data class HistoryWithDetails(
    @Embedded val history: HistoryEntity,
    @Relation(
        parentColumn = "categoryId",
        entityColumn = "id"
    )
    val category: CategoryEntity?,
    @Relation(
        parentColumn = "paymentMethodId",
        entityColumn = "id"
    )
    val paymentMethod: PaymentMethodEntity?,
)
