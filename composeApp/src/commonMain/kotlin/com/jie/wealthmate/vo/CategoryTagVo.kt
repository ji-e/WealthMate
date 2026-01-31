package com.jie.wealthmate.vo

import com.jie.wealthmate.database.eneity.CategoryTagEntity
import com.jie.wealthmate.utils.default

data class CategoryTagVo(
    val id: String? = null,
    val label: String,
) {
    companion object {
        fun CategoryTagEntity?.mapperToVo() = CategoryTagVo(
            id = this?.id,
            label = this?.tagLabel.default(),
        )
    }
}
