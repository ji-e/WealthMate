package com.jie.wealthmate.vo

import com.jie.wealthmate.database.eneity.PaymentMethodEntity
import com.jie.wealthmate.database.eneity.PaymentMethodWithGroupEntity
import com.jie.wealthmate.utils.default

data class PaymentMethodVo(
    val id: String,
    val label: String,
    val groupId: String?,
    val groupLabel: String?,
    val assetId: String? = null,
    val sort: Long,
) {
    companion object {
        val UNSET = PaymentMethodVo(
            id = "unset",
            label = "결제수단 없음",
            groupId = null,
            groupLabel = null,
            assetId = null,
            sort = -1L
        )

        fun PaymentMethodEntity?.mapperToVo() = PaymentMethodVo(
            id = this?.id.default(),
            label = this?.label.default(),
            groupId = this?.groupId,
            groupLabel = this?.groupLabel,
            assetId = this?.assetId,
            sort = this?.sort.default(),
        )

        fun PaymentMethodWithGroupEntity?.mapperToVo() = PaymentMethodVo(
            id = this?.paymentMethod?.id.default(),
            label = this?.paymentMethod?.label.default(),
            groupId = this?.paymentMethod?.groupId,
            groupLabel = this?.group?.label ?: this?.paymentMethod?.groupLabel,
            assetId = this?.paymentMethod?.assetId,
            sort = this?.paymentMethod?.sort.default(),
        )
    }
}
