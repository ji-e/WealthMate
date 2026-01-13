package com.jie.wealthmate.component

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
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
    maxLength: Int = Int.MAX_VALUE,
    readOnly: Boolean = false,
    enabled: Boolean = true,
    label: String? = null,
    placeholder: String? = null,
    supportingText: String? = null,
    isError: Boolean = false,
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
        value = textFieldValueState,
        onValueChange = {
            textFieldValueState = it
            onValueChange(it.text)
        },
        maxLength = maxLength,
        readOnly = readOnly,
        enabled = enabled,
        label = label,
        placeholder = placeholder,
        supportingText = supportingText,
        isError = isError,
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
    maxLength: Int = Int.MAX_VALUE,
    readOnly: Boolean = false,
    enabled: Boolean = true,
    label: String? = null,
    placeholder: String? = null,
    supportingText: String? = null,
    isError: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    var isFocused by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }

    val defaultColor = ColorGray.Gray_700
    val errorColor = ColorRed.Red_300
    val disabledColor = ColorGray.Gray_300

    Box(
        modifier = modifier,
        contentAlignment = Alignment.BottomCenter
    ) {
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .height(88.dp)
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
            label = label?.let {
                {
                    Text(
                        text = it,
                        modifier = Modifier.padding(bottom = if (value.text.isNotEmpty() || isFocused) 4.dp else 0.dp),
                        fontSize = if (value.text.isNotEmpty() || isFocused) Typography().titleSmall.fontSize else Typography().bodyLarge.fontSize,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = wantedSansFontFamily()
                    )
                }
            },
            placeholder = placeholder?.let {
                {
                    WMText(text = it, style = Typography().bodyLarge)
                }
            },
            supportingText = supportingText?.let {
                {
                    WMText(text = it)
                }
            },
            singleLine = true,
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
                errorCursorColor = errorColor,
                textSelectionColors = TextSelectionColors(
                    handleColor = if (isError) errorColor else ColorPrimary.Primary_700,
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
                focusedPlaceholderColor = disabledColor,
                unfocusedPlaceholderColor = disabledColor,
                disabledPlaceholderColor = disabledColor,
                errorPlaceholderColor = disabledColor,
                focusedSupportingTextColor = defaultColor,
                unfocusedSupportingTextColor = defaultColor,
                disabledSupportingTextColor = disabledColor,
                errorSupportingTextColor = errorColor,
            )
        )
        Spacer(
            modifier = Modifier
                .padding(bottom = 26.dp)
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
                .height(1.dp)
                .background(
                    when {
                        enabled.not() -> disabledColor
                        isError -> errorColor
                        isFocused -> ColorPrimary.Primary_700
                        else -> ColorGray.Gray_500
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
