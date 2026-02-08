package com.jie.wealthmate.vo

import com.jie.wealthmate.database.eneity.HistoryWithDetails
import com.jie.wealthmate.feature.menu.management.categoryManagement.component.LargeCategoryEnum
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
    val installmentTime: Long?,
    val repeatCycle: RepeatCycleVo?,
    val category: CategoryVo?,
    val categoryTag: CategoryTagVo?,
    val paymentMethod: PaymentMethodVo?,
    val content: String?,
    val isVisibility: Boolean,
    val userId: String?,
) {
    val historyInfo: String
        get() {
            val categoryPart = buildString {
                val middleLabel = category?.middleLabel
                val tagLabel = categoryTag?.label
                if (!middleLabel.isNullOrBlank()) {
                    append(middleLabel)
                }
                if (tagLabel.isNullOrBlank().not()) {
                    if (isNotEmpty()) append(" > ")
                    append(tagLabel)
                }
            }

            val parts = listOfNotNull(
                categoryPart.takeIf { it.isNotBlank() },
                paymentMethod?.label?.takeIf { it.isNotBlank() },
                content?.takeIf { it.isNotBlank() },
                installment?.let { "할부 $installmentTime/${it.count}회차" }
            )

            return parts.joinToString(" | ")
        }


    companion object {
        fun HistoryWithDetails?.mapperToVo() = HistoryVo(
            id = this?.history?.id.default(),
            largeCategory = LargeCategoryEnum.creator(this?.history?.largeCategory),
            date = this?.history?.date.toLocalDate(),
            amount = this?.history?.amount.default(),
            installment = this?.installment?.mapperToVo(),
            installmentTime = this?.history?.installmentTime,
            repeatCycle = this?.repeatCycle?.mapperToVo(),
            category = this?.category?.mapperToVo(),
            categoryTag = this?.category?.mapperToVo()?.tags?.find { it.id == this.history.categoryTagId },
            paymentMethod = this?.paymentMethod?.mapperToVo(),
            content = this?.history?.content,
            isVisibility = this?.history?.isVisibility ?: true,
            userId = this?.history?.userId
        )
    }
}
