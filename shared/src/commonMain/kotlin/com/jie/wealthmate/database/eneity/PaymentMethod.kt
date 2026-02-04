package com.jie.wealthmate.database.eneity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import kotlinx.serialization.Serializable
import kotlin.time.Clock

@Serializable
@Entity(tableName = "payment_method")
data class PaymentMethodEntity(
    @PrimaryKey val id: String,
    val label: String,
    val groupId: String?,
    val groupLabel: String?,
    val sort: Long,
    val assetId: String? = null,
    val updatedAt: Long = Clock.System.now().toEpochMilliseconds(),
    val isDeleted: Boolean = false,
)

data class PaymentMethodWithGroupEntity(
    @Embedded val paymentMethod: PaymentMethodEntity,
    @Relation(
        parentColumn = "groupId", // PaymentMethodEntity의 컬럼
        entityColumn = "id"       // PaymentMethodGroupEntity의 컬럼
    )
    val group: PaymentMethodGroupEntity?,
)
