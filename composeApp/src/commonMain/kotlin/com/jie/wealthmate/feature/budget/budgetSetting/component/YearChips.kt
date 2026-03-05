package com.jie.wealthmate.feature.budget.budgetSetting.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jie.wealthmate.component.ButtonSize
import com.jie.wealthmate.component.ButtonStyle
import com.jie.wealthmate.component.WMButton
import com.jie.wealthmate.theme.ColorGray
import com.jie.wealthmate.theme.ColorPrimary

@Composable
fun YearChips(
    modifier: Modifier = Modifier,
    yearList: List<String>,
    selectedYear: String,
    onYearSelected: (String) -> Unit,
) {
    // 선택 여부에 따른 컬러 객체를 미리 생성하여 재사용
    val selectedColors = ButtonDefaults.buttonColors(
        containerColor = ColorPrimary.Primary_500,
        contentColor = ColorGray.White
    )
    val unselectedColors = ButtonDefaults.filledTonalButtonColors(
        containerColor = ColorGray.Gray_50,
        contentColor = ColorGray.Gray_600
    )

    LazyRow(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 28.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            items = yearList,
            key = { it } // 리스트 아이템의 고유 키를 지정하여 효율적인 업데이트 지원
        ) { year ->
            val isSelected = year == selectedYear
            
            // 각 칩의 텍스트와 클릭 이벤트를 메모이제이션하여 불필요한 재계산 방지
            val yearText = remember(year) { "${year}년" }
            
            WMButton(
                text = yearText,
                buttonStyle = if (isSelected) ButtonStyle.FILLED else ButtonStyle.TONAL,
                buttonSize = ButtonSize.SMALL,
                isRounded = true,
                colors = if (isSelected) selectedColors else unselectedColors,
                onClick = { onYearSelected(year) }
            )
        }
    }
}
