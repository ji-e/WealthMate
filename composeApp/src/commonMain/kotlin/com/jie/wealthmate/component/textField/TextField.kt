package com.jie.wealthmate.component.textField

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.Typography
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
import com.jie.wealthmate.theme.ColorGray
import com.jie.wealthmate.theme.ColorPrimary
import com.jie.wealthmate.theme.ColorRed
import com.jie.wealthmate.utils.default
import wealthmate.composeapp.generated.resources.Res
import wealthmate.composeapp.generated.resources.ic_close_circle
import wealthmate.composeapp.generated.resources.ic_search

@Composable
fun WMTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    textFieldModifier: Modifier = Modifier,
    maxLength: Int = Int.MAX_VALUE,
    maxLines: Int = 1,
    readOnlyColor: Color = ColorGray.Gray_700,
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
    var textFieldValueState by remember {
        mutableStateOf(
            TextFieldValue(
                text = value,
                selection = TextRange(value.length)
            )
        )
    }

    WMTextField(
        modifier = modifier,
        textFieldModifier = textFieldModifier,
        value = if (readOnly) {
            TextFieldValue(
                text = value.default(),
                selection = TextRange(value.length)
            )
        } else {
            textFieldValueState
        },
        onValueChange = {
            textFieldValueState = it
            onValueChange(it.text)
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

@Composable
fun WMTextField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier,
    textFieldModifier: Modifier = Modifier,
    maxLength: Int = Int.MAX_VALUE,
    maxLines: Int = 1,
    readOnlyColor: Color = ColorGray.Gray_700,
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

    val defaultColor = ColorGray.Gray_700
    val errorColor = ColorRed.Red_300
    val disabledColor = ColorGray.Gray_100
    val placeholderColor = ColorGray.Gray_300
    val colors = TextFieldDefaults.colors().copy(
        focusedTextColor = defaultColor,
        disabledTextColor = disabledColor,
        unfocusedTextColor = defaultColor,
        errorTextColor = defaultColor,
        focusedContainerColor = Color.Transparent,
        disabledContainerColor = Color.Transparent,
        unfocusedContainerColor = Color.Transparent,
        errorContainerColor = Color.Transparent,
        cursorColor = ColorPrimary.Primary_500,
        errorCursorColor = errorColor,
        textSelectionColors = TextSelectionColors(
            handleColor = if (isError) errorColor else ColorPrimary.Primary_500,
            backgroundColor = ColorPrimary.Primary_200
        ),
        unfocusedIndicatorColor = placeholderColor,
        disabledIndicatorColor = disabledColor,
        errorIndicatorColor = errorColor,
        focusedPlaceholderColor = placeholderColor,
        unfocusedPlaceholderColor = placeholderColor,
        disabledPlaceholderColor = placeholderColor,
        errorPlaceholderColor = placeholderColor,
        focusedSupportingTextColor = placeholderColor,
        unfocusedSupportingTextColor = placeholderColor,
        disabledSupportingTextColor = disabledColor,
        errorSupportingTextColor = errorColor,
    )

    Column(modifier = modifier) {
        label?.let {
            LabelText(
                text = it,
                textColor = if (enabled.not()) disabledColor else defaultColor,
                isRequire = isRequire,
            )
        }
        Box {
            BasicTextField(
                modifier = textFieldModifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                value = value,
                onValueChange = {
                    if (it.text.length <= maxLength) {
                        onValueChange(it)
                    }
                },
                enabled = enabled,
                readOnly = readOnly,
                maxLines = maxLines,
                singleLine = true,
                visualTransformation = visualTransformation,
                keyboardOptions = keyboardOptions,
                keyboardActions = keyboardActions,
                interactionSource = interactionSource,
                textStyle = Typography().bodyLarge
                    .copy(
                        color =
                            when {
                                readOnly -> readOnlyColor
                                enabled.not() -> disabledColor
                                else -> defaultColor
                            },
                        textAlign = if (isRight) TextAlign.End else TextAlign.Start
                    ),
                cursorBrush = SolidColor(if (isError) errorColor else ColorPrimary.Primary_500),
                decorationBox = { innerTextField ->
                    TextFieldDefaults.DecorationBox(
                        value = value.text,
                        innerTextField = innerTextField,
                        enabled = enabled,
                        singleLine = true,
                        visualTransformation = visualTransformation,
                        interactionSource = interactionSource,
                        isError = isError,
                        placeholder = placeholder?.let {
                            {
                                WMText(
                                    text = it,
                                    style = Typography().bodyLarge,
                                    maxLines = 1,
                                    textAlign = if (isRight) TextAlign.End else TextAlign.Start,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        },
                        suffix = {
                            Row {
                                suffix?.invoke()
                                if (isCount) {
                                    WMText(
                                        text = "${value.text.length}/$maxLength",
                                        style = Typography().bodyMedium.copy(color = placeholderColor)
                                    )
                                }
                            }
                        },
                        trailingIcon = trailingIcon,
                        shape = TextFieldDefaults.shape,
                        colors = colors,
                        contentPadding = PaddingValues(
                            vertical = 10.dp,
                            horizontal = 0.dp
                        ),
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
            if (onReadOnlyClick != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .clickable {
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
                    .heightIn(min = 14.dp),
            ) {
                WMText(
                    text = supportingText.default(),
                    style = Typography().bodyMedium.copy(color = placeholderColor),
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 4.dp)
                )

                supportingContent?.invoke()
            }
        }
    }
}

@Composable
fun WMSearchTextField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    onSearch: () -> Unit = {},
    onClear: () -> Unit = {},
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.Top
    ) {

        WMTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = placeholder,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { onSearch() }),
            maxLines = 1,
            trailingIcon = {
                Row {
                    if (value.text.isNotEmpty()) {
                        WMIconButton(
                            iconRes = Res.drawable.ic_close_circle,
                            contentDescription = "지우기",
                            tint = ColorGray.Gray_400,
                            onClick = onClear,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            },
            modifier = Modifier.weight(1f)
        )

        WMIconButton(
            iconRes = Res.drawable.ic_search,
            contentDescription = "검색",
            onClick = onSearch,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun WMTextFieldPreview() {
    WMTextField(
        value = TextFieldValue("안녕"),
        label = "Label",
        onValueChange = {},
        placeholder = "이것은 힌트이다",
        supportingText = "성공입니다."
    )
}
