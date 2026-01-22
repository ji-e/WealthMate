package com.jie.wealthmate.feature.calendar.component.addHistory.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jie.wealthmate.component.ButtonStyle
import com.jie.wealthmate.component.WMButton
import com.jie.wealthmate.feature.menu.categorySetting.component.LargeCategoryEnum

@Composable
fun LargeCategorySelectBox(
    modifier: Modifier = Modifier,
    selectedLargeCategoryEnum: LargeCategoryEnum = LargeCategoryEnum.EXPENSES,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        LargeCategoryButton(
            isSelected = selectedLargeCategoryEnum == LargeCategoryEnum.INCOME,
            text = "수입"
        )
        LargeCategoryButton(
            isSelected = selectedLargeCategoryEnum == LargeCategoryEnum.EXPENSES,
            text = "지출"
        )
        LargeCategoryButton(
            isSelected = selectedLargeCategoryEnum == LargeCategoryEnum.SAVING,
            text = "저축"
        )
    }
}

@Composable
private fun RowScope.LargeCategoryButton(
    isSelected: Boolean,
    text: String,
    onClick: () -> Unit = {},
) {
    WMButton(
        text = text,
        buttonStyle = if (isSelected) ButtonStyle.FILLED else ButtonStyle.OUTLINED,
        onClick = onClick,
        modifier = Modifier.weight(1f)
    )
}