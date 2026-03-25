package com.jie.wealthmate.feature.budget.budgetDetail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.jie.wealthmate.base.BaseScreen
import com.jie.wealthmate.component.topbar.TopBarItem
import com.jie.wealthmate.component.topbar.WMTopBar
import com.jie.wealthmate.feature.budget.budgetDetail.component.BudgetMonthlyHeader
import com.jie.wealthmate.feature.budget.budgetDetail.component.CategoryBudgetGroup
import com.jie.wealthmate.feature.budget.budgetDetail.component.SectionHeader
import com.jie.wealthmate.feature.home.StatusType
import com.jie.wealthmate.feature.menu.management.categoryManagement.component.LargeCategoryEnum
import com.jie.wealthmate.theme.ColorGray
import com.jie.wealthmate.theme.Padding
import com.jie.wealthmate.utils.convertLocalDateToString
import com.jie.wealthmate.utils.default
import com.jie.wealthmate.utils.formatDateHyphenYM
import io.github.aakira.napier.Napier
import kotlinx.datetime.LocalDate
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import wealthmate.composeapp.generated.resources.Res
import wealthmate.composeapp.generated.resources.ic_edit

@Composable
fun BudgetDetailScreen(
    navController: NavController,
    selectedMonth: LocalDate,
    viewModel: BudgetDetailViewModel = koinViewModel { parametersOf(selectedMonth) },
) {
    BaseScreen(viewModel = viewModel) { uiState ->
        BudgetDetailContent(
            uiState = uiState,
            onBack = { navController.popBackStack() },
            onEdit = {
                val monthStr = uiState.selectedMonth.convertLocalDateToString(formatDateHyphenYM)
                navController.navigate("addBudget?selectedYearMonth=$monthStr&isEditMode=true")
            },
            onClickDetail = { largeCategory, categoryId ->
                Napier.e("selectedMonth: ${selectedMonth.toString()}, ${uiState.selectedMonth.toString()}")
                navController.navigate("categoryExpenses/${StatusType.MONTH.name}/${largeCategory.name}/$categoryId/${uiState.selectedMonth.toString()}")
            },
            onToggleSection = viewModel::toggleSection
        )
    }
}

@Composable
fun BudgetDetailContent(
    uiState: BudgetDetailUiState,
    onBack: () -> Unit,
    onEdit: () -> Unit = {},
    onClickDetail: (LargeCategoryEnum, String) -> Unit,
    onToggleSection: (LargeCategoryEnum) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .navigationBarsPadding()
            .fillMaxSize()
            .background(ColorGray.White)
    ) {
        WMTopBar(
            title = TopBarItem.Title("${uiState.selectedMonth.year}년 ${uiState.selectedMonth.monthNumber}월 예산 상세"),
            readingItem = TopBarItem.ReadingItem(action = onBack),
            trailingItem = listOf(
                TopBarItem.TrailingItem(
                    iconRes = Res.drawable.ic_edit,
                    action = onEdit
                )
            )
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(ColorGray.Gray_50)
        ) {
            item {
                Spacer(modifier = Modifier.height(Padding.SpacerM))
                BudgetMonthlyHeader(
                    actualIncome = uiState.totalIncome,
                    lastActualIncome = uiState.lastTotalIncome,
                    actualExpense = uiState.totalExpense,
                    lastActualExpense = uiState.lastTotalExpense,
                    actualSaving = uiState.totalSaving,
                    lastActualSaving = uiState.lastTotalSaving,
                    modifier = Modifier
                        .padding(horizontal = Padding.BackgroundHorizontal)
                        .padding(top = Padding.SpacerXXS, bottom = Padding.SpacerM)
                )
            }

            uiState.sections.forEach { section ->
                if (section.groups.isNotEmpty()) {
                    val isExpanded = uiState.expandedStates[section.largeCategory].default()

                    item {
                        Column(
                            modifier = Modifier
                                .padding(horizontal = Padding.BackgroundHorizontal)
                                .background(ColorGray.White, RoundedCornerShape(8.dp))
                                .padding(vertical = Padding.SpacerXXS)
                        ) {
                            SectionHeader(
                                section = section,
                                isExpanded = isExpanded,
                                onToggle = {
                                    onToggleSection(section.largeCategory)
                                },
                                modifier = Modifier.padding(
                                    horizontal = Padding.SpacerS,
                                    vertical = Padding.ContainerVertical
                                )
                            )
                            AnimatedVisibility(
                                visible = isExpanded,
                                enter = fadeIn() + expandVertically(),
                                exit = fadeOut() + shrinkVertically()
                            ) {
                                Column {
                                    HorizontalDivider(
                                        modifier = Modifier.padding(
                                            top = Padding.SpacerXXS,
                                            bottom = Padding.ContainerVertical
                                        ),
                                        color = ColorGray.Gray_100
                                    )
                                    section.groups.forEach { group ->
                                        CategoryBudgetGroup(
                                            group = group,
                                            largeCategory = section.largeCategory,
                                            onClickDetail = {
                                                onClickDetail(section.largeCategory, group.category.id)
                                            },
                                            modifier = Modifier
                                                .padding(start = 10.dp, end = Padding.SpacerS)
                                                .padding(vertical = Padding.ContainerVertical)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(Padding.SpacerM))
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}
