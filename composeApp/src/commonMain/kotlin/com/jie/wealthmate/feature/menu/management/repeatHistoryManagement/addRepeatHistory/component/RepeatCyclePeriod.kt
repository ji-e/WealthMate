package com.jie.wealthmate.feature.menu.management.repeatHistoryManagement.addRepeatHistory.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jie.wealthmate.component.LabelText
import com.jie.wealthmate.component.WMText
import com.jie.wealthmate.component.textField.WMTextField
import com.jie.wealthmate.theme.ColorGray
import com.jie.wealthmate.theme.noRippleClickable
import com.jie.wealthmate.utils.convertLocalDateToString
import com.jie.wealthmate.utils.formatDateDotYYYYMDE
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.painterResource
import wealthmate.composeapp.generated.resources.Res
import wealthmate.composeapp.generated.resources.ic_close

@Composable
fun RepeatCyclePeriod(
    modifier: Modifier = Modifier,
    startDate: LocalDate,
    endDate: LocalDate?,
    onStartDateClick: () -> Unit,
    onEndDateClick: () -> Unit,
    onEndDateResetClick: () -> Unit,
) {
    LabelText(
        modifier = modifier,
        text = "반복 기간",
        isRequire = true,
    )
    Row {
        WMTextField(
            value = startDate.convertLocalDateToString(formatDateDotYYYYMDE),
            onValueChange = {},
            readOnly = true,
            onReadOnlyClick = onStartDateClick,
            modifier = Modifier.weight(1f)
        )
        WMText(
            text = "~",
            modifier = Modifier.padding(top = 16.dp).padding(horizontal = 16.dp)
        )

        Box(modifier = Modifier.weight(1f)) {
            WMTextField(
                value = endDate.convertLocalDateToString(
                    formatDateDotYYYYMDE,
                    "없음"
                ),
                onValueChange = {},
                readOnly = true,
                onReadOnlyClick = onEndDateClick,
            )

            if (endDate != null) {
                Icon(
                    painter = painterResource(Res.drawable.ic_close),
                    modifier = Modifier
                        .padding(bottom = 20.dp)
                        .size(24.dp)
                        .noRippleClickable { onEndDateResetClick() }
                        .align(Alignment.CenterEnd),
                    contentDescription = null,
                    tint = ColorGray.Gray_500
                )
            }
        }
    }
}