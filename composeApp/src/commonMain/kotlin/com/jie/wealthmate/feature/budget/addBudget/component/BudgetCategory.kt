package com.jie.wealthmate.feature.budget.addBudget.component

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.jie.wealthmate.component.WMText
import com.jie.wealthmate.component.textField.WMTextField
import com.jie.wealthmate.component.textField.rememberIntegerVisualTransformation
import com.jie.wealthmate.feature.menu.management.categoryManagement.component.LargeCategoryEnum
import com.jie.wealthmate.theme.ColorGray
import com.jie.wealthmate.theme.ColorPrimary
import com.jie.wealthmate.theme.ColorRed
import com.jie.wealthmate.utils.default
import com.jie.wealthmate.vo.CategoryVo
import org.jetbrains.compose.resources.painterResource
import wealthmate.composeapp.generated.resources.Res
import wealthmate.composeapp.generated.resources.ic_push_pin
import kotlin.math.roundToLong

@Composable
fun CategoryIcon(
    icon: String,
    backgroundColor: Color,
    isFixed: Boolean,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.width(60.dp)
    ) {
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .background(backgroundColor)
                .size(40.dp)
                .align(Alignment.Center),
            contentAlignment = Alignment.Center
        ) {
            WMText(
                text = icon,
                style = typography.titleLarge
            )
        }
        if (isFixed) {
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BudgetSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    enabled: Boolean = true,
    sliderHeight: Dp = 16.dp,
    thumbTopPadding: Float = 0f,
    thumbStartPadding: Float = 0f,
    thumbEndPadding: Float = 0f,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    modifier: Modifier = Modifier,
) {
    CompositionLocalProvider(
        LocalMinimumInteractiveComponentSize provides 0.dp
    ) {
        Slider(
            value = value,
            onValueChange = onValueChange,
            enabled = enabled,
            colors = SliderDefaults.colors(
                activeTrackColor = ColorPrimary.Primary_500,
                inactiveTrackColor = ColorGray.Gray_100,
            ),
            track = { state ->
                Box(contentAlignment = Alignment.Center) {
                    SliderDefaults.Track(
                        modifier = Modifier.height(sliderHeight),
                        sliderState = state,
                        colors = SliderDefaults.colors(
                            activeTrackColor = ColorPrimary.Primary_400,
                            inactiveTrackColor = ColorGray.Gray_100,
                        ),
                        enabled = enabled,
                        thumbTrackGapSize = 0.dp,
                        trackInsideCornerSize = 0.dp
                    )
                }
            },
            thumb = {
                Box(
                    modifier = Modifier
                        .padding(
                            start = thumbStartPadding.dp,
                            end = thumbEndPadding.dp,
                            top = thumbTopPadding.dp
                        )
                        .size(sliderHeight)
                        .background(
                            if (enabled) ColorPrimary.Primary_500 else ColorGray.Gray_100,
                            CircleShape
                        )
                )
            },
            interactionSource = interactionSource,
            modifier = modifier.fillMaxWidth()
        )
    }
}

@Composable
fun BudgetCategorySliderItem(
    modifier: Modifier = Modifier,
    selectedLargeCategory: LargeCategoryEnum,
    totalBudget: Long,
    remainBudget: Long,
    item: CategoryVo,
    textFieldValue: TextFieldValue,
    isCategoryTagInclude: Boolean,
    tagTextFieldMap: Map<String, TextFieldValue>,
    onValueChange: (String, TextFieldValue) -> Unit,
) {
    val amount = remember(textFieldValue.text) { textFieldValue.text.toLongOrNull() ?: 0L }
    val percent = remember(amount, totalBudget) {
        if (totalBudget > 0) (amount.toFloat() / totalBudget).coerceIn(0f, 1f) else 0f
    }
    val isReadOnly = isCategoryTagInclude && item.tags.isNotEmpty()
    val maxAllowed = amount + remainBudget

    Column(
        modifier = modifier
            .padding(top = 16.dp, bottom = 20.dp)
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(start = 20.dp, end = 28.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CategoryIcon(
                icon = item.icon,
                backgroundColor = item.largeCategory.backgroundColor,
                isFixed = item.isFixed
            )

            Column(modifier = Modifier.weight(1f)) {
                WMText(
                    text = item.middleLabel,
                    style = typography.titleMedium.copy(fontWeight = FontWeight.Medium),
                    maxLines = 1,
                )
                WMText(
                    text = "${(percent * 100).toInt()}%",
                    style = typography.bodySmall.copy(color = ColorGray.Gray_500),
                )
            }

            WMTextField(
                value = textFieldValue,
                onValueChange = {
                    val inputAmount = it.text.toLongOrNull().default()
                    if (inputAmount <= maxAllowed || selectedLargeCategory == LargeCategoryEnum.INCOME) {
                        onValueChange(item.id, it)
                    }
                },
                readOnly = isReadOnly,
                maxLength = 10,
                maxLines = 1,
                placeholder = "0",
                suffix = {
                    WMText(
                        text = "원",
                        style = typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
                    )
                },
                visualTransformation = rememberIntegerVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isSupport = false,
                isRight = true,
                modifier = Modifier.width(150.dp)
            )
        }

        if (selectedLargeCategory != LargeCategoryEnum.INCOME) {
            BudgetSlider(
                value = percent,
                onValueChange = {
                    val roundedAmount = ((it * totalBudget) / 10).roundToLong() * 10
                    val newAmount = roundedAmount.coerceAtMost(maxAllowed)
                    onValueChange(item.id, TextFieldValue(newAmount.toString()))
                },
                enabled = isReadOnly.not(),
                thumbStartPadding = 26f,
                thumbEndPadding = 16f,
                modifier = Modifier.padding(top = 12.dp)
            )
        }

        if (isCategoryTagInclude && item.tags.isNotEmpty()) {
            Column(
                modifier = Modifier
                    .padding(top = 12.dp, start = 28.dp, end = 28.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(ColorGray.Gray_50)
                    .padding(vertical = 12.dp)
            ) {
                WMText(
                    text = "${item.middleLabel} 상세 태그",
                    style = typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                    maxLines = 1,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                item.tags.forEach { tag ->
                    key(tag.id) {
                        val tagId = tag.id.default()
                        val tagValue = tagTextFieldMap[tagId] ?: TextFieldValue("0")
                        val tagAmount =
                            remember(tagValue.text) { tagValue.text.toLongOrNull() ?: 0L }
                        val tagPercent = remember(tagAmount, totalBudget) {
                            if (totalBudget > 0) (tagAmount.toFloat() / totalBudget).coerceIn(
                                0f,
                                1f
                            )
                            else 0f
                        }
                        val tagMaxAllowed = tagAmount + remainBudget

                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier
                                    .padding(horizontal = 16.dp)
                                    .padding(top = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    WMText(
                                        text = tag.label,
                                        style = typography.titleSmall.copy(fontWeight = FontWeight.Medium),
                                        maxLines = 1,
                                    )
                                    WMText(
                                        text = "${(tagPercent * 100).toInt()}%",
                                        style = typography.labelMedium.copy(color = ColorGray.Gray_500),
                                    )
                                }

                                WMTextField(
                                    value = tagValue,
                                    onValueChange = {
                                        val inputAmount = it.text.toLongOrNull().default()
                                        if (inputAmount <= tagMaxAllowed || selectedLargeCategory == LargeCategoryEnum.INCOME) {
                                            onValueChange(tagId, it)
                                        }
                                    },
                                    maxLength = 10,
                                    maxLines = 1,
                                    placeholder = "0",
                                    suffix = {
                                        WMText(
                                            text = "원",
                                            style = typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
                                        )
                                    },
                                    visualTransformation = rememberIntegerVisualTransformation(),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    isSupport = false,
                                    isRight = true,
                                    modifier = Modifier.width(135.dp)
                                )
                            }

                            if (selectedLargeCategory != LargeCategoryEnum.INCOME) {
                                BudgetSlider(
                                    value = tagPercent,
                                    onValueChange = {
                                        val roundedAmount =
                                            ((it * totalBudget) / 10).roundToLong() * 10
                                        val newAmount = roundedAmount.coerceAtMost(tagMaxAllowed)
                                        onValueChange(tagId, TextFieldValue(newAmount.toString()))
                                    },
                                    sliderHeight = 12.dp,
                                    thumbStartPadding = 12f,
                                    thumbEndPadding = 8f,
                                    thumbTopPadding = 2f,
                                    modifier = Modifier.padding(top = 12.dp, bottom = 10.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
