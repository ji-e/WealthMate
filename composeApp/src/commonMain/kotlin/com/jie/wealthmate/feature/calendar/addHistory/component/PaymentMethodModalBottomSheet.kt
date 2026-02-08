package com.jie.wealthmate.feature.calendar.addHistory.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jie.wealthmate.component.WMModalBottomSheet
import com.jie.wealthmate.component.WMText
import com.jie.wealthmate.theme.ColorGray
import com.jie.wealthmate.theme.ColorGroup
import com.jie.wealthmate.theme.ColorPrimary
import com.jie.wealthmate.vo.PaymentMethodVo

@Composable
fun PaymentMethodModalBottomSheet(
    selectedPaymentMethod: PaymentMethodVo?,
    paymentMethodItems: List<PaymentMethodVo>,
    onConfirmClick: (PaymentMethodVo?) -> Unit,
    onDismissRequest: () -> Unit,
) {
    val listState = rememberLazyListState()
    var tempSelectedPaymentMethod by remember { mutableStateOf(selectedPaymentMethod) }

    LaunchedEffect(Unit) {
        val index = paymentMethodItems.indexOf(tempSelectedPaymentMethod)
        val movePosition = if (index <= 0) 0 else index - 1

        listState.scrollToItem(movePosition)
    }

    WMModalBottomSheet(
        title = "결제수단",
        onDismissRequest = onDismissRequest,
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 28.dp)
                .padding(bottom = 20.dp)
        ) {
            PaymentMethodList(
                listState = listState,
                paymentMethodItems = paymentMethodItems,
                tempSelectedPaymentMethod = tempSelectedPaymentMethod,
                onPaymentMethodClick = {
                    tempSelectedPaymentMethod = it
                    onDismissRequest()
                    onConfirmClick(tempSelectedPaymentMethod)
                }
            )
        }
    }
}

@Composable
fun PaymentMethodList(
    modifier: Modifier = Modifier,
    listState: LazyListState,
    paymentMethodItems: List<PaymentMethodVo>,
    tempSelectedPaymentMethod: PaymentMethodVo? = null,
    onPaymentMethodClick: (PaymentMethodVo) -> Unit = {},
) {
    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(max = 200.dp),
        state = listState,
    ) {
        val colorList = ColorGroup.getColorList()
        val groupColorMap = paymentMethodItems
            .distinctBy { it.groupLabel }
            .mapIndexed { index, item ->
                item.groupLabel to colorList[index % colorList.size].second
            }
            .toMap()

        items(
            count = paymentMethodItems.size,
            key = { index -> paymentMethodItems[index].id },
        ) {
            val paymentMethod = paymentMethodItems[it]

            PaymentMethodItem(
                label = paymentMethod.label,
                groupLabel = paymentMethod.groupLabel,
                groupBackgroundColor = groupColorMap[paymentMethod.groupLabel]
                    ?: ColorGray.Gray_200,
                isSelected = paymentMethod == tempSelectedPaymentMethod,
                onClick = { onPaymentMethodClick(paymentMethod) }
            )
        }
    }
}

@Composable
fun PaymentMethodItem(
    label: String,
    groupLabel: String?,
    groupBackgroundColor: Color,
    isSelected: Boolean,
    onClick: () -> Unit = {},
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp)
            .clip(CircleShape)
            .background(if (isSelected) ColorPrimary.Primary_200 else ColorGray.White)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Row {

            WMText(
                text = label,
                style = Typography().bodyLarge.copy(
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                    color = if (isSelected) ColorPrimary.Primary_700 else ColorGray.Gray_700,
                    fontSize = if (isSelected) 18.sp else 16.sp
                ),
            )

            if (groupLabel != null) {
                WMText(
                    text = groupLabel,
                    style = Typography().labelMedium.copy(fontWeight = FontWeight.SemiBold),
                    modifier = Modifier
                        .padding(horizontal = 6.dp)
                        .clip(CircleShape)
                        .background(groupBackgroundColor)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}