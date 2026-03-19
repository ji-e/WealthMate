package com.jie.wealthmate.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jie.wealthmate.theme.ColorGray
import com.jie.wealthmate.theme.WMTheme
import wealthmate.composeapp.generated.resources.Res
import wealthmate.composeapp.generated.resources.ic_close

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WMModalBottomSheet(
    modifier: Modifier = Modifier,
    title: String? = null,
    readingItem: @Composable (() -> Unit)? = null,
    trailingItem: @Composable (() -> Unit)? = null,
    onDismissRequest: () -> Unit,
    content: @Composable ColumnScope.() -> Unit,
) {
    val focusManager = LocalFocusManager.current

    LaunchedEffect(Unit) {
        focusManager.clearFocus()
    }

    ModalBottomSheet(
        modifier = modifier,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = ColorGray.White,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        onDismissRequest = {
            focusManager.clearFocus()
            onDismissRequest()
        },
        dragHandle = null,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header 영역
            Header(
                focusManager = focusManager,
                title = title,
                readingItem = readingItem,
                trailingItem = trailingItem,
                onDismissRequest = onDismissRequest
            )

            content()
        }
    }
}

@Composable
private fun Header(
    focusManager: FocusManager,
    title: String?,
    readingItem: @Composable (() -> Unit)?,
    trailingItem: @Composable (() -> Unit)?,
    onDismissRequest: () -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        title?.let {
            WMText(
                text = it,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(vertical = 20.dp, horizontal = 56.dp)
            )
        }

        // 왼쪽 아이템 (예: 뒤로가기)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            readingItem?.invoke()
        }

        // 오른쪽 아이템 (예: 닫기 또는 완료)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            contentAlignment = Alignment.CenterEnd
        ) {
            if (trailingItem != null) {
                trailingItem()
            } else if (title != null) {
                WMIconButton(
                    iconRes = Res.drawable.ic_close,
                    onClick = {
                        focusManager.clearFocus()
                        onDismissRequest()
                    }
                )
            }
        }
    }
}

@Preview
@Composable
private fun WMModalBottomSheetPreview() {
    WMTheme {
        WMModalBottomSheet(
            title = "바텀 시트 타이틀",
            onDismissRequest = {}
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                WMText(text = "Sheet Content Area")
                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}