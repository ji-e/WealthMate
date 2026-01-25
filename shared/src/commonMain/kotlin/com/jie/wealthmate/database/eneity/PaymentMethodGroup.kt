package com.jie.wealthmate.database.eneity

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlin.time.Clock

@Entity(tableName = "payment_method_groups")
data class PaymentMethodGroupEntity(
    @PrimaryKey val id: String,
    val label: String,
    val updatedAt: Long = Clock.System.now().toEpochMilliseconds(),
    val isDeleted: Boolean = false,
)