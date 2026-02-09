package com.jie.wealthmate.repository

import com.jie.wealthmate.database.eneity.RepeatCycleEntity
import kotlinx.coroutines.flow.Flow


interface RepeatCycleRepository {

    suspend fun insertRepeatCycle(repeatCycle: RepeatCycleEntity)

    suspend fun updateRepeatCycle(repeatCycle: RepeatCycleEntity)

    suspend fun deactivateRepeatCycle(repeatCycleId: String)

    suspend fun deleteRepeatCycle(repeatCycleId: String)

    fun getRepeatCycles(): Flow<List<RepeatCycleEntity>>
}
