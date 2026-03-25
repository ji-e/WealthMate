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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jie.wealthmate.component.WMIconButton
import com.jie.wealthmate.component.WMText
import com.jie.wealthmate.feature.menu.management.categoryManagement.component.LargeCategoryEnum
import com.jie.wealthmate.theme.ColorPrimary
import com.jie.wealthmate.theme.ColorSetting
import com.jie.wealthmate.theme.Padding
import com.jie.wealthmate.theme.Shapes
import com.jie.wealthmate.theme.WMTheme
import com.jie.wealthmate.utils.convertLocalDateToString
import com.jie.wealthmate.utils.formatDateDotYYMD
import com.jie.wealthmate.utils.formatWithCommas
import com.jie.wealthmate.utils.today
import com.jie.wealthmate.vo.HistoryVo
import com.jie.wealthmate.vo.InstallmentVo
import org.jetbrains.compose.resources.painterResource
import wealthmate.composeapp.generated.resources.Res
import wealthmate.composeapp.generated.resources.ic_error_outline
import wealthmate.composeapp.generated.resources.ic_keyboard_arrow_right

@Composable
fun Installment(
    modifier: Modifier = Modifier,
    installment: InstallmentVo,
    installmentTime: Long?,
    installmentHistoryItems: List<HistoryVo>,
    onModifyClick: () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(Shapes.medium)
            .background(ColorSetting.EmptyBackground)
            .padding(horizontal = 16.dp)
            .padding(top = 6.dp),
    ) {
        InstallmentHeader(
            amount = installment.amount,
            count = installment.count,
            onModifyClick = onModifyClick
        )

        InstallmentNotice(
            modifier = Modifier.padding(bottom = Padding.ContainerVertical)
        )

        WMText(
            text = "할부 내역",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
        )

        InstallmentTable(
            currentInstallmentTime = installmentTime,
            historyItems = installmentHistoryItems
        )

        Spacer(modifier = Modifier.height(Padding.ContainerVertical))
    }
}

@Composable
private fun InstallmentHeader(
    amount: Long,
    count: Long,
    onModifyClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        WMText(
            text = "총 ${amount.formatWithCommas()}원 / ${count}개월",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(1f, false)
        )

        WMIconButton(
            iconRes = Res.drawable.ic_keyboard_arrow_right,
            modifier = Modifier.size(32.dp),
            iconModifier = Modifier.size(24.dp),
            onClick = onModifyClick
        )
    }
}

@Composable
private fun InstallmentNotice(
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
            text = "수정시 바로 적용됩니다.",
            style = MaterialTheme.typography.bodySmall,
            color = ColorSetting.Info
        )
    }
}

@Composable
private fun InstallmentTable(
    currentInstallmentTime: Long?,
    historyItems: List<HistoryVo>,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        // Table Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Padding.SpacerXS, bottom = Padding.SpacerXXS),
            horizontalArrangement = Arrangement.spacedBy(Padding.SpacerXXS)
        ) {
            val headerStyle = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.SemiBold,
                color = ColorSetting.Info
            )
            WMText(text = "회차", style = headerStyle, modifier = Modifier.width(44.dp))
            WMText(text = "날짜", style = headerStyle, modifier = Modifier.width(56.dp), maxLines = 1)
            WMText(text = "금액", style = headerStyle, modifier = Modifier.weight(2f), maxLines = 1)
            WMText(text = "잔액", style = headerStyle, modifier = Modifier.weight(3f), maxLines = 1)
        }

        // Table Rows
        historyItems.forEach { history ->
            val isSelected = history.installmentTime == currentInstallmentTime
            val backgroundColor =
                if (isSelected) ColorPrimary.Primary_200 else ColorSetting.EmptyBackground
            val fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = backgroundColor)
                    .padding(vertical = Padding.SpacerXXS),
                horizontalArrangement = Arrangement.spacedBy(Padding.SpacerXXS),
                verticalAlignment = Alignment.CenterVertically
            ) {
                WMText(
                    text = "${history.installmentTime}회차",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = fontWeight,
                    modifier = Modifier.width(44.dp)
                )
                WMText(
                    text = history.date.convertLocalDateToString(formatDateDotYYMD),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = fontWeight,
                    modifier = Modifier.width(56.dp),
                    maxLines = 1
                )
                WMText(
                    text = "${history.amount.formatWithCommas()}원",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = fontWeight,
                    modifier = Modifier.weight(2f),
                    maxLines = 1
                )
                WMText(
                    text = "${history.installmentRemainAmount?.formatWithCommas() ?: "0"}원",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = fontWeight,
                    modifier = Modifier.weight(3f),
                    maxLines = 1
                )
            }
        }
    }
}

@Preview
@Composable
private fun InstallmentPreview() {
    WMTheme {
        Installment(
            installment = InstallmentVo(
                id = "1",
                content = "노트북 구매",
                amount = 1200000,
                count = 12,
                startDate = today,
                paymentMethodId = "card"
            ),
            installmentTime = 1,
            installmentHistoryItems = listOf(
                HistoryVo(
                    id = "h1",
                    largeCategory = LargeCategoryEnum.EXPENSES,
                    date = today,
                    amount = 100000,
                    installmentTime = 1,
                    installmentRemainAmount = 1100000
                ),
                HistoryVo(
                    id = "h2",
                    largeCategory = LargeCategoryEnum.EXPENSES,
                    date = today,
                    amount = 100000,
                    installmentTime = 2,
                    installmentRemainAmount = 1000000
                )
            ),
            onModifyClick = {}
        )
    }
}
