package com.jie.wealthmate.database.eneity

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlin.time.Clock


@Entity(tableName = "installments")
data class InstallmentEntity(
    @PrimaryKey val id: String,
    val content: String?,
    val amount: Long,
    val count: Long,
    val startDate: Long,
    val paymentMethodId: String?,
    val updatedAt: Long = Clock.System.now().toEpochMilliseconds(),
    val isDeleted: Boolean = false,
)