package com.jie.wealthmate.feature.budget.budgetDetail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.jie.wealthmate.base.BaseScreen
import com.jie.wealthmate.component.WMText
import com.jie.wealthmate.component.topbar.TopBarItem
import com.jie.wealthmate.component.topbar.WMTopBar
import com.jie.wealthmate.feature.menu.management.categoryManagement.component.LargeCategoryEnum
import com.jie.wealthmate.theme.ColorBlue
import com.jie.wealthmate.theme.ColorGray
import com.jie.wealthmate.theme.ColorPrimary
import com.jie.wealthmate.theme.ColorRed
import com.jie.wealthmate.utils.formatWithCommas
import kotlinx.datetime.LocalDate
import org.koin.core.parameter.parametersOf

class BudgetDetailScreen(
    private val selectedMonth: LocalDate,
) : BaseScreen() {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel: BudgetDetailScreenModel = koinScreenModel { parametersOf(selectedMonth) }
        val uiState by screenModel.container.uiState.collectAsState()

        Column(modifier = Modifier.fillMaxSize().background(ColorGray.White)) {
            WMTopBar(
                title = TopBarItem.Title("${uiState.selectedMonth.year}년 ${uiState.selectedMonth.monthNumber}월 예산 상세"),
                readingItem = TopBarItem.ReadingItem(
                    action = { navigator.pop() }
                )
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {


                uiState.sections.forEach { section ->
                    if (section.items.isNotEmpty()) {
                        item {
                            SectionHeader(section = section)
                        }

                        items(section.items) { item ->
                            CategoryBudgetItem(
                                item = item,
                                largeCategory = section.largeCategory,
                                modifier = Modifier.padding(horizontal = 28.dp, vertical = 12.dp)
                            )
                        }

                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}


@Composable
private fun SectionHeader(section: BudgetSectionVo) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 28.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            WMText(
                text = section.largeCategory.label,
                style = typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            WMText(
                text = "예산 ${section.totalBudget.formatWithCommas()}원",
                style = typography.bodySmall.copy(color = ColorGray.Gray_500)
            )
        }
        HorizontalDivider(modifier = Modifier.padding(top = 8.dp), color = ColorGray.Gray_100)
    }
}

@Composable
private fun CategoryBudgetItem(
    item: CategoryBudgetVo,
    largeCategory: LargeCategoryEnum,
    modifier: Modifier = Modifier,
) {
    val barRatio by remember(item.budgetAmount, item.usedAmount) {
        derivedStateOf {
            if (item.budgetAmount > 0) (item.usedAmount.toFloat() / item.budgetAmount).coerceIn(
                0f,
                1f
            ) else 0f
        }
    }

    val statusColor by remember(largeCategory, item.percentage) {
        derivedStateOf {
            if (item.percentage > 80) {
                when (largeCategory) {
                    LargeCategoryEnum.INCOME -> ColorBlue.Blue_300
                    LargeCategoryEnum.EXPENSES -> ColorRed.Red_300
                    LargeCategoryEnum.SAVING -> ColorPrimary.Primary_500
                }
            } else {
                largeCategory.backgroundColor
            }
        }
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(ColorGray.Gray_100),
                contentAlignment = Alignment.Center
            ) {
                WMText(text = item.category.icon, style = typography.bodyMedium)
            }
            WMText(
                text = item.category.middleLabel,
                style = typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                modifier = Modifier.padding(start = 8.dp).weight(1f)
            )
            WMText(
                text = "${item.usedAmount.formatWithCommas()}원",
                style = typography.titleSmall.copy(fontWeight = FontWeight.SemiBold)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(6.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(ColorGray.Gray_200)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(barRatio)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(6.dp))
                        .background(statusColor)
                )
            }
            WMText(
                text = "${item.percentage}%",
                style = typography.bodySmall.copy(
                    color = ColorGray.Gray_500,
                    fontWeight = FontWeight.Medium
                ),
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        WMText(
            text = "/ ${item.budgetAmount.formatWithCommas()}원",
            style = typography.labelSmall.copy(color = ColorGray.Gray_400),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.End
        )
    }
}
