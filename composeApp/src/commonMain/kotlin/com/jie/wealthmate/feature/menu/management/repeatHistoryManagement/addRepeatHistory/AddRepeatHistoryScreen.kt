package com.jie.wealthmate.feature.menu.management.repeatHistoryManagement.addRepeatHistory

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.jie.wealthmate.base.BaseScreen
import com.jie.wealthmate.component.ButtonSize
import com.jie.wealthmate.component.SpacerSize
import com.jie.wealthmate.component.WMFloatingButton
import com.jie.wealthmate.component.WMSaveBackDialog
import com.jie.wealthmate.component.WMSpacer
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
import com.jie.wealthmate.feature.menu.management.repeatHistoryManagement.addRepeatHistory.AddRepeatHistoryViewModel.Companion.END_DATE
import com.jie.wealthmate.feature.menu.management.repeatHistoryManagement.addRepeatHistory.AddRepeatHistoryViewModel.Companion.START_DATE
import com.jie.wealthmate.feature.menu.management.repeatHistoryManagement.addRepeatHistory.component.RepeatCycleDateFullModalBottomSheet
import com.jie.wealthmate.feature.menu.management.repeatHistoryManagement.addRepeatHistory.component.RepeatCycleDateModalBottomSheet
import com.jie.wealthmate.feature.menu.management.repeatHistoryManagement.addRepeatHistory.component.RepeatCyclePeriod
import com.jie.wealthmate.theme.WMTheme
import com.jie.wealthmate.utils.default
import com.jie.wealthmate.vo.CategoryTagVo
import com.jie.wealthmate.vo.CategoryVo
import com.jie.wealthmate.vo.PaymentMethodVo
import kotlinx.datetime.LocalDate
import kotlinx.datetime.number
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun AddRepeatHistoryScreen(
    navController: NavController,
    largeCategory: LargeCategoryEnum,
    viewModel: AddRepeatHistoryViewModel = koinViewModel() {
        parametersOf(largeCategory)
    },
) {
    var isShowSaveBackDialog by remember { mutableStateOf(false) }

    val onBack: () -> Unit = {
        if (viewModel.container.uiState.value.isDataChanged) {
            isShowSaveBackDialog = true
        } else {
            navController.popBackStack()
        }
    }

    BaseScreen(
        viewModel = viewModel,
        onSideEffect = { sideEffect ->
            when (sideEffect) {
                is AddRepeatHistoryUiSideEffect.OnSuccessSave -> navController.popBackStack()
            }
        }
    ) { uiState ->
        AddRepeatHistoryContent(
            uiState = uiState,
            onBack = onBack,
            onUpdateLargeCategory = viewModel::updateLargeCategory,
            onUpdateDate = viewModel::updateDate,
            onUpdateRepeatCycle = viewModel::updateRepeatCycle,
            onUpdateRepeatCycleDate = viewModel::updateRepeatCycleDate,
            onUpdateRepeatCycleDateFull = viewModel::updateRepeatCycleDateFull,
            onUpdateAmount = viewModel::updateAmount,
            onUpdateContent = viewModel::updateContent,
            onUpdateCategory = viewModel::updateCategory,
            onUpdateCategoryTag = viewModel::updateCategoryTag,
            onUpdatePaymentMethod = viewModel::updatePaymentMethod,
            onSaveRepeatCycle = viewModel::saveRepeatCycle
        )
    }

    if (isShowSaveBackDialog) {
        WMSaveBackDialog(
            onConfirm = {
                isShowSaveBackDialog = false
                navController.popBackStack()
            },
            onDismiss = { isShowSaveBackDialog = false }
        )
    }
}

@Composable
fun AddRepeatHistoryContent(
    uiState: AddRepeatHistoryUiState,
    onBack: () -> Unit,
    onUpdateLargeCategory: (LargeCategoryEnum) -> Unit,
    onUpdateDate: (String, LocalDate?) -> Unit,
    onUpdateRepeatCycle: (RepeatCycleEnum?) -> Unit,
    onUpdateRepeatCycleDate: (Int?) -> Unit,
    onUpdateRepeatCycleDateFull: (Int, Int) -> Unit,
    onUpdateAmount: (TextFieldValue) -> Unit,
    onUpdateContent: (TextFieldValue) -> Unit,
    onUpdateCategory: (CategoryVo) -> Unit,
    onUpdateCategoryTag: (CategoryTagVo) -> Unit,
    onUpdatePaymentMethod: (PaymentMethodVo?) -> Unit,
    onSaveRepeatCycle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var isShowStartDateSelectModalBottomSheet by remember { mutableStateOf(false) }
    var isShowEndDateSelectModalBottomSheet by remember { mutableStateOf(false) }
    var isShowRepeatCycleModalBottomSheet by remember { mutableStateOf(false) }
    var isShowRepeatDateModalBottomSheet by remember { mutableStateOf(false) }
    var isShowPaymentMethodModalBottomSheet by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()
    val density = LocalDensity.current
    val thresholdPx = remember(density) { with(density) { 56.dp.toPx() } }

    val isLargeCategoryVisible by remember {
        derivedStateOf {
            scrollState.value > thresholdPx
        }
    }

    Column(
        modifier = modifier
            .navigationBarsPadding()
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .imePadding()
    ) {
        WMTopBar(
            title = TopBarItem.Title("${MenuEnum.REPEAT_HISTORY.label} 추가"),
            readingItem = TopBarItem.ReadingItem(action = onBack),
            trailingCustomItem = if (isLargeCategoryVisible) {
                TopBarItem.TrailingCustomItem {
                    val selectedLargeCategoryEnum = uiState.selectedLargeCategory
                    WMText(
                        text = selectedLargeCategoryEnum.label,
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
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

            WMSpacer(size = SpacerSize.XX_SMALL)

            LargeCategorySelectBox(
                selectedLargeCategory = uiState.selectedLargeCategory,
                onLargeCategoryClick = onUpdateLargeCategory
            )

            WMSpacer()
            RepeatCyclePeriod(
                startDate = uiState.startDate,
                endDate = uiState.endDate,
                onStartDateClick = { isShowStartDateSelectModalBottomSheet = true },
                onEndDateClick = { isShowEndDateSelectModalBottomSheet = true },
                onEndDateResetClick = { onUpdateDate(END_DATE, null) }
            )

            WMSpacer(size = SpacerSize.LARGE)

            WMTextField(
                value = uiState.repeatCycle?.shortDescription.default(),
                onValueChange = {},
                label = "반복 주기",
                readOnly = true,
                isRequire = true,
                isSupport = false,
                placeholder = "반복 주기를 설정해 주세요.",
                onReadOnlyClick = { isShowRepeatCycleModalBottomSheet = true },
            )

            if (uiState.repeatCycle == RepeatCycleEnum.WEEKLY || uiState.repeatCycle == RepeatCycleEnum.MONTHLY || uiState.repeatCycle == RepeatCycleEnum.YEARLY) {
                WMSpacer(size = SpacerSize.LARGE)

                WMTextField(
                    value = uiState.repeatCycleDateText,
                    onValueChange = {},
                    label = "반복 날짜",
                    readOnly = true,
                    isRequire = true,
                    placeholder = "반복될 날짜를 설정해 주세요.",
                    isSupport = false,
                    onReadOnlyClick = { isShowRepeatDateModalBottomSheet = true },
                )
            }

            WMSpacer(size = SpacerSize.LARGE)
            WMTextField(
                isSupport = false,
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
            )

            WMSpacer(size = SpacerSize.LARGE)
            WMTextField(
                value = uiState.content,
                onValueChange = onUpdateContent,
                label = "내용",
                maxLength = 20,
                isSupport = false,
                placeholder = "내용을 입력해 주세요.",
            )

            if (uiState.selectedLargeCategory == LargeCategoryEnum.EXPENSES) {
                WMSpacer(size = SpacerSize.LARGE)
                PaymentMethodTextField(
                    selectedLargeCategory = uiState.selectedLargeCategory,
                    selectedPaymentMethod = uiState.paymentMethod,
                    onPaymentMethodClick = { isShowPaymentMethodModalBottomSheet = true }
                )
            }

            WMSpacer(size = SpacerSize.LARGE)
            CategorySelectionRow(
                categoryItems = uiState.categoryItems,
                selectedLargeCategory = uiState.selectedLargeCategory,
                selectedCategory = uiState.category,
                selectedCategoryTag = uiState.categoryTag,
                onCategoryClick = onUpdateCategory,
                onCategoryTagClick = onUpdateCategoryTag
            )

            WMSpacer(size = SpacerSize.LARGE)
        }

        WMFloatingButton(
            text = "저장",
            buttonSize = ButtonSize.LARGE,
            enabled = uiState.isSaveButtonEnable,
            onClick = onSaveRepeatCycle
        )
    }

    if (isShowStartDateSelectModalBottomSheet) {
        DateSelectModalBottomSheet(
            title = START_DATE,
            selectedDate = uiState.startDate,
            onSelectClick = { onUpdateDate(START_DATE, it) },
            onDismissRequest = { isShowStartDateSelectModalBottomSheet = false }
        )
    }
    if (isShowEndDateSelectModalBottomSheet) {
        DateSelectModalBottomSheet(
            title = END_DATE,
            selectedDate = uiState.endDate,
            onSelectClick = { onUpdateDate(END_DATE, it) },
            onDismissRequest = { isShowEndDateSelectModalBottomSheet = false }
        )
    }

    if (isShowRepeatCycleModalBottomSheet) {
        RepeatCycleModalBottomSheet(
            selectedRepeatCycle = uiState.repeatCycle,
            onConfirmClick = onUpdateRepeatCycle,
            onDismissRequest = { isShowRepeatCycleModalBottomSheet = false }
        )
    }

    if (isShowRepeatDateModalBottomSheet) {
        if (uiState.repeatCycle == RepeatCycleEnum.YEARLY) {
            RepeatCycleDateFullModalBottomSheet(
                repeatCycleDateMonth = uiState.repeatCycleDateFull?.month?.number,
                repeatCycleDateDay = uiState.repeatCycleDateFull?.day,
                onConfirmClick = onUpdateRepeatCycleDateFull,
                onDismissRequest = { isShowRepeatDateModalBottomSheet = false }
            )
        } else {
            RepeatCycleDateModalBottomSheet(
                repeatCycleDate = uiState.repeatCycleDate,
                repeatCycleDateItems = uiState.repeatCycleDateItems,
                onConfirmClick = onUpdateRepeatCycleDate,
                onDismissRequest = { isShowRepeatDateModalBottomSheet = false }
            )
        }
    }

    if (isShowPaymentMethodModalBottomSheet) {
        PaymentMethodModalBottomSheet(
            selectedPaymentMethod = uiState.paymentMethod,
            paymentMethodItems = uiState.paymentMethodItems,
            onConfirmClick = onUpdatePaymentMethod,
            onDismissRequest = { isShowPaymentMethodModalBottomSheet = false }
        )
    }
}

@Preview
@Composable
private fun AddRepeatHistoryContentPreview() {
    WMTheme {
        AddRepeatHistoryContent(
            uiState = AddRepeatHistoryUiState(
                selectedLargeCategory = LargeCategoryEnum.EXPENSES,
                repeatCycle = RepeatCycleEnum.MONTHLY,
                repeatCycleDate = 15
            ),
            onBack = {},
            onUpdateLargeCategory = {},
            onUpdateDate = { _, _ -> },
            onUpdateRepeatCycle = {},
            onUpdateRepeatCycleDate = {},
            onUpdateRepeatCycleDateFull = { _, _ -> },
            onUpdateAmount = {},
            onUpdateContent = {},
            onUpdateCategory = {},
            onUpdateCategoryTag = {},
            onUpdatePaymentMethod = {},
            onSaveRepeatCycle = {}
        )
    }
}
