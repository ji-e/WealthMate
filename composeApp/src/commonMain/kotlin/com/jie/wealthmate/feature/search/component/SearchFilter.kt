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
import androidx.compose.runtime.remember
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
    onSortClick: () -> Unit = {},
    onPeriodClick: () -> Unit = {},
    onLargeCategoryClick: () -> Unit = {},
    onCategoryClick: () -> Unit = {},
    onPaymentMethodClick: () -> Unit = {},
    onResetClick: () -> Unit = {},
) {
    val periodLabel = remember(startDate, endDate) {
        val startStr = startDate?.convertLocalDateToString(formatDateDotYYMD)
        val endStr = endDate?.convertLocalDateToString(formatDateDotYYMD)
        when {
            startStr != null && endStr != null -> if (startStr == endStr) startStr else "$startStr~$endStr"
            startStr != null -> startStr
            endStr != null -> endStr
            else -> "기간"
        }
    }

    val typeLabel = remember(selectedLargeCategories) {
        // 기준 순서대로 정렬하여 라벨 생성
        val sorted = selectedLargeCategories.sortedBy {
            when (it) {
                LargeCategoryEnum.INCOME -> 0
                LargeCategoryEnum.SAVING -> 1
                LargeCategoryEnum.EXPENSES -> 2
            }
        }
        getFilterLabel(sorted, "거래구분") { it.label }
    }

    val categoryLabel = remember(selectedCategories) {
        // 기준 순서대로 정렬하여 라벨 생성 (거래구분 우선, 그 다음 이름순)
        val sorted = selectedCategories.sortedWith(
            compareBy<CategoryVo> {
                when (it.largeCategory) {
                    LargeCategoryEnum.INCOME -> 0
                    LargeCategoryEnum.SAVING -> 1
                    LargeCategoryEnum.EXPENSES -> 2
                }
            }.thenBy { it.middleLabel }
        )
        getFilterLabel(sorted, "카테고리") { it.middleLabel }
    }

    val paymentMethodLabel = remember(selectedPaymentMethods) {
        getFilterLabel(selectedPaymentMethods, "결제수단") { it.label }
    }

    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 28.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
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
                isSelected = selectedLargeCategories.isNotEmpty(),
                onClick = onLargeCategoryClick
            )
        }
        item {
            SearchFilterChip(
                label = categoryLabel,
                isSelected = selectedCategories.isNotEmpty(),
                onClick = onCategoryClick
            )
        }
        item {
            SearchFilterChip(
                label = paymentMethodLabel,
                isSelected = selectedPaymentMethods.isNotEmpty(),
                onClick = onPaymentMethodClick
            )
        }
        item {
            SearchFilterChip(
                label = sortOrder.label,
                isSelected = true,
                onClick = onSortClick
            )
        }
        item {
            ResetFilterChip(onClick = onResetClick)
        }
    }
}

/**
 * 필터 라벨 생성을 위한 공통 헬퍼 함수
 */
private fun <T> getFilterLabel(
    items: List<T>,
    defaultLabel: String,
    labelSelector: (T) -> String
): String {
    return when {
        items.isEmpty() -> defaultLabel
        items.size == 1 -> labelSelector(items.first())
        else -> "${labelSelector(items.first())} 외 ${items.size - 1}"
    }
}

@Composable
fun SearchFilterChip(
    label: String,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    onClick: () -> Unit,
) {
    val backgroundColor = if (isSelected) ColorPrimary.Primary_500 else ColorGray.Gray_50
    val contentColor = if (isSelected) ColorGray.White else ColorGray.Gray_700

    Row(
        modifier = modifier
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
            style = MaterialTheme.typography.bodyMedium.copy(
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
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .clip(CircleShape)
            .background(Color.White)
            .border(1.dp, ColorGray.Gray_200, CircleShape)
            .clickable { onClick() }
            .padding(vertical = 4.dp, horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        WMText(
            text = "초기화",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = ColorGray.Gray_700
            )
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
