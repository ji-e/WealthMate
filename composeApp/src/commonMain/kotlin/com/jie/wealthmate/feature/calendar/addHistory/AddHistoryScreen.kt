package com.jie.wealthmate.feature.calendar.addHistory

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.jie.wealthmate.base.BaseScreen
import com.jie.wealthmate.component.ButtonSize
import com.jie.wealthmate.component.WMDialog
import com.jie.wealthmate.component.WMFloatingButton
import com.jie.wealthmate.component.WMListSelectionModalBottomSheet
import com.jie.wealthmate.component.WMSaveBackDialog
import com.jie.wealthmate.component.WMText
import com.jie.wealthmate.component.textField.WMTextField
import com.jie.wealthmate.component.textField.rememberIntegerVisualTransformation
import com.jie.wealthmate.component.textField.toIntegerTextFieldValue
import com.jie.wealthmate.component.topbar.TopBarItem
import com.jie.wealthmate.component.topbar.WMTopBar
import com.jie.wealthmate.feature.calendar.addHistory.component.CategorySelectionRow
import com.jie.wealthmate.feature.calendar.addHistory.component.DateSelectModalBottomSheet
import com.jie.wealthmate.feature.calendar.addHistory.component.DateTextField
import com.jie.wealthmate.feature.calendar.addHistory.component.InstallmentModalBottomSheet
import com.jie.wealthmate.feature.calendar.addHistory.component.LargeCategorySelectBox
import com.jie.wealthmate.feature.calendar.addHistory.component.PaymentMethodModalBottomSheet
import com.jie.wealthmate.feature.calendar.addHistory.component.PaymentMethodTextField
import com.jie.wealthmate.feature.calendar.addHistory.component.RepeatCycleEnum
import com.jie.wealthmate.feature.menu.management.categoryManagement.component.LargeCategoryEnum
import com.jie.wealthmate.theme.ColorGray
import com.jie.wealthmate.utils.default
import com.jie.wealthmate.vo.CategoryTagVo
import com.jie.wealthmate.vo.CategoryVo
import com.jie.wealthmate.vo.PaymentMethodVo
import kotlinx.datetime.LocalDate
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun AddHistoryScreen(
    navController: NavController,
    initialSelectedDate: LocalDate,
    onBack: () -> Unit,
    viewModel: AddHistoryViewModel = koinViewModel() {
        parametersOf(initialSelectedDate)
    },
) {
    var isShowSaveBackDialog by remember { mutableStateOf(false) }
    var isShowConfirmDialog by remember { mutableStateOf(false) }
    var confirmContent by remember { mutableStateOf("") }
    var confirmCallback by remember { mutableStateOf<() -> Unit>({}) }

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
                is AddHistoryUiSideEffect.OnSuccessSave -> {
                    onBack()
                }
            }
        }
    ) { uiState ->
        AddHistoryContent(
            uiState = uiState,
            onBack = {
                if (uiState.isDataChanged) {
                    isShowSaveBackDialog = true
                } else {
                    onBack()
                }
            },
            onUpdateLargeCategory = viewModel::updateLargeCategory,
            onUpdateDate = viewModel::updateDate,
            onUpdateRepeatCycle = viewModel::updateRepeatCycle,
            onUpdateTotalInstallmentCount = viewModel::updateTotalInstallmentCount,
            onUpdateAmount = viewModel::updateAmount,
            onUpdateCategory = viewModel::updateCategory,
            onUpdateCategoryTag = viewModel::updateCategoryTag,
            onUpdatePaymentMethod = viewModel::updatePaymentMethod,
            onUpdateContent = viewModel::updateContent,
            onSaveHistory = viewModel::saveHistory,
            onShowConfirmDialog = { content, callback ->
                confirmContent = content
                confirmCallback = callback
                isShowConfirmDialog = true
            }
        )

        // 다이얼로그 처리
        if (isShowSaveBackDialog) {
            WMSaveBackDialog(
                onConfirm = {
                    isShowSaveBackDialog = false
                    onBack()
                },
                onDismiss = { isShowSaveBackDialog = false }
            )
        }

        if (isShowConfirmDialog) {
            WMDialog(
                contentText = confirmContent,
                confirmCallback = {
                    isShowConfirmDialog = false
                    confirmCallback()
                },
                onDismissRequest = { isShowConfirmDialog = false }
            )
        }
    }
}

@Composable
fun AddHistoryContent(
    uiState: AddHistoryUiState,
    onBack: () -> Unit,
    onUpdateLargeCategory: (LargeCategoryEnum) -> Unit,
    onUpdateDate: (LocalDate) -> Unit,
    onUpdateRepeatCycle: (RepeatCycleEnum?) -> Unit,
    onUpdateTotalInstallmentCount: (Long?) -> Unit,
    onUpdateAmount: (TextFieldValue) -> Unit,
    onUpdateCategory: (CategoryVo) -> Unit,
    onUpdateCategoryTag: (CategoryTagVo) -> Unit,
    onUpdatePaymentMethod: (PaymentMethodVo?) -> Unit,
    onUpdateContent: (TextFieldValue) -> Unit,
    onSaveHistory: () -> Unit,
    onShowConfirmDialog: (String, () -> Unit) -> Unit,
    modifier: Modifier = Modifier,
) {
    var isShowDateSelectModalBottomSheet by remember { mutableStateOf(false) }
    var isShowRepeatCycleModalBottomSheet by remember { mutableStateOf(false) }
    var isShowInstallmentModalBottomSheet by remember { mutableStateOf(false) }
    var isShowPaymentMethodModalBottomSheet by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()
    val density = LocalDensity.current

    val isLargeCategoryVisible by remember {
        derivedStateOf {
            scrollState.value > with(density) { 56.dp.toPx() }
        }
    }

    Column(
        modifier = modifier
            .background(ColorGray.White)
            .fillMaxSize()
            .navigationBarsPadding()
            .imePadding()
    ) {
        WMTopBar(
            title = TopBarItem.Title("내역 추가"),
            readingItem = TopBarItem.ReadingItem().copy(
                action = { onBack() }
            ),
            trailingCustomItem = if (isLargeCategoryVisible) {
                TopBarItem.TrailingCustomItem {
                    val selectedLargeCategoryEnum = uiState.selectedLargeCategory
                    WMText(
                        text = selectedLargeCategoryEnum.label,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = ColorGray.White,
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .clip(CircleShape)
                            .background(selectedLargeCategoryEnum.middleColor)
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
                onLargeCategoryClick = onUpdateLargeCategory
            )

            // 날짜 선택
            DateTextField(
                modifier = Modifier.padding(top = 24.dp),
                selectedDate = uiState.date,
                amount = uiState.amount,
                repeatCycle = uiState.repeatCycle,
                totalInstallmentCount = uiState.totalInstallmentCount,
                onDateClick = { isShowDateSelectModalBottomSheet = true },
                onRepeatClick = {
                    if (uiState.totalInstallmentCount != null) {
                        onShowConfirmDialog(
                            "할부가 선택되어있습니다.\n할부 선택을 취소하시겠습니까?",
                        ) {
                            onUpdateTotalInstallmentCount(null)
                            isShowRepeatCycleModalBottomSheet = true
                        }
                    } else {
                        isShowRepeatCycleModalBottomSheet = true
                    }
                },
                onInstallmentClick = {
                    if (uiState.repeatCycle != null) {
                        onShowConfirmDialog(
                            "반복 주기가 선택되어있습니다.\n반복 주기 선택을 취소하시겠습니까?",
                        ) {
                            onUpdateRepeatCycle(null)
                            isShowInstallmentModalBottomSheet = true
                        }
                    } else {
                        isShowInstallmentModalBottomSheet = true
                    }
                },
                onResetClick = {
                    onUpdateRepeatCycle(null)
                    onUpdateTotalInstallmentCount(null)
                }
            )

            // 금액 입력
            WMTextField(
                modifier = Modifier.padding(top = 20.dp),
                value = uiState.amount,
                onValueChange = {
                    onUpdateAmount(it.toIntegerTextFieldValue())
                },
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
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
            )

            CategorySelectionRow(
                modifier = Modifier.padding(bottom = 24.dp),
                categoryItems = uiState.categoryItems,
                selectedLargeCategory = uiState.selectedLargeCategory,
                selectedCategory = uiState.category,
                selectedCategoryTag = uiState.categoryTag,
                onCategoryClick = { category ->
                    if (category.isFixed && uiState.repeatCycle == null) {
                        onShowConfirmDialog(
                            "고정 카테고리는 반복 설정이 필요합니다.\n반복 설정을 하시겠습니까?",
                        ) {
                            onUpdateCategory(category)
                            isShowRepeatCycleModalBottomSheet = true
                        }
                    } else {
                        onUpdateCategory(category)
                    }
                },
                onCategoryTagClick = onUpdateCategoryTag
            )

            // 결제 수단 선택
            PaymentMethodTextField(
                modifier = Modifier.padding(top = 24.dp),
                selectedLargeCategory = uiState.selectedLargeCategory,
                selectedPaymentMethod = uiState.paymentMethod,
                onPaymentMethodClick = { isShowPaymentMethodModalBottomSheet = true }
            )

            // 내용 입력
            WMTextField(
                modifier = Modifier.padding(top = 24.dp, bottom = 48.dp),
                value = uiState.content,
                onValueChange = onUpdateContent,
                label = "내용",
                maxLength = 20,
                isRequire = uiState.category?.isFixed.default() || uiState.repeatCycle != null,
                placeholder = "내용을 입력해 주세요.",
            )
        }

        WMFloatingButton(
            text = "저장하기",
            enabled = uiState.isSaveButtonEnable,
            onClick = onSaveHistory,
            buttonSize = ButtonSize.LARGE,
        )
    }

    // 바텀 시트
    if (isShowDateSelectModalBottomSheet) {
        DateSelectModalBottomSheet(
            onDismissRequest = { isShowDateSelectModalBottomSheet = false },
            onSelectClick = onUpdateDate,
            selectedDate = uiState.date
        )
    }

    if (isShowRepeatCycleModalBottomSheet) {
        WMListSelectionModalBottomSheet(
            title = "반복 주기 선택",
            items = RepeatCycleEnum.entries,
            selectedItem = uiState.repeatCycle,
            itemLabel = { it.label },
            onItemSelected = onUpdateRepeatCycle,
            onDismissRequest = { isShowRepeatCycleModalBottomSheet = false }
        )
    }

    if (isShowInstallmentModalBottomSheet) {
        InstallmentModalBottomSheet(
            onDismissRequest = { isShowInstallmentModalBottomSheet = false },
            onConfirmClick = onUpdateTotalInstallmentCount,
            totalInstallmentCount = uiState.totalInstallmentCount
        )
    }

    if (isShowPaymentMethodModalBottomSheet) {
        PaymentMethodModalBottomSheet(
            onDismissRequest = { isShowPaymentMethodModalBottomSheet = false },
            onConfirmClick = onUpdatePaymentMethod,
            selectedPaymentMethod = uiState.paymentMethod,
            paymentMethodItems = uiState.paymentMethodItems
        )
    }
}
