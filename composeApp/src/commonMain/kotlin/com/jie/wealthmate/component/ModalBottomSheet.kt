package com.jie.wealthmate.component

import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Typography
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jie.wealthmate.theme.ColorGray
import org.jetbrains.compose.resources.painterResource
import wealthmate.composeapp.generated.resources.Res
import wealthmate.composeapp.generated.resources.ic_horizontal_rule

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WMModalBottomSheet(
    modifier: Modifier = Modifier,
    title: String? = null,
    trailingItem: @Composable (() -> Unit)? = null,
    onDismissRequest: () -> Unit,
    content: @Composable ColumnScope.() -> Unit,
) {
    val focusManager = LocalFocusManager.current

    ModalBottomSheet(
        modifier = modifier,
        sheetState = rememberModalBottomSheetState( true),
        containerColor = ColorGray.White,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        onDismissRequest = {
            focusManager.clearFocus()
            onDismissRequest()
        },
        dragHandle = { ->
            Icon(
                painter = painterResource(Res.drawable.ic_horizontal_rule),
                contentDescription = null,
                tint = ColorGray.Gray_400,
                modifier = Modifier.size(40.dp),
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                // 본문 영역의 포인터 입력을 가로채서 시트 전체의 드래그 동작을 방해함
                // 드래그 제스처가 아래의 BottomSheetScaffold/ModalBottomSheet 감지기까지 도달하지 못하게 함
                .pointerInput(Unit) {
                    detectVerticalDragGestures { _, _ ->
                        // 아무것도 하지 않음으로써 본문 드래그를 무효화 (추측: 내부 스크롤이 있다면 충돌 가능성 있음)
                    }
                },
            horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                title?.run {
                    WMText(
                        text = this,
                        style = Typography().titleMedium.copy(fontWeight = FontWeight.SemiBold),
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )
                }
                Box(
                    modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    trailingItem?.invoke()
                }
            }
            content()
        }
    }
}
