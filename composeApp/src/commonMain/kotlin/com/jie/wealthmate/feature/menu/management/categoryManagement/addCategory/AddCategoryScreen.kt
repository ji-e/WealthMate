@file:OptIn(InternalVoyagerApi::class)

package com.jie.wealthmate.feature.menu.management.categoryManagement.addCategory

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import com.jie.wealthmate.component.WMButton
import com.jie.wealthmate.component.WMCheckBox
import com.jie.wealthmate.component.textField.WMTextField
import com.jie.wealthmate.component.topbar.TopBarItem
import com.jie.wealthmate.component.topbar.WMTopBar
import com.jie.wealthmate.feature.menu.management.categoryManagement.addCategory.component.CategoryIcon
import com.jie.wealthmate.feature.menu.management.categoryManagement.addCategory.component.CategoryIconModalBottomSheet
import com.jie.wealthmate.feature.menu.management.categoryManagement.addCategory.component.CategoryTag
import com.jie.wealthmate.feature.menu.management.categoryManagement.component.LargeCategoryEnum
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

        var isShowCategoryIconModalBottomSheet by remember { mutableStateOf(false) }

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
            WMTopBar(
                title = TopBarItem.Title("${largeCategory.label} 카테고리 추가"),
                readingItem = TopBarItem.ReadingItem().copy(action = { onBack() }),
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 28.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // 카테고리 아이콘
                CategoryIcon(
                    modifier = Modifier.padding(top = 32.dp),
                    largeCategory = largeCategory,
                    selectedCategoryIcon = uiState.categoryIcon,
                    onClickChange = { isShowCategoryIconModalBottomSheet = true }
                )

                // 카테고리 이름
                WMTextField(
                    value = uiState.label,
                    onValueChange = screenModel::updateCategoryLabel,
                    modifier = Modifier.padding(top = 32.dp),
                    maxLength = 15,
                    label = "카테고리 이름",
                    placeholder = largeCategory.tempMiddleCategoryLabel,
                    isCount = true,
                    isRequire = true,
                )

                // 카테고리 태그
                CategoryTag(
                    modifier = Modifier.padding(top = 4.dp),
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
                    .padding(start = 28.dp)
                    .padding(vertical = 16.dp)
                    .align(Alignment.Start),
                onCheckedChange = screenModel::updateIsFixed,
            )

            // 저장 버튼
            WMButton(
                text = "저장",
                buttonSize = ButtonSize.LARGE,
                modifier = Modifier
                    .padding(horizontal = 28.dp)
                    .padding(bottom = 20.dp)
                    .fillMaxWidth(),
                enabled = uiState.label.text.isNotBlank(),
                onClick = { screenModel.saveCategory() }
            )

            // 아이콘 변경 ModalBottomSheet
            if (isShowCategoryIconModalBottomSheet) {
                CategoryIconModalBottomSheet(
                    selectedCategoryIcon = uiState.categoryIcon,
                    onIconChange = screenModel::updateCategoryIcon,
                    onDismissRequest = { isShowCategoryIconModalBottomSheet = false }
                )
            }
        }
    }
}
