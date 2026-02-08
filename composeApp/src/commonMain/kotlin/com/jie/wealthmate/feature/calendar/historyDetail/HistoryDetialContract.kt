package com.jie.wealthmate.feature.calendar.historyDetail

import androidx.compose.ui.text.input.TextFieldValue
import com.jie.wealthmate.base.BaseUiState
import com.jie.wealthmate.base.UiSideEffect
import com.jie.wealthmate.feature.calendar.addHistory.component.RepeatCycleEnum
import com.jie.wealthmate.feature.menu.management.categoryManagement.component.LargeCategoryEnum
import com.jie.wealthmate.utils.today
import com.jie.wealthmate.vo.CategoryTagVo
import com.jie.wealthmate.vo.CategoryVo
import com.jie.wealthmate.vo.HistoryVo
import com.jie.wealthmate.vo.PaymentMethodVo
import kotlinx.datetime.LocalDate

data class HistoryDetailUiState(
    val isDataChanged: Boolean = false,
    val history: HistoryVo? = null,
    val largeCategory: LargeCategoryEnum = LargeCategoryEnum.EXPENSES,
    val date: LocalDate = today,
    val repeatCycle: RepeatCycleEnum? = null,
    val installmentCount: Int? = null,
    val content: TextFieldValue = TextFieldValue(""),
    val amount: TextFieldValue = TextFieldValue(""),
    val category: CategoryVo? = null,
    val categoryTag: CategoryTagVo? = null,
    val paymentMethod: PaymentMethodVo? = null,
    val isVisibility: Boolean = true,
    val categoryItems: List<CategoryVo> = emptyList(),
    val paymentMethodItems: List<PaymentMethodVo> = emptyList(),
) : BaseUiState {
    val isSaveButtonEnable = amount.text.isNotBlank() &&
            isDataChanged &&
            (category?.isFixed != true || content.text.isNotBlank())
}

sealed class HistoryDetailUiSideEffect : UiSideEffect {
    data object OnSuccess : HistoryDetailUiSideEffect()
}