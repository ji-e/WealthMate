package com.jie.wealthmate.component

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jie.wealthmate.theme.ColorGray
import org.jetbrains.compose.resources.painterResource
import wealthmate.composeapp.generated.resources.Res
import wealthmate.composeapp.generated.resources.ic_horizontal_rule

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WMTextModalBottomSheet(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    content: @Composable ColumnScope.() -> Unit,
) {
    ModalBottomSheet(
        modifier = modifier,
        containerColor = ColorGray.White,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        onDismissRequest = onDismissRequest,
        dragHandle = {
            Icon(
                painter = painterResource(Res.drawable.ic_horizontal_rule),
                contentDescription = null,
                tint = ColorGray.Gray_400,
                modifier = Modifier.size(40.dp),
            )
        }
    ) {
        content()
    }
}