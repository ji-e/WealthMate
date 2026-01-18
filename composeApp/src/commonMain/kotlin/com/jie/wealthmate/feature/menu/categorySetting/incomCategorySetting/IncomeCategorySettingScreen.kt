@file:OptIn(InternalVoyagerApi::class)

package com.jie.wealthmate.feature.menu.categorySetting.incomCategorySetting

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.annotation.InternalVoyagerApi
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.internal.BackHandler
import com.jie.wealthmate.base.BaseScreen
import com.jie.wealthmate.component.ButtonSize
import com.jie.wealthmate.component.WMFloatingButton
import com.jie.wealthmate.component.reorderable.ReorderableItem
import com.jie.wealthmate.component.reorderable.detectReorderAfterLongPress
import com.jie.wealthmate.component.reorderable.rememberReorderableLazyListState
import com.jie.wealthmate.component.reorderable.reorderable
import com.jie.wealthmate.component.topbar.TopBarItem
import com.jie.wealthmate.feature.menu.categorySetting.addCategory.AddCategoryScreen
import com.jie.wealthmate.feature.menu.categorySetting.component.CategoryItem
import com.jie.wealthmate.feature.menu.categorySetting.component.LargeCategoryEnum
import com.jie.wealthmate.feature.menu.component.MenuEnum
import com.jie.wealthmate.theme.ColorPrimary
import com.jie.wealthmate.theme.WMTheme
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.koinInject
import wealthmate.composeapp.generated.resources.Res
import wealthmate.composeapp.generated.resources.ic_add

class IncomeCategorySettingScreen(
    val menuEnum: MenuEnum,
) : BaseScreen() {

    @Composable
    override fun Content() {
        super.Content()

        val navigator = LocalNavigator.currentOrThrow
        val screenModel: IncomeCategorySettingScreenModel = koinInject()
        val uiState = screenModel.container.uiState.collectAsState().value

        var isDragging by remember { mutableStateOf(false) }
        val hapticFeedback = LocalHapticFeedback.current
        val listState = rememberReorderableLazyListState(
            onMove = { from, to -> screenModel.handleReorderImageItems(from.index, to.index) }
        )

        fun onBack() {
            if (isDragging) {
                showSaveBackDialog(isDragging) {
                    isDragging = false
                }
            } else {
                navigator.pop()
            }
        }


        BackHandler(true) {
            onBack()
        }

        LaunchedEffect(navigator.lastItem, uiState.incomeCategoryItems, isDragging) {
            if (navigator.lastItem is IncomeCategorySettingScreen && isDragging.not()) {
                screenModel.mainScreenModel.updateTopBar(
                    title = TopBarItem.Title(menuEnum.title),
                    readingItem = TopBarItem.ReadingItem().copy(
                        action = { navigator.pop() }
                    ),
                    trailingItem = listOf(
                        TopBarItem.TrailingItem(
                            iconRes = Res.drawable.ic_add,
                            action = {
                                navigator.push(
                                    AddCategoryScreen(
                                        largeCategory = LargeCategoryEnum.INCOME,
                                        categoryItems = uiState.incomeCategoryItems
                                    )
                                )
                            }
                        )
                    )
                )
            }
        }

        if (isDragging) {
            screenModel.mainScreenModel.updateTopBar(
                title = TopBarItem.Title(menuEnum.label + " 순서 변경"),
                readingItem = TopBarItem.ReadingItem().copy(
                    action = { onBack() }
                )
            )
        }

        Column {
            LazyColumn(
                state = listState.listState,
                modifier = Modifier
                    .weight(1f)
                    .reorderable(listState),
            ) {
                items(
                    count = uiState.incomeCategoryItems.size,
                    key = { index -> uiState.incomeCategoryItems[index].id }
                ) { index ->
                    val category = uiState.incomeCategoryItems[index]
                    ReorderableItem(
                        state = listState,
                        key = category.id,
                    ) {
                        if (isDragging.not()) {
                            isDragging = it
                        }

                        if (it) {
                            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                        }

                        CategoryItem(
                            data = category,
                            modifier = Modifier.then(
                                if (it) {
                                    Modifier
                                        .padding(horizontal = 12.dp)
                                        .dropShadow(
                                            shape = RoundedCornerShape(4.dp),
                                            shadow = Shadow(
                                                radius = 10.dp,
                                                spread = 10.dp,
                                                color = ColorPrimary.Primary_200,
                                                offset = DpOffset(x = 4.dp, 4.dp)
                                            )
                                        )
                                        .clip(RoundedCornerShape(4.dp))
                                        .clickable(
                                            onClick = { },
                                            indication = null,
                                            interactionSource = remember { MutableInteractionSource() }
                                        )

                                } else {
                                    Modifier.clickable { }
                                }
                            ),
                            onDragHandle = Modifier.detectReorderAfterLongPress(listState),
                        )
                    }
                }
            }

            // Drag and Drop 저장 버튼
            if (isDragging) {
                WMFloatingButton(
                    text = "저장",
                    buttonSize = ButtonSize.LARGE,
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .padding(bottom = 20.dp)
                        .fillMaxWidth(),
                    onClick = {
                        isDragging = false
                    }
                )
            }
        }
    }

    @Composable
    @Preview(showBackground = true)
    private fun IncomeCategorySettingScreenPreview() {
        WMTheme {
            IncomeCategorySettingScreen(MenuEnum.INCOME_CATEGORY)
        }
    }
}