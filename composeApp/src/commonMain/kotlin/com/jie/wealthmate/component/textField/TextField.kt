package com.jie.wealthmate.component.textField

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jie.wealthmate.component.LabelText
import com.jie.wealthmate.component.WMIconButton
import com.jie.wealthmate.component.WMText
import com.jie.wealthmate.theme.ColorPrimary
import com.jie.wealthmate.theme.ColorSetting
import com.jie.wealthmate.theme.WMTheme
import com.jie.wealthmate.utils.default
import wealthmate.composeapp.generated.resources.Res
import wealthmate.composeapp.generated.resources.ic_close_circle
import wealthmate.composeapp.generated.resources.ic_search

/**
 * WealthMate 공통 텍스트 필드 (String 버전)
 */
@Composable
fun WMTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    textFieldModifier: Modifier = Modifier,
    maxLength: Int = Int.MAX_VALUE,
    maxLines: Int = 1,
    readOnlyColor: Color = ColorSetting.Default,
    readOnly: Boolean = false,
    enabled: Boolean = true,
    isRight: Boolean = false,
    label: String? = null,
    placeholder: String? = null,
    supportingText: String? = null,
    supportingContent: @Composable (() -> Unit)? = null,
    isSupport: Boolean = true,
    isError: Boolean = false,
    isCount: Boolean = false,
    isRequire: Boolean = false,
    suffix: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    onReadOnlyClick: (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    var textFieldValueState by remember(value) {
        mutableStateOf(TextFieldValue(text = value, selection = TextRange(value.length)))
    }

    WMTextField(
        modifier = modifier,
        textFieldModifier = textFieldModifier,
        value = textFieldValueState,
        onValueChange = {
            textFieldValueState = it
            if (value != it.text) onValueChange(it.text)
        },
        maxLength = maxLength,
        maxLines = maxLines,
        readOnlyColor = readOnlyColor,
        readOnly = readOnly,
        enabled = enabled,
        isRight = isRight,
        label = label,
        placeholder = placeholder,
        supportingText = supportingText,
        supportingContent = supportingContent,
        isSupport = isSupport,
        isError = isError,
        isCount = isCount,
        isRequire = isRequire,
        suffix = suffix,
        trailingIcon = trailingIcon,
        onReadOnlyClick = onReadOnlyClick,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        interactionSource = interactionSource,
    )
}

/**
 * WealthMate 공통 텍스트 필드 (TextFieldValue 버전)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WMTextField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier,
    textFieldModifier: Modifier = Modifier,
    maxLength: Int = Int.MAX_VALUE,
    maxLines: Int = 1,
    readOnlyColor: Color = ColorSetting.Default,
    readOnly: Boolean = false,
    enabled: Boolean = true,
    isRight: Boolean = false,
    label: String? = null,
    placeholder: String? = null,
    supportingText: String? = null,
    supportingContent: @Composable (() -> Unit)? = null,
    isSupport: Boolean = true,
    isError: Boolean = false,
    isCount: Boolean = false,
    isRequire: Boolean = false,
    suffix: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    onReadOnlyClick: (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    val focusRequester = remember { FocusRequester() }
    val isSingleLine = maxLines == 1

    val colors = TextFieldDefaults.colors().copy(
        focusedTextColor = ColorSetting.Default,
        disabledTextColor = ColorSetting.DisabledContent,
        unfocusedTextColor = ColorSetting.Default,
        errorTextColor = ColorSetting.Default,
        focusedContainerColor = Color.Transparent,
        disabledContainerColor = Color.Transparent,
        unfocusedContainerColor = Color.Transparent,
        errorContainerColor = Color.Transparent,
        cursorColor = ColorSetting.Primary,
        errorCursorColor = ColorSetting.Error,
        textSelectionColors = TextSelectionColors(
            handleColor = if (isError) ColorSetting.Error else ColorSetting.Primary,
            backgroundColor = ColorPrimary.Primary_200
        ),
        unfocusedIndicatorColor = ColorSetting.DisabledContent,
        disabledIndicatorColor = ColorSetting.DisabledBackground,
        errorIndicatorColor = ColorSetting.Error,
        focusedPlaceholderColor = ColorSetting.DisabledContent,
        unfocusedPlaceholderColor = ColorSetting.DisabledContent,
        disabledPlaceholderColor = ColorSetting.DisabledContent,
        errorPlaceholderColor = ColorSetting.DisabledContent,
    )

    Column(modifier = modifier) {
        label?.let {
            LabelText(
                text = it,
                textColor = if (enabled.not()) ColorSetting.DisabledContent else ColorSetting.Default,
                isRequire = isRequire,
                modifier = Modifier.padding(bottom = 6.dp)
            )
        }

        Box(contentAlignment = Alignment.CenterStart) {
            BasicTextField(
                modifier = textFieldModifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                value = value,
                onValueChange = { if (it.text.length <= maxLength) onValueChange(it) },
                enabled = enabled,
                readOnly = readOnly,
                maxLines = maxLines,
                singleLine = isSingleLine,
                visualTransformation = visualTransformation,
                keyboardOptions = keyboardOptions,
                keyboardActions = keyboardActions,
                interactionSource = interactionSource,
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    color = when {
                        readOnly -> readOnlyColor
                        enabled.not() -> ColorSetting.DisabledContent
                        else -> ColorSetting.Default
                    },
                    textAlign = if (isRight) TextAlign.End else TextAlign.Start
                ),
                cursorBrush = SolidColor(if (isError) ColorSetting.Error else ColorSetting.Primary),
                decorationBox = { innerTextField ->
                    TextFieldDefaults.DecorationBox(
                        value = value.text,
                        innerTextField = innerTextField,
                        enabled = enabled,
                        singleLine = isSingleLine,
                        visualTransformation = visualTransformation,
                        interactionSource = interactionSource,
                        isError = isError,
                        placeholder = placeholder?.let {
                            {
                                WMText(
                                    text = it,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = ColorSetting.DisabledContent,
                                    maxLines = 1,
                                    textAlign = if (isRight) TextAlign.End else TextAlign.Start,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        },
                        suffix = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                suffix?.invoke()
                                if (isCount) {
                                    WMText(
                                        text = "${value.text.length}/$maxLength",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = ColorSetting.DisabledContent,
                                        modifier = Modifier.padding(start = 4.dp)
                                    )
                                }
                            }
                        },
                        trailingIcon = trailingIcon,
                        shape = TextFieldDefaults.shape,
                        colors = colors,
                        contentPadding = PaddingValues(vertical = 10.dp),
                        container = {
                            TextFieldDefaults.Container(
                                enabled = enabled,
                                isError = isError,
                                interactionSource = interactionSource,
                                colors = colors,
                                shape = TextFieldDefaults.shape,
                            )
                        }
                    )
                }
            )

            // ReadOnly 시 전체 클릭 처리를 위한 오버레이
            if (onReadOnlyClick != null && enabled) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            focusRequester.requestFocus()
                            onReadOnlyClick()
                        }
                )
            }
        }

        if (isSupport) {
            Row(
                modifier = Modifier
                    .padding(top = 4.dp)
                    .fillMaxWidth()
                    .heightIn(min = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                WMText(
                    text = supportingText.default(),
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isError) ColorSetting.Error else ColorSetting.Info,
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 4.dp)
                )
                supportingContent?.invoke()
            }
        }
    }
}

/**
 * 검색 전용 텍스트 필드
 */
@Composable
fun WMSearchTextField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    onSearch: () -> Unit = {},
    onClear: () -> Unit = {},
) {
    WMTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = placeholder,
        modifier = modifier,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { onSearch() }),
        maxLines = 1,
        isSupport = false,
        trailingIcon = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (value.text.isNotEmpty()) {
                    WMIconButton(
                        iconRes = Res.drawable.ic_close_circle,
                        contentDescription = "지우기",
                        tint = ColorSetting.DisabledContent,
                        onClick = onClear,
                        iconModifier = Modifier.size(20.dp)
                    )
                }
                WMIconButton(
                    iconRes = Res.drawable.ic_search,
                    contentDescription = "검색",
                    onClick = onSearch,
                    iconModifier = Modifier.size(24.dp)
                )
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun WMTextFieldPreview() {
    WMTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            WMTextField(
                value = "입력된 텍스트",
                label = "기본 입력 필드",
                onValueChange = {},
                placeholder = "힌트 텍스트",
                supportingText = "도움말이 여기에 표시됩니다."
            )

            WMTextField(
                value = "",
                label = "에러 상태 필드",
                isError = true,
                onValueChange = {},
                placeholder = "입력해주세요",
                supportingText = "에러 메시지입니다."
            )

            WMSearchTextField(
                value = TextFieldValue(""),
                onValueChange = {},
                placeholder = "검색어를 입력하세요"
            )
        }
    }
}
