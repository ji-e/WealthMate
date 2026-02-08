package com.jie.wealthmate.database.eneity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.benasher44.uuid.uuid4
import kotlinx.serialization.Serializable
import kotlin.time.Clock

@Serializable
data class HistoryInstallment(
    val installmentTime: Long,
    val installmentRemainAmount: Long
)

@Serializable
@Entity(tableName = "histories")
data class HistoryEntity(
    @PrimaryKey val id: String = uuid4().toString(),
    val largeCategory: String,
    val date: Long,
    val amount: Long,
    val repeatCycleId: String? = null,
    val installmentId: String? = null,
    @Embedded val installment: HistoryInstallment? = null,
    val categoryId: String? = null,
    val categoryTagId: String? = null,
    val paymentMethodId: String? = null,
    val content: String? = null,
    val isVisibility: Boolean = true,
    val userId: String? = null,
    val createdAt: Long = Clock.System.now().toEpochMilliseconds(),
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

    @Relation(
        parentColumn = "repeatCycleId",
        entityColumn = "id"
    )
    val repeatCycle: RepeatCycleEntity?,

    @Relation(
        parentColumn = "installmentId",
        entityColumn = "id"
    )
    val installment: InstallmentEntity?,
)
