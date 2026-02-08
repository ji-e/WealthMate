package com.jie.wealthmate.feature.menu.management.categoryManagement.addCategory

import androidx.compose.ui.text.input.TextFieldValue
import com.jie.wealthmate.base.BaseScreenModel
import com.jie.wealthmate.component.CategoryIconEnum
import com.jie.wealthmate.feature.menu.management.categoryManagement.component.LargeCategoryEnum
import com.jie.wealthmate.repository.CategoryRepository
import com.jie.wealthmate.vo.CategoryTagVo
import com.jie.wealthmate.vo.CategoryVo
import com.jie.wealthmate.vo.CategoryVo.Companion.mapperToVo

class AddCategoryScreenModel(
    private val categoryRepository: CategoryRepository,
) : BaseScreenModel<AddCategoryUiState>() {

    private var categoryItems: List<CategoryVo> = emptyList()

    override val initialState: AddCategoryUiState
        get() = AddCategoryUiState()

    fun updateInit(largeCategoryEnum: LargeCategoryEnum) {
        reduceState { state ->
            state.copy(
                largeCategory = largeCategoryEnum,
            )
        }
        getCategories(largeCategoryEnum)
    }

    fun updateCategoryIcon(icon: CategoryIconEnum) {
        reduceState { state ->
            state.copy(
                isDataChanged = true,
                categoryIcon = icon
            )
        }
    }

    fun updateCategoryLabel(textFieldValue: TextFieldValue) {
        reduceState { state ->
            state.copy(
                isDataChanged = true,
                label = textFieldValue
            )
        }
    }

    fun updateCategoryTagLabel(textFieldValue: TextFieldValue) {
        val currentState = container.uiState.value
        // 1. 단순 텍스트가 같다면 업데이트 무시
        if (currentState.tagLabel.text == textFieldValue.text) return

        // 2. IME Ghost Update 방지:
        // 현재 상태는 비어있는데(방금 추가됨), 들어온 값이 이미 추가된 태그 목록에 있다면 무시
        if (currentState.tagLabel.text.isEmpty() && textFieldValue.text.isNotEmpty()) {
            val isAlreadyAdded = currentState.tagLabelItems.any { it.label == textFieldValue.text }
            if (isAlreadyAdded) return
        }

        reduceState { state ->
            state.copy(
                tagLabel = textFieldValue
            )
        }
    }

    fun addCategoryTagLabel(tagLabel: TextFieldValue?) {
        tagLabel ?: return reduceState {
            it.copy(
                tagLabel = TextFieldValue()
            )
        }

        tagLabel.text.ifBlank {
            showSnackbar("상세 태그 이름을 입력해 주세요.")
            return
        }

        val categoryTag = CategoryTagVo(label = tagLabel.text)
        val isExisted = container.uiState.value.tagLabelItems.any { it.label == categoryTag.label }

        if (isExisted) {
            showSnackbar("이미 존재하는 태그 입니다.")
            return
        }

        reduceState { state ->
            state.copy(
                isDataChanged = true,
                tagLabel = TextFieldValue(),
                tagLabelItems = state.tagLabelItems + categoryTag
            )
        }
    }

    fun removeCategoryTagLabel(tag: CategoryTagVo) {
        reduceState { state ->
            state.copy(
                isDataChanged = true,
                tagLabelItems = state.tagLabelItems - tag
            )
        }
    }

    fun updateIsFixed(isFixed: Boolean) {
        reduceState { state ->
            state.copy(
                isDataChanged = true,
                isFixed = isFixed
            )
        }
    }

    fun saveCategory() {
        val uiState = container.uiState.value

        if (categoryItems.any { it.middleLabel == uiState.label.text }) {
            showSnackbar("이미 존재하는 카테고리입니다.")
            return
        }

        launchSafe(
            block = {
                categoryRepository.insertCategory(
                    icon = uiState.categoryIcon.text,
                    largeCategory = uiState.largeCategory.name,
                    middleLabel = uiState.label.text,
                    sort = categoryItems.size.toLong(),
                    isFixed = uiState.isFixed,
                    tagLabels = uiState.tagLabelItems.map { it.label }
                )
            },
        ) {
            showSnackbar("카테고리가 저장되었습니다.")
            postSideEffect { AddCategoryUiSideEffect.OnSuccessSave }
        }
    }

    fun getCategories(largeCategoryEnum: LargeCategoryEnum) {
        categoryRepository.getCategoriesByLargeCategory(largeCategoryEnum.name)
            .apiFlow { response ->
                categoryItems = response.map { it.mapperToVo() }
            }
    }
}
