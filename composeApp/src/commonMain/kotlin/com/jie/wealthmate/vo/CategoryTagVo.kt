package com.jie.wealthmate.vo

import androidx.compose.runtime.Immutable
import com.jie.wealthmate.database.eneity.CategoryTagEntity
import com.jie.wealthmate.utils.default

@Immutable
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
