package com.jie.wealthmate.feature.calendar.component.addHistory.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jie.wealthmate.component.ButtonSize
import com.jie.wealthmate.component.ButtonStyle
import com.jie.wealthmate.component.WMButton
import com.jie.wealthmate.component.WMTextField
import com.jie.wealthmate.feature.calendar.component.addHistory.component.RepeatCycleEnum.Companion.formattedDescription
import com.jie.wealthmate.feature.menu.categorySetting.component.LargeCategoryEnum
import com.jie.wealthmate.theme.WMTheme
import com.jie.wealthmate.utils.convertLocalDateToString
import com.jie.wealthmate.utils.formatDateHyphenYMDE
import com.jie.wealthmate.utils.today
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun DateTextField(
    modifier: Modifier = Modifier,
    isSelectedLargeCategoryEnum: LargeCategoryEnum = LargeCategoryEnum.EXPENSES,
    date: LocalDate,
    repeatCycle: RepeatCycleEnum? = null,
    installmentCount: Int? = null,
    onDateClick: () -> Unit = {},
    onRepeatClick: () -> Unit = {},
    onInstallmentClick: () -> Unit = {},
) {
    Box() {
        WMTextField(
            value = date.convertLocalDateToString(formatDateHyphenYMDE),
            onValueChange = {},
            label = "날짜",
            readOnly = true,
            isRequire = true,
            modifier = Modifier
                .padding(horizontal = 4.dp)
                .padding(top = 20.dp),
            supportingText = when {
                repeatCycle != null -> repeatCycle.formattedDescription(date)
                installmentCount != null -> "할부 $installmentCount 개월"
                else -> ""
            },
            supportingContent = {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    WMButton(
                        text = "반복",
                        onClick = onRepeatClick,
                        buttonSize = ButtonSize.X_SMALL,
                        buttonStyle = if (repeatCycle != null) ButtonStyle.FILLED else ButtonStyle.OUTLINED,
                    )

                    if (isSelectedLargeCategoryEnum == LargeCategoryEnum.EXPENSES) {
                        WMButton(
                            text = "할부",
                            onClick = onInstallmentClick,
                            buttonSize = ButtonSize.X_SMALL,
                            buttonStyle = if (installmentCount != null) ButtonStyle.FILLED else ButtonStyle.OUTLINED,
                        )
                    }
                }
            }
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(84.dp)
                .padding(horizontal = 20.dp)
                .padding(top = 48.dp)
                .clickable { onDateClick() }
        )
    }
}

@Composable
@Preview(showBackground = true)
private fun DateTextFieldPreview() {
    WMTheme {
        Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
            DateTextField(
                date = today,
            )
            DateTextField(
                date = today,
                repeatCycle = RepeatCycleEnum.WEEKLY,
            )
            DateTextField(
                date = today,
                repeatCycle = RepeatCycleEnum.YEARLY,
            )
            DateTextField(
                date = today,
                installmentCount = 4
            )
        }
    }

}

