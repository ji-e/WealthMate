package com.jie.wealthmate.vo

import androidx.compose.runtime.Immutable
import com.jie.wealthmate.database.eneity.CategoryEntity
import com.jie.wealthmate.feature.menu.management.categoryManagement.component.LargeCategoryEnum
import com.jie.wealthmate.utils.default
import com.jie.wealthmate.vo.CategoryTagVo.Companion.mapperToVo

@Immutable
data class CategoryVo(
    val id: String,
    val icon: String,
    val largeCategory: LargeCategoryEnum,
    val middleLabel: String,
    val sort: Long,
    val isFixed: Boolean,
    val tags: List<CategoryTagVo>,
) {
    companion object {
        fun CategoryEntity?.mapperToVo() = CategoryVo(
            id = this?.id.default(),
            icon = this?.icon.default(),
            largeCategory = LargeCategoryEnum.creator(this?.largeCategory),
            middleLabel = this?.middleLabel.default(),
            sort = this?.sort.default(),
            isFixed = this?.isFixed.default(),
            tags = this?.tags?.map { it.mapperToVo() }.default()
        )
    }
}
