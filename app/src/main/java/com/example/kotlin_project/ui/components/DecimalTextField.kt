package com.example.kotlin_project.ui.components

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import com.example.kotlin_project.ui.utils.filterDecimalInput
import com.example.kotlin_project.ui.utils.toDecimalOrNull

@Composable
fun DecimalTextField(
    value: Double,
    onValueChange: (Double) -> Unit,
    modifier: Modifier = Modifier,
    label: @Composable (() -> Unit)? = null,
    textStyle: TextStyle = LocalTextStyle.current
) {
    var text by rememberSaveable { mutableStateOf(value.toString()) }
    val parsedValue = text.toDecimalOrNull()?.takeIf { it > 0.0 }

    LaunchedEffect(value) {
        if (text.toDecimalOrNull() != value) {
            text = value.toString()
        }
    }

    OutlinedTextField(
        value = text,
        onValueChange = { newText ->
            text = newText.filterDecimalInput()
            text.toDecimalOrNull()?.takeIf { it > 0.0 }?.let(onValueChange)
        },
        modifier = modifier,
        label = label,
        isError = parsedValue == null,
        textStyle = textStyle,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        singleLine = true
    )
}