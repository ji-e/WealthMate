package com.jie.wealthmate.vo

import com.jie.wealthmate.database.eneity.InstallmentEntity
import com.jie.wealthmate.utils.default
import com.jie.wealthmate.utils.toLocalDate
import kotlinx.datetime.LocalDate


data class InstallmentVo(
    val id: String,
    val content: String,
    val amount: Long,
    val count: Long,
    val startDate: LocalDate,
    val paymentMethodId: String,
) {
    companion object {
        fun InstallmentEntity?.mapperToVo() = InstallmentVo(
            id = this?.id.default(),
            content = this?.content.default(),
            amount = this?.amount.default(),
            count = this?.count.default(),
            startDate = this?.startDate.toLocalDate(),
            paymentMethodId = this?.paymentMethodId.default(),
        )
    }
}