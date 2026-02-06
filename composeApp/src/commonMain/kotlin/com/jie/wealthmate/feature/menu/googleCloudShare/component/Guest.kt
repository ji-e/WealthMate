package com.jie.wealthmate.feature.menu.googleCloudShare.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import com.jie.wealthmate.component.ButtonSize
import com.jie.wealthmate.component.WMButton
import com.jie.wealthmate.component.textField.WMTextField
import com.jie.wealthmate.theme.ColorGray

@Composable
fun Guest(
    modifier: Modifier,
    inviteCode: TextFieldValue,
    onInviteCodeChange: (TextFieldValue) -> Unit,
    onInviteClick: () -> Unit,
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        WMTextField(
            label = "초대 코드",
            value = inviteCode,
            onValueChange = onInviteCodeChange,
            readOnlyColor = ColorGray.Gray_400,
            placeholder = "메일에 있는 초대 코드를 입력해 주세요."
        )

        WMButton(
            text = "참여하기",
            onClick = onInviteClick,
            enabled = inviteCode.text.isNotBlank(),
            buttonSize = ButtonSize.LARGE,
            modifier = Modifier.fillMaxWidth()
        )
    }
}