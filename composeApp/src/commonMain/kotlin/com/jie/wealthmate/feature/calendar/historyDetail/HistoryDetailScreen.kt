@file:OptIn(InternalVoyagerApi::class)

package com.jie.wealthmate.feature.calendar.historyDetail

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
import com.jie.wealthmate.feature.calendar.addHistory.component.DateTextField
import com.jie.wealthmate.feature.calendar.addHistory.component.InstallmentModalBottomSheet
import com.jie.wealthmate.feature.calendar.addHistory.component.PaymentMethodModalBottomSheet
import com.jie.wealthmate.feature.calendar.addHistory.component.PaymentMethodTextField
import com.jie.wealthmate.feature.calendar.addHistory.component.RepeatCycleModalBottomSheet
import com.jie.wealthmate.feature.calendar.historyDetail.component.Category
import com.jie.wealthmate.feature.calendar.historyDetail.component.CategorySelectModalBottomSheet
import com.jie.wealthmate.feature.calendar.historyDetail.component.Installment
import com.jie.wealthmate.feature.calendar.historyDetail.component.RepeatCycle
import com.jie.wealthmate.feature.menu.management.categoryManagement.component.LargeCategoryEnum
import org.koin.compose.koinInject
import wealthmate.composeapp.generated.resources.Res
import wealthmate.composeapp.generated.resources.ic_delete_outline

class HistoryDetailScreen(
    val largeCategory: LargeCategoryEnum,
    val historyId: String,
) : BaseScreen() {
    @Composable
    override fun Content() {
        super.Content()

        val navigator = LocalNavigator.currentOrThrow
        val screenModel: HistoryDetailScreenModel = koinInject()
        val uiState by screenModel.container.uiState.collectAsState()

        var isShowDateSelectModalBottomSheet by remember { mutableStateOf(false) }
        var isShowRepeatCycleModalBottomSheet by remember { mutableStateOf(false) }
        var isShowInstallmentModalBottomSheet by remember { mutableStateOf(false) }
        var isShowCategorySelectModalBottomSheet by remember { mutableStateOf(false) }
        var isShowPaymentMethodModalBottomSheet by remember { mutableStateOf(false) }

        fun onBack() {
            showSaveBackDialog(uiState.isDataChanged) {
                navigator.pop()
            }
        }

        BackHandler(true) { onBack() }

        screenModel.collectSideEffect { sideEffect ->
            when (sideEffect) {
                is HistoryDetailUiSideEffect.OnSuccess -> {
                    navigator.pop()
                }
            }
        }

        LaunchedEffect(Unit) {
            screenModel.updateInit(
                historyId = historyId
            )
        }


        Column(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
        ) {
            WMTopBar(
                title = TopBarItem.Title("${largeCategory.label} 내역 상세"),
                readingItem = TopBarItem.ReadingItem().copy(
                    action = { onBack() }
                ),
                trailingItem = listOf(
                    TopBarItem.TrailingItem(
                        iconRes = Res.drawable.ic_delete_outline,
                        action = {
                            showRemoveDialog() {
                                screenModel.removeHistory()
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
                // 날짜 선택
                DateTextField(
                    modifier = Modifier.padding(top = 4.dp),
                    selectedDate = uiState.date,
                    amount = uiState.amount,
                    isTrailingIconVisible = false,
                    onDateClick = { isShowDateSelectModalBottomSheet = true },
//                    onRepeatClick = {
//                        if (uiState.installmentCount != null) {
//                            showConfirmDialog(
//                                isShow = true,
//                                content = "할부가 선택되어있습니다.\n할부 선택을 취소하시겠습니까?",
//                                callback = {
//                                    screenModel.updateInstallmentCount(null)
//                                    isShowRepeatCycleModalBottomSheet = true
//                                }
//                            )
//                        } else {
//                            isShowRepeatCycleModalBottomSheet = true
//                        }
//                    },
//                    onInstallmentClick = {
//                        if (uiState.repeatCycle != null) {
//                            showConfirmDialog(
//                                isShow = true,
//                                content = "반복 주기가 선택되어있습니다.\n반복 주기 선택을 취소하시겠습니까?",
//                                callback = {
//                                    screenModel.updateRepeatCycle(null)
//                                    isShowInstallmentModalBottomSheet = true
//                                }
//                            )
//                        } else {
//                            isShowInstallmentModalBottomSheet = true
//                        }
//                    },
//                    onResetClick = {
//                        screenModel.updateRepeatCycle(null)
//                        screenModel.updateInstallmentCount(null)
//                    }
                )

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
                    supportingContent = {
                        uiState.history?.let { history ->
                            // 반복
                            if (history.repeatCycle != null) {
                                RepeatCycle(
                                    modifier = Modifier.padding(top = 4.dp, bottom = 24.dp),
                                    repeatCycle = history.repeatCycle
                                )
                            }
                            // 할부
                            if (history.installment != null) {
                                Installment(
                                    modifier = Modifier.padding(top = 8.dp, bottom = 24.dp),
                                    installment = history.installment,
                                    installmentTime = history.installmentTime,
                                    installmentHistoryItems = uiState.installmentHistoryItems
                                )
                            }
                        }
                    }
                )

                // 카테고리
                Category(
                    modifier = Modifier.padding(top = 4.dp),
                    category = uiState.category,
                    categoryTag = uiState.categoryTag,
                    onCategoryClick = { isShowCategorySelectModalBottomSheet = true }
                )

                // 결제수단/자산
                PaymentMethodTextField(
                    modifier = Modifier.padding(top = 24.dp),
                    selectedLargeCategory = uiState.largeCategory,
                    selectedPaymentMethod = uiState.paymentMethod,
                    placeholder = " 없음",
                    onPaymentMethodClick = { isShowPaymentMethodModalBottomSheet = true }
                )

                // 내용 입력
                WMTextField(
                    value = uiState.content,
                    onValueChange = screenModel::updateContent,
                    modifier = Modifier.padding(top = 4.dp),
                    label = "내용",
                    maxLength = 20,
                    placeholder = "내용 없음",
                )

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
                onClick = screenModel::saveHistory
            )
        }

        if (isShowDateSelectModalBottomSheet) {
            DateSelectModalBottomSheet(
                selectedDate = uiState.date,
                onSelectClick = screenModel::updateDate,
                onDismissRequest = { isShowDateSelectModalBottomSheet = false }
            )
        }

        if (isShowRepeatCycleModalBottomSheet) {
            RepeatCycleModalBottomSheet(
                selectedRepeatCycle = uiState.repeatCycle,
                onConfirmClick = screenModel::updateRepeatCycle,
                onDismissRequest = { isShowRepeatCycleModalBottomSheet = false }
            )
        }

        if (isShowInstallmentModalBottomSheet) {
            InstallmentModalBottomSheet(
                installmentCount = uiState.installmentCount,
                onConfirmClick = screenModel::updateInstallmentCount,
                onDismissRequest = { isShowInstallmentModalBottomSheet = false }
            )
        }

        if (isShowCategorySelectModalBottomSheet) {
            CategorySelectModalBottomSheet(
                categoryItems = uiState.categoryItems,
                selectedLargeCategory = uiState.largeCategory,
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
