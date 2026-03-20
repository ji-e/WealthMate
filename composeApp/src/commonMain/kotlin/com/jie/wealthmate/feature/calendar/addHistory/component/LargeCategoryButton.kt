package com.jie.wealthmate.feature.calendar.addHistory.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jie.wealthmate.component.WMText
import com.jie.wealthmate.feature.menu.management.categoryManagement.component.LargeCategoryEnum
import com.jie.wealthmate.theme.ColorGray

private object LargeCategorySelectBoxDefaults {
    val ContainerShape = RoundedCornerShape(8.dp)
    val ButtonShape = RoundedCornerShape(6.dp)
    val ContainerBackground = ColorGray.Gray_50
    val Padding = 4.dp
    val VerticalPadding = 10.dp
}

@Composable
fun LargeCategorySelectBox(
    modifier: Modifier = Modifier,
    selectedLargeCategory: LargeCategoryEnum,
    onLargeCategoryClick: (LargeCategoryEnum) -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(LargeCategorySelectBoxDefaults.ContainerShape)
            .background(LargeCategorySelectBoxDefaults.ContainerBackground)
            .padding(LargeCategorySelectBoxDefaults.Padding),
        horizontalArrangement = Arrangement.spacedBy(LargeCategorySelectBoxDefaults.Padding)
    ) {
        LargeCategoryEnum.entries.forEach { category ->
            LargeCategoryButton(
                modifier = Modifier.weight(1f),
                isSelected = selectedLargeCategory == category,
                category = category,
                onClick = { onLargeCategoryClick(category) }
            )
        }
    }
}

@Composable
private fun LargeCategoryButton(
    modifier: Modifier = Modifier,
    isSelected: Boolean,
    category: LargeCategoryEnum,
    onClick: () -> Unit,
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) category.middleColor else LargeCategorySelectBoxDefaults.ContainerBackground,
        label = "LargeCategoryButtonBg"
    )
    val textColor by animateColorAsState(
        targetValue = if (isSelected) ColorGray.White else ColorGray.Gray_700,
        label = "LargeCategoryButtonText"
    )

    Box(
        modifier = modifier
            .clip(LargeCategorySelectBoxDefaults.ButtonShape)
            .background(backgroundColor)
            .clickable { onClick() }
            .padding(vertical = LargeCategorySelectBoxDefaults.VerticalPadding),
        contentAlignment = Alignment.Center
    ) {
        WMText(
            text = category.label,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
            color = textColor
        )
    }
}
