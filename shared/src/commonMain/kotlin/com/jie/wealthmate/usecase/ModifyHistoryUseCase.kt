package com.jie.wealthmate.usecase

import com.jie.wealthmate.database.eneity.HistoryEntity
import com.jie.wealthmate.database.eneity.HistoryInstallment
import com.jie.wealthmate.repository.HistoryRepository
import com.jie.wealthmate.repository.RepeatCycleRepository
import com.jie.wealthmate.utils.default
import com.jie.wealthmate.utils.toEpochMilliseconds
import kotlinx.datetime.LocalDate

class ModifyHistoryUseCase(
    private val historyRepository: HistoryRepository,
    private val repeatCycleRepository: RepeatCycleRepository,
) {
    suspend operator fun invoke(
        historyId: String,
        newAmount: Long,
        newDate: LocalDate,
        newCategoryId: String?,
        newCategoryTagId: String?,
        newPaymentMethodId: String?,
        newContent: String?,
        isVisibility: Boolean,
    ) {
        val historyWithDetails = historyRepository.getHistoryById(historyId) ?: return

        historyWithDetails.repeatCycle?.let { repeatCycle ->
            repeatCycleRepository.updateRepeatCycle(
                repeatCycle.copy(
                    amount = newAmount,
                    categoryId = newCategoryId,
                    categoryTagId = newCategoryTagId,
                    paymentMethodId = newPaymentMethodId,
                    content = newContent,
//                    dayOfWeek = if (repeatCycle.repeatCycle == "WEEKLY") newDate.dayOfWeek.isoDayNumber else null,
//                    dayOfMonth = if (repeatCycle.repeatCycle == "MONTHLY") newDate.day else null,
                )
            )
        }

        val historyEntity = historyWithDetails.history
        val installmentId = historyEntity.installmentId
        val installmentTime = historyEntity.installment?.installmentTime

        if (installmentId != null && installmentTime != null) {
            val installmentHistoryItems =
                historyRepository.getHistoriesByInstallmentId(installmentId)
            val totalAmount = historyWithDetails.installment?.amount.default()

            val precedingSum = installmentHistoryItems
                .filter { it.installment?.installmentTime != null && it.installment.installmentTime < installmentTime }
                .sumOf { it.amount }

            val succeedingHistories = installmentHistoryItems
                .filter { it.installment?.installmentTime != null && it.installment.installmentTime > installmentTime }
                .sortedBy { it.installment!!.installmentTime }

            val currentRemainAmount = totalAmount - precedingSum - newAmount

            val historiesToUpdate = mutableListOf<HistoryEntity>()

            // 현재 수정 중인 내역
            historiesToUpdate.add(
                historyEntity.copy(
                    date = newDate.toEpochMilliseconds(),
                    amount = newAmount,
                    installment = HistoryInstallment(
                        installmentTime = installmentTime,
                        installmentRemainAmount = currentRemainAmount
                    ),
                    categoryId = newCategoryId,
                    categoryTagId = newCategoryTagId,
                    paymentMethodId = newPaymentMethodId,
                    content = newContent,
                    isVisibility = isVisibility
                )
            )

            // 이후 회차 내역들 금액 재계산 및 업데이트 목록 추가
            if (succeedingHistories.isNotEmpty()) {
                val count = succeedingHistories.size.toLong()
                val base = currentRemainAmount / count
                val remainder = currentRemainAmount % count

                var runningRemainAmount = currentRemainAmount
                succeedingHistories.forEachIndexed { index, entity ->
                    val redistributedAmount = if (index == 0) base + remainder else base
                    runningRemainAmount -= redistributedAmount
                    historiesToUpdate.add(
                        entity.copy(
                            amount = redistributedAmount,
                            installment = entity.installment?.copy(
                                installmentRemainAmount = runningRemainAmount
                            )
                        )
                    )
                }
            }

            historyRepository.updateHistories(historiesToUpdate)
        } else {
            historyRepository.updateHistory(
                historyEntity.copy(
                    date = newDate.toEpochMilliseconds(),
                    amount = newAmount,
                    categoryId = newCategoryId,
                    categoryTagId = newCategoryTagId,
                    paymentMethodId = newPaymentMethodId,
                    content = newContent,
                    isVisibility = isVisibility
                )
            )
        }
    }
}
