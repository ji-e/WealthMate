package com.jie.wealthmate.feature.home.preparednessStatus

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.jie.wealthmate.component.WMListSelectionModalBottomSheet
import com.jie.wealthmate.component.topbar.TopBarItem
import com.jie.wealthmate.component.topbar.WMTopBar
import com.jie.wealthmate.feature.home.StatusType
import com.jie.wealthmate.feature.home.preparednessStatus.component.ExpensesSection
import com.jie.wealthmate.feature.home.preparednessStatus.component.IncomeSection
import com.jie.wealthmate.feature.home.preparednessStatus.component.PreparednessStatusFilter
import com.jie.wealthmate.feature.home.preparednessStatus.component.SavingSection
import com.jie.wealthmate.feature.home.preparednessStatus.component.SummarySection
import com.jie.wealthmate.feature.menu.management.categoryManagement.component.LargeCategoryEnum
import com.jie.wealthmate.theme.ColorGray
import org.koin.core.parameter.parametersOf

class PreparednessStatusScreen(
    private val initialStatusType: StatusType,
    private val initialLargeCategory: LargeCategoryEnum,
) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel: PreparednessStatusScreenModel = koinScreenModel {
            parametersOf(initialStatusType, initialLargeCategory)
        }
        val uiState by screenModel.container.uiState.collectAsState()

        var isShowStatusTypeModal by remember { mutableStateOf(false) }
        var isShowLargeCategoryModal by remember { mutableStateOf(false) }

        Column(modifier = Modifier.fillMaxSize()) {
            WMTopBar(
                title = TopBarItem.Title("현황"),
                readingItem = TopBarItem.ReadingItem(
                    action = { navigator.pop() }
                ),
                trailingCustomItem = TopBarItem.TrailingCustomItem {
                    PreparednessStatusFilter(
                        statusTypeLabel = uiState.statusType.label,
                        largeCategoryLabel = uiState.largeCategory.label,
                        onStatusTypeClick = { isShowStatusTypeModal = true },
                        onLargeCategoryClick = { isShowLargeCategoryModal = true }
                    )
                }
            )
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(ColorGray.Gray_50)
            ) {

                item {
                    // 요약 섹션
                    SummarySection(
                        statusType = uiState.statusType,
                        largeCategory = uiState.largeCategory,
                        diffPercentage = uiState.diffPercentage,
                        diffAmount = uiState.diffAmount,
                        currentAmount = uiState.currentAmount,
                    )
                }
                item {
                    when (uiState.largeCategory) {
                        LargeCategoryEnum.INCOME -> {
                            IncomeSection(
                                modifier = Modifier
                                    .padding(top = 24.dp)
                                    .padding(horizontal = 28.dp),
                                statusType = uiState.statusType,
                                totalAmount = uiState.currentAmount,
                                categoryComparisons = uiState.categoryComparisons,
                                fixedCategoryComparisons = uiState.fixedCategoryComparisons,
                            )
                        }

                        LargeCategoryEnum.EXPENSES -> {
                            ExpensesSection(
                                modifier = Modifier
                                    .padding(top = 24.dp)
                                    .padding(horizontal = 28.dp),
                                statusType = uiState.statusType,
                                maxIncreaseCategory = uiState.maxIncreaseCategory,
                                maxDecreaseCategory = uiState.maxDecreaseCategory,
                                categoryComparisons = uiState.categoryComparisons,
                                fixedCategoryComparisons = uiState.fixedCategoryComparisons
                            )
                        }

                        LargeCategoryEnum.SAVING -> {
                            SavingSection(
                                modifier = Modifier
                                    .padding(top = 24.dp)
                                    .padding(horizontal = 28.dp),
                                statusType = uiState.statusType,
                                totalAmount = uiState.currentAmount,
                                budgetAmount = uiState.budgetAmount,
                                categoryComparisons = uiState.categoryComparisons,
                                fixedCategoryComparisons = uiState.fixedCategoryComparisons,
                            )
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }

        if (isShowStatusTypeModal) {
            WMListSelectionModalBottomSheet(
                title = "기간 선택",
                items = StatusType.entries,
                selectedItem = uiState.statusType,
                itemLabel = { it.label },
                onItemSelected = { screenModel.updateStatusType(it) },
                onDismissRequest = { isShowStatusTypeModal = false }
            )
        }

        if (isShowLargeCategoryModal) {
            WMListSelectionModalBottomSheet(
                title = "카테고리 선택",
                items = LargeCategoryEnum.entries,
                selectedItem = uiState.largeCategory,
                itemLabel = { it.label },
                onItemSelected = { screenModel.updateLargeCategory(it) },
                onDismissRequest = { isShowLargeCategoryModal = false }
            )
        }
    }
}
