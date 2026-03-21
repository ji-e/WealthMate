package com.jie.wealthmate.feature.menu.management.repeatHistoryManagement.addRepeatHistory

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
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
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.jie.wealthmate.base.BaseScreen
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
import com.jie.wealthmate.feature.menu.management.repeatHistoryManagement.addRepeatHistory.AddRepeatHistoryViewModel.Companion.END_DATE
import com.jie.wealthmate.feature.menu.management.repeatHistoryManagement.addRepeatHistory.AddRepeatHistoryViewModel.Companion.START_DATE
import com.jie.wealthmate.feature.menu.management.repeatHistoryManagement.addRepeatHistory.component.RepeatCycleDateFullModalBottomSheet
import com.jie.wealthmate.feature.menu.management.repeatHistoryManagement.addRepeatHistory.component.RepeatCycleDateModalBottomSheet
import com.jie.wealthmate.feature.menu.management.repeatHistoryManagement.addRepeatHistory.component.RepeatCyclePeriod
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
    val uiState by viewModel.container.uiState.collectAsState()

    BaseScreen(
        viewModel = viewModel,
        onSideEffect = { sideEffect ->
            when (sideEffect) {
                is AddRepeatHistoryUiSideEffect.OnSuccessSave -> navController.popBackStack()
            }
        }
    ) {
        AddRepeatHistoryContent(
            uiState = uiState,
            onBack = {
                if (uiState.isDataChanged) {
                    // TODO: show save back dialog
                    navController.popBackStack()
                } else {
                    navController.popBackStack()
                }
            },
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

    val isLargeCategoryVisible by remember {
        derivedStateOf {
            scrollState.value > with(density) { 56.dp.toPx() }
        }
    }

    Column(
        modifier = modifier
            .navigationBarsPadding()
            .fillMaxSize()
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
            LargeCategorySelectBox(
                modifier = Modifier.padding(top = 4.dp),
                selectedLargeCategory = uiState.selectedLargeCategory,
                onLargeCategoryClick = onUpdateLargeCategory
            )

            RepeatCyclePeriod(
                modifier = Modifier.padding(top = 24.dp),
                startDate = uiState.startDate,
                endDate = uiState.endDate,
                onStartDateClick = { isShowStartDateSelectModalBottomSheet = true },
                onEndDateClick = { isShowEndDateSelectModalBottomSheet = true },
                onEndDateResetClick = { onUpdateDate(END_DATE, null) }
            )

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

            WMTextField(
                modifier = Modifier.padding(top = 4.dp),
                value = uiState.amount,
                onValueChange = { onUpdateAmount(it.toIntegerTextFieldValue()) },
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
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            )

            WMTextField(
                value = uiState.content,
                onValueChange = onUpdateContent,
                label = "내용",
                maxLength = 20,
                isRequire = true,
                placeholder = "내용을 입력해 주세요.",
                modifier = Modifier.padding(top = 4.dp)
            )

            CategorySelectionRow(
                modifier = Modifier.padding(top = 4.dp, bottom = 24.dp),
                categoryItems = uiState.categoryItems,
                selectedLargeCategory = uiState.selectedLargeCategory,
                selectedCategory = uiState.category,
                selectedCategoryTag = uiState.categoryTag,
                onCategoryClick = onUpdateCategory,
                onCategoryTagClick = onUpdateCategoryTag
            )

            if (uiState.selectedLargeCategory == LargeCategoryEnum.EXPENSES) {
                PaymentMethodTextField(
                    selectedLargeCategory = uiState.selectedLargeCategory,
                    selectedPaymentMethod = uiState.paymentMethod,
                    onPaymentMethodClick = { isShowPaymentMethodModalBottomSheet = true }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }

        WMFloatingButton(
            text = "저장",
            buttonSize = ButtonSize.LARGE,
            modifier = Modifier
                .padding(horizontal = 28.dp)
                .padding(bottom = 20.dp)
                .fillMaxWidth(),
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
