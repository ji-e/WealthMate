package com.jie.wealthmate.feature.menu.categoryManagement.modifyCategory

import androidx.compose.ui.text.input.TextFieldValue
import com.jie.wealthmate.base.BaseScreenModel
import com.jie.wealthmate.component.CategoryIconEnum
import com.jie.wealthmate.database.eneity.CategoryEntity
import com.jie.wealthmate.database.eneity.CategoryTagEntity
import com.jie.wealthmate.feature.menu.categoryManagement.component.CategoryItemData
import com.jie.wealthmate.feature.menu.categoryManagement.component.LargeCategoryEnum
import com.jie.wealthmate.repository.CategoryRepository
import com.jie.wealthmate.utils.default
import com.jie.wealthmate.vo.CategoryTagVo
import kotlinx.coroutines.delay

class ModifyCategoryScreenModel(
    private val categoryRepository: CategoryRepository,
) : BaseScreenModel<ModifyCategoryUiState>() {
    override val initialState: ModifyCategoryUiState
        get() = ModifyCategoryUiState()

    private var categoryItems: List<CategoryItemData> = emptyList()
    private var categoryId: String = ""

    fun updateInit(largeCategoryEnum: LargeCategoryEnum, categoryId: String) {
        this.categoryId = categoryId

        reduceState { state ->
            state.copy(
                largeCategory = largeCategoryEnum,
            )
        }
        getCategories(largeCategoryEnum)
        getCategoryDetail()
    }

    fun updateCategoryIcon(icon: CategoryIconEnum) {
        reduceState { state ->
            state.copy(
                categoryIcon = icon,
                isDataChanged = true
            )
        }
    }

    fun updateCategoryLabel(textFieldValue: TextFieldValue) {
        reduceState { state ->
            state.copy(
                label = textFieldValue,
                isDataChanged = true
            )
        }
    }

    fun updateCategoryTagLabel(textFieldValue: TextFieldValue) {
        reduceState { state ->
            state.copy(
                tagLabel = textFieldValue,
                isDataChanged = true
            )
        }
    }

    fun selectedCategoryTagLabel(categoryTagVo: CategoryTagVo) {
        reduceState { state ->
            state.copy(
                modifyTagLabel = categoryTagVo,
                selectedTagLabel = categoryTagVo.label,
                isDataChanged = true
            )
        }
    }

    fun updateModifyCategoryTagLabel(text: String) {
        reduceState { state ->
            state.copy(
                modifyTagLabel = state.modifyTagLabel?.copy(label = text),
                isDataChanged = true
            )
        }
    }

    fun modifyCategoryTagLabel() {
        reduceState { state ->
            val modifyTagLabelText = state.modifyTagLabel?.label.default()

            if (modifyTagLabelText.isBlank()) {
                showSnackbar("상세 태그 이름을 입력해 주세요.")
                return@reduceState state
            }

            val categoryTag = CategoryTagVo(label = modifyTagLabelText)
            val isExisted = state.tagLabelItems.any {
                it.label != state.selectedTagLabel && it.label == categoryTag.label
            }

            if (isExisted) {
                showSnackbar("이미 존재하는 태그 입니다.")
                state
            } else {
                postSideEffect {
                    delay(300)
                    showSnackbar("상세 태그 이름이 수정되었습니다.")

                    ModifyCategoryUiSideEffect.OnSuccessModifyTagLabel
                }

                state.copy(
                    modifyTagLabel = null,
                    tagLabelItems = state.tagLabelItems.toMutableList()
                        .apply {
                            val modifyTagLabel = state.modifyTagLabel
                            if (modifyTagLabel != null) {
                                val index = indexOf(find { it.id == modifyTagLabel.id })
                                set(index, modifyTagLabel)
                            }
                        },
                    isDataChanged = true
                )

            }
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
                    isDataChanged = true
                )
            }
        }
    }

    fun removeCategoryTagLabel() {
        reduceState { state ->
            state.copy(
                modifyTagLabel = null,
                tagLabelItems = state.tagLabelItems.toMutableList()
                    .apply { remove(state.modifyTagLabel) },
                isDataChanged = true
            )
        }
    }

    fun updateIsFixed(isFixed: Boolean) {
        reduceState { state ->
            state.copy(
                isFixed = isFixed,
                isDataChanged = true
            )
        }
    }

    fun getCategoryDetail() {
        launchSafe(
            block = {
                categoryRepository.getCategoryById(categoryId)
            }
        ) { response ->
            reduceState { state ->
                state.copy(
                    categoryIcon = CategoryIconEnum.creatorFromText(response?.icon),
                    label = TextFieldValue(response?.middleLabel.default()),
                    tagLabelItems = response?.tags.default()
                        .map { CategoryTagVo(it.id, it.tagLabel) },
                    isFixed = response?.isFixed.default(),
                    sort = response?.sort.default()
                )
            }
        }
    }

    fun removeCategory() {
        launchSafe(
            block = {
                categoryRepository.deleteCategory(categoryId)
            },
        ) {
            showSnackbar("카테고리가 삭제되었습니다.")
            postSideEffect { ModifyCategoryUiSideEffect.OnSuccess }
        }
    }

    fun saveCategory() {
        val uiState = container.uiState.value
        val isExisted = categoryItems.any { it.id != categoryId && it.label == uiState.label.text }

        if (isExisted) {
            showSnackbar("이미 존재하는 카테고리 입니다.")
            return
        }

        launchSafe(
            block = {
                categoryRepository.updateCategory(
                    CategoryEntity(
                        id = categoryId,
                        icon = uiState.categoryIcon.text,
                        largeCategory = uiState.largeCategory.name,
                        middleLabel = uiState.label.text,
                        sort = uiState.sort,
                        isFixed = uiState.isFixed,
                        tags = uiState.tagLabelItems.map {
                            CategoryTagEntity(
                                it.id.default(),
                                it.label
                            )
                        },
                    )
                )
            },
        ) {
            showSnackbar("카테고리가 수정되었습니다.")
            postSideEffect { ModifyCategoryUiSideEffect.OnSuccess }
        }
    }

    fun getCategories(largeCategoryEnum: LargeCategoryEnum) {
        categoryRepository.getCategoriesByLargeCategory(largeCategoryEnum.name)
            .apiFlow { response ->
                categoryItems = response.map {
                    CategoryItemData(
                        id = it.id,
                        icon = it.icon,
                        label = it.middleLabel,
                        sort = it.sort,
                        isFixed = it.isFixed,
                        largeCategory = LargeCategoryEnum.creator(it.largeCategory)
                    )
                }
            }
    }
}
