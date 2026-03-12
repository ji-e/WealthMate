package com.jie.wealthmate.vo

import androidx.compose.runtime.Immutable
import com.jie.wealthmate.database.eneity.CategoryEntity
import com.jie.wealthmate.feature.menu.management.categoryManagement.component.LargeCategoryEnum
import com.jie.wealthmate.utils.default
import com.jie.wealthmate.vo.CategoryTagVo.Companion.mapperToVo
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

@Immutable
data class CategoryVo(
    val id: String,
    val icon: String,
    val largeCategory: LargeCategoryEnum,
    val middleLabel: String,
    val sort: Long,
    val isFixed: Boolean,
    val tags: ImmutableList<CategoryTagVo>,
) {
    val isUnset: Boolean get() = id.startsWith(UNSET_ID_PREFIX)

    companion object {
        const val UNSET_ID_PREFIX = "unset_"

        /**
         * 특정 거래구분에 대한 '카테고리 없음' 객체를 생성합니다.
         * 인자가 없으면 기본값으로 지출(EXPENSES) 구분의 객체를 반환합니다.
         */
        fun unset(largeCategory: LargeCategoryEnum = LargeCategoryEnum.EXPENSES) = CategoryVo(
            id = "${UNSET_ID_PREFIX}${largeCategory.name}",
            icon = "❓",
            largeCategory = largeCategory,
            middleLabel = "카테고리 없음",
            sort = -1L,
            isFixed = false,
            tags = persistentListOf()
        )

        fun CategoryEntity?.mapperToVo(largeCategory: LargeCategoryEnum = LargeCategoryEnum.EXPENSES): CategoryVo {
            if (this == null) return unset(largeCategory)
            return CategoryVo(
                id = id.default(),
                icon = icon.default(),
                largeCategory = LargeCategoryEnum.creator(this.largeCategory),
                middleLabel = middleLabel.default(),
                sort = sort.default(),
                isFixed = isFixed.default(),
                tags = tags?.map { it.mapperToVo() }.orEmpty().toImmutableList()
            )
        }
    }
}
