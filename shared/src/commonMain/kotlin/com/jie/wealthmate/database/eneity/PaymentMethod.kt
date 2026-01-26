package com.jie.wealthmate.database.eneity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import kotlin.time.Clock

@Entity(tableName = "payment_method")
data class PaymentMethodEntity(
    @PrimaryKey val id: String,
    val label: String,
    val groupId: String?,
    val sort: Long,
    val updatedAt: Long = Clock.System.now().toEpochMilliseconds(),
    val isDeleted: Boolean = false,
)

data class PaymentMethodWithGroupEntity(
    @Embedded val paymentMethod: PaymentMethodEntity,
    @Relation(
        parentColumn = "groupId", // PaymentMethodEntity의 컬럼
        entityColumn = "id"       // PaymentMethodGroupEntity의 컬럼
    )
    val group: PaymentMethodGroupEntity?
)