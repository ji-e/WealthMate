package com.jie.wealthmate.base

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import cafe.adriel.voyager.core.screen.Screen
import com.jie.wealthmate.component.WMText
import com.jie.wealthmate.theme.ColorGray
import com.jie.wealthmate.theme.ColorPrimary
import com.jie.wealthmate.theme.ColorRed

abstract class BaseScreen : Screen {
    var isShowSaveBackDialog = mutableStateOf(false)
    var isShowRemoveDialog = mutableStateOf(false)
    var confirmCallback = mutableStateOf({})

    fun showSaveBackDialog(
        isShow: Boolean,
        callback: () -> Unit,
    ) {
        if (isShow.not()) {
            callback()
            return
        }

        isShowSaveBackDialog.value = isShow
        confirmCallback.value = callback
    }

    fun showRemoveDialog(
        callback: () -> Unit,
    ) {

        isShowRemoveDialog.value = true
        confirmCallback.value = callback
    }


    @Composable
    override fun Content() {

        if (isShowSaveBackDialog.value) {
            BaseDialog(
                contentText = "저장되지 않았습니다.\n이전 화면으로 돌아갈까요?",
                onDismissRequest = { isShowSaveBackDialog.value = false }
            )
        }

        if (isShowRemoveDialog.value) {
            BaseDialog(
                contentText = "정말 삭제하시겠습니까?\n삭제된 정보는 복구할 수 없습니다.",
                confirmLabel = "삭제",
                confirmColor = ColorRed.Red_300,
                onDismissRequest = { isShowRemoveDialog.value = false }
            )
        }
    }

    @Composable
    private fun BaseDialog(
        contentText: String,
        confirmLabel: String = "확인",
        cancelLabel: String = "취소",
        confirmColor: Color = ColorPrimary.Primary_500,
        onDismissRequest: () -> Unit,
    ) {
        Dialog(
            onDismissRequest = onDismissRequest
        ) {
            Column(
                modifier = Modifier
                    .clip(shape = RoundedCornerShape(20.dp))
                    .background(ColorGray.White)
                    .height(200.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    WMText(
                        text = contentText,
                        textAlign = TextAlign.Center,
                        style = Typography().bodyLarge,
                    )
                }
                Row() {
                    Box(
                        modifier = Modifier
                            .height(60.dp)
                            .weight(1f)
                            .background(ColorGray.Gray_50)
                            .clickable {
                                onDismissRequest()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        WMText(
                            text = cancelLabel,
                            style = Typography().titleMedium.copy(fontWeight = FontWeight.Medium),
                        )
                    }
                    Box(
                        modifier = Modifier
                            .height(60.dp)
                            .weight(1f)
                            .background(confirmColor)
                            .clickable {
                                onDismissRequest()
                                confirmCallback.value.invoke()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        WMText(
                            text = confirmLabel,
                            style = Typography().titleMedium.copy(
                                color = ColorGray.White,
                                fontWeight = FontWeight.Medium
                            ),
                        )
                    }
                }
            }
        }
    }

    @Composable
    fun EmptyListView(
        modifier: Modifier = Modifier,
        contentText: String,
    ) {
        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center
        ) {
            WMText(
                text = contentText,
                style = Typography().bodyLarge.copy(color = ColorGray.Gray_400)
            )
        }
    }
}