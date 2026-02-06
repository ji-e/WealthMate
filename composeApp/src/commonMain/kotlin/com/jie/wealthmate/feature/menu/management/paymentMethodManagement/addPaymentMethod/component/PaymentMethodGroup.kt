package com.jie.wealthmate.feature.menu.management.paymentMethodManagement.addPaymentMethod.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jie.wealthmate.component.WMText
import com.jie.wealthmate.theme.ColorGray
import com.jie.wealthmate.theme.ColorPrimary

@Composable
fun PaymentMethodGroupList(
    modifier: Modifier = Modifier,
    listState: LazyListState,
    paymentMethodGroupItems: List<PaymentMethodGroupItemData>,
    tempSelectedPaymentMethodGroup: PaymentMethodGroupItemData? = null,
    onGroupClick: (PaymentMethodGroupItemData) -> Unit = {},
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        state = listState,
    ) {
        items(
            count = paymentMethodGroupItems.size,
            key = { index -> paymentMethodGroupItems[index].id ?: index },
        ) {
            val paymentMethodGroupItem = paymentMethodGroupItems[it]

            PaymentMethodGroupItem(
                groupLabel = paymentMethodGroupItem.label,
                isSelected = paymentMethodGroupItem.id == tempSelectedPaymentMethodGroup?.id,
                onClick = { onGroupClick(paymentMethodGroupItem) }
            )
        }
    }
}

@Composable
fun PaymentMethodGroupItem(
    groupLabel: String,
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
        WMText(
            text = groupLabel,
            style = Typography().bodyLarge.copy(
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                color = if (isSelected) ColorPrimary.Primary_700 else ColorGray.Gray_700,
                fontSize = if (isSelected) 18.sp else 16.sp
            ),
        )
    }
}