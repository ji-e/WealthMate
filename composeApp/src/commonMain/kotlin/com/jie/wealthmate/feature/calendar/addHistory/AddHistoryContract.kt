package com.jie.wealthmate.feature.calendar.addHistory

import androidx.compose.ui.text.input.TextFieldValue
import com.jie.wealthmate.base.BaseUiState
import com.jie.wealthmate.base.UiSideEffect
import com.jie.wealthmate.feature.calendar.addHistory.component.RepeatCycleEnum
import com.jie.wealthmate.feature.menu.categoryManagement.component.LargeCategoryEnum
import com.jie.wealthmate.utils.today
import com.jie.wealthmate.vo.CategoryTagVo
import com.jie.wealthmate.vo.CategoryVo
import com.jie.wealthmate.vo.PaymentMethodVo
import kotlinx.datetime.LocalDate

data class AddHistoryUiState(
    val isDataChanged: Boolean = false,
    val selectedLargeCategory: LargeCategoryEnum = LargeCategoryEnum.EXPENSES,
    val date: LocalDate = today,
    val repeatCycle: RepeatCycleEnum? = null,
    val totalInstallment: Int? = null,
    val content: TextFieldValue = TextFieldValue(""),
    val amount: TextFieldValue = TextFieldValue(""),
    val category: CategoryVo? = null,
    val categoryTag: CategoryTagVo? = null,
    val paymentMethod: PaymentMethodVo? = null,
    val categoryItems: List<CategoryVo> = emptyList(),
    val paymentMethodItems: List<PaymentMethodVo> = emptyList(),
) : BaseUiState {
    val isSaveButtonEnable = amount.text.isNotBlank()
}

sealed class AddHistoryUiSideEffect : UiSideEffect {
    data object OnSuccessSave : AddHistoryUiSideEffect()
}