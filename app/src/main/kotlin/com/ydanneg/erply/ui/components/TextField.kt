package com.ydanneg.erply.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.VisualTransformation


@Composable
fun ErplyTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit = {},
    placeholderText: String = "",
    singleLine: Boolean = false,
    isError: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    labelText: String? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    maxLength: Int = Int.MAX_VALUE,
    enabled: Boolean = true
) {
    Column {
        OutlinedTextField(
            modifier = modifier,
            value = value,
            onValueChange = {
                if (it.length <= maxLength) {
                    onValueChange(it)
                }
            },
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            placeholder = { Text(placeholderText) },
            label = { if (labelText != null) Text(labelText) },
            visualTransformation = visualTransformation,
            singleLine = singleLine,
            isError = isError,
            enabled = enabled,
            trailingIcon = trailingIcon,
        )
    }
}
