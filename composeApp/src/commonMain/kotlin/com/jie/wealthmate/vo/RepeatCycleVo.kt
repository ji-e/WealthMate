package com.jie.wealthmate.vo

import com.jie.wealthmate.database.eneity.RepeatCycleEntity
import com.jie.wealthmate.feature.menu.categoryManagement.component.LargeCategoryEnum
import com.jie.wealthmate.utils.default


data class RepeatCycleVo(
    val id: String,
    val largeCategory: LargeCategoryEnum,
    val content: String?,
    val amount: Long,
    val repeatCycle: String,        // RepeatCycleEnum
    val dayOfWeek: Int? = null,     // WEEKLY일 때 사용 (1=월, 7=일)
    val dayOfMonth: Int? = null,    // MONTHLY일 때 사용 (1~31)
    val startDate: Long,
    val endDate: Long?,
    val categoryId: String?,
    val paymentMethodId: String?,
) {
    companion object {
        fun RepeatCycleEntity.mapperToVo() = RepeatCycleVo(
            id = this?.id.default(),
            largeCategory = LargeCategoryEnum.creator(this?.largeCategory),
            content = this?.content.default(),
            amount = this?.amount.default(),
            repeatCycle = this?.repeatCycle.default(),
            dayOfWeek = this?.dayOfWeek.default(),
            dayOfMonth = this?.dayOfMonth.default(),
            startDate = this?.startDate.default(),
            endDate = this?.endDate.default(),
            categoryId = this?.categoryId.default(),
            paymentMethodId = this?.paymentMethodId.default(),
        )
    }
}