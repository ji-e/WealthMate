package com.jie.wealthmate.usecase

import com.jie.wealthmate.repository.HistoryRepository
import com.jie.wealthmate.repository.RepeatCycleRepository
import com.jie.wealthmate.utils.toEpochMilliseconds
import com.jie.wealthmate.utils.toLocalDate
import kotlinx.datetime.LocalDate
import kotlinx.datetime.isoDayNumber

class UpdateRepeatCycleUseCase(
    private val historyRepository: HistoryRepository,
    private val repeatCycleRepository: RepeatCycleRepository,
) {
    suspend operator fun invoke(
        historyId: String,
        newIsActive: Boolean,
        newRepeatCycle: String,
        newEndDate: LocalDate?,
    ) {
        val historyWithDetails = historyRepository.getHistoryById(historyId) ?: return
        val repeatCycleEntity = historyWithDetails.repeatCycle ?: return

        if (newRepeatCycle == "UNKNOWN") {
            repeatCycleRepository.deleteRepeatCycle(repeatCycleEntity.id)
            return
        }

        val updatedRepeatCycle = repeatCycleEntity.copy(
            isActive = newIsActive,
            repeatCycle = newRepeatCycle,
            endDate = newEndDate?.toEpochMilliseconds(),
            dayOfWeek = if (newRepeatCycle == "WEEKLY") repeatCycleEntity.startDate.toLocalDate().dayOfWeek.isoDayNumber else null,
            dayOfMonth = if (newRepeatCycle == "MONTHLY") repeatCycleEntity.startDate.toLocalDate().day else null,
        )
        repeatCycleRepository.updateRepeatCycle(updatedRepeatCycle)
    }
}
