package com.jie.wealthmate.feature.calendar.component.listCalendar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jie.wealthmate.component.WMText
import com.jie.wealthmate.feature.menu.management.categoryManagement.component.LargeCategoryEnum
import com.jie.wealthmate.theme.ColorGray
import com.jie.wealthmate.theme.ColorRed
import com.jie.wealthmate.utils.default
import com.jie.wealthmate.utils.formatWithCommas
import com.jie.wealthmate.vo.HistoryVo
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.painterResource
import wealthmate.composeapp.generated.resources.Res
import wealthmate.composeapp.generated.resources.ic_push_pin


@Composable
fun DateHeader(
    date: LocalDate,
) {
    WMText(
        text = "${date.day}일",
        style = Typography().titleMedium.copy(fontWeight = FontWeight.SemiBold),
        modifier = Modifier
            .fillMaxWidth()
            .background(color = ColorGray.White)
            .padding(horizontal = 20.dp)
            .padding(top = 12.dp, bottom = 4.dp)
    )
}

@Composable
fun HistoryItem(
    history: HistoryVo,
    onItemClick: () -> Unit,
) {
    val category = history.category
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onItemClick() }
            .padding(horizontal = 20.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.width(60.dp)) {
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(category?.largeCategory?.backgroundColor ?: ColorGray.Gray_100)
                    .size(40.dp),
                contentAlignment = Alignment.Center
            ) {
                WMText(
                    text = category?.icon ?: "？",
                    style = Typography().bodyLarge.copy(fontSize = 20.sp)
                )
            }
            if (category?.isFixed.default()) {
                Icon(
                    painter = painterResource(Res.drawable.ic_push_pin),
                    contentDescription = null,
                    tint = ColorRed.Red_300,
                    modifier = Modifier
                        .padding(start = 24.dp)
                        .size(24.dp)
                        .align(Alignment.TopStart)
                )
            }
        }

        val mark = when (history.largeCategory) {
            LargeCategoryEnum.INCOME -> "+"
            LargeCategoryEnum.EXPENSES -> "-"
            else -> ""
        }

        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            WMText(
                text = "$mark${formatWithCommas(history.amount.toString())}원",
                style = Typography().titleMedium.copy(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                ),
            )

            Row {
                category?.middleLabel?.let {
                    WMText(text = it,)
                }

                history.categoryTag?.let {
                    WMText(text = " > ${it.label}",)
                }

                history.paymentMethod?.let{
                    WMText(text = " | ${it.label}",)
                }

                if (history.content.isNullOrEmpty().not()) {
                    WMText(text = " | ${history.content}",)
                }
            }
        }
    }
}