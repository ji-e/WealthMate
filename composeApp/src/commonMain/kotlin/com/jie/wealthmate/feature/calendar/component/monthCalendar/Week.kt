package com.jie.wealthmate.feature.calendar.component.monthCalendar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.jie.wealthmate.component.WMText

@Composable
fun WeekHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        horizontalArrangement = Arrangement.Center,
    ) {
        WeekEnum.entries.forEach { day ->
            WMText(
                text = day.korDisplayName,
                style = Typography().bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = day.color
                ),
                modifier = Modifier
                    .weight(1f)
                    .padding(top = 4.dp, bottom = 8.dp),
                textAlign = TextAlign.Center
            )
        }
    }
}