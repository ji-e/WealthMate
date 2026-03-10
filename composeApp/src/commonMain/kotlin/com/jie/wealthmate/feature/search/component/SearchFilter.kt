package com.jie.wealthmate.feature.search.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jie.wealthmate.component.WMText
import com.jie.wealthmate.feature.search.SearchSortOrder
import com.jie.wealthmate.theme.ColorGray
import com.jie.wealthmate.theme.ColorPrimary
import kotlinx.datetime.LocalDate
import kotlinx.datetime.number
import org.jetbrains.compose.resources.painterResource
import wealthmate.composeapp.generated.resources.Res
import wealthmate.composeapp.generated.resources.ic_arrow_drop_down

@Composable
fun SearchFilterRow(
    modifier: Modifier = Modifier,
    onPeriodClick: () -> Unit = {},
    onCategoryClick: () -> Unit = {},
    onTypeClick: () -> Unit = {},
    onPaymentMethodClick: () -> Unit = {},
    onSortClick: () -> Unit = {},
    startDate: LocalDate? = null,
    endDate: LocalDate? = null,
    selectedType: String? = null,
    isFixedCategory: Boolean = false,
    sortOrder: SearchSortOrder = SearchSortOrder.LATEST,
) {
    val periodLabel = when {
        startDate != null && endDate != null -> {
            if (startDate == endDate) {
                "${startDate.month.number}.${startDate.day}"
            } else {
                "${startDate.month.number}.${startDate.day}~${endDate.month.number}.${endDate.day}"
            }
        }
        startDate != null -> "${startDate.month.number}.${startDate.day}"
        endDate != null -> "${endDate.month.number}.${endDate.day}"
        else -> "기간"
    }

    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 28.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        item {
            SearchFilterChip(
                label = sortOrder.label,
                isSelected = true,
                onClick = onSortClick
            )
        }
        item {
            SearchFilterChip(
                label = periodLabel,
                isSelected = startDate != null || endDate != null,
                onClick = onPeriodClick
            )
        }
        item {
            SearchFilterChip(
                label = if (isFixedCategory) "고정 카테고리" else "카테고리",
                isSelected = isFixedCategory,
                onClick = onCategoryClick
            )
        }
        item {
            SearchFilterChip(
                label = selectedType ?: "거래구분",
                isSelected = selectedType != null,
                onClick = onTypeClick
            )
        }
        item {
            SearchFilterChip(
                label = "결제수단",
                onClick = onPaymentMethodClick
            )
        }
    }
}

@Composable
fun SearchFilterChip(
    label: String,
    isSelected: Boolean = false,
    onClick: () -> Unit,
) {
    val backgroundColor = if (isSelected) ColorPrimary.Primary_500 else ColorGray.Gray_50
    val contentColor = if (isSelected) ColorGray.White else ColorGray.Gray_700

    Row(
        modifier = Modifier
            .clip(CircleShape)
            .background(backgroundColor)
            .clickable { onClick() }
            .padding(vertical = 4.dp)
            .padding(start = 12.dp, end = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        WMText(
            text = label,
            style = Typography().bodyMedium.copy(
                color = contentColor,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
            )
        )
        Icon(
            painter = painterResource(Res.drawable.ic_arrow_drop_down),
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = contentColor
        )
    }
}
