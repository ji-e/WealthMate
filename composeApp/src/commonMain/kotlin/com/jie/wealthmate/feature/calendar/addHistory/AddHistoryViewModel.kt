package com.jie.wealthmate.feature.calendar.addHistory

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.viewModelScope
import com.jie.wealthmate.base.BaseViewModel
import com.jie.wealthmate.database.eneity.HistoryEntity
import com.jie.wealthmate.feature.calendar.addHistory.component.RepeatCycleEnum
import com.jie.wealthmate.feature.menu.management.categoryManagement.component.LargeCategoryEnum
import com.jie.wealthmate.repository.CategoryRepository
import com.jie.wealthmate.repository.HistoryRepository
import com.jie.wealthmate.repository.PaymentMethodRepository
import com.jie.wealthmate.usecase.SaveHistoryUseCase
import com.jie.wealthmate.utils.formatRemoveCommas
import com.jie.wealthmate.utils.toEpochMilliseconds
import com.jie.wealthmate.vo.CategoryTagVo
import com.jie.wealthmate.vo.CategoryVo
import com.jie.wealthmate.vo.CategoryVo.Companion.mapperToVo
import com.jie.wealthmate.vo.PaymentMethodVo
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

class AddHistoryViewModel(
    private val categoryRepository: CategoryRepository,
    private val paymentMethodRepository: PaymentMethodRepository,
    private val historyRepository: HistoryRepository,
    private val saveHistoryUseCase: SaveHistoryUseCase,
    private val initialSelectedDate: LocalDate
) : BaseViewModel<AddHistoryUiState>() {

    override val initialState: AddHistoryUiState = AddHistoryUiState(
        date = initialSelectedDate
    )

    // 대분류 상태를 관리하는 Flow (카테고리 목록 로딩 트리거)
    private val largeCategoryFlow = MutableStateFlow(initialState.selectedLargeCategory)

    init {
        largeCategoryFlow.value = initialState.selectedLargeCategory
        observeCategories()
        observePaymentMethods()
    }

    /**
     * 대분류 선택에 따른 카테고리 목록을 관찰합니다.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeCategories() {
        largeCategoryFlow.flatMapLatest { largeCategory ->
            categoryRepository.getCategoriesByLargeCategory(largeCategory.name)
        }.apiFlow { response ->
            reduceState { state ->
                state.copy(
                    categoryItems = response.map { it.mapperToVo() }
                )
            }
        }
    }

    /**
     * 결제 수단 목록을 관찰합니다.
     */
    private fun observePaymentMethods() {
        paymentMethodRepository.getPaymentMethods()
            .apiFlow { response ->
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

    fun updateLargeCategory(largeCategory: LargeCategoryEnum) {
        largeCategoryFlow.value = largeCategory
        reduceState { state ->
            state.copy(
                isDataChanged = true,
                selectedLargeCategory = largeCategory
            )
        }
    }

    fun updateDate(date: LocalDate) {
        reduceState { state ->
            state.copy(isDataChanged = true, date = date)
        }
    }

    fun updateRepeatCycle(repeatCycle: RepeatCycleEnum?) {
        reduceState { state ->
            state.copy(isDataChanged = true, repeatCycle = repeatCycle)
        }
    }

    fun updateTotalInstallmentCount(totalInstallment: Long?) {
        reduceState { state ->
            state.copy(isDataChanged = true, totalInstallmentCount = totalInstallment)
        }
    }

    fun updateAmount(amount: TextFieldValue) {
        reduceState { state ->
            state.copy(isDataChanged = true, amount = amount)
        }
    }

    fun updateCategory(category: CategoryVo) {
        reduceState { state ->
            state.copy(isDataChanged = true, category = category)
        }
    }

    fun updateCategoryTag(categoryTag: CategoryTagVo) {
        reduceState { state ->
            state.copy(isDataChanged = true, categoryTag = categoryTag)
        }
    }

    fun updatePaymentMethod(paymentMethod: PaymentMethodVo?) {
        reduceState { state ->
            state.copy(isDataChanged = true, paymentMethod = paymentMethod)
        }
    }

    fun updateContent(content: TextFieldValue) {
        reduceState { state ->
            state.copy(isDataChanged = true, content = content)
        }
    }

    /**
     * 내역을 저장합니다.
     */
    fun saveHistory() {
        val uiState = container.uiState.value
        viewModelScope.launch {
            showLoading(true)
            try {
                saveHistoryUseCase(
                    history = HistoryEntity(
                        largeCategory = uiState.selectedLargeCategory.name,
                        date = uiState.date.toEpochMilliseconds(),
                        amount = uiState.amount.text.formatRemoveCommas().toLong(),
                        categoryId = uiState.category?.id,
                        categoryTagId = uiState.categoryTag?.id,
                        paymentMethodId = uiState.paymentMethod?.id,
                        content = uiState.content.text,
                    ),
                    repeatCycle = uiState.repeatCycle?.name,
                    totalInstallmentCount = uiState.totalInstallmentCount
                )
                showSnackbar("저장되었습니다.")
                postSideEffect(AddHistoryUiSideEffect.OnSuccessSave)
            } catch (e: Exception) {
                logError(e)
                showSnackbar(e.message ?: "저장 중 오류가 발생했습니다.")
            } finally {
                showLoading(false)
            }
        }
    }
}
