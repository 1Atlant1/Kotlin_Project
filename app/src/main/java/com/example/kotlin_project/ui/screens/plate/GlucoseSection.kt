package com.example.kotlin_project.ui.screens.plate

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.kotlin_project.R
import com.example.kotlin_project.domain.model.Glucose
import com.example.kotlin_project.domain.model.GlucoseLevel
import com.example.kotlin_project.ui.theme.GlucoseDanger
import com.example.kotlin_project.ui.theme.GlucoseNormal
import com.example.kotlin_project.ui.theme.GlucoseWarning
import com.example.kotlin_project.ui.theme.Kotlin_projectTheme

private const val FIELD_TINT_ALPHA = 0.08f

@Composable
fun GlucoseSection(
    glucose: GlucoseInput,
    onGlucoseChange: (String) -> Unit,
    onSyncClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            GlucoseTextField(
                glucose = glucose,
                onValueChange = onGlucoseChange,
                modifier = Modifier.weight(1f)
            )
            CgmSyncButton(onClick = onSyncClick)
        }

        if (glucose.isError) {
            Text(
                text = stringResource(
                    R.string.glucose_out_of_range,
                    Glucose.format(Glucose.MIN_MMOL),
                    Glucose.format(Glucose.MAX_MMOL)
                ),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error
            )
        }

        GlucoseLegend(currentLevel = glucose.level)
    }
}

@Composable
private fun GlucoseTextField(
    glucose: GlucoseInput,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val level = glucose.level
    val levelColor by animateColorAsState(
        targetValue = level?.color ?: MaterialTheme.colorScheme.outline,
        label = "glucoseLevelColor"
    )
    val colors = if (level != null) {
        OutlinedTextFieldDefaults.colors(
            focusedBorderColor = levelColor,
            unfocusedBorderColor = levelColor,
            focusedLabelColor = levelColor,
            unfocusedLabelColor = levelColor,
            cursorColor = levelColor,
            focusedContainerColor = levelColor.copy(alpha = FIELD_TINT_ALPHA),
            unfocusedContainerColor = levelColor.copy(alpha = FIELD_TINT_ALPHA)
        )
    } else {
        OutlinedTextFieldDefaults.colors()
    }

    OutlinedTextField(
        value = glucose.text,
        onValueChange = onValueChange,
        modifier = modifier,
        label = { Text(stringResource(R.string.glucose_label)) },
        suffix = { Text(stringResource(R.string.glucose_unit)) },
        isError = glucose.isError,
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Decimal,
            imeAction = ImeAction.Done
        ),
        colors = colors
    )
}

@Composable
private fun CgmSyncButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(OutlinedTextFieldDefaults.MinHeight),
        shape = OutlinedTextFieldDefaults.shape
    ) {
        Icon(
            imageVector = Icons.Default.Sync,
            contentDescription = null,
            modifier = Modifier.size(ButtonDefaults.IconSize)
        )
        Spacer(modifier = Modifier.width(ButtonDefaults.IconSpacing))
        Text(stringResource(R.string.sync_cgm))
    }
}

@Composable
private fun GlucoseLegend(
    currentLevel: GlucoseLevel?,
    modifier: Modifier = Modifier
) {
    FlowRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        GlucoseLevel.entries.forEach { level ->
            GlucoseLegendItem(level = level, isCurrent = level == currentLevel)
        }
    }
}

@Composable
private fun GlucoseLegendItem(
    level: GlucoseLevel,
    isCurrent: Boolean
) {
    val textColor = if (isCurrent) level.color else MaterialTheme.colorScheme.onSurfaceVariant
    val fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal

    Row(
        modifier = Modifier.semantics(mergeDescendants = true) {},
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(level.color)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = level.rangeLabel(),
            style = MaterialTheme.typography.bodySmall,
            fontWeight = fontWeight,
            color = textColor
        )
        if (isCurrent) {
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = stringResource(R.string.glucose_current),
                style = MaterialTheme.typography.bodySmall,
                fontWeight = fontWeight,
                color = textColor
            )
        }
    }
}

private val GlucoseLevel.color: Color
    get() = when (this) {
        GlucoseLevel.NORMAL -> GlucoseNormal
        GlucoseLevel.ELEVATED -> GlucoseWarning
        GlucoseLevel.DANGER -> GlucoseDanger
    }

@Composable
private fun GlucoseLevel.rangeLabel(): String = when (this) {
    GlucoseLevel.NORMAL -> stringResource(
        R.string.glucose_range_between,
        Glucose.formatThreshold(GlucoseLevel.NORMAL_MIN),
        Glucose.formatThreshold(GlucoseLevel.NORMAL_MAX)
    )

    GlucoseLevel.ELEVATED -> stringResource(
        R.string.glucose_range_between,
        Glucose.formatThreshold(GlucoseLevel.ELEVATED_MIN),
        Glucose.formatThreshold(GlucoseLevel.ELEVATED_MAX)
    )

    GlucoseLevel.DANGER -> stringResource(
        R.string.glucose_range_outside,
        Glucose.formatThreshold(GlucoseLevel.ELEVATED_MAX),
        Glucose.formatThreshold(GlucoseLevel.NORMAL_MIN)
    )
}

@Preview(name = "Сахар повышен", showBackground = true)
@Composable
private fun GlucoseSectionElevatedPreview() {
    Kotlin_projectTheme {
        GlucoseSection(glucose = GlucoseInput("12.0"), onGlucoseChange = {}, onSyncClick = {})
    }
}

@Preview(name = "Поле пустое", showBackground = true)
@Composable
private fun GlucoseSectionEmptyPreview() {
    Kotlin_projectTheme {
        GlucoseSection(glucose = GlucoseInput(), onGlucoseChange = {}, onSyncClick = {})
    }
}