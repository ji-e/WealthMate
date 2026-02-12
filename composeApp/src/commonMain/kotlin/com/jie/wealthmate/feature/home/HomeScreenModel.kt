package com.jie.wealthmate.feature.home

import com.jie.wealthmate.base.BaseScreenModel
import com.jie.wealthmate.feature.home.component.CategorySegmentChartData
import com.jie.wealthmate.feature.home.component.PaymentMethodSegmentChartData
import com.jie.wealthmate.feature.menu.management.categoryManagement.component.LargeCategoryEnum
import com.jie.wealthmate.repository.CategoryRepository
import com.jie.wealthmate.repository.PaymentMethodRepository
import com.jie.wealthmate.utils.default
import com.jie.wealthmate.vo.CategoryVo.Companion.mapperToVo
import com.jie.wealthmate.vo.PaymentMethodVo.Companion.mapperToVo

class HomeScreenModel(
    private val categoryRepository: CategoryRepository,
    private val paymentMethodRepository: PaymentMethodRepository,
) : BaseScreenModel<HomeUiState>() {

    override val initialState: HomeUiState
        get() = HomeUiState(
            currentAmount = Amount(
                expensesAmount = 3000000,
                incomeAmount = 8000000,
                savingAmount = 10,
            ),
            lastAmount = Amount(
                expensesAmount = 1000000,
                incomeAmount = 8000000,
                savingAmount = 500,
            )
        )


    init {
        // temp
        getCategories()
        getPaymentMethods()
    }

    fun getCategories() {
        val largeCategoryEnum = LargeCategoryEnum.EXPENSES
        categoryRepository.getCategoriesByLargeCategory(largeCategoryEnum.name)
            .apiFlow { response ->
                var total = container.uiState.value.currentAmount?.expensesAmount.default()
                reduceState { state ->
                    state.copy(
                        categorySegment = response.filter { it.isFixed.not() }
                            .mapIndexed { index, it ->
                                val amount = (total / 1.8 - (index + 1) - (index % 15)).toLong()
                                total -= amount
                                CategorySegmentChartData(
                                    category = it.mapperToVo(),
                                    amount = amount
                                )
                            }
                    )
                }
            }
    }

    private fun getPaymentMethods() {
        paymentMethodRepository.getPaymentMethods()
            .apiFlow { response ->
                var total = container.uiState.value.currentAmount?.expensesAmount.default()
                reduceState { state ->
                    state.copy(
                        paymentMethodSegment = response.mapIndexed { index, it ->
                            val amount = (total / 1.8 - (index + 1) - (index % 15)).toLong()
                            total -= amount
                            PaymentMethodSegmentChartData(
                                paymentMethod = it.paymentMethod.mapperToVo(),
                                amount = amount
                            )
                        }
                    )
                }
            }
    }
}