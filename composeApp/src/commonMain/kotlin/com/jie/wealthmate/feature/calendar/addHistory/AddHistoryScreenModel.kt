package com.jie.wealthmate.feature.calendar.addHistory

import androidx.compose.ui.text.input.TextFieldValue
import com.jie.wealthmate.base.BaseScreenModel
import com.jie.wealthmate.database.eneity.HistoryEntity
import com.jie.wealthmate.feature.calendar.addHistory.component.RepeatCycleEnum
import com.jie.wealthmate.feature.menu.categoryManagement.component.LargeCategoryEnum
import com.jie.wealthmate.repository.CategoryRepository
import com.jie.wealthmate.repository.HistoryRepository
import com.jie.wealthmate.repository.PaymentMethodRepository
import com.jie.wealthmate.usecase.HistorySaveUseCase
import com.jie.wealthmate.utils.formatRemoveCommas
import com.jie.wealthmate.utils.toEpochMilliseconds
import com.jie.wealthmate.vo.CategoryTagVo
import com.jie.wealthmate.vo.CategoryVo
import com.jie.wealthmate.vo.PaymentMethodVo
import kotlinx.datetime.LocalDate

class AddHistoryScreenModel(
    private val categoryRepository: CategoryRepository,
    private val paymentMethodRepository: PaymentMethodRepository,
    private val historyRepository: HistoryRepository,
    private val historySaveUseCase: HistorySaveUseCase,
) : BaseScreenModel<AddHistoryUiState>() {

    override val initialState: AddHistoryUiState
        get() = AddHistoryUiState()

    init {
        getCategories(LargeCategoryEnum.EXPENSES)
        getPaymentMethods()
    }

    fun updateLargeCategory(largeCategory: LargeCategoryEnum) {
        reduceState { state ->
            state.copy(
                isDataChanged = true,
                selectedLargeCategory = largeCategory
            )
        }
        getCategories(largeCategory)
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

    fun updateTotalInstallmentCount(totalInstallment: Int?) {
        reduceState { state ->
            state.copy(
                isDataChanged = true,
                totalInstallment = totalInstallment
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

    fun updateCategory(category: CategoryVo) {
        reduceState { state ->
            state.copy(
                isDataChanged = true,
                category = category
            )
        }
    }

    fun updateCategoryTag(categoryTag: CategoryTagVo) {
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
        launchSafe(
            block = {
                historySaveUseCase(
                    history = HistoryEntity(
                        largeCategory = uiState.selectedLargeCategory.name,
                        date = uiState.date.toEpochMilliseconds(),
                        amount = uiState.amount.text.formatRemoveCommas().toLong(),
                        totalInstallment = uiState.totalInstallment?.toLong(),
                        categoryId = uiState.category?.id,
                        categoryTagId = uiState.categoryTag?.id,
                        paymentMethodId = uiState.paymentMethod?.id,
                        content = uiState.content.text,
                    ),
                    repeatCycle = uiState.repeatCycle?.name
                )
            }
        ) {
            showSnackbar("저장되었습니다.")
            postSideEffect { AddHistoryUiSideEffect.OnSuccessSave }
        }
    }
}