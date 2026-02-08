package com.jie.wealthmate.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Typography
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jie.wealthmate.theme.ColorGray
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

    LaunchedEffect(Unit){
        focusManager.clearFocus()
    }

    ModalBottomSheet(
        modifier = modifier,
        sheetState = rememberModalBottomSheetState(true),
        containerColor = ColorGray.White,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        onDismissRequest = {
            focusManager.clearFocus()
            onDismissRequest()
        },
        sheetGesturesEnabled = false,
        dragHandle = null,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                title?.run {
                    WMText(
                        text = this,
                        style = Typography().titleMedium.copy(fontWeight = FontWeight.SemiBold),
                        modifier = Modifier.padding(vertical = 20.dp, horizontal = 28.dp)
                    )
                }

                Box(
                    modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    readingItem?.invoke()
                }

                Box(
                    modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    if (trailingItem != null) {
                        trailingItem()
                    } else {
                        title?.run {
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

            content()
        }
    }
}
