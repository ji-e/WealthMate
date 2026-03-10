package com.jie.wealthmate.feature.search.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jie.wealthmate.component.WMText
import com.jie.wealthmate.feature.menu.management.categoryManagement.component.LargeCategoryEnum
import com.jie.wealthmate.feature.search.SearchSortOrder
import com.jie.wealthmate.theme.ColorGray
import com.jie.wealthmate.theme.ColorPrimary
import com.jie.wealthmate.utils.convertLocalDateToString
import com.jie.wealthmate.utils.formatDateDotYYMD
import com.jie.wealthmate.vo.CategoryVo
import com.jie.wealthmate.vo.PaymentMethodVo
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.painterResource
import wealthmate.composeapp.generated.resources.Res
import wealthmate.composeapp.generated.resources.ic_arrow_drop_down
import wealthmate.composeapp.generated.resources.ic_refresh

@Composable
fun SearchFilterRow(
    modifier: Modifier = Modifier,
    sortOrder: SearchSortOrder = SearchSortOrder.LATEST,
    startDate: LocalDate? = null,
    endDate: LocalDate? = null,
    selectedLargeCategories: List<LargeCategoryEnum> = emptyList(),
    selectedCategories: List<CategoryVo> = emptyList(),
    selectedPaymentMethods: List<PaymentMethodVo> = emptyList(),
    totalCategories: Int = 0,
    totalPaymentMethods: Int = 0,
    onSortClick: () -> Unit = {},
    onPeriodClick: () -> Unit = {},
    onLargeCategoryClick: () -> Unit = {},
    onCategoryClick: () -> Unit = {},
    onPaymentMethodClick: () -> Unit = {},
    onResetClick: () -> Unit = {},
) {
    val periodLabel = when {
        startDate != null && endDate != null -> {
            if (startDate == endDate) {
                startDate.convertLocalDateToString(formatDateDotYYMD)
            } else {
                "${startDate.convertLocalDateToString(formatDateDotYYMD)}~${
                    endDate.convertLocalDateToString(
                        formatDateDotYYMD
                    )
                }"
            }
        }

        startDate != null -> startDate.convertLocalDateToString(formatDateDotYYMD)
        endDate != null -> endDate.convertLocalDateToString(formatDateDotYYMD)
        else -> "기간"
    }

    val typeLabel = when {
        selectedLargeCategories.isEmpty() ||
                selectedLargeCategories.size == LargeCategoryEnum.entries.size -> "거래구분"

        selectedLargeCategories.size == 1 -> selectedLargeCategories.first().label
        else -> "${selectedLargeCategories.first().label} 외 ${selectedLargeCategories.size - 1}"
    }

    val categoryLabel = when {
        selectedCategories.isEmpty() ||
                (totalCategories > 0 && selectedCategories.size == totalCategories) -> "카테고리"

        selectedCategories.size == 1 -> selectedCategories.first().middleLabel
        else -> "${selectedCategories.first().middleLabel} 외 ${selectedCategories.size - 1}"
    }

    val paymentMethodLabel = when {
        selectedPaymentMethods.isEmpty() ||
                (totalPaymentMethods > 0 && selectedPaymentMethods.size == totalPaymentMethods) -> "결제수단"

        selectedPaymentMethods.size == 1 -> selectedPaymentMethods.first().label
        else -> "${selectedPaymentMethods.first().label} 외 ${selectedPaymentMethods.size - 1}"
    }

    val isTypeSelected = selectedLargeCategories.isNotEmpty() &&
            selectedLargeCategories.size != LargeCategoryEnum.entries.size
    val isCategorySelected = (selectedCategories.isNotEmpty() &&
            (totalCategories > 0 && selectedCategories.size != totalCategories))
    val isPaymentMethodSelected = selectedPaymentMethods.isNotEmpty() &&
            (totalPaymentMethods > 0 && selectedPaymentMethods.size != totalPaymentMethods)

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
                label = typeLabel,
                isSelected = isTypeSelected,
                onClick = onLargeCategoryClick
            )
        }
        item {
            SearchFilterChip(
                label = categoryLabel,
                isSelected = isCategorySelected,
                onClick = onCategoryClick
            )
        }
        item {
            SearchFilterChip(
                label = paymentMethodLabel,
                isSelected = isPaymentMethodSelected,
                onClick = onPaymentMethodClick
            )
        }
        item {
            ResetFilterChip(onClick = onResetClick)
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
    val typography = MaterialTheme.typography

    Row(
        modifier = Modifier
            .clip(CircleShape)
            .background(backgroundColor)
            .clickable { onClick() }
            .padding(vertical = 4.dp)
            .padding(start = 12.dp, end = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        WMText(
            text = label,
            style = typography.bodyMedium.copy(
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

@Composable
fun ResetFilterChip(
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .clip(CircleShape)
            .background(Color.White)
            .border(1.dp, ColorGray.Gray_200, CircleShape)
            .clickable { onClick() }
            .padding(vertical = 4.dp)
            .padding(start = 12.dp, end = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        WMText(
            text = "초기화",
            style = MaterialTheme.typography.bodyMedium
        )
        Icon(
            painter = painterResource(Res.drawable.ic_refresh),
            contentDescription = null,
            modifier = Modifier
                .padding(start = 4.dp)
                .size(16.dp),
            tint = ColorGray.Gray_700
        )
    }
}
