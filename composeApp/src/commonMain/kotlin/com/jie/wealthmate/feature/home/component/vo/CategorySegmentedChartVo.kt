package com.jie.wealthmate.feature.home.component.vo

import com.jie.wealthmate.vo.CategoryVo

data class CategorySegmentedChartVo(
    val category: CategoryVo,
    val amount: Long,
)