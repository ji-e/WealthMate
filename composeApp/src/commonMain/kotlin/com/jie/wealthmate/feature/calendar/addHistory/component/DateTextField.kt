package com.jie.wealthmate.feature.calendar.addHistory.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jie.wealthmate.component.WMIconButton
import com.jie.wealthmate.component.WMText
import com.jie.wealthmate.component.textField.WMTextField
import com.jie.wealthmate.feature.calendar.addHistory.component.RepeatCycleEnum.Companion.formattedDescription
import com.jie.wealthmate.feature.menu.categoryManagement.component.LargeCategoryEnum
import com.jie.wealthmate.theme.ColorGray
import com.jie.wealthmate.theme.ColorPrimary
import com.jie.wealthmate.theme.WMTheme
import com.jie.wealthmate.utils.convertLocalDateToString
import com.jie.wealthmate.utils.formatDateHyphenYMDE
import com.jie.wealthmate.utils.formatWithCommas
import com.jie.wealthmate.utils.today
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.painterResource
import wealthmate.composeapp.generated.resources.Res
import wealthmate.composeapp.generated.resources.ic_close_circle
import wealthmate.composeapp.generated.resources.ic_percent
import wealthmate.composeapp.generated.resources.ic_repeat

@Composable
fun DateTextField(
    modifier: Modifier = Modifier,
    selectedLargeCategoryEnum: LargeCategoryEnum = LargeCategoryEnum.EXPENSES,
    selectedDate: LocalDate,
    amount: TextFieldValue? = null,
    repeatCycle: RepeatCycleEnum? = null,
    totalInstallment: Int? = null,
    isTrailingIconVisible: Boolean = true,
    onDateClick: () -> Unit = {},
    onRepeatClick: () -> Unit = {},
    onInstallmentClick: () -> Unit = {},
    onResetClick: () -> Unit = {},
) {
    Box(modifier = modifier) {
        WMTextField(
            value = selectedDate.convertLocalDateToString(formatDateHyphenYMDE),
            onValueChange = {},
            label = "날짜",
            readOnly = true,
            isRequire = true,
            onReadOnlyClick = onDateClick,
            supportingContent = {
                val supportingText = when {
                    repeatCycle != null -> repeatCycle.formattedDescription(selectedDate)
                    totalInstallment != null -> {
                        val installmentAmount = amount?.text?.toIntOrNull()?.div(totalInstallment)

                        "할부 ${totalInstallment}개월" +
                                if (installmentAmount != null) {
                                    " (매월 ${formatWithCommas(installmentAmount.toString())}원)"
                                } else {
                                    ""
                                }

                    }

                    else -> ""
                }
                Row {
                    WMText(
                        text = supportingText,
                        style = Typography().bodyMedium.copy(color = ColorGray.Gray_500),
                        modifier = Modifier.weight(1f)
                    )

                    if (supportingText.isNotEmpty()) {
                        WMIconButton(
                            iconButtonModifier = Modifier.size(20.dp),
                            iconRes = Res.drawable.ic_close_circle,
                            tint = ColorGray.Gray_400,
                            onClick = onResetClick
                        )
                    }
                }
            },
        )

        if (isTrailingIconVisible) {
            Row(
                modifier = Modifier
                    .padding(bottom = 4.dp)
                    .align(Alignment.CenterEnd),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_repeat),
                    contentDescription = "반복",
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(if (repeatCycle != null) ColorPrimary.Primary_500 else ColorGray.Gray_50)
                        .clickable { onRepeatClick() }
                        .padding(6.dp),
                    tint = if (repeatCycle != null) ColorGray.White else ColorGray.Gray_500
                )
                if (selectedLargeCategoryEnum == LargeCategoryEnum.EXPENSES) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_percent),
                        contentDescription = "할부",
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .background(if (totalInstallment != null) ColorPrimary.Primary_500 else ColorGray.Gray_50)
                            .clickable { onInstallmentClick() }
                            .padding(6.dp),
                        tint = if (totalInstallment != null) ColorGray.White else ColorGray.Gray_500
                    )
                }
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun DateTextFieldPreview() {
    WMTheme {
        Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
            DateTextField(
                selectedDate = today,
            )
            DateTextField(
                selectedDate = today,
                repeatCycle = RepeatCycleEnum.WEEKLY,
            )
            DateTextField(
                selectedDate = today,
                repeatCycle = RepeatCycleEnum.YEARLY,
            )
            DateTextField(
                selectedDate = today,
                totalInstallment = 4
            )
        }
    }
}

