package com.jie.wealthmate.feature.budget.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jie.wealthmate.component.WMText
import com.jie.wealthmate.theme.ColorBlue
import com.jie.wealthmate.theme.ColorGray
import com.jie.wealthmate.theme.ColorRed
import com.jie.wealthmate.theme.noRippleClickable
import com.jie.wealthmate.utils.convertLocalDateToString
import com.jie.wealthmate.utils.formatDateKorYM
import com.jie.wealthmate.utils.formatWithCommas
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.painterResource
import wealthmate.composeapp.generated.resources.Res
import wealthmate.composeapp.generated.resources.ic_arrow_drop_down
import kotlin.math.roundToLong

@Composable
fun BudgetHeader(
    modifier: Modifier = Modifier,
    selectedMonth: LocalDate,
    onSelectedMonthClick: () -> Unit,
    usedAmount: Long = 0L,
    budgetAmount: Long = 0L,
) {
    var isStarted by remember { mutableStateOf(false) }

    val displaySelectedMonth = remember(selectedMonth) {
        selectedMonth.convertLocalDateToString(formatDateKorYM)
    }

    // 금액 애니메이션 (지출 합계)
    val animatedUsed by animateFloatAsState(
        targetValue = if (isStarted) usedAmount.toFloat() else 0f,
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
        label = "UsedAnimation"
    )

    // budgetAmount가 0일 경우를 대비해 remember에 key를 추가
    val barRatio by remember(budgetAmount) {
        derivedStateOf {
            if (budgetAmount > 0) (animatedUsed / budgetAmount).coerceIn(0f, 1f) else 0f
        }
    }

    val percentageText by remember(budgetAmount) {
        derivedStateOf {
            if (budgetAmount > 0) "${((animatedUsed / budgetAmount) * 100).toInt()}%" else "0%"
        }
    }

    val remainAmount by remember(budgetAmount) {
        derivedStateOf {
            (budgetAmount - animatedUsed.roundToLong())
        }
    }

    // 80% 이상 사용 시 경고 색상 애니메이션
    val animatedColor by animateColorAsState(
        targetValue = if (barRatio > 0.8f) ColorRed.Red_300 else ColorBlue.Blue_200,
        animationSpec = tween(500),
        label = "ColorAnimation"
    )

    LaunchedEffect(Unit) {
        isStarted = true
    }

    Column(modifier = modifier) {
        // 날짜 선택부
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
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                )
                Icon(
                    painter = painterResource(Res.drawable.ic_arrow_drop_down),
                    contentDescription = "날짜 선택",
                    modifier = Modifier.size(24.dp),
                    tint = ColorGray.Gray_700
                )
            }

            WMText(
                text = "예산 현황",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                modifier = Modifier.padding(start = 4.dp)
            )
        }

        // 사용 금액 및 예산 금액 표시
        Row(
            modifier = Modifier.padding(vertical = 8.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            WMText(
                text = "${animatedUsed.roundToLong().formatWithCommas()}원",
                style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.SemiBold),
            )

            WMText(
                text = " / ${budgetAmount.formatWithCommas()}원",
                modifier = Modifier.padding(bottom = 4.dp),
                style = MaterialTheme.typography.bodyLarge.copy(color = ColorGray.Gray_500),
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 사용률 정보 및 그래프
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row(
                modifier = Modifier.padding(bottom = 10.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                WMText(
                    text = "지출 예산 사용률 $percentageText",
                    style = MaterialTheme.typography.bodyMedium.copy(color = ColorGray.Gray_500),
                    modifier = Modifier.weight(1f)
                )

                WMText(
                    text = if (remainAmount >= 0) "잔액 ${remainAmount.formatWithCommas()}원" else "초과 ${(-remainAmount).formatWithCommas()}원",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = if (barRatio > 0.8f) ColorRed.Red_300 else ColorGray.Gray_700
                    )
                )
            }

            // 바 그래프 레이아웃
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
        }
    }
}
