package com.jie.wealthmate.feature.budget.addBudget.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.jie.wealthmate.component.WMText
import com.jie.wealthmate.component.textField.WMTextField
import com.jie.wealthmate.component.textField.rememberIntegerVisualTransformation
import com.jie.wealthmate.theme.ColorRed
import com.jie.wealthmate.utils.default
import com.jie.wealthmate.vo.CategoryVo
import org.jetbrains.compose.resources.painterResource
import wealthmate.composeapp.generated.resources.Res
import wealthmate.composeapp.generated.resources.ic_push_pin


@Composable
fun BudgetCategoryHeader(
    modifier: Modifier = Modifier,
    label: String,
) {

    WMText(
        text = label,
        modifier = modifier
    )

}

@Composable
fun BudgetCategoryItem(
    modifier: Modifier = Modifier,
    item: CategoryVo,
    textFieldValue: TextFieldValue,
    isCategoryTagInclude: Boolean,
    tagTextFieldMap: Map<String, TextFieldValue>,
    onValueChange: (String, TextFieldValue) -> Unit,
) {
    Row(
        modifier = Modifier,
    ) {
        Box(
            modifier = Modifier
                .width(60.dp)
        ) {
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(item.largeCategory.backgroundColor)
                    .size(40.dp)
                    .align(Alignment.Center),
                contentAlignment = Alignment.Center
            ) {
                WMText(
                    text = item.icon,
                    style = androidx.compose.material3.Typography().titleLarge
                )
            }
            if (item.isFixed) {
                Icon(
                    painter = painterResource(Res.drawable.ic_push_pin),
                    contentDescription = null,
                    tint = ColorRed.Red_300,
                    modifier = Modifier
                        .size(24.dp)
                        .align(Alignment.TopStart)
                )
            }
        }

        Column {
            WMTextField(
                label = item.middleLabel,
                value = textFieldValue,
                onValueChange = { onValueChange(item.id, it) },
                readOnly = isCategoryTagInclude && item.tags.isNotEmpty(),
                maxLength = 10,
                maxLines = 1,
                placeholder = if (isCategoryTagInclude && item.tags.isNotEmpty()) "자동으로 계산됩니다." else "금액을 입력해 주세요.",
                suffix = {
                    WMText(
                        text = "원",
                        style = androidx.compose.material3.Typography().bodyLarge.copy(fontWeight = FontWeight.SemiBold)
                    )
                },
                visualTransformation = rememberIntegerVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
            )

            if (isCategoryTagInclude) {
                item.tags.forEach { tag ->
                    val tagValue = tagTextFieldMap[tag.id.default()] ?: TextFieldValue()
                    WMTextField(
                        label = tag.label,
                        value = tagValue,
                        onValueChange = { onValueChange(tag.id.default(), it) },
                        maxLength = 10,
                        maxLines = 1,
                        placeholder = "금액을 입력해 주세요.",
                        suffix = {
                            WMText(
                                text = "원",
                                style = Typography().bodyLarge.copy(fontWeight = FontWeight.SemiBold)
                            )
                        },
                        visualTransformation = rememberIntegerVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        ),
                    )
                }
            }
        }
    }
}