package com.jie.wealthmate.component.textField

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layout
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.jie.wealthmate.component.WMText
import com.jie.wealthmate.theme.ColorGray
import com.jie.wealthmate.theme.ColorPrimary
import com.jie.wealthmate.theme.ColorRed
import com.jie.wealthmate.theme.wantedSansFontFamily
import org.jetbrains.compose.ui.tooling.preview.Preview

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
    onClickReadOnly: (() -> Unit)? = null,
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
        onClickReadOnly = onClickReadOnly,
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
    onClickReadOnly: (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    var isFocused by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }

    val defaultColor = ColorGray.Gray_700
    val errorColor = ColorRed.Red_300
    val disabledColor = ColorGray.Gray_100
    val placeholderColor = ColorGray.Gray_300


    Box(modifier = modifier) {
        TextField(
            modifier = textFieldModifier
                .fillMaxWidth()
                .layout { measurable, constraints ->
                    val padding = 16.dp.roundToPx()
                    val expandedWidth = constraints.maxWidth + (padding * 2)
                    val placeable = measurable.measure(
                        constraints.copy(
                            maxWidth = expandedWidth,
                            minWidth = expandedWidth
                        )
                    )
                    layout(constraints.maxWidth, placeable.height) {
                        placeable.placeRelative(-padding, 0)
                    }
                }
                .heightIn(min = 92.dp)
                .focusRequester(focusRequester)
                .onFocusChanged { focusState ->
                    isFocused = focusState.isFocused
                },
            value = value,
            onValueChange = {
                if (it.text.length <= maxLength) {
                    onValueChange(it)
                }
            },
            enabled = enabled,
            readOnly = readOnly,
            isError = isError,
            maxLines = maxLines,
            label = label?.let {
                {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = it,
                            modifier = Modifier.padding(bottom = if (value.text.isNotEmpty() || isFocused) 12.dp else 0.dp),
                            color =
                                if (enabled.not()) disabledColor
                                else if (value.text.isNotEmpty() || isFocused) defaultColor
                                else ColorGray.Gray_500,
                            fontSize = if (value.text.isNotEmpty() || isFocused) Typography().titleSmall.fontSize else Typography().bodyLarge.fontSize,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = wantedSansFontFamily()
                        )

                        if (isRequire) {
                            Text(
                                text = "*",
                                modifier = Modifier.padding(bottom = if (value.text.isNotEmpty() || isFocused) 12.dp else 0.dp),
                                color = if (enabled.not()) disabledColor else errorColor,
                                fontSize = if (value.text.isNotEmpty() || isFocused) Typography().titleSmall.fontSize else Typography().bodyLarge.fontSize,
                                fontWeight = FontWeight.SemiBold,
                                fontFamily = wantedSansFontFamily()
                            )
                        }
                    }
                }
            },
            placeholder = placeholder?.let {
                {
                    WMText(text = it, style = Typography().bodyLarge)
                }
            },
            supportingText =
                {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        supportingText?.let {
                            WMText(text = it, modifier = Modifier.weight(1f))
                        }

                        if (isCount) {
                            WMText(text = "${value.text.length}/$maxLength")
                        }
                        supportingContent?.invoke()
                    }
                },
            singleLine = true,
            suffix = suffix,
            trailingIcon = trailingIcon,
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            interactionSource = interactionSource,
            colors = TextFieldDefaults.colors().copy(
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
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                errorIndicatorColor = Color.Transparent,
                focusedLabelColor = defaultColor,
                unfocusedLabelColor = defaultColor,
                disabledLabelColor = disabledColor,
                errorLabelColor = defaultColor,
                focusedPlaceholderColor = placeholderColor,
                unfocusedPlaceholderColor = placeholderColor,
                disabledPlaceholderColor = placeholderColor,
                errorPlaceholderColor = placeholderColor,
                focusedSupportingTextColor = placeholderColor,
                unfocusedSupportingTextColor = placeholderColor,
                disabledSupportingTextColor = disabledColor,
                errorSupportingTextColor = errorColor,
            )
        )
        if (onClickReadOnly != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp)
                    .padding(top = 28.dp)
                    .clickable {
                        focusRequester.requestFocus()
                        onClickReadOnly()
                    }
            )
        }
        Spacer(
            modifier = Modifier
                .padding(top = 69.dp)
                .fillMaxWidth()
                .height(1.dp)
                .background(
                    when {
                        enabled.not() -> disabledColor
                        isError -> errorColor
                        isFocused -> ColorPrimary.Primary_500
                        value.text.isNotEmpty() -> ColorGray.Gray_500
                        else -> placeholderColor
                    }
                )
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
