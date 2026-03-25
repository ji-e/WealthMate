package com.jie.wealthmate.vo

import com.jie.wealthmate.database.eneity.HistoryEntity
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
    val installment: InstallmentVo? = null,
    val installmentTime: Long? = null,
    val installmentRemainAmount: Long? = null,
    val repeatCycle: RepeatCycleVo? = null,
    val category: CategoryVo? = null,
    val categoryTag: CategoryTagVo? = null,
    val paymentMethod: PaymentMethodVo? = null,
    val content: String? = null,
    val isVisibility: Boolean = true,
    val userId: String? = null,
) {
    val historyInfo: String
        get() {
            val categoryPart = categoryInfo

            val parts = listOfNotNull(
                categoryPart.takeIf { it.isNotBlank() },
                paymentMethod?.label?.takeIf { it.isNotBlank() },
                installment?.let { "할부 $installmentTime/${it.count}회차" }
            )

            return parts.joinToString(" | ")
        }

    val categoryInfo: String
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
            return categoryPart
        }

    companion object {
        fun HistoryWithDetails?.mapperToVo() = HistoryVo(
            id = this?.history?.id.default(),
            largeCategory = LargeCategoryEnum.creator(this?.history?.largeCategory),
            date = this?.history?.date.toLocalDate(),
            amount = this?.history?.amount.default(),
            installment = this?.installment?.mapperToVo(),
            installmentTime = this?.history?.installment?.installmentTime,
            installmentRemainAmount = this?.history?.installment?.installmentRemainAmount,
            repeatCycle = this?.repeatCycle?.mapperToVo(),
            category = this?.category?.mapperToVo(),
            categoryTag = this?.category?.mapperToVo()?.tags?.find { it.id == this.history.categoryTagId },
            paymentMethod = this?.paymentMethod?.mapperToVo(),
            content = this?.history?.content,
            isVisibility = this?.history?.isVisibility ?: true,
            userId = this?.history?.userId
        )

        fun HistoryEntity.mapperToVo() = HistoryVo(
            id = id,
            largeCategory = LargeCategoryEnum.creator(largeCategory),
            date = date.toLocalDate(),
            amount = amount,
            installmentTime = installment?.installmentTime,
            installmentRemainAmount = installment?.installmentRemainAmount,
            content = content,
            isVisibility = isVisibility,
            userId = userId
        )
    }
}
