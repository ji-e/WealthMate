package com.jie.wealthmate.feature.menu.data.googleCloudShare.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.jie.wealthmate.component.EmptyListView
import com.jie.wealthmate.component.LabelText
import com.jie.wealthmate.component.WMText
import com.jie.wealthmate.theme.ColorGroup
import com.jie.wealthmate.theme.ColorYellow
import com.jie.wealthmate.utils.default
import com.jie.wealthmate.vo.DrivePermission
import org.jetbrains.compose.resources.painterResource
import wealthmate.composeapp.generated.resources.Res
import wealthmate.composeapp.generated.resources.ic_close
import wealthmate.composeapp.generated.resources.ic_star

@Composable
fun SharedMemberList(
    modifier: Modifier = Modifier,
    permissionsItems: List<DrivePermission>,
    isOwner: Boolean,
    onRemoveClick: (String) -> Unit,
) {
    val backgroundColors = ColorGroup.getColorList()

    Column(modifier = modifier) {
        LabelText(
            text = "공유 멤버 목록",
            modifier = Modifier
                .padding(top = 4.dp)
                .padding(horizontal = 28.dp)
        )

        if (permissionsItems.isEmpty()) {
            EmptyListView(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 20.dp, horizontal = 28.dp),
                contentText = "공유된 멤버가 없습니다.",
            )
            return
        }

        LazyColumn(
            modifier = Modifier.padding(top = 12.dp),
        ) {
            items(
                count = permissionsItems.size,
                key = { index -> permissionsItems[index].id })
            {
                val item = permissionsItems[it]
                SharedMemberItem(
                    item = item,
                    isOwner = isOwner,
                    backgroundColor = backgroundColors[it % 10].second,
                    onRemoveClick = { onRemoveClick(item.id) },
                )
            }
        }
    }
}

@Composable
fun SharedMemberItem(
    item: DrivePermission,
    backgroundColor: Color,
    isOwner: Boolean,
    onRemoveClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .height(40.dp)
            .fillMaxWidth()
            .padding(horizontal = 28.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .background(backgroundColor)
                .size(24.dp),
            contentAlignment = Alignment.Center
        ) {
            WMText(
                text = item.displayName.default().take(1),
                style = Typography().titleMedium,
            )
        }

        WMText(
            text = item.emailAddress.default(),
            style = Typography().bodyLarge,
            modifier = Modifier.weight(1f)
        )

        if (item.role == "owner") {
            Icon(
                painter = painterResource(Res.drawable.ic_star),
                contentDescription = null,
                tint = ColorYellow.Yellow_300,
                modifier = Modifier.size(24.dp)
            )
        } else if (isOwner) {
            Icon(
                painter = painterResource(Res.drawable.ic_close),
                contentDescription = "삭제",
                modifier = Modifier
                    .clip(CircleShape)
                    .size(24.dp)
                    .clickable { onRemoveClick() }
            )
        }
    }
}