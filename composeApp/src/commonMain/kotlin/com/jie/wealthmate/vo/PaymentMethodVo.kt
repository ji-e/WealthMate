package com.jie.wealthmate.vo

import com.jie.wealthmate.database.eneity.PaymentMethodEntity
import com.jie.wealthmate.utils.default

data class PaymentMethodVo(
    val id: String,
    val label: String,
    val groupId: String?,
    val groupLabel: String?,
    val sort: Long,
) {
    companion object {
        fun PaymentMethodEntity?.mapperToVo() = PaymentMethodVo(
            id = this?.id.default(),
            label = this?.label.default(),
            groupId = this?.groupId.default(),
            groupLabel = this?.groupLabel.default(),
            sort = this?.sort.default(),
        )
    }
}
