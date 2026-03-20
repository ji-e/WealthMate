package com.jie.wealthmate.feature.home.component.vo

import com.jie.wealthmate.vo.PaymentMethodVo

data class PaymentMethodSegmentedChartVo(
    val paymentMethod: PaymentMethodVo?, // null 허용으로 변경
    val amount: Long,
)