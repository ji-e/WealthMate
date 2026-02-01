package com.jie.wealthmate.vo

import com.jie.wealthmate.database.eneity.HistoryWithDetails
import com.jie.wealthmate.feature.menu.categoryManagement.component.LargeCategoryEnum
import com.jie.wealthmate.utils.default
import com.jie.wealthmate.utils.toLocalDate
import com.jie.wealthmate.vo.CategoryVo.Companion.mapperToVo
import com.jie.wealthmate.vo.InstallmentVo.Companion.mapperToVo
import com.jie.wealthmate.vo.PaymentMethodVo.Companion.mapperToVo
import com.jie.wealthmate.vo.RepeatCycleVo.Companion.mapperToVo
import kotlinx.datetime.LocalDate

data class HistoryVo(
    val id: String,
    val largeCategory: LargeCategoryEnum,
    val date: LocalDate,
    val amount: Long,
    val installment: InstallmentVo?,
    val repeatCycle: RepeatCycleVo?,
    val category: CategoryVo?,
    val categoryTag: CategoryTagVo?,
    val paymentMethod: PaymentMethodVo?,
    val content: String?,
) {
    companion object {
        fun HistoryWithDetails?.mapperToVo() = HistoryVo(
            id = this?.history?.id.default(),
            largeCategory = LargeCategoryEnum.creator(this?.history?.largeCategory),
            date = this?.history?.date.toLocalDate(),
            amount = this?.history?.amount.default(),
            installment = this?.installment?.mapperToVo(),
            repeatCycle = this?.repeatCycle?.mapperToVo(),
            category = this?.category?.mapperToVo(),
            categoryTag = this?.category?.mapperToVo()?.tags?.find { it.id == this.history.categoryTagId },
            paymentMethod = this?.paymentMethod?.mapperToVo(),
            content = this?.history?.content,
        )
    }
}

