package com.jie.wealthmate.usecase

import com.benasher44.uuid.uuid4
import com.jie.wealthmate.database.eneity.HistoryEntity
import com.jie.wealthmate.database.eneity.RepeatCycleEntity
import com.jie.wealthmate.repository.HistoryRepository
import com.jie.wealthmate.repository.RepeatCycleRepository
import com.jie.wealthmate.utils.default
import com.jie.wealthmate.utils.toLocalDate
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.number

class HistorySaveUseCase(
    private val historyRepository: HistoryRepository,
    private val repeatRepository: RepeatCycleRepository,
) {
    private fun generateId(): String = uuid4().toString()

    suspend operator fun invoke(
        history: HistoryEntity,
        repeatCycle: String?,
    ) {
        if (repeatCycle != null) {
            // 1. 반복 설정 정보 먼저 생성 및 저장
            val repeatCycleId = generateId()
            val dayOfWeek =
                if (repeatCycle == "WEEKLY") history.date.toLocalDate().dayOfWeek.isoDayNumber else null
            val dayOfMonth =
                if (repeatCycle == "MONTHLY") history.date.toLocalDate().month.number else null

            val repeatCycleEntity = RepeatCycleEntity(
                id = repeatCycleId,
                largeCategory = history.largeCategory,
                content = history.content.default(),
                amount = history.amount,
                repeatCycle = repeatCycle,
                dayOfWeek = dayOfWeek,
                dayOfMonth = dayOfMonth,
                startDate = history.date,
                endDate = null,
                categoryId = history.categoryId,
                paymentMethodId = history.paymentMethodId,
            )
            repeatRepository.insertRepeatCycle(repeatCycleEntity)

            // 2. 생성된 ID를 연결하여 첫 내역 저장
            val linkedHistory = history.copy(repeatCycleId = repeatCycleId)
            historyRepository.insertHistory(linkedHistory)
        } else {
            // 단일 내역만 저장
            historyRepository.insertHistory(history)
        }
    }
}
