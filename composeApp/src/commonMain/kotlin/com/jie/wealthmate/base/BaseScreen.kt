//package com.jie.wealthmate.base
//
//import androidx.compose.foundation.background
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.heightIn
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.rememberScrollState
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.foundation.verticalScroll
//import androidx.compose.material3.Typography
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.collectAsState
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.platform.LocalFocusManager
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.text.style.TextAlign
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.window.Dialog
//import com.jie.wealthmate.MainUiManager
//import com.jie.wealthmate.component.WMText
//import com.jie.wealthmate.theme.ColorGray
//import com.jie.wealthmate.theme.ColorPrimary
//import com.jie.wealthmate.theme.ColorRed
//
//@Composable
//fun <S : UiState> BaseScreen(
//    viewModel: BaseViewModel<S>,
//    content: @Composable (S) -> Unit
//) {
//    val uiState by viewModel.container.uiState.collectAsState()
//    val focusManager = LocalFocusManager.current
//
//    var isShowConfirmDialog by remember { mutableStateOf(false) }
//    var confirmContent by remember { mutableStateOf("") }
//    var confirmDialogButtonLabel by remember { mutableStateOf<Pair<String?, String>>("취소" to "확인") }
//    var confirmCallback by remember { mutableStateOf<() -> Unit>({}) }
//
//    var isShowSaveBackDialog by remember { mutableStateOf(false) }
//    var isShowRemoveDialog by remember { mutableStateOf(false) }
//
//    LaunchedEffect(Unit) {
//        MainUiManager.sideEffect.collect { sideEffect ->
//            when (sideEffect) {
//                is BaseUiSideEffect.HideKeyboard -> {
//                    focusManager.clearFocus(true)
//                }
//                // Loading, Snackbar 등은 MainScreen에서 전역 처리
//                else -> {}
//            }
//        }
//    }
//
//    Box(modifier = Modifier.fillMaxSize()) {
//        content(uiState)
//
//        if (isShowConfirmDialog) {
//            BaseDialog(
//                contentText = confirmContent,
//                cancelLabel = confirmDialogButtonLabel.first,
//                confirmLabel = confirmDialogButtonLabel.second,
//                onConfirm = {
//                    isShowConfirmDialog = false
//                    confirmCallback()
//                },
//                onDismissRequest = { isShowConfirmDialog = false }
//            )
//        }
//
//        if (isShowSaveBackDialog) {
//            BaseDialog(
//                contentText = "저장되지 않았습니다.\n이전 화면으로 돌아갈까요?",
//                onConfirm = {
//                    isShowSaveBackDialog = false
//                    confirmCallback()
//                },
//                onDismissRequest = { isShowSaveBackDialog = false }
//            )
//        }
//
//        if (isShowRemoveDialog) {
//            BaseDialog(
//                contentText = "정말 삭제하시겠습니까?\n삭제된 정보는 복구할 수 없습니다.",
//                confirmLabel = "삭제",
//                confirmColor = ColorRed.Red_300,
//                onConfirm = {
//                    isShowRemoveDialog = false
//                    confirmCallback()
//                },
//                onDismissRequest = { isShowRemoveDialog = false }
//            )
//        }
//    }
//}
//
//@Composable
//private fun BaseDialog(
//    contentText: String,
//    confirmLabel: String = "확인",
//    cancelLabel: String? = "취소",
//    confirmColor: Color = ColorPrimary.Primary_500,
//    onConfirm: () -> Unit,
//    onDismissRequest: () -> Unit,
//) {
//    Dialog(
//        onDismissRequest = onDismissRequest
//    ) {
//        Column(
//            modifier = Modifier
//                .clip(shape = RoundedCornerShape(20.dp))
//                .background(ColorGray.White)
//                .heightIn(min = 180.dp)
//                .verticalScroll(rememberScrollState()),
//            horizontalAlignment = Alignment.CenterHorizontally,
//        ) {
//            Box(
//                modifier = Modifier
//                    .weight(1f)
//                    .padding(20.dp),
//                contentAlignment = Alignment.Center
//            ) {
//                WMText(
//                    text = contentText,
//                    textAlign = TextAlign.Center,
//                    style = Typography().titleMedium,
//                )
//            }
//            Row {
//                cancelLabel?.let {
//                    Box(
//                        modifier = Modifier
//                            .height(52.dp)
//                            .weight(1f)
//                            .background(ColorGray.Gray_50)
//                            .clickable {
//                                onDismissRequest()
//                            },
//                        contentAlignment = Alignment.Center
//                    ) {
//                        WMText(
//                            text = cancelLabel,
//                            style = Typography().titleMedium.copy(fontWeight = FontWeight.Medium),
//                        )
//                    }
//                }
//                Box(
//                    modifier = Modifier
//                        .height(52.dp)
//                        .weight(1f)
//                        .background(confirmColor)
//                        .clickable {
//                            onConfirm()
//                        },
//                    contentAlignment = Alignment.Center
//                ) {
//                    WMText(
//                        text = confirmLabel,
//                        style = Typography().titleMedium.copy(
//                            color = ColorGray.White,
//                            fontWeight = FontWeight.Medium
//                        ),
//                    )
//                }
//            }
//        }
//    }
//}
