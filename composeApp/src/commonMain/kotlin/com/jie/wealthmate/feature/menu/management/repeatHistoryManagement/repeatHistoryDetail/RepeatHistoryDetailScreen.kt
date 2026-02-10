@file:OptIn(InternalVoyagerApi::class)

package com.jie.wealthmate.feature.menu.management.repeatHistoryManagement.repeatHistoryDetail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
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
import com.jie.wealthmate.feature.calendar.addHistory.component.DateSelectModalBottomSheet
import com.jie.wealthmate.feature.calendar.addHistory.component.PaymentMethodModalBottomSheet
import com.jie.wealthmate.feature.calendar.addHistory.component.PaymentMethodTextField
import com.jie.wealthmate.feature.calendar.addHistory.component.RepeatCycleEnum
import com.jie.wealthmate.feature.calendar.addHistory.component.RepeatCycleModalBottomSheet
import com.jie.wealthmate.feature.calendar.historyDetail.component.Category
import com.jie.wealthmate.feature.calendar.historyDetail.component.CategorySelectModalBottomSheet
import com.jie.wealthmate.feature.menu.component.MenuEnum
import com.jie.wealthmate.feature.menu.management.categoryManagement.component.LargeCategoryEnum
import com.jie.wealthmate.feature.menu.management.repeatHistoryManagement.addRepeatHistory.AddRepeatHistoryScreenModel.Companion.END_DATE
import com.jie.wealthmate.feature.menu.management.repeatHistoryManagement.addRepeatHistory.AddRepeatHistoryScreenModel.Companion.START_DATE
import com.jie.wealthmate.feature.menu.management.repeatHistoryManagement.addRepeatHistory.component.RepeatCycleDateFullModalBottomSheet
import com.jie.wealthmate.feature.menu.management.repeatHistoryManagement.addRepeatHistory.component.RepeatCycleDateModalBottomSheet
import com.jie.wealthmate.feature.menu.management.repeatHistoryManagement.addRepeatHistory.component.RepeatCyclePeriod
import kotlinx.datetime.number
import wealthmate.composeapp.generated.resources.Res
import wealthmate.composeapp.generated.resources.ic_delete_outline

class RepeatHistoryDetailScreen(
    private val repeatCycleId: String,
) : BaseScreen() {

    @Composable
    override fun Content() {
        super.Content()

        val navigator = LocalNavigator.currentOrThrow
        val screenModel: RepeatHistoryDetailScreenModel = koinScreenModel()
        val uiState by screenModel.container.uiState.collectAsState()

        var isShowStartDateSelectModalBottomSheet by remember { mutableStateOf(false) }
        var isShowEndDateSelectModalBottomSheet by remember { mutableStateOf(false) }
        var isShowRepeatCycleModalBottomSheet by remember { mutableStateOf(false) }
        var isShowRepeatDateModalBottomSheet by remember { mutableStateOf(false) }
        var isShowCategorySelectModalBottomSheet by remember { mutableStateOf(false) }
        var isShowPaymentMethodModalBottomSheet by remember { mutableStateOf(false) }

        fun onBack() {
            showSaveBackDialog(uiState.isDataChanged) {
                navigator.pop()
            }
        }

        BackHandler(true) { onBack() }

        LaunchedEffect(Unit) {
            screenModel.updateInit(repeatCycleId)
        }

        screenModel.collectSideEffect { sideEffect ->
            when (sideEffect) {
                is RepeatHistoryDetailUiSideEffect.OnSuccess -> {
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
                title = TopBarItem.Title("${uiState.selectedLargeCategory.label} ${MenuEnum.REPEAT_HISTORY.label} 상세"),
                readingItem = TopBarItem.ReadingItem().copy(action = { navigator.pop() }),
                trailingItem = listOf(
                    TopBarItem.TrailingItem(
                        iconRes = Res.drawable.ic_delete_outline,
                        action = {
                            showRemoveDialog() {
                                screenModel.removeRepeatCycle()
                            }
                        }
                    )
                )
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 28.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // 기간
                RepeatCyclePeriod(
                    modifier = Modifier.padding(top = 4.dp),
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
                    value = uiState.repeatCycle.shortDescription,
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
                Category(
                    modifier = Modifier.padding(top = 4.dp, bottom = 24.dp),
                    category = uiState.category,
                    categoryTag = uiState.categoryTag,
                    onCategoryClick = { isShowCategorySelectModalBottomSheet = true }
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

            // 수정 버튼
            WMFloatingButton(
                text = "수정",
                buttonSize = ButtonSize.LARGE,
                modifier = Modifier
                    .padding(horizontal = 28.dp)
                    .padding(bottom = 20.dp)
                    .fillMaxWidth(),
                enabled = uiState.isSaveButtonEnable,
                onClick = {
                    showConfirmDialog(
                        isShow = true,
                        content = "다음 달 부터 적용됩니다."
                    ) {
                        screenModel.modifyRepeatCycle()
                    }
                }
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
                    repeatCycleDateMonth = uiState.repeatCycleDateFull?.month?.number,
                    repeatCycleDateDay = uiState.repeatCycleDateFull?.day,
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

        if (isShowCategorySelectModalBottomSheet) {
            CategorySelectModalBottomSheet(
                categoryItems = uiState.categoryItems,
                selectedLargeCategory = uiState.selectedLargeCategory,
                selectedCategory = uiState.category,
                selectedCategoryTag = uiState.categoryTag,
                onConfirmClick = { category, categoryTag ->
                    screenModel.updateCategory(category)
                    screenModel.updateCategoryTag(categoryTag)
                },
                onDismissRequest = { isShowCategorySelectModalBottomSheet = false }
            )
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