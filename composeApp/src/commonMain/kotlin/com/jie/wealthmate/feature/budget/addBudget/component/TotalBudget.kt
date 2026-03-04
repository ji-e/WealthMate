package com.jie.wealthmate.feature.budget.addBudget.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jie.wealthmate.component.LabelText
import com.jie.wealthmate.component.WMText
import com.jie.wealthmate.theme.ColorGray
import com.jie.wealthmate.theme.noRippleClickable
import com.jie.wealthmate.utils.convertLocalDateToString
import com.jie.wealthmate.utils.formatDateKorYM
import com.jie.wealthmate.utils.formatWithCommas
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.painterResource
import wealthmate.composeapp.generated.resources.Res
import wealthmate.composeapp.generated.resources.ic_arrow_drop_down

@Composable
fun TotalBudget(
    modifier: Modifier = Modifier,
    selectedMonth: LocalDate,
    totalBudget: Long,
    onSelectedMonthClick: () -> Unit,
) {
    val displaySelectedMonth = remember(selectedMonth) {
        selectedMonth.convertLocalDateToString(formatDateKorYM)
    }

    val budgetText = remember(totalBudget) {
        "${formatWithCommas(totalBudget.toString())}원"
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.noRippleClickable(onClick = onSelectedMonthClick),
                verticalAlignment = Alignment.CenterVertically
            ) {
                WMText(
                    text = displaySelectedMonth,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = ColorGray.Gray_700
                    )
                )
                Icon(
                    painter = painterResource(Res.drawable.ic_arrow_drop_down),
                    contentDescription = "날짜 선택",
                    modifier = Modifier.size(24.dp),
                    tint = ColorGray.Gray_700
                )
            }

            LabelText(
                text = "총 예산 (수입)",
                modifier = Modifier.padding(start = 4.dp)
            )
        }

        WMText(
            text = budgetText,
            style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.SemiBold),
            modifier = Modifier.padding(vertical = 8.dp)
        )
    }
}
