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

data class PaymentGroupWithMethods(
    @Embedded val group: PaymentMethodGroupEntity,
    @Relation(
        parentColumn = "id",      // PaymentMethodGroupEntity의 PK
        entityColumn = "groupId"  // PaymentMethodEntity의 FK
    )
    val methods: List<PaymentMethodEntity>
)