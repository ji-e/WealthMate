package com.jie.wealthmate.feature.calendar.addHistory.component

import androidx.compose.runtime.Composable
import com.jie.wealthmate.component.WMListSelectionModalBottomSheet
import com.jie.wealthmate.vo.PaymentMethodVo

@Composable
fun PaymentMethodModalBottomSheet(
    selectedPaymentMethod: PaymentMethodVo?,
    paymentMethodItems: List<PaymentMethodVo>,
    onConfirmClick: (PaymentMethodVo?) -> Unit,
    onDismissRequest: () -> Unit,
) {
    WMListSelectionModalBottomSheet(
        title = "결제수단",
        items = paymentMethodItems,
        selectedItem = selectedPaymentMethod,
        itemLabel = { it.label },
        onItemSelected = onConfirmClick,
        onDismissRequest = onDismissRequest
    )
}
