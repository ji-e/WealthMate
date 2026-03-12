package com.jie.wealthmate.feature.search.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jie.wealthmate.component.WMText
import com.jie.wealthmate.feature.menu.management.categoryManagement.component.LargeCategoryEnum
import com.jie.wealthmate.theme.ColorGray
import com.jie.wealthmate.utils.formatWithCommas
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.datetime.LocalDate

@Composable
fun SearchSummary(
    startDate: LocalDate?,
    endDate: LocalDate?,
    summary: ImmutableMap<LargeCategoryEnum, Long>,
    selectedLargeCategories: List<LargeCategoryEnum>,
    modifier: Modifier = Modifier,
) {
    if (startDate == null || endDate == null) return

    val displayData = remember(summary, selectedLargeCategories) {
        val categoriesToShow = selectedLargeCategories.ifEmpty { LargeCategoryEnum.entries }

        categoriesToShow.map { it to (summary[it] ?: 0L) }
            .sortedBy { (category, _) ->
                when (category) {
                    LargeCategoryEnum.INCOME -> 0
                    LargeCategoryEnum.SAVING -> 1
                    LargeCategoryEnum.EXPENSES -> 2
                }
            }
    }

    if (displayData.isEmpty()) return

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 28.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(ColorGray.Gray_50)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        displayData.forEach { (category, amount) ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                WMText(
                    text = category.label,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = ColorGray.Gray_600,
                        fontWeight = FontWeight.Medium
                    )
                )
                WMText(
                    text = "${amount.formatWithCommas()}원",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }
        }
    }
}
