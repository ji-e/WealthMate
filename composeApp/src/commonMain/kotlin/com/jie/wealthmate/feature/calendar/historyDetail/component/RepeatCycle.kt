package com.jie.wealthmate.feature.calendar.historyDetail.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jie.wealthmate.component.WMText
import com.jie.wealthmate.feature.calendar.addHistory.component.RepeatCycleEnum.Companion.formattedShortDescription
import com.jie.wealthmate.theme.ColorGray
import com.jie.wealthmate.utils.convertLocalDateToString
import com.jie.wealthmate.utils.formatDateDotYYMDE
import com.jie.wealthmate.vo.RepeatCycleVo

@Composable
fun RepeatCycle(
    modifier: Modifier = Modifier,
    repeatCycle: RepeatCycleVo,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(ColorGray.Gray_50)
            .padding(12.dp),
    ) {
        WMText(
            text = "반복 내역 | ${repeatCycle.repeatCycle.formattedShortDescription(repeatCycle.startDate)}",
            style = Typography().bodyMedium.copy(fontWeight = FontWeight.SemiBold),
        )

        Row(modifier = Modifier.padding(top = 8.dp, bottom = 2.dp)) {
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
                modifier = Modifier.weight(1f)
            )
        }

        Row {
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
                modifier = Modifier.weight(1f)
            )
        }
    }

}