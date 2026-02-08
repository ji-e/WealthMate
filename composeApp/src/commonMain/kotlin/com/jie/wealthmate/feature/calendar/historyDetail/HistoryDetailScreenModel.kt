package com.jie.wealthmate.feature.calendar.historyDetail

import androidx.compose.ui.text.input.TextFieldValue
import com.jie.wealthmate.base.BaseScreenModel
import com.jie.wealthmate.feature.calendar.addHistory.component.RepeatCycleEnum
import com.jie.wealthmate.feature.menu.management.categoryManagement.component.LargeCategoryEnum
import com.jie.wealthmate.repository.CategoryRepository
import com.jie.wealthmate.repository.HistoryRepository
import com.jie.wealthmate.repository.PaymentMethodRepository
import com.jie.wealthmate.usecase.ModifyHistoryUseCase
import com.jie.wealthmate.usecase.UpdateInstallmentUseCase
import com.jie.wealthmate.usecase.UpdateRepeatCycleUseCase
import com.jie.wealthmate.utils.default
import com.jie.wealthmate.utils.formatRemoveCommas
import com.jie.wealthmate.vo.CategoryTagVo
import com.jie.wealthmate.vo.CategoryVo
import com.jie.wealthmate.vo.HistoryVo.Companion.mapperToVo
import com.jie.wealthmate.vo.PaymentMethodVo
import kotlinx.datetime.LocalDate

class HistoryDetailScreenModel(
    private val categoryRepository: CategoryRepository,
    private val paymentMethodRepository: PaymentMethodRepository,
    private val historyRepository: HistoryRepository,
    private val updateInstallmentUseCase: UpdateInstallmentUseCase,
    private val updateRepeatCycleUseCase: UpdateRepeatCycleUseCase,
    private val modifyHistoryUseCase: ModifyHistoryUseCase,
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

    fun updateEndDate(date: LocalDate?) {
        val uiState = container.uiState.value

        uiState.history?.repeatCycle?.let { repeatCycleVo ->
            updateRepeatCycle(
                repeatCycle = repeatCycleVo.repeatCycle,
                endDate = date
            )
        }
    }

    fun updateRepeatCycle(repeatCycle: RepeatCycleEnum?) {
        repeatCycle ?: return
        val uiState = container.uiState.value


        uiState.history?.repeatCycle?.let { repeatCycleVo ->
            updateRepeatCycle(
                repeatCycle = repeatCycle,
                endDate = repeatCycleVo.endDate
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

    fun updateInstallment(totalAmount: Long, totalCount: Long) {
        val uiState = container.uiState.value
        val historyVo = uiState.history ?: return

        launchSafe(
            block = {
                updateInstallmentUseCase(
                    historyId = historyVo.id,
                    totalAmount = totalAmount,
                    totalCount = totalCount
                )
            }
        ) {
            getHistory(historyVo.id)
            showSnackbar("할부 정보가 수정되었습니다.")
        }
    }

    private fun updateRepeatCycle(repeatCycle: RepeatCycleEnum, endDate: LocalDate?) {
        val uiState = container.uiState.value
        val historyVo = uiState.history ?: return

        launchSafe(
            block = {
                updateRepeatCycleUseCase(
                    historyId = historyVo.id,
                    newRepeatCycle = repeatCycle.name,
                    newEndDate = endDate
                )
            }
        ) {
            getHistory(historyVo.id)
            showSnackbar("반복 정보가 수정되었습니다.")
        }
    }

    fun modifyHistory() {
        val uiState = container.uiState.value
        val historyVo = uiState.history ?: return
        val newAmount = uiState.amount.text.formatRemoveCommas().toLongOrNull().default()

        launchSafe(
            block = {
                modifyHistoryUseCase(
                    historyId = historyVo.id,
                    newAmount = newAmount,
                    newDate = uiState.date,
                    newCategoryId = uiState.category?.id,
                    newCategoryTagId = uiState.categoryTag?.id,
                    newPaymentMethodId = uiState.paymentMethod?.id,
                    newContent = uiState.content.text,
                    isVisibility = uiState.isVisibility
                )
            }
        ) {
            getHistory(historyVo.id)
            showSnackbar("저장되었습니다.")
            reduceState { state ->
                state.copy(isDataChanged = false)
            }
        }
    }

    fun removeHistory(isInstallmentAllRemove: Boolean = false) {
        val uiState = container.uiState.value
        val historyVo = uiState.history ?: return

        launchSafe(
            block = {
                val installmentId = historyVo.installment?.id
                if (isInstallmentAllRemove && installmentId != null) {
                    historyRepository.deleteHistoriesByInstallmentId(installmentId)
                } else {
                    historyRepository.deleteHistory(historyVo.id)
                }
            }
        ) {
            showSnackbar("${uiState.largeCategory.label} 내역이 삭제되었습니다.")
            postSideEffect { HistoryDetailUiSideEffect.OnSuccess }
        }
    }

}
