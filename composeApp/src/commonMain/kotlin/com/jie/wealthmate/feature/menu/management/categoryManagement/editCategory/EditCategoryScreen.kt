package com.jie.wealthmate.feature.menu.management.categoryManagement.editCategory

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import com.jie.wealthmate.base.BaseScreen
import com.jie.wealthmate.component.ButtonSize
import com.jie.wealthmate.component.CategoryIconEnum
import com.jie.wealthmate.component.SpacerSize
import com.jie.wealthmate.component.WMButton
import com.jie.wealthmate.component.WMCheckBox
import com.jie.wealthmate.component.WMRemoveDialog
import com.jie.wealthmate.component.WMSaveBackDialog
import com.jie.wealthmate.component.WMSpacer
import com.jie.wealthmate.component.textField.WMTextField
import com.jie.wealthmate.component.topbar.TopBarItem
import com.jie.wealthmate.component.topbar.WMTopBar
import com.jie.wealthmate.feature.menu.management.categoryManagement.component.LargeCategoryEnum
import com.jie.wealthmate.feature.menu.management.categoryManagement.editCategory.component.CategoryIcon
import com.jie.wealthmate.feature.menu.management.categoryManagement.editCategory.component.CategoryIconModalBottomSheet
import com.jie.wealthmate.feature.menu.management.categoryManagement.editCategory.component.CategoryTag
import com.jie.wealthmate.feature.menu.management.categoryManagement.editCategory.component.CategoryTagLabelModalBottomSheet
import com.jie.wealthmate.theme.Padding
import com.jie.wealthmate.theme.WMTheme
import com.jie.wealthmate.utils.default
import com.jie.wealthmate.vo.CategoryTagVo
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import wealthmate.composeapp.generated.resources.Res
import wealthmate.composeapp.generated.resources.ic_close_circle2
import wealthmate.composeapp.generated.resources.ic_delete_outline
import wealthmate.composeapp.generated.resources.ic_expand_circle_right

@Composable
fun EditCategoryScreen(
    navController: NavController,
    largeCategory: LargeCategoryEnum,
    categoryId: String?,
    viewModel: EditCategoryViewModel = koinViewModel {
        parametersOf(largeCategory, categoryId)
    },
) {
    var isShowSaveBackDialog by remember { mutableStateOf(false) }
    var isShowRemoveDialog by remember { mutableStateOf(false) }

    val onBack: () -> Unit = {
        if (viewModel.container.uiState.value.isDataChanged) {
            isShowSaveBackDialog = true
        } else {
            navController.popBackStack()
        }
    }

    BaseScreen(
        viewModel = viewModel,
        onBack = onBack,
        onSideEffect = { sideEffect ->
            when (sideEffect) {
                is EditCategoryUiSideEffect.OnSuccess -> {
                    navController.popBackStack()
                }
            }
        }
    ) { uiState ->
        EditCategoryContent(
            uiState = uiState,
            onBack = onBack,
            onRemove = { isShowRemoveDialog = true },
            onUpdateCategoryLabel = viewModel::updateCategoryLabel,
            onUpdateCategoryIcon = viewModel::updateCategoryIcon,
            onUpdateCategoryTagLabel = viewModel::updateCategoryTagLabel,
            onAddCategoryTagLabel = viewModel::addCategoryTagLabel,
            onRemoveCategoryTagLabel = viewModel::removeCategoryTagLabel,
            onModifyCategoryTagLabel = viewModel::modifyCategoryTagLabel,
            onUpdateIsFixed = viewModel::updateIsFixed,
            onSaveCategory = viewModel::saveCategory,
        )

        if (isShowSaveBackDialog) {
            WMSaveBackDialog(
                onConfirm = {
                    isShowSaveBackDialog = false
                    navController.popBackStack()
                },
                onDismiss = { isShowSaveBackDialog = false }
            )
        }

        if (isShowRemoveDialog) {
            WMRemoveDialog(
                onConfirm = {
                    isShowRemoveDialog = false
                    viewModel.removeCategory()
                },
                onDismiss = { isShowRemoveDialog = false }
            )
        }
    }
}

@Composable
fun EditCategoryContent(
    uiState: EditCategoryUiState,
    onBack: () -> Unit,
    onRemove: () -> Unit,
    onUpdateCategoryLabel: (TextFieldValue) -> Unit,
    onUpdateCategoryIcon: (CategoryIconEnum) -> Unit,
    onUpdateCategoryTagLabel: (TextFieldValue) -> Unit,
    onAddCategoryTagLabel: (TextFieldValue?) -> Unit,
    onRemoveCategoryTagLabel: (CategoryTagVo) -> Unit,
    onModifyCategoryTagLabel: (CategoryTagVo) -> Unit,
    onUpdateIsFixed: (Boolean) -> Unit,
    onSaveCategory: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var isShowCategoryIconModalBottomSheet by remember { mutableStateOf(false) }
    var isShowCategoryTagLabelModalBottomSheet by remember { mutableStateOf(false) }

    var modifyTagLabel by remember { mutableStateOf<CategoryTagVo?>(null) }
    var modifyTagTextFieldValue by remember { mutableStateOf(TextFieldValue("")) }

    val isEditMode = uiState.categoryId.isNullOrBlank().not()

    Column(
        modifier = modifier
            .navigationBarsPadding()
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        WMTopBar(
            title = TopBarItem.Title("${uiState.largeCategory.label} 카테고리 ${if (isEditMode) "수정" else "추가"}"),
            readingItem = TopBarItem.ReadingItem(action = onBack),
            trailingItem = if (isEditMode) {
                listOf(
                    TopBarItem.TrailingItem(
                        iconRes = Res.drawable.ic_delete_outline,
                        action = onRemove
                    )
                )
            } else {
                null
            }
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = Padding.BackgroundHorizontal)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CategoryIcon(
                modifier = Modifier.padding(top = Padding.SpacerL),
                largeCategory = uiState.largeCategory,
                selectedCategoryIcon = uiState.categoryIcon,
                onClickChange = { isShowCategoryIconModalBottomSheet = true }
            )

            WMTextField(
                value = uiState.label,
                onValueChange = onUpdateCategoryLabel,
                modifier = Modifier.padding(top = Padding.SpacerL),
                maxLength = 15,
                label = "카테고리 이름",
                placeholder = uiState.largeCategory.tempMiddleCategoryLabel,
                isCount = true,
                isRequire = true,
            )

            CategoryTag(
                modifier = Modifier.padding(top = Padding.SpacerXXS),
                largeCategory = uiState.largeCategory,
                tagLabel = uiState.tagLabel,
                trailingIcon = if (isEditMode) Res.drawable.ic_expand_circle_right else Res.drawable.ic_close_circle2,
                tagLabelItems = uiState.tagLabelItems,
                onValueChange = onUpdateCategoryTagLabel,
                onChipAdd = onAddCategoryTagLabel,
                onChipClick = {
                    if (isEditMode) {
                        modifyTagLabel = it
                        modifyTagTextFieldValue =
                            TextFieldValue(it.label, TextRange(it.label.length))
                        isShowCategoryTagLabelModalBottomSheet = true
                    } else {
                        onRemoveCategoryTagLabel(it)
                    }
                },
            )

            WMSpacer()
        }

        WMCheckBox(
            label = "고정 카테고리",
            checked = uiState.isFixed,
            modifier = Modifier
                .padding(start = Padding.BackgroundHorizontal)
                .padding(vertical = Padding.SpacerS)
                .align(Alignment.Start),
            onCheckedChange = onUpdateIsFixed,
        )

        WMButton(
            text = "저장",
            buttonSize = ButtonSize.LARGE,
            modifier = Modifier
                .padding(horizontal = Padding.BackgroundHorizontal)
                .fillMaxWidth(),
            enabled = uiState.label.text.isNotBlank(),
            onClick = onSaveCategory
        )

        WMSpacer(size = SpacerSize.LARGE)
    }

    if (isShowCategoryIconModalBottomSheet) {
        CategoryIconModalBottomSheet(
            selectedCategoryIcon = uiState.categoryIcon,
            onIconChange = onUpdateCategoryIcon,
            onDismissRequest = { isShowCategoryIconModalBottomSheet = false }
        )
    }

    if (isShowCategoryTagLabelModalBottomSheet) {
        CategoryTagLabelModalBottomSheet(
            isKeyboardOpen = false,
            tagLabel = modifyTagTextFieldValue,
            selectedTagLabel = modifyTagLabel?.label.default(),
            onTagLabelChange = { modifyTagTextFieldValue = it },
            onRemoveClick = {
                modifyTagLabel?.let(onRemoveCategoryTagLabel)
                isShowCategoryTagLabelModalBottomSheet = false
            },
            onModifyClick = {
                modifyTagLabel?.let {
                    onModifyCategoryTagLabel(it.copy(label = modifyTagTextFieldValue.text))
                }
                isShowCategoryTagLabelModalBottomSheet = false
            },
            onDismissRequest = { isShowCategoryTagLabelModalBottomSheet = false }
        )
    }
}

@Preview
@Composable
private fun EditCategoryContentPreview() {
    WMTheme {
        EditCategoryContent(
            uiState = EditCategoryUiState(
                largeCategory = LargeCategoryEnum.EXPENSES,
                label = TextFieldValue("식비"),
                tagLabelItems = listOf(
                    CategoryTagVo(id = "1", label = "외식"),
                    CategoryTagVo(id = "2", label = "카페")
                )
            ),
            onBack = {},
            onRemove = {},
            onUpdateCategoryLabel = {},
            onUpdateCategoryIcon = {},
            onUpdateCategoryTagLabel = {},
            onAddCategoryTagLabel = {},
            onRemoveCategoryTagLabel = {},
            onModifyCategoryTagLabel = {},
            onUpdateIsFixed = {},
            onSaveCategory = {}
        )
    }
}
