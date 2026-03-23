package com.jie.wealthmate.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.jie.wealthmate.theme.ColorGray
import com.jie.wealthmate.theme.ColorPrimary
import com.jie.wealthmate.theme.WMTheme

/**
 * WealthMate 공통 다이얼로그 컴포넌트
 */
@Composable
fun WMDialog(
    contentText: String,
    confirmLabel: String = "확인",
    cancelLabel: String? = "취소",
    confirmColor: Color = ColorPrimary.Primary_500,
    confirmCallback: () -> Unit,
    onDismissRequest: () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismissRequest
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(shape = RoundedCornerShape(20.dp))
                .background(ColorGray.White)
                .heightIn(min = 180.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // 컨텐츠 영역
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 24.dp, vertical = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                WMText(
                    text = contentText,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleMedium,
                    color = ColorGray.Gray_700
                )
            }

            // 하단 버튼 영역
            Row(modifier = Modifier.fillMaxWidth()) {
                // 취소 버튼 (있을 경우만 표시)
                cancelLabel?.let {
                    DialogButton(
                        text = it,
                        backgroundColor = ColorGray.Gray_50,
                        contentColor = ColorGray.Gray_700,
                        modifier = Modifier.weight(1f),
                        onClick = onDismissRequest
                    )
                }

                // 확인 버튼
                DialogButton(
                    text = confirmLabel,
                    backgroundColor = confirmColor,
                    contentColor = ColorGray.White,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        onDismissRequest()
                        confirmCallback()
                    }
                )
            }
        }
    }
}

/**
 * 저장되지 않은 변경사항이 있을 때 뒤로가기 시 표시되는 다이얼로그
 */
@Composable
fun WMSaveBackDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    WMDialog(
        contentText = "저장되지 않았습니다.\n이전 화면으로 돌아갈까요?",
        confirmCallback = onConfirm,
        onDismissRequest = onDismiss
    )
}

/**
 * 삭제 확인 다이얼로그
 */
@Composable
fun WMRemoveDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    WMDialog(
        contentText = "정말로 삭제하시겠습니까?\n삭제된 데이터는 복구할 수 없습니다.",
        confirmCallback = onConfirm,
        onDismissRequest = onDismiss
    )
}


/**
 * 다이얼로그 내부에서 사용되는 버튼 컴포넌트
 */
@Composable
private fun DialogButton(
    text: String,
    backgroundColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .height(56.dp)
            .background(backgroundColor)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        WMText(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = contentColor
        )
    }
}

@Preview
@Composable
private fun WMDialogPreview() {
    WMTheme {
        WMDialog(
            contentText = "정말로 삭제하시겠습니까?\n삭제된 데이터는 복구할 수 없습니다.",
            confirmCallback = {},
            onDismissRequest = {}
        )
    }
}

@Preview
@Composable
private fun WMDialogSingleButtonPreview() {
    WMTheme {
        WMDialog(
            contentText = "처리가 완료되었습니다.",
            cancelLabel = null,
            confirmCallback = {},
            onDismissRequest = {}
        )
    }
}
