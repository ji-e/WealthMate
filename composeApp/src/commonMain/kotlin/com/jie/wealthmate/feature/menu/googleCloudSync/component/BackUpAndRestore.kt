package com.jie.wealthmate.feature.menu.googleCloudShare.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.jie.wealthmate.component.WMText
import com.jie.wealthmate.theme.ColorGray
import com.jie.wealthmate.theme.ColorPrimary
import org.jetbrains.compose.resources.painterResource
import wealthmate.composeapp.generated.resources.Res
import wealthmate.composeapp.generated.resources.ic_cloud_download
import wealthmate.composeapp.generated.resources.ic_cloud_upload

@Composable
fun BackupAndRestore(
    modifier: Modifier = Modifier,
    onBackupClick: () -> Unit,
    onRestoreClick: () -> Unit,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(8.dp))
                .background(ColorGray.Gray_50)
                .clickable { onBackupClick() }
                .padding(vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painterResource(Res.drawable.ic_cloud_upload),
                contentDescription = "백업하기",
                modifier = Modifier.padding(bottom = 16.dp),
                tint = ColorPrimary.Primary_500
            )
            WMText(
                text = "백업하기",
                style = androidx.compose.material3.Typography().titleMedium
            )
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(8.dp))
                .background(ColorGray.Gray_50)
                .clickable { onRestoreClick() }
                .padding(vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painterResource(Res.drawable.ic_cloud_download),
                contentDescription = "복원하기",
                modifier = Modifier.padding(bottom = 16.dp),
                tint = ColorPrimary.Primary_500
            )
            WMText(
                text = "복원하기",
                style = Typography().titleMedium
            )
        }
    }
}