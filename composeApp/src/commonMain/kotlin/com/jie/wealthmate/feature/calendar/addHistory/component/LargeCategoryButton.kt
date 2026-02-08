package com.jie.wealthmate.feature.calendar.addHistory.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.jie.wealthmate.component.WMText
import com.jie.wealthmate.feature.menu.management.categoryManagement.component.LargeCategoryEnum
import com.jie.wealthmate.theme.ColorGray
import com.jie.wealthmate.theme.ColorPrimary

@Composable
fun LargeCategorySelectBox(
    modifier: Modifier = Modifier,
    selectedLargeCategory: LargeCategoryEnum,
    onLargeCategoryClick : (LargeCategoryEnum) -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(ColorGray.Gray_50),
    ) {
        LargeCategoryEnum.entries.forEach {
            LargeCategoryButton(
                isSelected = selectedLargeCategory == it,
                text = it.label,
                onClick = { onLargeCategoryClick(it) }
            )
        }
    }
}

@Composable
private fun RowScope.LargeCategoryButton(
    isSelected: Boolean,
    text: String,
    onClick: () -> Unit = {},
) {
    WMText(
        text = text,
        modifier = Modifier
            .padding(4.dp)
            .weight(1f)
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) ColorPrimary.Primary_500 else ColorGray.Gray_50)
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        textAlign = TextAlign.Center,
        style = Typography().bodyMedium.copy(
            fontWeight = FontWeight.SemiBold,
            color = if (isSelected) ColorGray.White else ColorGray.Gray_700
        )
    )
}