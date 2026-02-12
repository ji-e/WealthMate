package com.jie.wealthmate.vo

import com.jie.wealthmate.database.eneity.RepeatCycleEntity
import com.jie.wealthmate.feature.calendar.addHistory.component.RepeatCycleEnum
import com.jie.wealthmate.feature.menu.management.categoryManagement.component.LargeCategoryEnum
import com.jie.wealthmate.utils.default
import com.jie.wealthmate.utils.toLocalDate
import kotlinx.datetime.LocalDate


data class RepeatCycleVo(
    val id: String,
    val largeCategory: LargeCategoryEnum,
    val content: String?,
    val amount: Long,
    val repeatCycle: RepeatCycleEnum,        // RepeatCycleEnum
    val dayOfWeek: Int? = null,     // WEEKLY일 때 사용 (1=월, 7=일)
    val dayOfMonth: Int? = null,    // MONTHLY일 때 사용 (1~31)
    val date: LocalDate,
    val startDate: LocalDate,
    val endDate: LocalDate?,
    val categoryId: String?,
    val paymentMethodId: String?,
    val isActive: Boolean,
    val isModified: Boolean,
    val isDeleted: Boolean,
    val updateAt: LocalDate?,
) {
    companion object {
        fun RepeatCycleEntity?.mapperToVo() = RepeatCycleVo(
            id = this?.id.default(),
            largeCategory = LargeCategoryEnum.creator(this?.largeCategory),
            content = this?.content.default(),
            amount = this?.amount.default(),
            repeatCycle = RepeatCycleEnum.create(this?.repeatCycle),
            dayOfWeek = this?.dayOfWeek.default(),
            dayOfMonth = this?.dayOfMonth.default(),
            date = this?.date.toLocalDate(),
            startDate = this?.startDate.toLocalDate(),
            endDate = this?.endDate?.toLocalDate(),
            categoryId = this?.categoryId.default(),
            paymentMethodId = this?.paymentMethodId.default(),
            isActive = this?.isActive.default(),
            isModified = this?.isModified.default(),
            isDeleted = this?.isDeleted.default(),
            updateAt = this?.updatedAt?.toLocalDate(),
        )
    }
}