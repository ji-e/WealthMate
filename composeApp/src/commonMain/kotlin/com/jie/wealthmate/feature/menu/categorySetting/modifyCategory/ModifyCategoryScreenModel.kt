package com.jie.wealthmate.feature.menu.categorySetting.modifyCategory

import androidx.compose.ui.text.input.TextFieldValue
import com.jie.wealthmate.base.BaseScreenModel
import com.jie.wealthmate.component.CategoryIconEnum
import com.jie.wealthmate.feature.menu.categorySetting.component.LargeCategoryEnum
import com.jie.wealthmate.repository.CategoryRepository
import com.jie.wealthmate.utils.default
import com.jie.wealthmate.vo.CategoryTagVo

class ModifyCategoryScreenModel(
    private val categoryRepository: CategoryRepository,
) : BaseScreenModel<ModifyCategoryUiState>() {
    override val initialState: ModifyCategoryUiState
        get() = ModifyCategoryUiState()

    private var categoryId: Long = 0L

    fun updateInit(largeCategoryEnum: LargeCategoryEnum, categoryId: Long) {
        this.categoryId = categoryId

        reduceState { state ->
            state.copy(
                largeCategory = largeCategoryEnum,
            )
        }

        getCategoryDetail()
    }

    fun updateCategoryIcon(icon: CategoryIconEnum) {
        reduceState { state ->
            state.copy(
                categoryIcon = icon,
                isChangedData = true
            )
        }
    }

    fun updateCategoryLabel(textFieldValue: TextFieldValue) {
        reduceState { state ->
            state.copy(
                label = textFieldValue,
                isChangedData = true
            )
        }
    }

    fun updateCategoryTagLabel(textFieldValue: TextFieldValue) {
        reduceState { state ->
            state.copy(
                tagLabel = textFieldValue,
                isChangedData = true
            )
        }
    }

    fun addCategoryTagLabel(tagLabel: TextFieldValue?) {
        reduceState { state ->
            if (tagLabel == null) {
                return@reduceState state.copy(tagLabel = TextFieldValue(""))
            }

            if (tagLabel.text.isBlank()) {
                showSnackbar("상세 태그 이름을 입력해 주세요.")
                return@reduceState state
            }

            val categoryTag = CategoryTagVo(label = tagLabel.text)
            val isExisted = state.tagLabelItems.any { it.label == categoryTag.label }

            if (isExisted) {
                showSnackbar("이미 존재하는 태그 입니다.")
                state
            } else {
                state.copy(
                    tagLabel = TextFieldValue(""),
                    tagLabelItems = state.tagLabelItems.toMutableList().apply { add(categoryTag) },
                    isChangedData = true
                )
            }
        }
    }

    fun removeCategoryTagLabel(tag: CategoryTagVo) {
        reduceState { state ->
            state.copy(
                tagLabelItems = state.tagLabelItems.toMutableList().apply { remove(tag) },
                isChangedData = true
            )
        }
    }

    fun updateIsFixed(isFixed: Boolean) {
        reduceState { state ->
            state.copy(
                isFixed = isFixed,
                isChangedData = true
            )
        }
    }

    fun getCategoryDetail() {
        launchSafe(
            block = {
                categoryRepository.getCategoryWithTags(categoryId)
            }
        ) { response ->
            reduceState { state ->
                state.copy(
                    categoryIcon = CategoryIconEnum.creatorFromText(response?.icon),
                    label = TextFieldValue(response?.middleLabel.default()),
                    tagLabelItems = response?.tags.default()
                        .map { CategoryTagVo(it.id, it.tagLabel) },
                    isFixed = response?.fixed.default()
                )
            }

        }
    }
}
