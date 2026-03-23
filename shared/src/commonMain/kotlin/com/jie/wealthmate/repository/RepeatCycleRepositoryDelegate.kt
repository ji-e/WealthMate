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

    override suspend fun insertRepeatCycle(repeatCycle: RepeatCycleEntity) =
        d.dualCall { it.insertRepeatCycle(repeatCycle) }

    override suspend fun updateRepeatCycle(repeatCycle: RepeatCycleEntity) =
        d.dualCall { it.updateRepeatCycle(repeatCycle) }

    override suspend fun deactivateRepeatCycle(repeatCycleId: String) =
        d.dualCall { it.deactivateRepeatCycle(repeatCycleId) }

    override suspend fun deleteRepeatCycle(repeatCycleId: String) =
        d.dualCall { it.deleteRepeatCycle(repeatCycleId) }

    override fun getRepeatCycles(): Flow<List<RepeatCycleEntity>> =
        d.flatFlow { it.getRepeatCycles() }

    override fun getRepeatCycleWithDetails(): Flow<List<RepeatCycleWithDetails>> =
        d.flatFlow { it.getRepeatCycleWithDetails() }

    override suspend fun getRepeatCycleById(repeatCycleId: String): RepeatCycleWithDetails? =
        d.call { it.getRepeatCycleById(repeatCycleId) }

    // ✅ 복원용 메서드들 구현
    override suspend fun getAllRepeatCyclesList(): List<RepeatCycleEntity> = d.call { it.getAllRepeatCyclesList() }
    override suspend fun insertRepeatCycles(repeatCycles: List<RepeatCycleEntity>) = d.dualCall { it.insertRepeatCycles(repeatCycles) }
    override suspend fun deleteAllRepeatCycles() = d.dualCall { it.deleteAllRepeatCycles() }

    // ✅ 핵심 복원 로직: Firestore -> Local
    override suspend fun syncRemoteToLocal() {
        d.restore { special, normal ->
            val remoteData = special.getAllRepeatCyclesList()
            if (remoteData.isNotEmpty()) {
                normal.deleteAllRepeatCycles()
                normal.insertRepeatCycles(remoteData)
            }
        }
    }
}
