package com.jie.wealthmate.usecase

import com.benasher44.uuid.uuid4
import com.jie.wealthmate.database.eneity.HistoryEntity
import com.jie.wealthmate.database.eneity.InstallmentEntity
import com.jie.wealthmate.database.eneity.RepeatCycleEntity
import com.jie.wealthmate.repository.HistoryRepository
import com.jie.wealthmate.repository.InstallmentRepository
import com.jie.wealthmate.repository.RepeatCycleRepository
import com.jie.wealthmate.utils.default
import com.jie.wealthmate.utils.toEpochMilliseconds
import com.jie.wealthmate.utils.toLocalDate
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.number
import kotlinx.datetime.plus

class HistorySaveUseCase(
    private val historyRepository: HistoryRepository,
    private val repeatRepository: RepeatCycleRepository,
    private val installmentRepository: InstallmentRepository,
) {
    private fun generateId(): String = uuid4().toString()

    suspend operator fun invoke(
        history: HistoryEntity,
        repeatCycle: String?,
        totalInstallment: Long?,
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
        } else if (totalInstallment != null && totalInstallment > 0) {
            // 1. 할부 정보 먼저 생성 및 저장
            val installmentId = generateId()
            val installmentPlan = getInstallmentPlan(
                totalAmount = history.amount,
                months = totalInstallment
            )

            val installmentEntity = InstallmentEntity(
                id = installmentId,
                content = history.content,
                amount = history.amount,
                count = totalInstallment,
                startDate = history.date,
                paymentMethodId = history.paymentMethodId,
            )
            installmentRepository.insertInstallment(installmentEntity)

            // 2. 생성된 ID를 연결하여 첫 내역 저장
            installmentPlan.forEachIndexed { index, installmentAmount ->
                historyRepository.insertHistory(
                    history.copy(
                        date = history.date.toLocalDate()
                            .plus(index, DateTimeUnit.MONTH)
                            .toEpochMilliseconds(),
                        amount = installmentAmount,
                        installmentId = installmentId,
                        installmentTime = index + 1L
                    )
                )
            }
        } else {
            // 단일 내역만 저장
            historyRepository.insertHistory(history)
        }
    }
}

/**
 * 무이자 할부 금액 리스트 반환.
 * @param totalAmount 총 결제 금액
 * @param months 할부 개월 수
 * @return 각 회차별 결제 금액 리스트
 */
fun getInstallmentPlan(totalAmount: Long, months: Long): List<Long> {
    if (months <= 0) return emptyList()

    val base = totalAmount / months    // 매달 내는 최소 원금
    val remainder = totalAmount % months // 첫 달에 몰아낼 나머지 금액

    return List(months.toInt()) { index ->
        if (index == 0) base + remainder else base
    }
}
