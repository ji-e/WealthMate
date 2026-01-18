package com.jie.wealthmate.feature.menu.categorySetting.addCategory

import androidx.compose.ui.text.input.TextFieldValue
import com.jie.wealthmate.base.BaseScreenModel
import com.jie.wealthmate.component.CategoryIconEnum
import com.jie.wealthmate.feature.menu.categorySetting.component.CategoryItemData
import com.jie.wealthmate.feature.menu.categorySetting.component.LargeCategoryEnum
import com.jie.wealthmate.repository.CategoryRepository
import com.jie.wealthmate.vo.CategoryTagVo

class AddCategoryScreenModel(
    private val categoryRepository: CategoryRepository,
) : BaseScreenModel<AddCategoryUiState>() {

    private var categoryItems: List<CategoryItemData> = emptyList()

    override val initialState: AddCategoryUiState
        get() = AddCategoryUiState()

    fun updateInit(
        categoryItems: List<CategoryItemData>,
        largeCategoryEnum: LargeCategoryEnum,
    ) {
        this.categoryItems = categoryItems

        println(largeCategoryEnum)
        reduceState { state ->
            state.copy(
                largeCategory = largeCategoryEnum,
            )
        }
    }

    fun updateCategoryIcon(icon: CategoryIconEnum) {
        reduceState { state ->
            state.copy(
                categoryIcon = icon
            )
        }
    }

    fun updateCategoryLabel(textFieldValue: TextFieldValue) {
        reduceState { state ->
            state.copy(
                label = textFieldValue
            )
        }
    }

    fun updateCategoryTagLabel(textFieldValue: TextFieldValue) {
        reduceState { state ->
            state.copy(
                tagLabel = textFieldValue
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
                    tagLabelItems = state.tagLabelItems.toMutableList().apply { add(categoryTag) }
                )
            }
        }
    }

    fun removeCategoryTagLabel(tag: CategoryTagVo) {
        reduceState { state ->
            state.copy(
                tagLabelItems = state.tagLabelItems.toMutableList().apply { remove(tag) }
            )
        }
    }

    fun updateIsFixed(isFixed: Boolean) {
        reduceState { state ->
            state.copy(
                isFixed = isFixed
            )
        }
    }

    fun saveCategory() {
        val uiState = container.uiState.value

        if (categoryItems.any { it.label == uiState.label.text }) {
            showSnackbar("존재하는 카테고리입니다.")
            return
        }

        launchSafe(
            block = {
                categoryRepository.addCategory(
                    icon = uiState.categoryIcon.text,
                    largeCategory = uiState.largeCategory.name,
                    middleLabel = uiState.label.text,
                    sort = categoryItems.size.toLong(),
                    isFixed = uiState.isFixed,
                    tagIds = createTags()
                )
            },
        ) {
            showSnackbar("카테고리가 저장되었습니다.")
            postSideEffect { AddCategoryUiSideEffect.OnSuccessSave }
        }
    }

    private suspend fun createTags(): List<Long> {
        val uiState = container.uiState.value
        return uiState.tagLabelItems.map {
            categoryRepository.addTag(
                largeCategory = uiState.largeCategory.name,
                middleLabel = uiState.label.text,
                tagLabel = it.label
            )
        }
    }
}
