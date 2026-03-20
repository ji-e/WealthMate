package com.jie.wealthmate.feature.calendar.component

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
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
import com.jie.wealthmate.feature.calendar.CalendarFilterOption
import com.jie.wealthmate.theme.ColorGray
import com.jie.wealthmate.theme.ColorPrimary

@Composable
fun CalendarFilterModalBottomSheet(
    title: String,
    selectedOptions: Set<CalendarFilterOption>,
    onOptionsSelected: (Set<CalendarFilterOption>) -> Unit,
    onDismissRequest: () -> Unit,
) {
    var tempSelectedOptions by remember(selectedOptions) {
        mutableStateOf(selectedOptions)
    }

    // 그룹화 구조 정의: (상위 노출 옵션 to 하위 세부 옵션 리스트)
    val groupedStructure = remember {
        listOf(
            CalendarFilterOption.SHOW_INCOME to listOf(
                CalendarFilterOption.INCLUDE_FIXED_INCOME
            ),
            CalendarFilterOption.SHOW_EXPENSES to listOf(
                CalendarFilterOption.INCLUDE_FIXED_EXPENSES,
                CalendarFilterOption.SHOW_SAVINGS,
                CalendarFilterOption.INCLUDE_FIXED_SAVINGS
            ),
            CalendarFilterOption.SHOW_REPEAT to emptyList()
        )
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
                    groupedStructure.forEach { (parentOption, children) ->
                        // 1. 상위 노출 옵션 (헤더 역할)
                        item(key = "parent_${parentOption.name}") {
                            val isSelected = parentOption in tempSelectedOptions

                            WMCheckBox(
                                modifier = Modifier
                                    .padding(horizontal = 28.dp)
                                    .padding(top = 16.dp, bottom = 8.dp),
                                label = parentOption.label,
                                labelStyle = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = ColorPrimary.Primary_500
                                ),
                                checked = isSelected,
                                onCheckedChange = { checked ->
                                    tempSelectedOptions = if (checked) {
                                        tempSelectedOptions + parentOption
                                    } else {
                                        // 헤더가 꺼지면 하위 항목도 모두 해제
                                        tempSelectedOptions - parentOption - children.toSet()
                                    }
                                }
                            )

                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 28.dp, vertical = 4.dp),
                                color = ColorGray.Gray_200,
                                thickness = 1.dp
                            )
                        }

                        // 2. 하위 세부 옵션 (들여쓰기 제거)
                        items(
                            count = children.size,
                            key = { index -> children[index].name }
                        ) { index ->
                            val option = children[index]
                            val isSelected = option in tempSelectedOptions

                            WMCheckBox(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(44.dp)
                                    .padding(horizontal = 28.dp), // 들여쓰기 제거 (52.dp -> 28.dp)
                                iconModifier = Modifier.size(28.dp),
                                label = option.label,
                                labelStyle = MaterialTheme.typography.bodyLarge,
                                checked = isSelected,
                                onCheckedChange = { checked ->
                                    tempSelectedOptions = if (checked) {
                                        // 하위 항목 선택 시 상위 노출 옵션도 함께 선택
                                        tempSelectedOptions + option + parentOption
                                    } else {
                                        tempSelectedOptions - option
                                    }
                                }
                            )
                        }

                        item {
                            Spacer(modifier = Modifier.height(8.dp))
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
                        onOptionsSelected(tempSelectedOptions)
                        onDismissRequest()
                    }
                )
            }
        }
    }
}
