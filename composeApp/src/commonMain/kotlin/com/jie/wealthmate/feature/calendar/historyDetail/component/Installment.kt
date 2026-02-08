package com.jie.wealthmate.feature.calendar.historyDetail.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jie.wealthmate.component.WMText
import com.jie.wealthmate.database.eneity.HistoryEntity
import com.jie.wealthmate.theme.ColorGray
import com.jie.wealthmate.theme.ColorPrimary
import com.jie.wealthmate.utils.convertLocalDateToString
import com.jie.wealthmate.utils.formatDateDotYYMD
import com.jie.wealthmate.utils.formatWithCommas
import com.jie.wealthmate.utils.toLocalDate
import com.jie.wealthmate.vo.InstallmentVo

@Composable
fun Installment(
    modifier: Modifier = Modifier,
    installment: InstallmentVo,
    installmentTime: Long?,
    installmentHistoryItems: List<HistoryEntity>,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(ColorGray.Gray_50)
            .padding(vertical = 12.dp),
    ) {
        WMText(
            text = "할부 내역",
            style = Typography().bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            modifier = Modifier.padding(horizontal = 12.dp)
        )
        WMText(
            text = "총 ${formatWithCommas(installment.amount.toString())}원 / ${installment.count}개월",
            style = Typography().titleMedium.copy(fontWeight = FontWeight.SemiBold),
            modifier = Modifier
                .padding(top = 4.dp)
                .padding(horizontal = 12.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 4.dp)
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            WMText(
                text = "회차",
                style = Typography().bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = ColorGray.Gray_500
                ),
                modifier = Modifier.width(48.dp)
            )
            WMText(
                text = "날짜",
                style = Typography().bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = ColorGray.Gray_500
                ),
                modifier = Modifier.width(60.dp),
                maxLines = 1

            )
            WMText(
                text = "금액",
                style = Typography().bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = ColorGray.Gray_500
                ),
                modifier = Modifier.weight(2f),
                maxLines = 1
            )
            WMText(
                text = "잔액",
                style = Typography().bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = ColorGray.Gray_500
                ),
                modifier = Modifier.weight(3f),
                maxLines = 1
            )
        }
        installmentHistoryItems.forEachIndexed { index, installmentHistory ->
            val isInstallmentSelected = installmentHistory.installment?.installmentTime == installmentTime
            val backgroundColor: Color
            val fontWeight: FontWeight
            if (isInstallmentSelected) {
                backgroundColor = ColorPrimary.Primary_200
                fontWeight = FontWeight.SemiBold
            } else {
                backgroundColor = ColorGray.Gray_50
                fontWeight = FontWeight.Normal
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = backgroundColor)
                    .padding(vertical = 2.dp, horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {

                WMText(
                    text = "${installmentHistory.installment?.installmentTime}회차",
                    style = Typography().bodyMedium.copy(fontWeight = fontWeight),
                    modifier = Modifier.width(48.dp)
                )
                WMText(
                    text = installmentHistory.date.toLocalDate()
                        .convertLocalDateToString(formatDateDotYYMD),
                    style = Typography().bodyMedium.copy(fontWeight = fontWeight),
                    modifier = Modifier.width(60.dp),
                    maxLines = 1

                )
                WMText(
                    text = "${formatWithCommas(installmentHistory.amount.toString())}원",
                    style = Typography().bodyMedium.copy(fontWeight = fontWeight),
                    modifier = Modifier.weight(2f),
                    maxLines = 1
                )
                WMText(
                    text = "${formatWithCommas(installmentHistory.installment?.installmentRemainAmount.toString())}원",
                    style = Typography().bodyMedium.copy(fontWeight = fontWeight),
                    modifier = Modifier.weight(3f),
                    maxLines = 1
                )
            }
        }
    }
}
