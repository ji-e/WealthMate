package com.jie.wealthmate.feature.menu.googleCloudShare.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.jie.wealthmate.component.ButtonSize
import com.jie.wealthmate.component.WMButton
import com.jie.wealthmate.component.textField.WMTextField
import com.jie.wealthmate.theme.ColorGray

@Composable
fun Owner(
    sharedFolderId: String,
    email: TextFieldValue,
    updateEmail: (TextFieldValue) -> Unit,
    onInviteClick: () -> Unit,
    showSnackbar: (String) -> Unit,
){
    val clipboardManager = LocalClipboardManager.current

    WMTextField(
        label = "나의 초대 코드",
        value = sharedFolderId,
        onValueChange = {},
        readOnlyColor = ColorGray.Gray_400,
        readOnly = true,
        modifier = Modifier
            .padding(top = 4.dp)
            .padding(horizontal = 20.dp),
        onReadOnlyClick = {
            if (sharedFolderId.isNotEmpty()) {
                clipboardManager.setText(AnnotatedString(sharedFolderId))
                showSnackbar("초대 코드가 복사되었습니다.")
            }
        }
    )

    WMTextField(
        label = "초대 할 이메일",
        value = email,
        onValueChange =updateEmail,
        modifier = Modifier
            .padding(top = 4.dp)
            .padding(horizontal = 20.dp),
        placeholder = "초대 할 구글 이메일을 입력해 주세요."
    )

    WMButton(
        text = "초대하기",
        onClick = onInviteClick,
        enabled = email.text.isNotBlank(),
        buttonSize = ButtonSize.LARGE,
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth()
    )


    HorizontalDivider(
        modifier = Modifier
            .padding(top = 32.dp, bottom = 28.dp)
            .padding(horizontal = 20.dp),
        color = ColorGray.Gray_100
    )
}