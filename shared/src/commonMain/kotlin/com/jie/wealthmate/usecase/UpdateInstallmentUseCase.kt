package com.jie.wealthmate.usecase

import com.jie.wealthmate.database.eneity.HistoryEntity
import com.jie.wealthmate.database.eneity.HistoryInstallment
import com.jie.wealthmate.repository.HistoryRepository
import com.jie.wealthmate.repository.InstallmentRepository
import com.jie.wealthmate.utils.default
import com.jie.wealthmate.utils.toEpochMilliseconds
import com.jie.wealthmate.utils.toLocalDate
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.plus

class UpdateInstallmentUseCase(
    private val historyRepository: HistoryRepository,
    private val installmentRepository: InstallmentRepository,
) {
    suspend operator fun invoke(
        historyId: String,
        totalAmount: Long,
        totalCount: Long,
    ) {
        val historyWithDetails = historyRepository.getHistoryById(historyId) ?: return
        val historyEntity = historyWithDetails.history
        val installmentEntity = historyWithDetails.installment ?: return
        val installmentId = installmentEntity.id

        // 1. 할부 정보 업데이트
        val currentInstallment = installmentEntity.copy(
            amount = totalAmount,
            count = totalCount
        )
        installmentRepository.updateInstallment(currentInstallment)

        // 2. 관련 내역들 재계산
        val histories = historyRepository.getHistoriesByInstallmentId(installmentId)
            .sortedBy { it.installment?.installmentTime }

        val baseAmount = totalAmount / totalCount
        val remainder = totalAmount % totalCount

        val updatedHistories = mutableListOf<HistoryEntity>()
        val newHistories = mutableListOf<HistoryEntity>()
        val historiesToDelete = mutableListOf<HistoryEntity>()

        var runningRemainAmount = totalAmount
        var lastDate = histories.firstOrNull()?.date?.toLocalDate()
            ?: installmentEntity.startDate.toLocalDate()

        for (i in 1..totalCount) {
            val amount = if (i == 1L) baseAmount + remainder else baseAmount
            runningRemainAmount -= amount

            val existingHistory = histories.find { it.installment?.installmentTime == i }

            if (existingHistory != null) {
                updatedHistories.add(
                    existingHistory.copy(
                        amount = amount,
                        installment = existingHistory.installment?.copy(
                            installmentRemainAmount = runningRemainAmount
                        )
                    )
                )
                lastDate = existingHistory.date.toLocalDate()
            } else {
                lastDate = lastDate.plus(1, DateTimeUnit.MONTH)
                newHistories.add(
                    HistoryEntity(
                        largeCategory = historyEntity.largeCategory,
                        date = lastDate.toEpochMilliseconds(),
                        amount = amount,
                        installmentId = installmentId,
                        installment = HistoryInstallment(
                            installmentTime = i,
                            installmentRemainAmount = runningRemainAmount
                        ),
                        categoryId = historyEntity.categoryId,
                        categoryTagId = historyEntity.categoryTagId,
                        paymentMethodId = historyEntity.paymentMethodId,
                        content = historyEntity.content,
                        isVisibility = historyEntity.isVisibility
                    )
                )
            }
        }

        // totalCount가 줄어든 경우 기존 내역 삭제
        histories.forEach { history ->
            if (history.installment?.installmentTime.default() > totalCount) {
                historiesToDelete.add(history)
            }
        }

        // DB 작업
        if (updatedHistories.isNotEmpty()) {
            historyRepository.updateHistories(updatedHistories)
        }
        newHistories.forEach {
            historyRepository.insertHistory(it)
        }
        historiesToDelete.forEach {
            historyRepository.deleteHistory(it.id)
        }
    }
}
