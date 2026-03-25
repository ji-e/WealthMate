package com.jie.wealthmate.feature.calendar.historyDetail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import com.jie.wealthmate.base.BaseScreen
import com.jie.wealthmate.component.ButtonSize
import com.jie.wealthmate.component.SpacerSize
import com.jie.wealthmate.component.WMFloatingButton
import com.jie.wealthmate.component.WMRemoveDialog
import com.jie.wealthmate.component.WMSaveBackDialog
import com.jie.wealthmate.component.WMSpacer
import com.jie.wealthmate.component.WMText
import com.jie.wealthmate.component.textField.WMTextField
import com.jie.wealthmate.component.textField.rememberIntegerVisualTransformation
import com.jie.wealthmate.component.textField.toIntegerTextFieldValue
import com.jie.wealthmate.component.topbar.TopBarItem
import com.jie.wealthmate.component.topbar.WMTopBar
import com.jie.wealthmate.feature.calendar.addHistory.component.ContentTextField
import com.jie.wealthmate.feature.calendar.addHistory.component.DateSelectModalBottomSheet
import com.jie.wealthmate.feature.calendar.addHistory.component.DateTextField
import com.jie.wealthmate.feature.calendar.addHistory.component.PaymentMethodModalBottomSheet
import com.jie.wealthmate.feature.calendar.addHistory.component.PaymentMethodTextField
import com.jie.wealthmate.feature.calendar.addHistory.component.RepeatCycleEnum
import com.jie.wealthmate.feature.calendar.addHistory.component.RepeatCycleModalBottomSheet
import com.jie.wealthmate.feature.calendar.historyDetail.component.Category
import com.jie.wealthmate.feature.calendar.historyDetail.component.CategorySelectModalBottomSheet
import com.jie.wealthmate.feature.calendar.historyDetail.component.Installment
import com.jie.wealthmate.feature.calendar.historyDetail.component.ModifyInstallmentModalBottomSheet
import com.jie.wealthmate.feature.calendar.historyDetail.component.RemoveInstallmentConfirmDialog
import com.jie.wealthmate.feature.calendar.historyDetail.component.RepeatCycle
import com.jie.wealthmate.feature.menu.management.categoryManagement.component.LargeCategoryEnum
import com.jie.wealthmate.theme.Padding
import com.jie.wealthmate.theme.WMTheme
import com.jie.wealthmate.utils.default
import com.jie.wealthmate.vo.CategoryTagVo
import com.jie.wealthmate.vo.CategoryVo
import com.jie.wealthmate.vo.PaymentMethodVo
import kotlinx.datetime.LocalDate
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import wealthmate.composeapp.generated.resources.Res
import wealthmate.composeapp.generated.resources.ic_delete_outline

@Composable
fun HistoryDetailScreen(
    navController: NavController,
    largeCategory: LargeCategoryEnum,
    historyId: String,
    onNavigateToRepeatDetail: (String) -> Unit,
    viewModel: HistoryDetailViewModel = koinViewModel() {
        parametersOf(historyId)
    },
) {
    var isShowSaveBackDialog by remember { mutableStateOf(false) }
    var isShowRemoveDialog by remember { mutableStateOf(false) }

    val onBack: () -> Unit = {
        if (viewModel.container.uiState.value.isDataChanged) {
            isShowSaveBackDialog = true
        } else {
            navController.popBackStack()
        }
    }


    BaseScreen(
        viewModel = viewModel,
        onBack = {
            if (viewModel.container.uiState.value.isDataChanged) {
                isShowSaveBackDialog = true
            } else {
                onBack()
            }
        },
        onSideEffect = { sideEffect ->
            when (sideEffect) {
                is HistoryDetailUiSideEffect.OnSuccess -> {
                    onBack()
                }
            }
        }
    ) { uiState ->
        HistoryDetailContent(
            uiState = uiState,
            largeCategory = largeCategory,
            onBack = {
                if (uiState.isDataChanged) {
                    isShowSaveBackDialog = true
                } else {
                    onBack()
                }
            },
            onRemoveClick = {
                if (uiState.history?.installment == null) {
                    isShowRemoveDialog = true
                }
            },
            onUpdateAmount = viewModel::updateAmount,
            onUpdateDate = viewModel::updateDate,
            onUpdateRepeatCycleIsActive = viewModel::updateRepeatCycleIsActive,
            onUpdateRepeatCycle = viewModel::updateRepeatCycle,
            onUpdateCategory = viewModel::updateCategory,
            onUpdateCategoryTag = viewModel::updateCategoryTag,
            onUpdatePaymentMethod = viewModel::updatePaymentMethod,
            onUpdateContent = viewModel::updateContent,
            onUpdateInstallment = viewModel::updateInstallment,
            onModifyHistory = viewModel::modifyHistory,
            onRemoveHistory = viewModel::removeHistory,
            onNavigateToRepeatDetail = onNavigateToRepeatDetail
        )

        if (isShowSaveBackDialog) {
            WMSaveBackDialog(
                onConfirm = {
                    navController.popBackStack()
                    isShowSaveBackDialog = false
                },
                onDismiss = { isShowSaveBackDialog = false }
            )
        }

        if (isShowRemoveDialog) {
            WMRemoveDialog(
                onConfirm = {
                    isShowRemoveDialog = false
                    viewModel.removeHistory()
                },
                onDismiss = { isShowRemoveDialog = false }
            )
        }
    }
}

@Composable
fun HistoryDetailContent(
    uiState: HistoryDetailUiState,
    largeCategory: LargeCategoryEnum,
    onBack: () -> Unit,
    onRemoveClick: () -> Unit,
    onUpdateAmount: (TextFieldValue) -> Unit,
    onUpdateDate: (LocalDate) -> Unit,
    onUpdateRepeatCycleIsActive: (Boolean) -> Unit,
    onUpdateRepeatCycle: (RepeatCycleEnum?) -> Unit,
    onUpdateCategory: (CategoryVo?) -> Unit,
    onUpdateCategoryTag: (CategoryTagVo?) -> Unit,
    onUpdatePaymentMethod: (PaymentMethodVo?) -> Unit,
    onUpdateContent: (TextFieldValue) -> Unit,
    onUpdateInstallment: (Long, Long) -> Unit,
    onModifyHistory: () -> Unit,
    onRemoveHistory: (Boolean) -> Unit,
    onNavigateToRepeatDetail: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var isShowDateSelectModalBottomSheet by remember { mutableStateOf(false) }
    var isShowRepeatCycleModalBottomSheet by remember { mutableStateOf(false) }
    var isShowCategorySelectModalBottomSheet by remember { mutableStateOf(false) }
    var isShowPaymentMethodModalBottomSheet by remember { mutableStateOf(false) }
    var isShowModifyInstallmentModalBottomSheet by remember { mutableStateOf(false) }
    var isShowRemoveInstallmentConfirmDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .navigationBarsPadding()
            .imePadding()
    ) {
        WMTopBar(
            title = TopBarItem.Title("${largeCategory.label} 내역 상세"),
            readingItem = TopBarItem.ReadingItem().copy(
                action = onBack
            ),
            trailingItem = listOf(
                TopBarItem.TrailingItem(
                    iconRes = Res.drawable.ic_delete_outline,
                    action = {
                        if (uiState.history?.installment == null) {
                            onRemoveClick()
                        } else {
                            isShowRemoveInstallmentConfirmDialog = true
                        }
                    }
                )
            )
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = Padding.BackgroundHorizontal)
                .verticalScroll(rememberScrollState())
        ) {
            // 날짜 선택
            WMSpacer(size = SpacerSize.XX_SMALL)
            DateTextField(
                modifier = Modifier.padding(),
                selectedDate = uiState.date,
                amount = uiState.amount,
                isTrailingIconVisible = false,
                onDateClick = { isShowDateSelectModalBottomSheet = true },
            )

            // 금액 입력
            WMSpacer()
            WMTextField(
                value = uiState.amount,
                onValueChange = { onUpdateAmount(it.toIntegerTextFieldValue()) },
                label = "금액",
                isRequire = true,
                maxLength = 10,
                placeholder = "금액을 입력해 주세요.",
                suffix = {
                    WMText(
                        text = "원",
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
                    )
                },
                visualTransformation = rememberIntegerVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isSupport = uiState.history?.repeatCycle != null || uiState.history?.installment != null,
                supportingContent = {
                    uiState.history?.let { history ->
                        // 반복
                        if (history.repeatCycle != null) {
                            RepeatCycle(
                                modifier = Modifier.padding(top = Padding.SpacerXS,),
                                date = history.date,
                                repeatCycle = history.repeatCycle,
                                onIsActiveChange = onUpdateRepeatCycleIsActive,
                                onModifyRepeatCycleClick = { onNavigateToRepeatDetail(history.repeatCycle.id) },
                            )
                        }
                        // 할부
                        if (history.installment != null) {
                            Installment(
                                modifier = Modifier.padding(top = Padding.SpacerXS,),
                                installment = history.installment,
                                installmentTime = history.installmentTime,
                                installmentHistoryItems = uiState.installmentHistoryItems,
                                onModifyClick = { isShowModifyInstallmentModalBottomSheet = true }
                            )
                        }
                    }
                }
            )

            // 내용 입력
            WMSpacer()
            ContentTextField(
                content = uiState.content,
                onUpdateContent = onUpdateContent
            )

            // 결제수단/자산
            if (uiState.largeCategory == LargeCategoryEnum.EXPENSES) {
                WMSpacer()
                PaymentMethodTextField(
                    selectedLargeCategory = uiState.largeCategory,
                    selectedPaymentMethod = uiState.paymentMethod,
                    placeholder = " 없음",
                    onPaymentMethodClick = { isShowPaymentMethodModalBottomSheet = true }
                )
            }

            // 카테고리
            WMSpacer()
            Category(
                category = uiState.category,
                categoryTag = uiState.categoryTag,
                onCategoryClick = { isShowCategorySelectModalBottomSheet = true }
            )

            WMSpacer(size = SpacerSize.LARGE)
        }

        // 저장 버튼
        WMFloatingButton(
            text = "저장",
            buttonSize = ButtonSize.LARGE,
            enabled = uiState.isSaveButtonEnable,
            onClick = onModifyHistory
        )
    }

    if (isShowDateSelectModalBottomSheet) {
        DateSelectModalBottomSheet(
            title = "",
            selectedDate = uiState.date,
            onSelectClick = onUpdateDate,
            onDismissRequest = { isShowDateSelectModalBottomSheet = false }
        )
    }

    if (isShowRepeatCycleModalBottomSheet) {
        RepeatCycleModalBottomSheet(
            selectedRepeatCycle = uiState.history?.repeatCycle?.repeatCycle,
            onConfirmClick = onUpdateRepeatCycle,
            onDismissRequest = { isShowRepeatCycleModalBottomSheet = false }
        )
    }

    if (isShowCategorySelectModalBottomSheet) {
        CategorySelectModalBottomSheet(
            categoryItems = uiState.categoryItems,
            selectedLargeCategory = uiState.largeCategory,
            selectedCategory = uiState.category,
            selectedCategoryTag = uiState.categoryTag,
            onConfirmClick = { category, categoryTag ->
                onUpdateCategory(category)
                onUpdateCategoryTag(categoryTag)
            },
            onDismissRequest = { isShowCategorySelectModalBottomSheet = false }
        )
    }

    if (isShowPaymentMethodModalBottomSheet) {
        PaymentMethodModalBottomSheet(
            selectedPaymentMethod = uiState.paymentMethod,
            paymentMethodItems = uiState.paymentMethodItems,
            onConfirmClick = onUpdatePaymentMethod,
            onDismissRequest = { isShowPaymentMethodModalBottomSheet = false }
        )
    }

    if (isShowModifyInstallmentModalBottomSheet) {
        ModifyInstallmentModalBottomSheet(
            totalAmount = uiState.history?.installment?.amount,
            totalCount = uiState.history?.installment?.count,
            onConfirmClick = { totalAmount, totalCount ->
                onUpdateInstallment(
                    totalAmount.toLongOrNull().default(),
                    totalCount.toLongOrNull().default()
                )
            },
            onDismissRequest = { isShowModifyInstallmentModalBottomSheet = false },
        )
    }

    if (isShowRemoveInstallmentConfirmDialog) {
        RemoveInstallmentConfirmDialog(
            onConfirmClick = { onRemoveHistory(false) },
            onDismissRequest = { isShowRemoveInstallmentConfirmDialog = false }
        )
    }
}

@Preview
@Composable
private fun HistoryDetailContentPreview() {
    WMTheme {
        HistoryDetailContent(
            uiState = HistoryDetailUiState(
                amount = TextFieldValue("10000"),
                content = TextFieldValue("점심 식사"),
                largeCategory = LargeCategoryEnum.EXPENSES
            ),
            largeCategory = LargeCategoryEnum.EXPENSES,
            onBack = {},
            onRemoveClick = {},
            onUpdateAmount = {},
            onUpdateDate = {},
            onUpdateRepeatCycleIsActive = {},
            onUpdateRepeatCycle = {},
            onUpdateCategory = {},
            onUpdateCategoryTag = {},
            onUpdatePaymentMethod = {},
            onUpdateContent = {},
            onUpdateInstallment = { _, _ -> },
            onModifyHistory = {},
            onRemoveHistory = {},
            onNavigateToRepeatDetail = {}
        )
    }
}
