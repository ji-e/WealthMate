package com.jie.wealthmate.feature.home.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jie.wealthmate.component.HorizontalBar
import com.jie.wealthmate.component.WMText
import com.jie.wealthmate.theme.ColorBlue
import com.jie.wealthmate.theme.ColorGray
import com.jie.wealthmate.theme.ColorRed
import com.jie.wealthmate.theme.ColorSetting
import com.jie.wealthmate.theme.WMTheme
import com.jie.wealthmate.utils.formatWithCommas
import kotlin.math.absoluteValue
import kotlin.math.roundToInt
import kotlin.math.roundToLong

@Composable
fun RemainBudget(
    modifier: Modifier = Modifier,
    budgetAmount: Long,
    expensesAmount: Long,
) {
    val typography = MaterialTheme.typography

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        WMText(
            text = "이번 달 남은 금액",
            style = typography.titleSmall,
            color = ColorSetting.Info,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        AnimatedBudgetBar(spent = expensesAmount, totalBudget = budgetAmount)
    }
}

@Composable
private fun AnimatedBudgetBar(spent: Long, totalBudget: Long) {
    val typography = MaterialTheme.typography

    // 애니메이션되는 현재 남은 금액 상태 (초기값은 전체 예산)
    var currentRemaining by remember { mutableStateOf(totalBudget) }

    // 퍼센트 텍스트 계산 로직 최적화
    val percentageText by remember(currentRemaining, totalBudget) {
        derivedStateOf {
            if (totalBudget > 0) {
                val ratio =
                    (currentRemaining.absoluteValue.toFloat() / totalBudget * 100).roundToInt()
                if (currentRemaining >= 0) "총 $ratio% 남음" else "총 $ratio% 초과"
            } else {
                "0%"
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            WMText(
                text = "${formatWithCommas(currentRemaining.toString())}원",
                style = typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
                modifier = Modifier.weight(1f),
            )

            WMText(
                text = percentageText,
                style = typography.bodySmall.copy(color = ColorGray.Gray_500),
                modifier = Modifier.padding(bottom = 2.dp)
            )
        }

        HorizontalBar(
            spent = spent,
            total = totalBudget,
            firstBarColor = ColorBlue.Blue_200,
            secondBarColor = ColorRed.Red_300,
            changeColorRatio = 0.8f, // 80% 이상 사용 시 색상 변경
            isAnimated = true,
            onAnimatedSpentChange = { animatedSpentValue ->
                // 바가 차오름에 따라 남은 금액을 실시간으로 계산
                currentRemaining = (totalBudget - animatedSpentValue).roundToLong()
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun RemainBudgetPreview() {
    WMTheme {
        Column(
            modifier = Modifier
                .background(Color.White)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            // 1. 여유 있는 상태 (30% 사용)
            RemainBudget(
                budgetAmount = 1000000L,
                expensesAmount = 300000L
            )

            // 2. 아슬아슬한 상태 (95% 사용 - 색상 변경 확인)
            RemainBudget(
                budgetAmount = 1000000L,
                expensesAmount = 950000L
            )

            // 3. 예산 초과 상태 (120% 사용)
            RemainBudget(
                budgetAmount = 1000000L,
                expensesAmount = 1200000L
            )
        }
    }
}
