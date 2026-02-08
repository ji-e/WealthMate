package com.jie.wealthmate.feature.calendar.historyDetail

import androidx.compose.ui.text.input.TextFieldValue
import com.jie.wealthmate.base.BaseScreenModel
import com.jie.wealthmate.database.eneity.HistoryEntity
import com.jie.wealthmate.database.eneity.HistoryInstallment
import com.jie.wealthmate.feature.calendar.addHistory.component.RepeatCycleEnum
import com.jie.wealthmate.feature.menu.management.categoryManagement.component.LargeCategoryEnum
import com.jie.wealthmate.repository.CategoryRepository
import com.jie.wealthmate.repository.HistoryRepository
import com.jie.wealthmate.repository.PaymentMethodRepository
import com.jie.wealthmate.utils.default
import com.jie.wealthmate.utils.formatRemoveCommas
import com.jie.wealthmate.utils.toEpochMilliseconds
import com.jie.wealthmate.vo.CategoryTagVo
import com.jie.wealthmate.vo.CategoryVo
import com.jie.wealthmate.vo.HistoryVo.Companion.mapperToVo
import com.jie.wealthmate.vo.PaymentMethodVo
import kotlinx.datetime.LocalDate

class HistoryDetailScreenModel(
    private val categoryRepository: CategoryRepository,
    private val paymentMethodRepository: PaymentMethodRepository,
    private val historyRepository: HistoryRepository,
) : BaseScreenModel<HistoryDetailUiState>() {

    override val initialState: HistoryDetailUiState
        get() = HistoryDetailUiState()


    fun updateInit(historyId: String) {
        getHistory(historyId)
        getPaymentMethods()
    }

    fun updateDate(date: LocalDate) {
        reduceState { state ->
            state.copy(
                isDataChanged = true,
                date = date
            )
        }
    }

    fun updateRepeatCycle(repeatCycle: RepeatCycleEnum?) {
        reduceState { state ->
            state.copy(
                isDataChanged = true,
                repeatCycle = repeatCycle
            )
        }
    }

    fun updateInstallmentCount(installmentCount: Long?) {
        reduceState { state ->
            state.copy(
                isDataChanged = true,
                totalInstallmentCount = installmentCount
            )
        }
    }

    fun updateAmount(amount: TextFieldValue) {
        reduceState { state ->
            state.copy(
                isDataChanged = true,
                amount = amount
            )
        }
    }

    fun updateCategory(category: CategoryVo?) {
        reduceState { state ->
            state.copy(
                isDataChanged = true,
                category = category
            )
        }
    }

    fun updateCategoryTag(categoryTag: CategoryTagVo?) {
        reduceState { state ->
            state.copy(
                isDataChanged = true,
                categoryTag = categoryTag
            )
        }
    }

    fun updatePaymentMethod(paymentMethod: PaymentMethodVo?) {
        reduceState { state ->
            state.copy(
                isDataChanged = true,
                paymentMethod = paymentMethod
            )
        }
    }

    fun updateContent(content: TextFieldValue) {
        reduceState { state ->
            state.copy(
                isDataChanged = true,
                content = content
            )
        }
    }

    fun getHistory(historyId: String) {
        launchSafe(
            block = {
                historyRepository.getHistoryById(historyId)
            }
        ) { response ->
            val history = response.mapperToVo()
            reduceState { state ->
                state.copy(
                    history = history,
                    date = history.date,
                    amount = TextFieldValue(history.amount.toString()),
                    category = history.category,
                    categoryTag = history.categoryTag,
                    paymentMethod = history.paymentMethod,
                    content = TextFieldValue(history.content.default()),
                )
            }
            getInstallmentHistory(history.installment?.id)
            getCategories(history.largeCategory)
            println(response)
        }
    }

    fun getInstallmentHistory(installmentId: String?) {
        installmentId ?: return

        launchSafe(
            block = {
                historyRepository.getHistoriesByInstallmentId(installmentId)
            }
        ) { response ->
            reduceState { state ->
                state.copy(
                    installmentHistoryItems = response
                )
            }
        }
    }

    private fun getCategories(largeCategoryEnum: LargeCategoryEnum) {
        categoryRepository.getCategoriesByLargeCategory(largeCategoryEnum.name)
            .apiFlow { response ->
                reduceState { state ->
                    state.copy(
                        categoryItems = response.map {
                            CategoryVo(
                                id = it.id,
                                icon = it.icon,
                                largeCategory = LargeCategoryEnum.creator(it.largeCategory),
                                middleLabel = it.middleLabel,
                                sort = it.sort,
                                isFixed = it.isFixed,
                                tags = it.tags.map { tag ->
                                    CategoryTagVo(
                                        id = tag.id,
                                        label = tag.tagLabel
                                    )
                                }

                            )
                        }
                    )
                }
            }
    }

    private fun getPaymentMethods() {
        paymentMethodRepository.getPaymentMethods()
            .apiFlow { response ->
                println("response: $response")
                reduceState { state ->
                    state.copy(
                        paymentMethodItems = response.map {
                            PaymentMethodVo(
                                id = it.paymentMethod.id,
                                label = it.paymentMethod.label,
                                groupId = it.group?.id,
                                groupLabel = it.group?.label,
                                sort = it.paymentMethod.sort
                            )
                        }
                    )
                }
            }
    }

    fun saveHistory() {
        val uiState = container.uiState.value
        val historyVo = uiState.history ?: return
        val newAmount = uiState.amount.text.formatRemoveCommas().toLongOrNull() ?: 0L

        launchSafe(
            block = {
                val installmentId = historyVo.installment?.id
                val installmentTime = historyVo.installmentTime

                if (installmentId != null && installmentTime != null) {
                    val installmentHistoryItems = uiState.installmentHistoryItems
                    val totalAmount = historyVo.installment.amount.default()

                    val precedingSum = installmentHistoryItems
                        .filter { it.installment?.installmentTime != null && it.installment?.installmentTime.default() < installmentTime }
                        .sumOf { it.amount }

                    val succeedingHistories = installmentHistoryItems
                        .filter { it.installment?.installmentTime != null && it.installment?.installmentTime.default() > installmentTime }
                        .sortedBy { it.installment?.installmentTime }

                    val currentRemainAmount = totalAmount - precedingSum - newAmount

                    val historiesToUpdate = mutableListOf<HistoryEntity>()

                    // 현재 수정 중인 내역
                    historiesToUpdate.add(
                        HistoryEntity(
                            id = historyVo.id,
                            largeCategory = uiState.largeCategory.name,
                            date = uiState.date.toEpochMilliseconds(),
                            amount = newAmount,
                            installmentId = installmentId,
                            installment = HistoryInstallment(
                                installmentTime = installmentTime,
                                installmentRemainAmount = currentRemainAmount
                            ),
                            categoryId = uiState.category?.id,
                            categoryTagId = uiState.categoryTag?.id,
                            paymentMethodId = uiState.paymentMethod?.id,
                            content = uiState.content.text,
                            isVisibility = uiState.isVisibility
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

                    getInstallmentHistory(installmentId)
                } else {
                    historyRepository.updateHistory(
                        HistoryEntity(
                            id = historyVo.id,
                            largeCategory = uiState.largeCategory.name,
                            date = uiState.date.toEpochMilliseconds(),
                            amount = newAmount,
                            installmentId = historyVo.installment?.id,
                            installment = historyVo.installmentTime?.let {
                                HistoryInstallment(
                                    installmentTime = it,
                                    installmentRemainAmount = historyVo.installmentRemainAmount.default()
                                )
                            },
                            categoryId = uiState.category?.id,
                            categoryTagId = uiState.categoryTag?.id,
                            paymentMethodId = uiState.paymentMethod?.id,
                            content = uiState.content.text,
                            isVisibility = uiState.isVisibility
                        )
                    )
                }
            }
        ) {
            showSnackbar("저장되었습니다.")
            reduceState { state ->
                state.copy(isDataChanged = false)
            }
        }
    }

    fun removeHistory() {
        val uiState = container.uiState.value
        uiState.history?.id ?: return

        launchSafe(
            block = {
                historyRepository.deleteHistory(uiState.history.id)
            }
        ) {
            showSnackbar("${uiState.largeCategory.label} 내역이 삭제되었습니다.")
            postSideEffect { HistoryDetailUiSideEffect.OnSuccess }
        }
    }

}
