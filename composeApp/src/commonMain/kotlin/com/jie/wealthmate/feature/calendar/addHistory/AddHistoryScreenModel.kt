package com.jie.wealthmate.feature.calendar.addHistory

import androidx.compose.ui.text.input.TextFieldValue
import com.jie.wealthmate.base.BaseScreenModel
import com.jie.wealthmate.feature.menu.categoryManagement.component.LargeCategoryEnum
import com.jie.wealthmate.repository.CategoryRepository
import com.jie.wealthmate.repository.PaymentMethodRepository
import com.jie.wealthmate.vo.CategoryTagVo
import com.jie.wealthmate.vo.CategoryVo
import com.jie.wealthmate.vo.PaymentMethodVo
import kotlinx.datetime.LocalDate

class AddHistoryScreenModel(
    private val categoryRepository: CategoryRepository,
    private val paymentMethodRepository: PaymentMethodRepository,
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
                selectedLargeCategory = largeCategory
            )
        }
        getCategories(largeCategory)
    }

    fun updateDate(date: LocalDate) {
        reduceState { state ->
            state.copy(
                date = date
            )
        }
    }

    fun updateInstallmentCount(installmentCount: Int?) {
        reduceState { state ->
            state.copy(
                installmentCount = installmentCount
            )
        }
    }

    fun updateAmount(amount: TextFieldValue) {
        reduceState { state ->
            state.copy(
                amount = amount
            )
        }
    }

    fun updateCategory(category: CategoryVo) {
        reduceState { state ->
            state.copy(
                category = category
            )
        }
    }

    fun updateCategoryTag(categoryTag: CategoryTagVo) {
        reduceState { state ->
            state.copy(
                categoryTag = categoryTag
            )
        }
    }

    fun updateContent(content: TextFieldValue) {
        reduceState { state ->
            state.copy(
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
        // todo
    }
}