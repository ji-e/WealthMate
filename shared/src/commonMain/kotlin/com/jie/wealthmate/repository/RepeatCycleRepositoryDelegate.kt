package com.jie.wealthmate.repository

import com.jie.wealthmate.account.AccountAwareDelegate
import com.jie.wealthmate.account.AccountProvider
import com.jie.wealthmate.database.eneity.RepeatCycleEntity
import com.jie.wealthmate.database.eneity.RepeatCycleWithDetails
import kotlinx.coroutines.flow.Flow

class RepeatCycleRepositoryDelegate(
    accountProvider: AccountProvider,
    private val localRepository: RepeatCycleRepository,
    private val firestoreRepository: RepeatCycleRepository,
) : RepeatCycleRepository {
    private val d = AccountAwareDelegate(
        special = firestoreRepository,
        normal = localRepository,
        accountProvider = accountProvider,
    )

    override suspend fun insertRepeatCycle(repeatCycle: RepeatCycleEntity) = d.current.insertRepeatCycle(repeatCycle)

    override suspend fun updateRepeatCycle(repeatCycle: RepeatCycleEntity) = d.current.updateRepeatCycle(repeatCycle)

    override suspend fun deactivateRepeatCycle(repeatCycleId: String) = d.current.deactivateRepeatCycle(repeatCycleId)

    override suspend fun deleteRepeatCycle(repeatCycleId: String) = d.current.deleteRepeatCycle(repeatCycleId)

    override fun getRepeatCycles(): Flow<List<RepeatCycleEntity>> = d.flatFlow { it.getRepeatCycles() }

    override fun getRepeatCycleWithDetails(): Flow<List<RepeatCycleWithDetails>> = d.flatFlow { it.getRepeatCycleWithDetails() }

    override suspend fun getRepeatCycleById(repeatCycleId: String): RepeatCycleWithDetails? = d.current.getRepeatCycleById(repeatCycleId)
}
