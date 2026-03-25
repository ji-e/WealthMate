package com.jie.wealthmate.feature.search.component

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jie.wealthmate.component.ButtonSize
import com.jie.wealthmate.component.ButtonStyle
import com.jie.wealthmate.component.WMButton
import com.jie.wealthmate.component.WMCheckBox
import com.jie.wealthmate.component.WMModalBottomSheet
import com.jie.wealthmate.feature.menu.management.categoryManagement.component.LargeCategoryEnum
import com.jie.wealthmate.theme.ColorGray
import com.jie.wealthmate.theme.ColorPrimary
import com.jie.wealthmate.vo.CategoryVo

@Composable
fun CategoryMultiSelectModalBottomSheet(
    title: String,
    categories: List<CategoryVo>,
    selectedCategories: List<CategoryVo>,
    onItemsSelected: (List<CategoryVo>) -> Unit,
    onDismissRequest: () -> Unit,
    showHeaders: Boolean = true,
    showUnset: Boolean = true,
    largeCategoryFilter: List<LargeCategoryEnum> = emptyList(),
) {
    // 1. ID 기반 Set으로 선택 상태 관리 (검색 및 수정 성능 최적화)
    var tempSelectedIds by remember(selectedCategories) {
        mutableStateOf(selectedCategories.map { it.id }.toSet())
    }

    // 2. 카테고리 데이터 가공 및 그룹화 로직을 remember로 최적화
    val displayData = remember(categories, showUnset, largeCategoryFilter) {
        // 기준 정렬 순서 정의
        val preferredOrder = listOf(
            LargeCategoryEnum.INCOME,
            LargeCategoryEnum.SAVING,
            LargeCategoryEnum.EXPENSES
        )

        // 필터가 있으면 필터 항목들 중 기준 순서대로 정렬, 없으면 전체 기준 순서 사용
        val activeLargeCategories = if (largeCategoryFilter.isEmpty()) {
            preferredOrder
        } else {
            preferredOrder.filter { it in largeCategoryFilter }
        }

        val grouped = categories.groupBy { it.largeCategory }

        activeLargeCategories.mapNotNull { largeCategory ->
            val itemsInGroup = grouped[largeCategory] ?: emptyList()

            val finalItems = if (showUnset) {
                val hasUnset = itemsInGroup.any { it.isUnset }
                if (hasUnset) {
                    val (unsets, normals) = itemsInGroup.partition { it.isUnset }
                    normals + unsets
                } else {
                    itemsInGroup + CategoryVo.unset(largeCategory)
                }
            } else {
                itemsInGroup.filter { !it.isUnset }
            }

            if (finalItems.isNotEmpty() || (showUnset && showHeaders)) largeCategory to finalItems
            else null
        }
    }

    // ID로부터 CategoryVo 객체를 다시 매핑하기 위한 Map
    val allSelectableMap = remember(displayData) {
        displayData.flatMap { it.second }.associateBy { it.id }
    }

    WMModalBottomSheet(
        title = title,
        onDismissRequest = onDismissRequest,
    ) {
        BoxWithConstraints {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = maxHeight * 0.7f)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false)
                ) {
                    displayData.forEach { (largeCategory, finalItems) ->
                        if (showHeaders) {
                            item(key = "header_${largeCategory.name}") {
                                val isAllSelectedInGroup = finalItems.isNotEmpty() &&
                                        finalItems.all { it.id in tempSelectedIds }

                                WMCheckBox(
                                    modifier = Modifier
                                        .padding(horizontal = 28.dp)
                                        .padding(top = 16.dp, bottom = 8.dp),
                                    label = largeCategory.label,
                                    labelStyle = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        color = ColorPrimary.Primary_500
                                    ),
                                    checked = isAllSelectedInGroup,
                                    onCheckedChange = { checked ->
                                        val groupIds = finalItems.map { it.id }
                                        tempSelectedIds =
                                            if (checked) tempSelectedIds + groupIds
                                            else tempSelectedIds - groupIds.toSet()
                                    }
                                )

                                HorizontalDivider(
                                    modifier = Modifier.padding(
                                        horizontal = 28.dp,
                                        vertical = 4.dp
                                    ),
                                    color = ColorGray.Gray_200,
                                    thickness = 1.dp
                                )
                            }
                        }

                        items(
                            items = finalItems,
                            key = { it.id }
                        ) { category ->
                            val isSelected = category.id in tempSelectedIds
                            val label =
                                if (category.isUnset) category.middleLabel
                                else "${category.middleLabel} ${if (category.isFixed) "| 고정" else ""}"


                            WMCheckBox(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(44.dp)
                                    .padding(horizontal = 28.dp),
                                iconModifier = Modifier.size(28.dp),
                                label = label,
                                labelStyle = MaterialTheme.typography.bodyLarge,
                                checked = isSelected,
                                onCheckedChange = { checked ->
                                    tempSelectedIds =
                                        if (checked) tempSelectedIds + category.id
                                        else tempSelectedIds - category.id
                                }
                            )
                        }

                        item {
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                WMButton(
                    text = "확인",
                    buttonStyle = ButtonStyle.FILLED,
                    buttonSize = ButtonSize.LARGE,
                    modifier = Modifier
                        .padding(horizontal = 28.dp)
                        .padding(bottom = 20.dp)
                        .fillMaxWidth(),
                    onClick = {
                        val result = tempSelectedIds.mapNotNull { allSelectableMap[it] }
                        onItemsSelected(result)
                        onDismissRequest()
                    }
                )
            }
        }
    }
}
