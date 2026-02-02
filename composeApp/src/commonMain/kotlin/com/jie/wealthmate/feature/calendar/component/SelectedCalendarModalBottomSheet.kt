package com.jie.wealthmate.feature.calendar.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jie.wealthmate.component.ButtonSize
import com.jie.wealthmate.component.WMButton
import com.jie.wealthmate.component.WMModalBottomSheet
import com.jie.wealthmate.component.WMText
import com.jie.wealthmate.theme.ColorGray
import com.jie.wealthmate.theme.ColorPrimary
import com.jie.wealthmate.utils.convertLocalDateToString
import com.jie.wealthmate.utils.firstDayOfMonth
import com.jie.wealthmate.utils.formatDateKorYM
import com.jie.wealthmate.utils.today
import kotlinx.datetime.LocalDate
import kotlinx.datetime.yearMonth

/**
 * 월 선택 ModalBottomSheet
 */
@Composable
fun SelectedCalendarModalBottomSheet(
    monthItem: List<LocalDate>,
    selectedMonth: LocalDate = today,
    onMonthChange: (LocalDate) -> Unit = {},
    onDismissRequest: () -> Unit = {},
) {
    val listState = rememberLazyListState()
    var tempSelectedMonth by remember { mutableStateOf(selectedMonth) }

    WMModalBottomSheet(
        title = "월 선택",
        onDismissRequest = { onDismissRequest() },
    ) {

        LaunchedEffect(Unit) {
            val movePosition = monthItem.indexOf(tempSelectedMonth.firstDayOfMonth())
                .run { if (this <= 0) 0 else this - 1 }

            listState.scrollToItem(movePosition)
        }

        LazyColumn(
            modifier = Modifier.height(240.dp),
            state = listState
        ) {
            items(monthItem.size) {
                val month = monthItem[it]
                val isSelected = month.yearMonth == tempSelectedMonth.yearMonth

                Box(
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .fillMaxWidth()
                        .height(44.dp)
                        .clip(CircleShape)
                        .background(if (isSelected) ColorPrimary.Primary_200 else ColorGray.White)
                        .clickable { tempSelectedMonth = month },
                    contentAlignment = Alignment.Center
                ) {
                    WMText(
                        text = month.convertLocalDateToString(formatDateKorYM),
                        style = Typography().bodyLarge.copy(
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                            color = if (isSelected) ColorPrimary.Primary_700 else ColorGray.Gray_700,
                            fontSize = if (isSelected) 18.sp else 16.sp
                        ),
                    )
                }
            }
        }
        WMButton(
            text = "확인",
            buttonSize = ButtonSize.LARGE,
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            onClick = {
                onMonthChange(tempSelectedMonth)
                onDismissRequest()
            }
        )
    }
}