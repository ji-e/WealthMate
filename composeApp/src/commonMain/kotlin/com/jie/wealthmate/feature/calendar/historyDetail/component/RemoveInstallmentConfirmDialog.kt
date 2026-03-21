package com.jie.wealthmate.feature.calendar.historyDetail.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.jie.wealthmate.component.WMCheckBox
import com.jie.wealthmate.component.WMText
import com.jie.wealthmate.theme.ColorGray
import com.jie.wealthmate.theme.ColorRed

@Composable
fun RemoveInstallmentConfirmDialog(
    onConfirmClick: (Boolean) -> Unit,
    onDismissRequest: () -> Unit,
) {
    var isChecked by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismissRequest
    ) {
        Column(
            modifier = Modifier
                .clip(shape = RoundedCornerShape(20.dp))
                .background(ColorGray.White)
                .wrapContentHeight()
                .heightIn(min = 220.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 20.dp, vertical = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier,
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    WMText(
                        text = "정말 삭제하시겠습니까?\n삭제된 정보는 복구할 수 없습니다.",
                        textAlign = TextAlign.Center,
                        style = androidx.compose.material3.MaterialTheme.typography.titleMedium,
                    )

                    WMCheckBox(
                        label = "관련 모든 할부 내역 삭제",
                        labelStyle = MaterialTheme.typography.titleSmall,
                        checked = isChecked,
                        onCheckedChange = { isChecked = it },
                    )
                }
            }
            Row {
                Box(
                    modifier = Modifier
                        .height(52.dp)
                        .weight(1f)
                        .background(ColorGray.Gray_50)
                        .clickable { onDismissRequest() },
                    contentAlignment = Alignment.Center
                ) {
                    WMText(
                        text = "취소",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium,
                    )
                }

                Box(
                    modifier = Modifier
                        .height(52.dp)
                        .weight(1f)
                        .background(ColorRed.Red_300)
                        .clickable {
                            onDismissRequest()
                            onConfirmClick(isChecked)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    WMText(
                        text = "삭제",
                        style = MaterialTheme.typography.titleMedium,
                        color = ColorGray.White,
                        fontWeight = FontWeight.Medium,
                    )
                }
            }
        }
    }
}