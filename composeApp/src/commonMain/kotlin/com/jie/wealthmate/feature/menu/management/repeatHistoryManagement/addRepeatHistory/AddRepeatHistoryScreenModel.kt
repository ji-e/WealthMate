package com.jie.wealthmate.feature.menu.management.repeatHistoryManagement.addRepeatHistory

import androidx.compose.ui.text.input.TextFieldValue
import com.jie.wealthmate.base.BaseScreenModel
import com.jie.wealthmate.feature.calendar.addHistory.component.RepeatCycleEnum
import com.jie.wealthmate.feature.menu.management.categoryManagement.component.LargeCategoryEnum
import com.jie.wealthmate.repository.CategoryRepository
import com.jie.wealthmate.repository.PaymentMethodRepository
import com.jie.wealthmate.repository.RepeatCycleRepository
import com.jie.wealthmate.utils.today
import com.jie.wealthmate.vo.CategoryTagVo
import com.jie.wealthmate.vo.CategoryVo
import com.jie.wealthmate.vo.PaymentMethodVo
import kotlinx.datetime.LocalDate

class AddRepeatHistoryScreenModel(
    private val repeatCycleRepository: RepeatCycleRepository,
    private val categoryRepository: CategoryRepository,
    private val paymentMethodRepository: PaymentMethodRepository,
) : BaseScreenModel<AddRepeatHistoryUiState>() {

    override val initialState: AddRepeatHistoryUiState
        get() = AddRepeatHistoryUiState()

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


    fun updateDate(type: String, date: LocalDate?) {
        reduceState { state ->
            state.copy(
                isDataChanged = true,
                startDate = if (type == START_DATE) date ?: today else state.startDate,
                endDate = if (type == END_DATE) date else state.endDate
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

    fun updateTotalInstallmentCount(totalInstallment: Long?) {
        reduceState { state ->
            state.copy(
                isDataChanged = true,
                totalInstallmentCount = totalInstallment
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

    fun saveRepeatCycle() {
        launchSafe(
            block = {
//                repeatCycleRepository.updateRepeatCycle(
//                    repeatCycle.copy(
//                        isActive = isActive
//                    )
//                )
            }
        ) {
            showSnackbar("반복 정보가 저장되었습니다.")
        }
    }

    companion object {
        const val START_DATE = "시작일"
        const val END_DATE = "종료일"
    }
}
