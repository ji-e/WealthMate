package com.jie.wealthmate.database.eneity

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlin.time.Clock


@Entity(tableName = "repeat_cycle")
data class RepeatCycleEntity(
    @PrimaryKey val id: String,
    val largeCategory: String,
    val content: String?,
    val amount: Long,
    val repeatCycle: String,        // RepeatCycleEnum
    val dayOfWeek: Int? = null,     // WEEKLY일 때 사용 (1=월, 7=일)
    val dayOfMonth: Int? = null,    // MONTHLY일 때 사용 (1~31)
    val startDate: Long,
    val endDate: Long? = null,
    val categoryId: String?,
    val paymentMethodId: String?,
    val isActive: Boolean = true, // 반복 중단 여부
    val updatedAt: Long = Clock.System.now().toEpochMilliseconds(),
)