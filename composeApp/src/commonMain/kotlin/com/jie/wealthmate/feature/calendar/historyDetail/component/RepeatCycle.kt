package com.jie.wealthmate.feature.calendar.historyDetail.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jie.wealthmate.component.SwitchSize
import com.jie.wealthmate.component.WMIconButton
import com.jie.wealthmate.component.WMSwitch
import com.jie.wealthmate.component.WMText
import com.jie.wealthmate.feature.calendar.addHistory.component.RepeatCycleEnum
import com.jie.wealthmate.feature.calendar.addHistory.component.formattedShortDescription
import com.jie.wealthmate.feature.menu.management.categoryManagement.component.LargeCategoryEnum
import com.jie.wealthmate.theme.ColorGray
import com.jie.wealthmate.theme.ColorSetting
import com.jie.wealthmate.theme.Padding
import com.jie.wealthmate.theme.Shapes
import com.jie.wealthmate.theme.WMTheme
import com.jie.wealthmate.utils.convertLocalDateToString
import com.jie.wealthmate.utils.formatDateDotYYMDE
import com.jie.wealthmate.vo.RepeatCycleVo
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.painterResource
import wealthmate.composeapp.generated.resources.Res
import wealthmate.composeapp.generated.resources.ic_error_outline
import wealthmate.composeapp.generated.resources.ic_keyboard_arrow_right

@Composable
fun RepeatCycle(
    modifier: Modifier = Modifier,
    date: LocalDate,
    repeatCycle: RepeatCycleVo,
    onIsActiveChange: (Boolean) -> Unit,
    onModifyRepeatCycleClick: () -> Unit,
) {
    val isActivated = repeatCycle.isActive
    val contentColor = if (isActivated) ColorSetting.Default else ColorSetting.Info

    val repeatDescription = remember(repeatCycle) {
        when (val repeatCycleEnum = repeatCycle.repeatCycle) {
            RepeatCycleEnum.WEEKLY -> {
                repeatCycleEnum.formattedShortDescription(repeatCycle.dayOfWeek ?: 1)
            }

            RepeatCycleEnum.MONTHLY -> {
                repeatCycleEnum.formattedShortDescription(repeatCycle.dayOfMonth ?: 1)
            }

            else -> {
                repeatCycleEnum.formattedShortDescription(repeatCycle.date)
            }
        }
    }

    val isModifiedInCurrentMonth = remember(repeatCycle, date) {
        repeatCycle.isModified &&
                repeatCycle.updateAt?.year == date.year &&
                repeatCycle.updateAt.month == date.month
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(Shapes.medium)
            .background(ColorGray.Gray_50)
            .padding(horizontal = Padding.ContainerHorizontal)
            .padding(top = 6.dp, bottom = Padding.SpacerS),
    ) {
        Row(
            modifier = Modifier
                .padding(bottom = 6.dp)
                .height(36.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            WMText(
                text = repeatDescription,
                style = MaterialTheme.typography.titleMedium,
                color = contentColor,
                fontWeight = FontWeight.SemiBold,
            )

            WMIconButton(
                iconRes = Res.drawable.ic_keyboard_arrow_right,
                modifier = Modifier.size(32.dp),
                iconModifier = Modifier.size(24.dp),
                onClick = onModifyRepeatCycleClick,
                tint = contentColor
            )

            Spacer(modifier = Modifier.weight(1f))

            WMSwitch(
                checked = isActivated,
                switchSize = SwitchSize.SMALL,
                onCheckedChange = onIsActiveChange,
            )
        }

        DateInfoRow(
            label = "시작일",
            date = repeatCycle.startDate,
            contentColor = contentColor
        )

        DateInfoRow(
            label = "종료일",
            date = repeatCycle.endDate,
            contentColor = contentColor,
            modifier = Modifier.padding(top = Padding.SpacerXXS)
        )

        if (isModifiedInCurrentMonth) {
            ModifiedNoticeRow(modifier = Modifier.padding(top = Padding.ContainerVertical))
        }
    }
}

@Composable
private fun DateInfoRow(
    modifier: Modifier = Modifier,
    label: String,
    date: LocalDate?,
    contentColor: Color,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        WMText(
            text = label,
            fontWeight = FontWeight.SemiBold,
            color = ColorGray.Gray_500,
            modifier = Modifier.width(50.dp)
        )

        WMText(
            text = date.convertLocalDateToString(formatDateDotYYMDE, "없음"),
            color = contentColor
        )
    }
}

@Composable
private fun ModifiedNoticeRow(
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Padding.SpacerXXS)
    ) {
        Icon(
            painter = painterResource(Res.drawable.ic_error_outline),
            contentDescription = null,
            modifier = Modifier.size(14.dp),
            tint = ColorSetting.Info
        )

        WMText(
            text = "내역 작성 이후 반복 설정이 변경되었습니다.",
            style = MaterialTheme.typography.bodySmall,
            color = ColorSetting.Info
        )
    }
}

@Preview
@Composable
private fun RepeatCyclePreview() {
    val mockDate = LocalDate(2024, 1, 15)
    val mockRepeatCycle = RepeatCycleVo(
        id = "1",
        largeCategory = LargeCategoryEnum.EXPENSES,
        content = "정기 결제",
        amount = 10000,
        repeatCycle = RepeatCycleEnum.MONTHLY,
        dayOfMonth = 15,
        date = LocalDate(2024, 1, 15),
        startDate = LocalDate(2024, 1, 1),
        endDate = null,
        categoryId = "cat_1",
        paymentMethodId = "pay_1",
        isActive = true,
        isModified = true,
        isDeleted = false,
        updateAt = LocalDate(2024, 1, 10)
    )

    WMTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Padding.BackgroundHorizontal),
            verticalArrangement = Arrangement.spacedBy(Padding.SpacerS)
        ) {
            RepeatCycle(
                date = mockDate,
                repeatCycle = mockRepeatCycle,
                onIsActiveChange = {},
                onModifyRepeatCycleClick = {}
            )

            RepeatCycle(
                date = mockDate,
                repeatCycle = mockRepeatCycle.copy(isActive = false, isModified = false),
                onIsActiveChange = {},
                onModifyRepeatCycleClick = {}
            )
        }
    }
}
