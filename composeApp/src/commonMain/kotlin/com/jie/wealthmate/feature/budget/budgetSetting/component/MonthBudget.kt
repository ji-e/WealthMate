package com.jie.wealthmate.feature.budget.budgetSetting.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jie.wealthmate.component.WMIconButton
import com.jie.wealthmate.component.WMText
import com.jie.wealthmate.feature.budget.budgetSetting.MonthBudgetGroup
import com.jie.wealthmate.feature.budget.budgetSetting.YearlySummary
import com.jie.wealthmate.feature.menu.management.categoryManagement.component.LargeCategoryEnum
import com.jie.wealthmate.theme.ColorBlue
import com.jie.wealthmate.theme.ColorGray
import com.jie.wealthmate.theme.ColorPrimary
import com.jie.wealthmate.theme.ColorRed
import com.jie.wealthmate.utils.formatWithCommas
import com.jie.wealthmate.utils.today
import kotlinx.datetime.number
import wealthmate.composeapp.generated.resources.Res
import wealthmate.composeapp.generated.resources.ic_more_vert
import kotlin.math.absoluteValue

@Composable
fun MonthBudgetList(
    modifier: Modifier = Modifier,
    year: String,
    monthBudgetList: List<MonthBudgetGroup>,
    yearlySummary: YearlySummary,
    onMoreClick: (String, Boolean) -> Unit,
    onMonthClick: (String) -> Unit,
) {
    val listState = rememberLazyListState()

    LaunchedEffect(year) {
        listState.scrollToItem(0)
    }

    LazyColumn(
        state = listState,
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(
            items = monthBudgetList,
            key = { it.yearMonth }
        ) { group ->
            MonthBudgetItem(
                group = group,
                onMoreClick = { onMoreClick(group.yearMonth, group.budgets.isNotEmpty()) },
                onClick = { onMonthClick(group.yearMonth) },
            )
        }

        item {
            YearBudget(
                year = year,
                summary = yearlySummary
            )
        }
    }
}

@Composable
fun MonthBudgetItem(
    group: MonthBudgetGroup,
    onMoreClick: () -> Unit,
    onClick: () -> Unit,
) {
    val (year, month, isCurrentMonth) = remember(group.yearMonth) {
        val parts = group.yearMonth.split("-")
        val y = parts[0]
        val m = parts[1].toInt()
        val current = y.toInt() == today.year && m == today.month.number
        Triple(y, m, current)
    }

    val summary = remember(group.budgets) {
        var income = 0L
        var saving = 0L
        var expense = 0L
        var hasIncome = false
        var hasSaving = false
        var hasExpense = false

        group.budgets.forEach { item ->
            val amount = item.budget.amount
            when (item.category?.largeCategory) {
                LargeCategoryEnum.INCOME.name -> {
                    income += amount
                    hasIncome = true
                }

                LargeCategoryEnum.SAVING.name -> {
                    saving += amount
                    hasSaving = true
                }

                LargeCategoryEnum.EXPENSES.name -> {
                    expense += amount
                    hasExpense = true
                }
            }
        }
        BudgetSummary(
            incomeAmount = income,
            savingAmount = saving,
            expenseAmount = expense,
            hasIncome = hasIncome,
            hasSaving = hasSaving,
            hasExpense = hasExpense,
            hasAnyBudget = group.budgets.isNotEmpty()
        )
    }

    Column(
        modifier = Modifier
            .padding(horizontal = 28.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .border(width = 1.dp, color = ColorGray.Gray_100, shape = RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(bottom = 12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            WMText(
                text = "${year}년 ${month}월",
                style = typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            )

            if (isCurrentMonth || !summary.hasAnyBudget) {
                val badgeText = if (summary.hasAnyBudget) "진행중" else "미설정"
                val badgeColor = if (summary.hasAnyBudget) ColorPrimary.Primary_500 else ColorRed.Red_300

                WMText(
                    text = badgeText,
                    style = typography.labelSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = ColorGray.White
                    ),
                    modifier = Modifier
                        .padding(start = 4.dp)
                        .background(
                            color = badgeColor,
                            shape = CircleShape
                        )
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
            Spacer(modifier = Modifier.weight(1f))

            WMIconButton(
                iconRes = Res.drawable.ic_more_vert,
                onClick = onMoreClick
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(IntrinsicSize.Min)
        ) {
            BudgetSummaryItem(
                label = "수입",
                actualAmount = group.actualIncome,
                targetAmount = summary.incomeAmount,
                hasBudget = summary.hasIncome,
                modifier = Modifier.weight(1f)
            )

            VerticalDivider(
                modifier = Modifier.padding(horizontal = 8.dp),
                color = ColorGray.Gray_100,
                thickness = 1.dp
            )

            BudgetSummaryItem(
                label = "저축",
                actualAmount = group.actualSaving,
                targetAmount = summary.savingAmount,
                hasBudget = summary.hasSaving,
                modifier = Modifier.weight(1f)
            )

            VerticalDivider(
                modifier = Modifier.padding(horizontal = 8.dp),
                color = ColorGray.Gray_100,
                thickness = 1.dp
            )

            BudgetSummaryItem(
                label = "지출",
                actualAmount = group.actualExpense,
                targetAmount = summary.expenseAmount,
                hasBudget = summary.hasExpense,
                modifier = Modifier.weight(1f)
            )
        }

        Column(
            modifier = Modifier
                .padding(top = 12.dp)
                .padding(horizontal = 16.dp),
        ) {
            val spent = group.actualExpense
            val expenseAmount = summary.expenseAmount
            val hasExpenseBudget = summary.hasExpense

            val barRatio = remember(spent, expenseAmount, hasExpenseBudget) {
                if (hasExpenseBudget && expenseAmount > 0) {
                    (spent.toFloat() / expenseAmount).coerceIn(0f, 1f)
                } else {
                    1f
                }
            }

            val animatedColor by animateColorAsState(
                targetValue = when {
                    !hasExpenseBudget -> ColorGray.Gray_50
                    spent > 0 && (expenseAmount == 0L || spent >= expenseAmount) -> ColorRed.Red_300
                    else -> ColorBlue.Blue_200
                },
                animationSpec = tween(500),
                label = "ColorAnimation"
            )

            Row(
                modifier = Modifier
                    .padding(bottom = 4.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.Bottom
            ) {
                val percentageText = remember(spent, expenseAmount, hasExpenseBudget) {
                    when {
                        hasExpenseBudget.not() -> "미설정"
                        expenseAmount > 0 -> "${(spent.toFloat() / expenseAmount * 100).toInt()}%"
                        spent > 0 -> "100%+"
                        else -> "0%"
                    }
                }
                WMText(
                    text = "지출 예산 사용률",
                    style = typography.titleSmall.copy(
                        color = if (hasExpenseBudget) ColorGray.Gray_700 else ColorGray.Gray_200
                    ),
                    modifier = Modifier.weight(1f)
                )

                WMText(
                    text = percentageText,
                    style = typography.bodySmall.copy(
                        color = if (hasExpenseBudget) ColorGray.Gray_500 else ColorGray.Gray_200
                    ),
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(ColorGray.Gray_200)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(barRatio)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(8.dp))
                        .background(animatedColor)
                )
            }

            Column(
                modifier = Modifier
                    .wrapContentSize()
                    .padding(vertical = 4.dp)
            ) {
                if (hasExpenseBudget) {
                    val currentRemaining = expenseAmount - spent
                    val isMinus = currentRemaining < 0

                    WMText(
                        text = "${if (isMinus) "▲" else "▼"} ${
                            formatWithCommas(currentRemaining.absoluteValue.toString())
                        }원 ${if (isMinus) "초과" else "절약"}",
                        style = typography.bodySmall.copy(
                            color = if (isMinus) ColorRed.Red_300 else ColorBlue.Blue_300
                        ),
                    )
                } else {
                    WMText(
                        text = "지출 예산 설정 필요",
                        style = typography.bodySmall,
                    )
                }
            }
        }
    }
}

@Composable
private fun BudgetSummaryItem(
    label: String,
    actualAmount: Long,
    targetAmount: Long,
    hasBudget: Boolean,
    modifier: Modifier = Modifier,
    targetLabel: String = "목표",
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        WMText(
            text = label,
            style = typography.bodyMedium.copy(
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp
            ),
        )
        WMText(
            text = "${formatWithCommas(actualAmount.toString())}원",
            style = typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
            maxLines = 1,
            autoSize = TextAutoSize.StepBased(
                minFontSize = 9.sp,
                maxFontSize = 15.sp,
                stepSize = 1.sp
            )
        )
        Row {
            WMText(
                text = targetLabel,
                style = typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
            )
            WMText(
                text = if (hasBudget) " ${formatWithCommas(targetAmount.toString())}원" else " 미설정",
                style = typography.bodySmall,
                maxLines = 1,
                autoSize = TextAutoSize.StepBased(
                    minFontSize = 9.sp,
                    maxFontSize = 12.sp,
                    stepSize = 1.sp
                )
            )
        }
    }
}

private data class BudgetSummary(
    val incomeAmount: Long,
    val savingAmount: Long,
    val expenseAmount: Long,
    val hasIncome: Boolean,
    val hasSaving: Boolean,
    val hasExpense: Boolean,
    val hasAnyBudget: Boolean,
)
