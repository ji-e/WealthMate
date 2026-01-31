@file:OptIn(InternalVoyagerApi::class)

package com.jie.wealthmate.feature.calendar.addHistory

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.annotation.InternalVoyagerApi
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.internal.BackHandler
import com.jie.wealthmate.base.BaseScreen
import com.jie.wealthmate.base.collectSideEffect
import com.jie.wealthmate.component.ButtonSize
import com.jie.wealthmate.component.WMFloatingButton
import com.jie.wealthmate.component.WMText
import com.jie.wealthmate.component.textField.WMTextField
import com.jie.wealthmate.component.textField.rememberIntegerVisualTransformation
import com.jie.wealthmate.component.textField.toIntegerTextFieldValue
import com.jie.wealthmate.component.topbar.TopBarItem
import com.jie.wealthmate.feature.calendar.addHistory.component.CategorySelectionRow
import com.jie.wealthmate.feature.calendar.addHistory.component.DateSelectModalBottomSheet
import com.jie.wealthmate.feature.calendar.addHistory.component.DateTextField
import com.jie.wealthmate.feature.calendar.addHistory.component.InstallmentModalBottomSheet
import com.jie.wealthmate.feature.calendar.addHistory.component.LargeCategorySelectBox
import com.jie.wealthmate.feature.calendar.addHistory.component.PaymentMethodModalBottomSheet
import com.jie.wealthmate.feature.calendar.addHistory.component.PaymentMethodTextField
import com.jie.wealthmate.feature.calendar.addHistory.component.RepeatCycleModalBottomSheet
import com.jie.wealthmate.feature.menu.categoryManagement.component.LargeCategoryEnum
import com.jie.wealthmate.theme.WMTheme
import org.koin.compose.koinInject

class AddHistoryScreen() : BaseScreen() {
    @Composable
    override fun Content() {
        super.Content()

        val navigator = LocalNavigator.currentOrThrow
        val screenModel: AddHistoryScreenModel = koinInject()
        val uiState by screenModel.container.uiState.collectAsState()

        var isShowDateSelectModalBottomSheet by remember { mutableStateOf(false) }
        var isShowRepeatCycleModalBottomSheet by remember { mutableStateOf(false) }
        var isShowInstallmentModalBottomSheet by remember { mutableStateOf(false) }
        var isShowPaymentMethodModalBottomSheet by remember { mutableStateOf(false) }

        val scrollState = rememberScrollState()
        val density = LocalDensity.current

        val isLargeCategoryVisible by remember {
            derivedStateOf {
                scrollState.value > with(density) { 56.dp.toPx() }
            }
        }

        fun onBack() {
            showSaveBackDialog(uiState.isDataChanged) {
                navigator.pop()
            }
        }

        BackHandler(true) { onBack() }

        screenModel.collectSideEffect { sideEffect ->
            when (sideEffect) {
                is AddHistoryUiSideEffect.OnSuccessSave -> {
                    navigator.pop()
                }
            }
        }

        // 스크롤 상태에 따라 TopBar 업데이트
        LaunchedEffect(isLargeCategoryVisible) {
            screenModel.updateTopBar(
                title = TopBarItem.Title("내역 추가"),
                readingItem = TopBarItem.ReadingItem().copy(
                    action = { onBack() }
                ),
                trailingCustomItem = if (isLargeCategoryVisible) {
                    TopBarItem.TrailingCustomItem {
                        val selectedLargeCategoryEnum = uiState.selectedLargeCategory
                        WMText(
                            text = selectedLargeCategoryEnum.label,
                            style = Typography().labelMedium.copy(fontWeight = FontWeight.SemiBold),
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .clip(CircleShape)
                                .background(selectedLargeCategoryEnum.backgroundColor)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                } else null
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 20.dp)
                    .verticalScroll(scrollState)
            ) {
                // 수입, 지출, 저출 카테고리 선택
                LargeCategorySelectBox(
                    modifier = Modifier.padding(top = 8.dp),
                    selectedLargeCategory = uiState.selectedLargeCategory,
                    onLargeCategoryClick = screenModel::updateLargeCategory
                )

                // 날짜 선택
                DateTextField(
                    modifier = Modifier.padding(top = 24.dp),
                    selectedDate = uiState.date,
                    amount = uiState.amount,
                    repeatCycle = uiState.repeatCycle,
                    installmentCount = uiState.installmentCount,
                    onDateClick = { isShowDateSelectModalBottomSheet = true },
                    onRepeatClick = {
                        if (uiState.installmentCount != null) {
                            showConfirmDialog(
                                isShow = true,
                                content = "할부가 선택되어있습니다.\n할부 선택을 취소하시겠습니까?",
                                callback = {
                                    screenModel.updateInstallmentCount(null)
                                    isShowRepeatCycleModalBottomSheet = true
                                }
                            )
                        } else {
                            isShowRepeatCycleModalBottomSheet = true
                        }
                    },
                    onInstallmentClick = {
                        if (uiState.repeatCycle != null) {
                            showConfirmDialog(
                                isShow = true,
                                content = "반복 주기가 선택되어있습니다.\n반복 주기 선택을 취소하시겠습니까?",
                                callback = {
                                    screenModel.updateRepeatCycle(null)
                                    isShowInstallmentModalBottomSheet = true
                                }
                            )
                        } else {
                            isShowInstallmentModalBottomSheet = true
                        }
                    },
                    onResetClick = {
                        screenModel.updateRepeatCycle(null)
                        screenModel.updateInstallmentCount(null)
                    }
                )

                // 금액 입력
                WMTextField(
                    modifier = Modifier.padding(top = 20.dp),
                    value = uiState.amount,
                    onValueChange = {
                        screenModel.updateAmount(it.toIntegerTextFieldValue())
                    },
                    label = "금액",
                    isRequire = true,
                    maxLength = 10,
                    placeholder = "금액을 입력해 주세요.",
                    suffix = {
                        WMText(
                            text = "원",
                            style = Typography().bodyLarge.copy(fontWeight = FontWeight.SemiBold)
                        )
                    },
                    visualTransformation = rememberIntegerVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                )

                // 카테고리 선택
//                CategoryTextField(
//                    selectedCategory = uiState.categoryItems.firstOrNull(), // todo temp
//                    selectedCategoryTag = uiState.categoryItems.firstOrNull()?.tags?.firstOrNull(), // todo temp
//                    onCategoryClick = { isShowCategorySelectModalBottomSheet = true }
//                )

//                CategorySelectionAllTagColumn(
//                    categoryItems = uiState.categoryItems,
//                    selectedCategory = uiState.categoryItems.firstOrNull(), // todo temp
//                    selectedCategoryTag = uiState.categoryItems.firstOrNull()?.tags?.firstOrNull(), // todo temp
//                )

                if (uiState.selectedLargeCategory != LargeCategoryEnum.TRANSFER) {
                    CategorySelectionRow(
                        modifier = Modifier.padding(bottom = 24.dp),
                        categoryItems = uiState.categoryItems,
                        selectedLargeCategory = uiState.selectedLargeCategory,
                        selectedCategory = uiState.category,
                        selectedCategoryTag = uiState.categoryTag,
                        onCategoryClick = screenModel::updateCategory,
                        onCategoryTagClick = screenModel::updateCategoryTag
                    )
                }

                // 결제수단/자산 선택
                PaymentMethodTextField(
                    selectedLargeCategory = uiState.selectedLargeCategory,
                    selectedPaymentMethod = uiState.paymentMethod,
                    onPaymentMethodClick = { isShowPaymentMethodModalBottomSheet = true }
                )

                // 내용 입력
                WMTextField(
                    value = uiState.content,
                    onValueChange = screenModel::updateContent,
                    label = "내용",
                    maxLength = 20,
                    placeholder = "내용을 입력해 주세요.",
                )

                Spacer(modifier = Modifier.height(32.dp))

            }

            // 저장 버튼
            WMFloatingButton(
                text = "저장",
                buttonSize = ButtonSize.LARGE,
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 20.dp)
                    .fillMaxWidth(),
                enabled = uiState.isSaveButtonEnable,
                onClick = screenModel::saveHistory
            )
        }

        if (isShowDateSelectModalBottomSheet) {
            DateSelectModalBottomSheet(
                selectedDate = uiState.date,
                onSelectClick = screenModel::updateDate,
                onDismissRequest = { isShowDateSelectModalBottomSheet = false }
            )
        }

        if (isShowRepeatCycleModalBottomSheet) {
            RepeatCycleModalBottomSheet(
                selectedRepeatCycle = uiState.repeatCycle,
                onConfirmClick = screenModel::updateRepeatCycle,
                onDismissRequest = { isShowRepeatCycleModalBottomSheet = false }
            )
        }

        if (isShowInstallmentModalBottomSheet) {
            InstallmentModalBottomSheet(
                installmentCount = uiState.installmentCount,
                onConfirmClick = screenModel::updateInstallmentCount,
                onDismissRequest = { isShowInstallmentModalBottomSheet = false }
            )
        }

        if (isShowPaymentMethodModalBottomSheet) {
            PaymentMethodModalBottomSheet(
                selectedPaymentMethod = uiState.paymentMethod,
                paymentMethodItems = uiState.paymentMethodItems,
                onConfirmClick = screenModel::updatePaymentMethod,
                onDismissRequest = { isShowPaymentMethodModalBottomSheet = false }
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun AddHistoryScreenPreview() {
    WMTheme {
        AddHistoryScreen().Content()
    }
}
