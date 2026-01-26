@file:OptIn(InternalVoyagerApi::class)

package com.jie.wealthmate.feature.menu.categoryManagement.addCategory

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.annotation.InternalVoyagerApi
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.internal.BackHandler
import com.jie.wealthmate.base.BaseScreen
import com.jie.wealthmate.base.collectSideEffect
import com.jie.wealthmate.component.ButtonSize
import com.jie.wealthmate.component.WMButton
import com.jie.wealthmate.component.WMCheckBox
import com.jie.wealthmate.component.WMModalBottomSheet
import com.jie.wealthmate.component.textField.WMTextField
import com.jie.wealthmate.component.topbar.TopBarItem
import com.jie.wealthmate.feature.menu.categoryManagement.addCategory.component.CategoryIcon
import com.jie.wealthmate.feature.menu.categoryManagement.addCategory.component.CategoryIconGrid
import com.jie.wealthmate.feature.menu.categoryManagement.addCategory.component.CategoryTag
import com.jie.wealthmate.feature.menu.categoryManagement.component.LargeCategoryEnum
import com.jie.wealthmate.theme.WMTheme
import org.koin.compose.koinInject

class AddCategoryScreen(
    val largeCategory: LargeCategoryEnum,
) : BaseScreen() {

    @Composable
    override fun Content() {
        super.Content()

        val navigator = LocalNavigator.currentOrThrow
        val screenModel: AddCategoryScreenModel = koinInject()
        val uiState by screenModel.container.uiState.collectAsState()

        val onBack: () -> Unit = remember(uiState.isDataChanged) {
            {
                showSaveBackDialog(
                    isShow = uiState.isDataChanged,
                    callback = { navigator.pop() }
                )
            }
        }

        BackHandler(
            enabled = true,
            onBack = onBack
        )

        if (navigator.lastItem is AddCategoryScreen) {
            SideEffect {
                screenModel.updateTopBar(
                    title = TopBarItem.Title("${largeCategory.label} 카테고리 추가"),
                    readingItem = TopBarItem.ReadingItem().copy(
                        action = { onBack() }
                    ),
                )
            }
        }

        LaunchedEffect(Unit) {
            screenModel.updateInit(
                largeCategoryEnum = largeCategory
            )
        }

        screenModel.collectSideEffect { sideEffect ->
            when (sideEffect) {
                is AddCategoryUiSideEffect.OnSuccessSave -> {
                    navigator.pop()
                }
            }
        }

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            var isShowCategoryIconModalBottomSheet by remember { mutableStateOf(false) }

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
                        .padding(horizontal = 20.dp),
                    maxLength = 15,
                    label = "카테고리 이름",
                    placeholder = largeCategory.tempMiddleCategoryLabel,
                    supportingText = "15자 이내로 입력해 주세요.",
                    isCount = true,
                    isRequire = true,
                )

                // 카테고리 태그
                CategoryTag(
                    modifier = Modifier
                        .padding(top = 20.dp)
                        .padding(horizontal = 20.dp),
                    largeCategory = largeCategory,
                    tagLabel = uiState.tagLabel,
                    tagLabelItems = uiState.tagLabelItems,
                    onValueChange = screenModel::updateCategoryTagLabel,
                    onChipAdd = screenModel::addCategoryTagLabel,
                    onChipClick = screenModel::removeCategoryTagLabel,
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
                enabled = uiState.label.text.isNotBlank(),
                onClick = { screenModel.saveCategory() }
            )

            // 아이콘 변경 ModalBottomSheet
            if (isShowCategoryIconModalBottomSheet) {
                WMModalBottomSheet(
                    onDismissRequest = { isShowCategoryIconModalBottomSheet = false },
                ) {
                    CategoryIconGrid(
                        selectedCategoryIcon = uiState.categoryIcon,
                        onIconChange = screenModel::updateCategoryIcon
                    )
                }
            }
        }
    }

    @Composable
    @Preview(showBackground = true)
    private fun AddCategoryScreenPreview() {
        WMTheme {
            AddCategoryScreen(largeCategory = LargeCategoryEnum.INCOME)
        }
    }
}
