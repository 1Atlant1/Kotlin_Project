package com.example.kotlin_project.ui.screens.plate

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.kotlin_project.R

private val MEAL_PERIODS = listOf(
    "Breakfast" to R.string.breakfast,
    "Lunch" to R.string.lunch,
    "Dinner" to R.string.dinner
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealPeriodSelector(
    mealPeriod: String,
    isAutoDetected: Boolean,
    onPeriodSelected: (String) -> Unit,
    onAutoDetectClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val periodResId = MEAL_PERIODS.find { it.first == mealPeriod }?.second ?: R.string.lunch
    val hintResId = if (isAutoDetected) R.string.meal_period_auto else R.string.meal_period_manual

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = stringResource(periodResId),
            onValueChange = {},
            readOnly = true,
            label = { Text(stringResource(R.string.meal_period)) },
            supportingText = { Text(stringResource(hintResId)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(type = ExposedDropdownMenuAnchorType.PrimaryNotEditable, enabled = true)
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            MEAL_PERIODS.forEach { (period, resId) ->
                DropdownMenuItem(
                    text = { Text(stringResource(resId)) },
                    onClick = {
                        onPeriodSelected(period)
                        expanded = false
                    }
                )
            }
            if (!isAutoDetected) {
                HorizontalDivider()
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.meal_period_detect_auto)) },
                    leadingIcon = { Icon(Icons.Default.Schedule, contentDescription = null) },
                    onClick = {
                        onAutoDetectClick()
                        expanded = false
                    }
                )
            }
        }
    }
}