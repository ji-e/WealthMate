package com.jie.wealthmate.database.eneity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.benasher44.uuid.uuid4
import kotlinx.serialization.Serializable
import kotlin.time.Clock

@Serializable
@Entity(tableName = "repeat_cycle")
data class RepeatCycleEntity(
    @PrimaryKey val id: String = uuid4().toString(),
    val largeCategory: String,
    val content: String?,
    val amount: Long,
    val repeatCycle: String,        // RepeatCycleEnum
    val dayOfWeek: Int? = null,     // WEEKLY일 때 사용 (1=월, 7=일)
    val dayOfMonth: Int? = null,    // MONTHLY일 때 사용 (1~31)
    val startDate: Long,
    val endDate: Long? = null,
    val categoryId: String?,
    val categoryTagId: String?,
    val paymentMethodId: String?,
    val isActive: Boolean = true, // 반복 중단 여부
    val updatedAt: Long = Clock.System.now().toEpochMilliseconds(),
    val isDeleted: Boolean = false,
)

data class RepeatCycleWithDetails(
    @Embedded val repeatCycle: RepeatCycleEntity,
    @Relation(
        parentColumn = "categoryId",
        entityColumn = "id"
    )
    val category: CategoryEntity?,
    
    @Relation(
        parentColumn = "paymentMethodId",
        entityColumn = "id"
    )
    val paymentMethod: PaymentMethodEntity?
) {
    val categoryTag: CategoryTagEntity?
        get() = category?.tags?.find { it.id == repeatCycle.categoryTagId }
}
