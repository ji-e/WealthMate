package com.jie.wealthmate.component.textField

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jie.wealthmate.component.WMText
import com.jie.wealthmate.theme.ColorGray
import com.jie.wealthmate.theme.ColorPrimary
import com.jie.wealthmate.theme.ColorRed
import com.jie.wealthmate.utils.default

@Composable
fun WMTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    textFieldModifier: Modifier = Modifier,
    maxLength: Int = Int.MAX_VALUE,
    maxLines: Int = 1,
    readOnly: Boolean = false,
    enabled: Boolean = true,
    label: String? = null,
    placeholder: String? = null,
    supportingText: String? = null,
    supportingContent: @Composable (() -> Unit)? = null,
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
        value = textFieldValueState,
        onValueChange = {
            textFieldValueState = it
            onValueChange(it.text)
        },
        maxLength = maxLength,
        maxLines = maxLines,
        readOnly = readOnly,
        enabled = enabled,
        label = label,
        placeholder = placeholder,
        supportingText = supportingText,
        supportingContent = supportingContent,
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
    readOnly: Boolean = false,
    enabled: Boolean = true,
    label: String? = null,
    placeholder: String? = null,
    supportingText: String? = null,
    supportingContent: @Composable (() -> Unit)? = null,
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
        focusedContainerColor = ColorGray.White,
        disabledContainerColor = ColorGray.White,
        unfocusedContainerColor = ColorGray.White,
        errorContainerColor = ColorGray.White,
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                WMText(
                    text = it,
                    style = Typography().titleSmall.copy(
                        color = if (enabled.not()) disabledColor
                        else defaultColor,
                        fontWeight = FontWeight.SemiBold,
                    )
                )

                if (isRequire) {
                    WMText(
                        text = "*",
                        style = Typography().titleSmall.copy(
                            color = if (enabled.not()) disabledColor else errorColor,
                            fontWeight = FontWeight.SemiBold,
                        )
                    )
                }
            }
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
                textStyle = Typography().bodyLarge.copy(
                    color = if (enabled.not()) disabledColor else defaultColor,
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
                                    style = Typography().bodyLarge
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
