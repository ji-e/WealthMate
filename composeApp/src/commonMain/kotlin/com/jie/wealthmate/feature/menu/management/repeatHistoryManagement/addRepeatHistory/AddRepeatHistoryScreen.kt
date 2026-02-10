@file:OptIn(InternalVoyagerApi::class)

package com.jie.wealthmate.feature.menu.management.repeatHistoryManagement.addRepeatHistory

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.annotation.InternalVoyagerApi
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.internal.BackHandler
import com.jie.wealthmate.base.BaseScreen
import com.jie.wealthmate.base.collectSideEffect
import com.jie.wealthmate.component.ButtonSize
import com.jie.wealthmate.component.WMFloatingButton
import com.jie.wealthmate.component.WMText
import com.jie.wealthmate.component.textField.WMTextField
import com.jie.wealthmate.component.textField.rememberIntegerVisualTransformation
import com.jie.wealthmate.component.textField.toIntegerTextFieldValue
import com.jie.wealthmate.component.topbar.TopBarItem
import com.jie.wealthmate.component.topbar.WMTopBar
import com.jie.wealthmate.feature.calendar.addHistory.component.CategorySelectionRow
import com.jie.wealthmate.feature.calendar.addHistory.component.DateSelectModalBottomSheet
import com.jie.wealthmate.feature.calendar.addHistory.component.LargeCategorySelectBox
import com.jie.wealthmate.feature.calendar.addHistory.component.PaymentMethodModalBottomSheet
import com.jie.wealthmate.feature.calendar.addHistory.component.PaymentMethodTextField
import com.jie.wealthmate.feature.calendar.addHistory.component.RepeatCycleEnum
import com.jie.wealthmate.feature.calendar.addHistory.component.RepeatCycleModalBottomSheet
import com.jie.wealthmate.feature.menu.component.MenuEnum
import com.jie.wealthmate.feature.menu.management.categoryManagement.component.LargeCategoryEnum
import com.jie.wealthmate.feature.menu.management.repeatHistoryManagement.addRepeatHistory.AddRepeatHistoryScreenModel.Companion.END_DATE
import com.jie.wealthmate.feature.menu.management.repeatHistoryManagement.addRepeatHistory.AddRepeatHistoryScreenModel.Companion.START_DATE
import com.jie.wealthmate.feature.menu.management.repeatHistoryManagement.addRepeatHistory.component.RepeatCycleDateFullModalBottomSheet
import com.jie.wealthmate.feature.menu.management.repeatHistoryManagement.addRepeatHistory.component.RepeatCycleDateModalBottomSheet
import com.jie.wealthmate.feature.menu.management.repeatHistoryManagement.addRepeatHistory.component.RepeatCyclePeriod
import com.jie.wealthmate.utils.default
import kotlinx.datetime.number

class AddRepeatHistoryScreen : BaseScreen() {

    @Composable
    override fun Content() {
        super.Content()

        val navigator = LocalNavigator.currentOrThrow
        val screenModel: AddRepeatHistoryScreenModel = koinScreenModel()
        val uiState by screenModel.container.uiState.collectAsState()

        var isShowStartDateSelectModalBottomSheet by remember { mutableStateOf(false) }
        var isShowEndDateSelectModalBottomSheet by remember { mutableStateOf(false) }
        var isShowRepeatCycleModalBottomSheet by remember { mutableStateOf(false) }
        var isShowRepeatDateModalBottomSheet by remember { mutableStateOf(false) }
        var isShowPaymentMethodModalBottomSheet by remember { mutableStateOf(false) }

        val scrollState = rememberScrollState()
        val density = LocalDensity.current

        val isLargeCategoryVisible by remember {
            derivedStateOf {
                scrollState.value > with(density) { 56.dp.toPx() }
            }
        }

        fun onBack() {
            showSaveBackDialog(uiState.isDataChanged) {
                navigator.pop()
            }
        }

        BackHandler(true) { onBack() }

        screenModel.collectSideEffect { sideEffect ->
            when (sideEffect) {
                is AddRepeatHistoryUiSideEffect.OnSuccessSave -> {
                    navigator.pop()
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
        ) {
            WMTopBar(
                title = TopBarItem.Title("${MenuEnum.REPEAT_HISTORY.label} 추가"),
                readingItem = TopBarItem.ReadingItem().copy(action = { navigator.pop() }),
                trailingCustomItem = if (isLargeCategoryVisible) {
                    TopBarItem.TrailingCustomItem {
                        val selectedLargeCategoryEnum = uiState.selectedLargeCategory
                        WMText(
                            text = selectedLargeCategoryEnum.label,
                            style = Typography().labelMedium.copy(fontWeight = FontWeight.SemiBold),
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .clip(CircleShape)
                                .background(selectedLargeCategoryEnum.backgroundColor)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                } else null
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 28.dp)
                    .verticalScroll(scrollState)
            ) {
                // 수입, 지출, 저출 카테고리 선택
                LargeCategorySelectBox(
                    modifier = Modifier.padding(top = 4.dp),
                    selectedLargeCategory = uiState.selectedLargeCategory,
                    onLargeCategoryClick = screenModel::updateLargeCategory
                )

                // 기간
                RepeatCyclePeriod(
                    startDate = uiState.startDate,
                    endDate = uiState.endDate,
                    onStartDateClick = { isShowStartDateSelectModalBottomSheet = true },
                    onEndDateClick = { isShowEndDateSelectModalBottomSheet = true },
                    onEndDateResetClick = {
                        screenModel.updateDate(END_DATE, null)
                    }
                )

                // 반복 주기
                WMTextField(
                    value = uiState.repeatCycle?.shortDescription.default(),
                    onValueChange = {},
                    label = "반복 주기",
                    readOnly = true,
                    isRequire = true,
                    placeholder = "반복 주기를 설정해 주세요.",
                    modifier = Modifier.padding(top = 4.dp),
                    onReadOnlyClick = { isShowRepeatCycleModalBottomSheet = true },
                )

                // 반복 날짜
                if (uiState.repeatCycle == RepeatCycleEnum.WEEKLY || uiState.repeatCycle == RepeatCycleEnum.MONTHLY || uiState.repeatCycle == RepeatCycleEnum.YEARLY) {
                    WMTextField(
                        value = uiState.repeatCycleDateText,
                        onValueChange = {},
                        label = "반복 날짜",
                        readOnly = true,
                        isRequire = true,
                        placeholder = "반복될 날짜를 설정해 주세요.",
                        modifier = Modifier.padding(top = 4.dp),
                        onReadOnlyClick = { isShowRepeatDateModalBottomSheet = true },
                    )
                }


                // 금액 입력
                WMTextField(
                    modifier = Modifier.padding(top = 4.dp),
                    value = uiState.amount,
                    onValueChange = {
                        screenModel.updateAmount(it.toIntegerTextFieldValue())
                    },
                    label = "금액",
                    isRequire = true,
                    maxLength = 10,
                    placeholder = "금액을 입력해 주세요.",
                    suffix = {
                        WMText(
                            text = "원",
                            style = Typography().bodyLarge.copy(fontWeight = FontWeight.SemiBold)
                        )
                    },
                    visualTransformation = rememberIntegerVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                )

                // 내용 입력
                WMTextField(
                    value = uiState.content,
                    onValueChange = screenModel::updateContent,
                    label = "내용",
                    maxLength = 20,
                    isRequire = true,
                    placeholder = "내용을 입력해 주세요.",
                    modifier = Modifier.padding(top = 4.dp)
                )

                // 카테고리 선택
                CategorySelectionRow(
                    modifier = Modifier.padding(top = 4.dp, bottom = 24.dp),
                    categoryItems = uiState.categoryItems,
                    selectedLargeCategory = uiState.selectedLargeCategory,
                    selectedCategory = uiState.category,
                    selectedCategoryTag = uiState.categoryTag,
                    onCategoryClick = screenModel::updateCategory,
                    onCategoryTagClick = screenModel::updateCategoryTag
                )

                // 결제수단/자산 선택
                if (uiState.selectedLargeCategory == LargeCategoryEnum.EXPENSES) {
                    PaymentMethodTextField(
                        selectedLargeCategory = uiState.selectedLargeCategory,
                        selectedPaymentMethod = uiState.paymentMethod,
                        onPaymentMethodClick = { isShowPaymentMethodModalBottomSheet = true }
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

            }

            // 저장 버튼
            WMFloatingButton(
                text = "저장",
                buttonSize = ButtonSize.LARGE,
                modifier = Modifier
                    .padding(horizontal = 28.dp)
                    .padding(bottom = 20.dp)
                    .fillMaxWidth(),
                enabled = uiState.isSaveButtonEnable,
                onClick = screenModel::saveRepeatCycle
            )
        }

        if (isShowStartDateSelectModalBottomSheet) {
            DateSelectModalBottomSheet(
                title = START_DATE,
                selectedDate = uiState.startDate,
                onSelectClick = {
                    screenModel.updateDate(
                        type = START_DATE,
                        date = it
                    )
                },
                onDismissRequest = { isShowStartDateSelectModalBottomSheet = false }
            )
        }
        if (isShowEndDateSelectModalBottomSheet) {
            DateSelectModalBottomSheet(
                title = END_DATE,
                selectedDate = uiState.endDate,
                onSelectClick = {
                    screenModel.updateDate(
                        type = END_DATE,
                        date = it
                    )
                },
                onDismissRequest = { isShowEndDateSelectModalBottomSheet = false }
            )
        }

        if (isShowRepeatCycleModalBottomSheet) {
            RepeatCycleModalBottomSheet(
                selectedRepeatCycle = uiState.repeatCycle,
                onConfirmClick = screenModel::updateRepeatCycle,
                onDismissRequest = { isShowRepeatCycleModalBottomSheet = false }
            )
        }

        if (isShowRepeatDateModalBottomSheet) {
            if (uiState.repeatCycle == RepeatCycleEnum.YEARLY) {
                RepeatCycleDateFullModalBottomSheet(
                    repeatCycleDateMonth = uiState.repeatCycleDateFull?.month?.number?.toLong(),
                    repeatCycleDateDay = uiState.repeatCycleDateFull?.day?.toLong(),
                    onConfirmClick = { month, day ->
                        screenModel.updateRepeatCycleDateFull(month, day)
                    },
                    onDismissRequest = { isShowRepeatDateModalBottomSheet = false }
                )
            } else {
                RepeatCycleDateModalBottomSheet(
                    repeatCycleDate = uiState.repeatCycleDate,
                    repeatCycleDateItems = uiState.repeatCycleDateItems,
                    onConfirmClick = screenModel::updateRepeatCycleDate,
                    onDismissRequest = { isShowRepeatDateModalBottomSheet = false }
                )
            }
        }

        if (isShowPaymentMethodModalBottomSheet) {
            PaymentMethodModalBottomSheet(
                selectedPaymentMethod = uiState.paymentMethod,
                paymentMethodItems = uiState.paymentMethodItems,
                onConfirmClick = screenModel::updatePaymentMethod,
                onDismissRequest = { isShowPaymentMethodModalBottomSheet = false }
            )
        }
    }
}
