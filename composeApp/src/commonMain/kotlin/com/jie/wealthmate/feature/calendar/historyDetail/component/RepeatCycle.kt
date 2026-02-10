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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jie.wealthmate.component.SwitchSize
import com.jie.wealthmate.component.WMIconButton
import com.jie.wealthmate.component.WMSwitch
import com.jie.wealthmate.component.WMText
import com.jie.wealthmate.feature.calendar.addHistory.component.RepeatCycleEnum
import com.jie.wealthmate.feature.calendar.addHistory.component.formattedShortDescription
import com.jie.wealthmate.theme.ColorGray
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
    val contentColor = if (isActivated) ColorGray.Gray_700 else ColorGray.Gray_500
    val repeatDescription =
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
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(ColorGray.Gray_50)
            .padding(horizontal = 16.dp)
            .padding(top = 6.dp, bottom = 16.dp),
    ) {

        Row(
            modifier = Modifier
                .padding(bottom = 6.dp)
                .height(36.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            WMText(
                text = repeatDescription,
                style = Typography().titleMedium.copy(
                    color = contentColor,
                    fontWeight = FontWeight.SemiBold
                ),
            )

            WMIconButton(
                iconRes = Res.drawable.ic_keyboard_arrow_right,
                iconButtonModifier = Modifier.size(32.dp),
                modifier = Modifier.size(24.dp),
                onClick = onModifyRepeatCycleClick,
                tint = contentColor
            )

            Spacer(modifier = Modifier.weight(1f))

            WMSwitch(
                checked = isActivated,
                switchSize = SwitchSize.X_SMALL,
                onCheckedChange = onIsActiveChange,
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            WMText(
                text = "시작일",
                style = Typography().bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = ColorGray.Gray_500
                ),
                modifier = Modifier.width(50.dp)
            )

            WMText(
                text = repeatCycle.startDate.convertLocalDateToString(formatDateDotYYMDE),
                style = Typography().bodyMedium.copy(color = contentColor)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
//                modifier = Modifier.noRippleClickable(isActivated) { onEndDateClick(false) },
                verticalAlignment = Alignment.CenterVertically
            ) {
                WMText(
                    text = "종료일",
                    style = Typography().bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = ColorGray.Gray_500
                    ),
                    modifier = Modifier.width(50.dp)
                )

                WMText(
                    text = repeatCycle.endDate.convertLocalDateToString(formatDateDotYYMDE, "없음"),
                    style = Typography().bodyMedium.copy(color = contentColor)
                )
            }
        }

        if (repeatCycle.isModified && repeatCycle.updateAt?.year == date.year && repeatCycle.updateAt.month == date.month) {
            Row(
                modifier = Modifier.padding(top = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_error_outline),
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = ColorGray.Gray_500
                )

                WMText(
                    text = "내역 작성 이후 반복 내역 설정이 변경되었습니다.",
                    style = Typography().bodySmall.copy(color = ColorGray.Gray_500)
                )
            }
        }
    }
}