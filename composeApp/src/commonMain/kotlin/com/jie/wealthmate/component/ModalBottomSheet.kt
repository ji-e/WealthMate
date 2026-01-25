package com.jie.wealthmate.component

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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, bottom = 12.dp),
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
