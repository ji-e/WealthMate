@file:OptIn(InternalVoyagerApi::class)

package com.jie.wealthmate.feature.menu.categorySetting.modifyCategory

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.annotation.InternalVoyagerApi
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.internal.BackHandler
import com.jie.wealthmate.base.BaseScreen
import com.jie.wealthmate.base.collectSideEffect
import com.jie.wealthmate.component.ButtonSize
import com.jie.wealthmate.component.ButtonStyle
import com.jie.wealthmate.component.CategoryIconEnum
import com.jie.wealthmate.component.WMButton
import com.jie.wealthmate.component.WMCheckBox
import com.jie.wealthmate.component.WMModalBottomSheet
import com.jie.wealthmate.component.WMTextField
import com.jie.wealthmate.component.topbar.TopBarItem
import com.jie.wealthmate.feature.menu.categorySetting.addCategory.AddCategoryUiSideEffect
import com.jie.wealthmate.feature.menu.categorySetting.addCategory.component.CategoryIcon
import com.jie.wealthmate.feature.menu.categorySetting.addCategory.component.CategoryIconGrid
import com.jie.wealthmate.feature.menu.categorySetting.addCategory.component.CategoryTag
import com.jie.wealthmate.feature.menu.categorySetting.component.LargeCategoryEnum
import com.jie.wealthmate.theme.ColorRed
import com.jie.wealthmate.theme.WMTheme
import com.jie.wealthmate.utils.default
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.koinInject
import wealthmate.composeapp.generated.resources.Res
import wealthmate.composeapp.generated.resources.ic_keyboard_arrow_right

class ModifyCategoryScreen(
    val largeCategory: LargeCategoryEnum,
    val categoryId: Long,
) : BaseScreen() {

    @Composable
    override fun Content() {
        super.Content()

        val navigator = LocalNavigator.currentOrThrow
        val screenModel: ModifyCategoryScreenModel = koinInject()
        val uiState = screenModel.container.uiState.collectAsState().value

        fun onBack() {
            showSaveBackDialog(uiState.isChangedData) {
                navigator.pop()
            }
        }

        BackHandler(true) { onBack() }

        screenModel.collectSideEffect { sideEffect ->
            when (sideEffect) {
                is AddCategoryUiSideEffect.OnSuccessSave -> {
                    navigator.pop()
                }
            }
        }

        LaunchedEffect(navigator.lastItem, uiState.label.text) {
            if (navigator.lastItem is ModifyCategoryScreen) {
                screenModel.updateTopBar(
                    title = TopBarItem.Title("${largeCategory.label} 카테고리 수정"),
                    readingItem = TopBarItem.ReadingItem().copy(
                        action = { onBack() }
                    ),
                )

                screenModel.updateInit(
                    largeCategoryEnum = largeCategory,
                    categoryId = categoryId
                )
            }
        }

        Column(modifier = Modifier.fillMaxSize()) {
            var isShowCategoryIconModalBottomSheet by remember { mutableStateOf(false) }
            var isShowCategoryTagLabelModalBottomSheet by remember { mutableStateOf(false) }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // 카테고리 아이콘
                CategoryIcon(
                    modifier = Modifier.padding(top = 24.dp),
                    largeCategory = largeCategory,
                    selectedCategoryIcon = uiState.categoryIcon,
                    onClickChange = { isShowCategoryIconModalBottomSheet = true }
                )

                // 카테고리 라벨
                WMTextField(
                    value = uiState.label,
                    onValueChange = screenModel::updateCategoryLabel,
                    modifier = Modifier
                        .padding(top = 20.dp)
                        .padding(horizontal = 4.dp),
                    maxLength = 15,
                    label = "카테고리 이름",
                    placeholder = largeCategory.tempMiddleCategoryLabel,
                    supportingText = "15자 이내로 입력해 주세요.",
                    isCount = true,
                    isRequire = true,
                )

                // 카테고리 태그
                CategoryTag(
                    modifier = Modifier.padding(top = 20.dp),
                    largeCategory = largeCategory,
                    tagLabel = uiState.tagLabel,
                    trailingIcon = Res.drawable.ic_keyboard_arrow_right,
                    tagLabelItems = uiState.tagLabelItems,
                    onValueChange = screenModel::updateCategoryTagLabel,
                    onChipAdd = screenModel::addCategoryTagLabel,
                    onChipClick = {
                        screenModel.selectedCategoryTagLabel(it)
                        isShowCategoryTagLabelModalBottomSheet = true
                    }
                )
                Spacer(modifier = Modifier.weight(1f))
            }

            // 고정 카테고리
            WMCheckBox(
                label = "고정 카테고리",
                checked = uiState.isFixed,
                modifier = Modifier
                    .padding(start = 6.dp)
                    .padding(vertical = 8.dp)
                    .align(Alignment.Start),
                onCheckedChange = screenModel::updateIsFixed,
            )

            // 저장 버튼
            WMButton(
                text = "저장",
                buttonSize = ButtonSize.LARGE,
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 20.dp)
                    .fillMaxWidth(),
                enabled = uiState.isChangedData,
                onClick = { }
            )

            if (isShowCategoryTagLabelModalBottomSheet) {
                ShowCategoryTagLabelModalBottomSheet(
                    tagLabel = uiState.modifyTagLabel?.label.default(),
                    onTagLabelChange = screenModel::updateModifyCategoryTagLabel,
                    onRemoveClick = screenModel::removeCategoryTagLabel,
                    onModifyClick = screenModel::modifyCategoryTagLabel,
                    onDismissRequest = { isShowCategoryTagLabelModalBottomSheet = false }
                )
            }

            if (isShowCategoryIconModalBottomSheet) {
                ShowCategoryIconModalBottomSheet(
                    selectedCategoryIcon = uiState.categoryIcon,
                    onIconChange = screenModel::updateCategoryIcon,
                    onDismissRequest = { isShowCategoryIconModalBottomSheet = false }
                )
            }
        }
    }

    /**
     * 카테고리 상세 태그 수정 ModalBottomSheet
     */
    @Composable
    private fun ShowCategoryTagLabelModalBottomSheet(
        tagLabel: String,
        onTagLabelChange: (String) -> Unit,
        onRemoveClick: () -> Unit = {},
        onModifyClick: () -> Unit = {},
        onDismissRequest: () -> Unit = {},
    ) {
        WMModalBottomSheet(
            title = "상세 태그 수정",
            onDismissRequest = onDismissRequest,
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 20.dp)
            ) {
                WMTextField(
                    value = tagLabel,
                    onValueChange = onTagLabelChange,
                    maxLength = 15,
                    label = "상세 태그 이름",
                    placeholder = largeCategory.tempTagLabel,
                    supportingText = "15자 이내로 입력해 주세요.",
                    isCount = true,
                )

                Row(
                    modifier = Modifier
                        .padding(top = 20.dp)
                        .padding(horizontal = 4.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    WMButton(
                        text = "삭제",
                        buttonStyle = ButtonStyle.TONAL,
                        buttonSize = ButtonSize.LARGE,
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = ColorRed.Red_50,
                            contentColor = ColorRed.Red_300
                        ),
                        modifier = Modifier.weight(1f),
                        onClick = {
                            onRemoveClick()
                            onDismissRequest()
                        },
                    )

                    WMButton(
                        text = "수정",
                        buttonStyle = ButtonStyle.FILLED,
                        buttonSize = ButtonSize.LARGE,
                        modifier = Modifier.weight(4f),
                        onClick = {
                            onModifyClick()
                            onDismissRequest()
                        }
                    )
                }
            }
        }
    }

    /**
     * 아이콘 변경 ModalBottomSheet
     */

    @Composable
    private fun ShowCategoryIconModalBottomSheet(
        selectedCategoryIcon: CategoryIconEnum,
        onIconChange: (CategoryIconEnum) -> Unit = {},
        onDismissRequest: () -> Unit = {},
    ) {
        WMModalBottomSheet(
            onDismissRequest = { onDismissRequest() },
        ) {
            CategoryIconGrid(
                selectedCategoryIcon = selectedCategoryIcon,
                onIconChange = onIconChange,
            )
        }
    }

    @Composable
    @Preview(showBackground = true)
    private fun ModifyCategoryScreenPreview() {
        WMTheme {
            ModifyCategoryScreen(
                largeCategory = LargeCategoryEnum.INCOME,
                categoryId = 0
            )
        }
    }
}
