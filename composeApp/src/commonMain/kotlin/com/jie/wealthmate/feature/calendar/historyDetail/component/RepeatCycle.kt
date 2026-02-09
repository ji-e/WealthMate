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
import com.jie.wealthmate.feature.calendar.addHistory.component.RepeatCycleEnum.Companion.formattedShortDescription
import com.jie.wealthmate.theme.ColorGray
import com.jie.wealthmate.theme.noRippleClickable
import com.jie.wealthmate.utils.convertLocalDateToString
import com.jie.wealthmate.utils.formatDateDotYYMDE
import com.jie.wealthmate.vo.RepeatCycleVo
import org.jetbrains.compose.resources.painterResource
import wealthmate.composeapp.generated.resources.Res
import wealthmate.composeapp.generated.resources.ic_delete_outline
import wealthmate.composeapp.generated.resources.ic_edit
import wealthmate.composeapp.generated.resources.ic_error_outline
import wealthmate.composeapp.generated.resources.ic_keyboard_arrow_right

@Composable
fun RepeatCycle(
    modifier: Modifier = Modifier,
    repeatCycle: RepeatCycleVo,
    onIsActiveChange: (Boolean) -> Unit,
    onModifyRepeatCycleClick: () -> Unit,
    onEndDateClick: (isRemoveEndDateClick: Boolean) -> Unit,
) {
    val isActivated = repeatCycle.isActive
    val contentColor = if (isActivated) ColorGray.Gray_700 else ColorGray.Gray_500
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
                text = repeatCycle.repeatCycle.formattedShortDescription(repeatCycle.startDate),
                style = Typography().titleMedium.copy(
                    color = contentColor,
                    fontWeight = FontWeight.SemiBold
                ),
            )

            if (isActivated) {
                WMIconButton(
                    iconRes = Res.drawable.ic_edit,
                    iconButtonModifier = Modifier.size(36.dp),
                    modifier = Modifier.size(18.dp),
                    onClick = onModifyRepeatCycleClick,
                    tint = contentColor
                )
            }

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
                modifier = Modifier.noRippleClickable(isActivated) { onEndDateClick(false) },
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

                if (isActivated.not()) return
                if (repeatCycle.endDate == null) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_keyboard_arrow_right),
                        modifier = Modifier
                            .padding(start = 4.dp)
                            .size(18.dp),
                        contentDescription = null,
                        tint = ColorGray.Gray_500
                    )
                } else {
                    Icon(
                        painter = painterResource(Res.drawable.ic_delete_outline),
                        modifier = Modifier
                            .padding(start = 4.dp)
                            .size(18.dp)
                            .noRippleClickable { onEndDateClick(true) },
                        contentDescription = null,
                        tint = ColorGray.Gray_500
                    )
                }
            }

        }

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
                text = "수정시 바로 적용됩니다.",
                style = Typography().bodySmall.copy(color = ColorGray.Gray_500)
            )
        }
    }

}