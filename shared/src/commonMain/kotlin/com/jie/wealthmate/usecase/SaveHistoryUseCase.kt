package com.jie.wealthmate.usecase

import com.benasher44.uuid.uuid4
import com.jie.wealthmate.database.eneity.HistoryEntity
import com.jie.wealthmate.database.eneity.HistoryInstallment
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

class SaveHistoryUseCase(
    private val historyRepository: HistoryRepository,
    private val repeatRepository: RepeatCycleRepository,
    private val installmentRepository: InstallmentRepository,
) {
    private fun generateId(): String = uuid4().toString()

    suspend operator fun invoke(
        history: HistoryEntity,
        repeatCycle: String?,
        totalInstallmentCount: Long?,
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
                categoryTagId = history.categoryTagId,
                paymentMethodId = history.paymentMethodId,
            )
            repeatRepository.insertRepeatCycle(repeatCycleEntity)

            // 2. 생성된 ID를 연결하여 첫 내역 저장
            val linkedHistory = history.copy(repeatCycleId = repeatCycleId)
            historyRepository.insertHistory(linkedHistory)
        } else if (totalInstallmentCount != null && totalInstallmentCount > 0) {
            // 1. 할부 정보 먼저 생성 및 저장
            val installmentId = generateId()
            val installmentPlan = getInstallmentPlan(
                totalAmount = history.amount,
                months = totalInstallmentCount
            )

            val installmentEntity = InstallmentEntity(
                id = installmentId,
                content = history.content,
                amount = history.amount,
                count = totalInstallmentCount,
                startDate = history.date,
                paymentMethodId = history.paymentMethodId,
            )
            installmentRepository.insertInstallment(installmentEntity)

            // 2. 생성된 ID를 연결하여 내역 저장
            var remainAmount = history.amount
            installmentPlan.forEachIndexed { index, installmentAmount ->
                remainAmount -= installmentAmount
                historyRepository.insertHistory(
                    history.copy(
                        date = history.date.toLocalDate()
                            .plus(index, DateTimeUnit.MONTH)
                            .toEpochMilliseconds(),
                        amount = installmentAmount,
                        installmentId = installmentId,
                        installment = HistoryInstallment(
                            installmentTime = index + 1L,
                            installmentRemainAmount = remainAmount
                        )
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
 * 특정 회차의 금액이 지정된 경우, 이전 회차는 기본값으로 유지하고 이후 회차에서 남은 금액을 분할합니다.
 *
 * @param totalAmount 총 결제 금액
 * @param months 할부 개월 수
 * @param currentAmount 현재 확인 중인 회차의 금액 (선택)
 * @param currentTime 현재 확인 중인 회차 (1부터 시작, 선택)
 * @return 각 회차별 결제 금액 리스트
 */
fun getInstallmentPlan(
    totalAmount: Long,
    months: Long,
    currentAmount: Long? = null,
    currentTime: Long? = null
): List<Long> {
    if (months <= 0) return emptyList()

    // 특정 회차 정보가 없으면 기본 균등 분할 (첫 달에 나머지 몰아주기)
    if (currentAmount == null || currentTime == null || currentTime < 1 || currentTime > months) {
        val base = totalAmount / months
        val remainder = totalAmount % months
        return List(months.toInt()) { index ->
            if (index == 0) base + remainder else base
        }
    }

    val defaultBase = totalAmount / months
    val defaultRemainder = totalAmount % months
    val plan = MutableList(months.toInt()) { 0L }
    var sumSoFar = 0L

    // 1. 현재 회차 이전은 기본 할부금으로 채움
    for (i in 0 until (currentTime.toInt() - 1)) {
        val amount = if (i == 0) defaultBase + defaultRemainder else defaultBase
        plan[i] = amount
        sumSoFar += amount
    }

    // 2. 현재 회차 금액 설정
    plan[currentTime.toInt() - 1] = currentAmount
    sumSoFar += currentAmount

    // 3. 남은 금액을 이후 회차들에 균등 분할
    val remainingAmount = totalAmount - sumSoFar
    val remainingMonths = months.toInt() - currentTime.toInt()

    if (remainingMonths > 0) {
        val baseRemaining = remainingAmount / remainingMonths
        val remainderRemaining = remainingAmount % remainingMonths

        for (i in currentTime.toInt() until months.toInt()) {
            val amount = if (i == currentTime.toInt()) baseRemaining + remainderRemaining else baseRemaining
            plan[i] = amount
        }
    }

    return plan
}
