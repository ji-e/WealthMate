package com.jie.wealthmate.feature.calendar.addHistory.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jie.wealthmate.component.ButtonSize
import com.jie.wealthmate.component.WMButton
import com.jie.wealthmate.component.WMModalBottomSheet
import com.jie.wealthmate.theme.ColorGray
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Instant

@Composable
fun DateSelectModalBottomSheet(
    title: String? = null,
    selectedDate: LocalDate?,
    onSelectClick: (LocalDate) -> Unit,
    onDismissRequest: () -> Unit,
) {
    val initialMillis = remember(selectedDate) {
        selectedDate?.atStartOfDayIn(TimeZone.UTC)?.toEpochMilliseconds()
    }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialMillis,
        initialDisplayedMonthMillis = initialMillis
    )
    val selectedLocalDate by remember {
        derivedStateOf {
            datePickerState.selectedDateMillis?.let { millis ->
                Instant.fromEpochMilliseconds(millis)
                    .toLocalDateTime(TimeZone.UTC)
                    .date
            }
        }
    }

    WMModalBottomSheet(
        title = title,
        onDismissRequest = onDismissRequest,
    ) {
        DatePicker(
            state = datePickerState,
            colors = DatePickerDefaults.colors(
                containerColor = ColorGray.White,
                dayContentColor = ColorGray.Gray_700,
                selectedDayContentColor = ColorGray.White
            ),
            showModeToggle = false, // 캘린더/입력 모드 전환 버튼 숨기기 (옵션)
            title = null,           // 내부 타이틀 숨기기 (옵션)
            headline = null         // 내부 헤드라인 숨기기 (옵션)
        )

        // 선택 버튼
        WMButton(
            text = "선택",
            buttonSize = ButtonSize.LARGE,
            modifier = Modifier
                .padding(horizontal = 28.dp)
                .padding(top = 12.dp, bottom = 20.dp)
                .fillMaxWidth(),
            onClick = {
                selectedLocalDate?.let { date ->
                    onSelectClick(date)
                    onDismissRequest()
                }
            }
        )
    }
}