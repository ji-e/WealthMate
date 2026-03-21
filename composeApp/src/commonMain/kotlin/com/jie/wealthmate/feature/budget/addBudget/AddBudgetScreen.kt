package com.jie.wealthmate.feature.budget.addBudget

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.jie.wealthmate.base.BaseScreen
import com.jie.wealthmate.component.ButtonSize
import com.jie.wealthmate.component.WMCheckBox
import com.jie.wealthmate.component.WMFloatingButton
import com.jie.wealthmate.component.WMText
import com.jie.wealthmate.component.textField.toIntegerTextFieldValue
import com.jie.wealthmate.component.topbar.TopBarItem
import com.jie.wealthmate.component.topbar.WMTopBar
import com.jie.wealthmate.feature.budget.addBudget.component.BudgetCategorySliderItem
import com.jie.wealthmate.feature.budget.addBudget.component.RemainBudget
import com.jie.wealthmate.feature.budget.addBudget.component.TotalBudget
import com.jie.wealthmate.feature.calendar.START_DATE
import com.jie.wealthmate.feature.calendar.addHistory.component.LargeCategorySelectBox
import com.jie.wealthmate.feature.calendar.component.SelectedCalendarModalBottomSheet
import com.jie.wealthmate.feature.menu.management.categoryManagement.component.LargeCategoryEnum
import com.jie.wealthmate.utils.convertDate
import com.jie.wealthmate.utils.formatDateKorYM
import com.jie.wealthmate.utils.today
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AddBudgetScreen(
    navController: NavController,
    selectedYearMonth: String? = null,
    isEditMode: Boolean = false,
    isCopyMode: Boolean = false,
    viewModel: AddBudgetViewModel = koinViewModel(),
) {
    BaseScreen(
        viewModel = viewModel,
        onBack = {
            if (viewModel.container.uiState.value.isDataChanged) {
                // TODO: Show confirm dialog if needed, but for now simple pop
                navController.popBackStack()
            } else {
                navController.popBackStack()
            }
        },
        onSideEffect = { sideEffect ->
            when (sideEffect) {
                is AddBudgetUiSideEffect.OnSuccess -> navController.popBackStack()
            }
        }
    ) { uiState ->
        AddBudgetContent(
            uiState = uiState,
            isEditMode = isEditMode,
            isCopyMode = isCopyMode,
            selectedYearMonth = selectedYearMonth,
            onBack = { navController.popBackStack() },
            onUpdateInit = { screenModelSelectedYearMonth, screenModelIsCopyMode ->
                viewModel.updateInit(screenModelSelectedYearMonth, screenModelIsCopyMode)
            },
            onUpdateSelectedMonth = viewModel::updateSelectedMonth,
            onUpdateLargeCategory = viewModel::updateLargeCategory,
            onUpdateIsCategoryTagInclude = viewModel::updateIsCategoryTagInclude,
            onUpdateIncomeTextField = viewModel::updateIncomeTextField,
            onUpdateExpensesTextField = viewModel::updateExpensesTextField,
            onUpdateSavingTextField = viewModel::updateSavingTextField,
            onSaveBudget = viewModel::saveBudget
        )
    }
}

@Composable
fun AddBudgetContent(
    uiState: AddBudgetUiState,
    isEditMode: Boolean,
    isCopyMode: Boolean,
    selectedYearMonth: String?,
    onBack: () -> Unit,
    onUpdateInit: (String?, Boolean) -> Unit,
    onUpdateSelectedMonth: (LocalDate) -> Unit,
    onUpdateLargeCategory: (LargeCategoryEnum) -> Unit,
    onUpdateIsCategoryTagInclude: (Boolean) -> Unit,
    onUpdateIncomeTextField: (String, TextFieldValue) -> Unit,
    onUpdateExpensesTextField: (String, TextFieldValue) -> Unit,
    onUpdateSavingTextField: (String, TextFieldValue) -> Unit,
    onSaveBudget: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var isShowSelectedCalendarModalBottomSheet by remember { mutableStateOf(false) }

    val monthItems = remember {
        val totalMonths = (today.year - START_DATE.year) * 12 + 12
        List(totalMonths) { START_DATE.plus(it, DateTimeUnit.MONTH) }
    }

    val categoryItems = remember(
        uiState.selectedLargeCategory,
        uiState.incomeCategoryItems,
        uiState.expensesCategoryItems,
        uiState.savingCategoryItems
    ) {
        when (uiState.selectedLargeCategory) {
            LargeCategoryEnum.INCOME -> uiState.incomeCategoryItems
            LargeCategoryEnum.EXPENSES -> uiState.expensesCategoryItems
            LargeCategoryEnum.SAVING -> uiState.savingCategoryItems
        }
    }

    val currentTextFieldMap = remember(
        uiState.selectedLargeCategory,
        uiState.incomeCategoryTextFieldMap,
        uiState.expensesCategoryTextFieldMap,
        uiState.savingCategoryTextFieldMap
    ) {
        when (uiState.selectedLargeCategory) {
            LargeCategoryEnum.INCOME -> uiState.incomeCategoryTextFieldMap
            LargeCategoryEnum.EXPENSES -> uiState.expensesCategoryTextFieldMap
            LargeCategoryEnum.SAVING -> uiState.savingCategoryTextFieldMap
        }
    }

    val onValueChange: (String, TextFieldValue) -> Unit = { id, value ->
        val integerValue = value.toIntegerTextFieldValue()
        when (uiState.selectedLargeCategory) {
            LargeCategoryEnum.INCOME -> onUpdateIncomeTextField(id, integerValue)
            LargeCategoryEnum.EXPENSES -> onUpdateExpensesTextField(id, integerValue)
            LargeCategoryEnum.SAVING -> onUpdateSavingTextField(id, integerValue)
        }
    }

    LaunchedEffect(Unit) {
        onUpdateInit(selectedYearMonth, isCopyMode)
    }

    val title = when {
        isCopyMode -> {
            val sourceMonth = selectedYearMonth?.convertDate(formatDateKorYM) ?: ""
            if (sourceMonth.isNotEmpty()) "예산 복사 (복사한 달: $sourceMonth)" else "예산 복사"
        }

        isEditMode -> "예산 수정"
        else -> "예산 추가"
    }

    Column(
        modifier = modifier
            .navigationBarsPadding()
            .fillMaxSize()
            .imePadding(),
    ) {
        WMTopBar(
            title = TopBarItem.Title(title),
            readingItem = TopBarItem.ReadingItem(action = onBack),
        )

        LazyColumn(modifier = Modifier.weight(1f)) {
            item {
                Column(modifier = Modifier.padding(horizontal = 28.dp)) {
                    TotalBudget(
                        selectedMonth = uiState.selectedMonth,
                        totalBudget = uiState.totalBudget,
                        onSelectedMonthClick = {
                            if (isCopyMode || !isEditMode) {
                                isShowSelectedCalendarModalBottomSheet = true
                            }
                        },
                    )

                    LargeCategorySelectBox(
                        modifier = Modifier.padding(top = 20.dp),
                        selectedLargeCategory = uiState.selectedLargeCategory,
                        onLargeCategoryClick = onUpdateLargeCategory
                    )
                }
            }

            item {
                RemainBudget(
                    modifier = Modifier
                        .padding(horizontal = 28.dp)
                        .padding(top = 12.dp),
                    selectedLargeCategory = uiState.selectedLargeCategory,
                    totalBudget = uiState.totalBudget,
                    remainBudget = uiState.remainBudget
                )
            }

            item {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 28.dp)
                        .padding(top = 40.dp, bottom = 16.dp)
                ) {
                    WMText(
                        text = "카테고리별 예산",
                        modifier = Modifier.weight(1f),
                        style = typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                    WMCheckBox(
                        label = "카테고리 상세 태그 포함",
                        checked = uiState.isCategoryTagInclude,
                        onCheckedChange = onUpdateIsCategoryTagInclude
                    )
                }
            }

            items(
                items = categoryItems,
                key = { it.id }
            ) { item ->
                BudgetCategorySliderItem(
                    selectedLargeCategory = uiState.selectedLargeCategory,
                    totalBudget = uiState.totalBudget,
                    remainBudget = uiState.remainBudget,
                    item = item,
                    textFieldValue = currentTextFieldMap[item.id] ?: TextFieldValue(),
                    isCategoryTagInclude = uiState.isCategoryTagInclude,
                    tagTextFieldMap = currentTextFieldMap,
                    onValueChange = onValueChange
                )
            }
        }

        WMFloatingButton(
            text = "저장",
            buttonSize = ButtonSize.LARGE,
            modifier = Modifier
                .padding(horizontal = 28.dp)
                .padding(bottom = 20.dp)
                .fillMaxWidth(),
            enabled = uiState.isDataChanged,
            onClick = onSaveBudget
        )
    }

    if (isShowSelectedCalendarModalBottomSheet) {
        SelectedCalendarModalBottomSheet(
            monthItem = monthItems,
            selectedMonth = uiState.selectedMonth,
            onMonthChange = { month ->
                onUpdateSelectedMonth(month)
                isShowSelectedCalendarModalBottomSheet = false
            },
            onDismissRequest = { isShowSelectedCalendarModalBottomSheet = false }
        )
    }
}
