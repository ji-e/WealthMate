package com.jie.wealthmate.feature.home.preparednessStatus.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jie.wealthmate.component.WMText
import com.jie.wealthmate.theme.noRippleClickable
import org.jetbrains.compose.resources.painterResource
import wealthmate.composeapp.generated.resources.Res
import wealthmate.composeapp.generated.resources.ic_arrow_drop_down

@Composable
fun PreparednessStatusFilter(
    modifier: Modifier = Modifier,
    statusTypeLabel: String,
    largeCategoryLabel: String?,
    onStatusTypeClick: () -> Unit,
    onLargeCategoryClick: (() -> Unit)?,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        FilterItem(
            text = statusTypeLabel,
            onClick = onStatusTypeClick
        )
        Spacer(modifier = Modifier.size(12.dp))
        if (largeCategoryLabel.isNullOrBlank().not() && onLargeCategoryClick != null) {
            FilterItem(
                text = largeCategoryLabel,
                onClick = onLargeCategoryClick
            )
        }
    }
}

@Composable
private fun FilterItem(text: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier.noRippleClickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        WMText(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
        )
        Icon(
            painter = painterResource(Res.drawable.ic_arrow_drop_down),
            contentDescription = null,
            modifier = Modifier.size(20.dp)
        )
    }
}