package com.jie.wealthmate.vo

import com.jie.wealthmate.feature.menu.categoryManagement.component.LargeCategoryEnum

data class CategoryVo(
    val id: String,
    val icon: String,
    val largeCategory: LargeCategoryEnum,
    val middleLabel: String,
    val sort: Long,
    val isFixed: Boolean,
    val tags: List<CategoryTagVo>,
)
