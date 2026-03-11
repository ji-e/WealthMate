package com.jie.wealthmate.feature.search.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jie.wealthmate.component.ButtonSize
import com.jie.wealthmate.component.ButtonStyle
import com.jie.wealthmate.component.WMButton
import com.jie.wealthmate.component.WMModalBottomSheet
import com.jie.wealthmate.theme.ColorGray
import com.jie.wealthmate.theme.ColorPrimary
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Instant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateRangeSelectModalBottomSheet(
    title: String? = "기간 선택",
    startDate: LocalDate?,
    endDate: LocalDate?,
    onSelectClick: (LocalDate?, LocalDate?) -> Unit,
    onDismissRequest: () -> Unit,
) {
    val initialStartMillis = remember(startDate) {
        startDate?.atStartOfDayIn(TimeZone.UTC)?.toEpochMilliseconds()
    }
    val initialEndMillis = remember(endDate) {
        endDate?.atStartOfDayIn(TimeZone.UTC)?.toEpochMilliseconds()
    }

    val dateRangePickerState = rememberDateRangePickerState(
        initialSelectedStartDateMillis = initialStartMillis,
        initialSelectedEndDateMillis = initialEndMillis,
    )

    val selectedDates by remember {
        derivedStateOf {
            val start = dateRangePickerState.selectedStartDateMillis?.let { millis ->
                Instant.fromEpochMilliseconds(millis)
                    .toLocalDateTime(TimeZone.UTC)
                    .date
            }
            val end = dateRangePickerState.selectedEndDateMillis?.let { millis ->
                Instant.fromEpochMilliseconds(millis)
                    .toLocalDateTime(TimeZone.UTC)
                    .date
            }
            start to end
        }
    }

    WMModalBottomSheet(
        title = title,
        onDismissRequest = onDismissRequest,
    ) {
        DateRangePicker(
            state = dateRangePickerState,
            colors = DatePickerDefaults.colors(
                containerColor = ColorGray.White,
                dayContentColor = ColorGray.Gray_700,
                selectedDayContentColor = ColorGray.White,
                selectedDayContainerColor = ColorPrimary.Primary_500,
                todayContentColor = ColorPrimary.Primary_500,
                dayInSelectionRangeContentColor = ColorPrimary.Primary_500,
                dayInSelectionRangeContainerColor = ColorPrimary.Primary_200
            ),
            showModeToggle = false,
            title = null,
            headline = null,
            modifier = Modifier.fillMaxHeight(0.6f)
        )

        Row(
            modifier = Modifier
                .padding(horizontal = 28.dp)
                .padding(top = 12.dp, bottom = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            WMButton(
                text = "초기화",
                buttonSize = ButtonSize.LARGE,
                buttonStyle = ButtonStyle.TONAL,
                modifier = Modifier.weight(1f),
                onClick = {
                    onSelectClick(null, null)
                    onDismissRequest()
                }
            )
            WMButton(
                text = "선택",
                buttonSize = ButtonSize.LARGE,
                modifier = Modifier.weight(3f),
                onClick = {
                    onSelectClick(selectedDates.first, selectedDates.second)
                    onDismissRequest()
                }
            )
        }
    }
}
