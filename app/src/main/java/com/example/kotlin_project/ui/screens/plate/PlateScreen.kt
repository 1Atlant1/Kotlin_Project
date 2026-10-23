package com.example.kotlin_project.ui.screens.plate

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kotlin_project.R
import com.example.kotlin_project.domain.model.PlateItem
import com.example.kotlin_project.ui.screens.settings.SettingsViewModel
import com.example.kotlin_project.ui.theme.GlucoseDanger
import com.example.kotlin_project.ui.theme.GlucoseNormal
import com.example.kotlin_project.ui.theme.GlucoseWarning
import com.example.kotlin_project.ui.theme.WarningOrange
import com.example.kotlin_project.ui.theme.WarningOrangeText
import com.example.kotlin_project.ui.utils.getDisplayName

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlateScreen(
    viewModel: PlateViewModel,
    settingsViewModel: SettingsViewModel,
    onBackClick: () -> Unit
) {
    val plateItems by viewModel.plateItems.collectAsState()
    val glucoseText by viewModel.glucose.collectAsState()
    val mealPeriod by viewModel.mealPeriod.collectAsState()
    val isSportsMode by viewModel.isSportsMode.collectAsState()
    val selectedInsulin by viewModel.selectedInsulin.collectAsState()
    val settings by settingsViewModel.settings.collectAsState()

    var doseResult by remember { mutableStateOf<com.example.kotlin_project.domain.calculator.DoseResult?>(null) }
    var insulinExpanded by remember { mutableStateOf(false) }
    var periodExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(text = stringResource(R.string.app_name), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Text(text = stringResource(R.string.my_plate_title), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                navigationIcon = { IconButton(onClick = onBackClick) { Text("←", fontSize = 24.sp) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp)
        ) {
            if (plateItems.isNotEmpty()) {
                val totalCarbs = plateItems.sumOf { it.carbsInPortion }
                val totalXE = plateItems.sumOf { it.xe }
                item {
                    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Column {
                                Text(text = "${String.format("%.0f", totalCarbs)}g carbs = ${String.format("%.2f", totalXE)} XE", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                Text(text = "${plateItems.size} dishes", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }

            if (plateItems.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
                        Text(stringResource(R.string.plate_empty), color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            } else {
                itemsIndexed(plateItems) { index, item ->
                    PlateItemCard(item = item, onRemove = { viewModel.removeDishFromPlate(index) })
                }
            }

            item {
                OutlinedTextField(
                    value = glucoseText,
                    onValueChange = { viewModel.updateGlucose(it) },
                    label = { Text(stringResource(R.string.glucose_label)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    trailingIcon = { TextButton(onClick = { }) { Text(stringResource(R.string.sync_cgm), fontSize = 12.sp) } }
                )
            }

            item {
                val glucoseValue = glucoseText.toDoubleOrNull()
                val glucoseColor = when {
                    glucoseValue == null -> MaterialTheme.colorScheme.onSurfaceVariant
                    glucoseValue < 4.0 || glucoseValue > 13.9 -> GlucoseDanger
                    glucoseValue in 4.0..10.0 -> GlucoseNormal
                    else -> GlucoseWarning
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    GlucoseLegendItem(color = GlucoseNormal, range = stringResource(R.string.glucose_range_normal))
                    GlucoseLegendItem(color = GlucoseWarning, range = stringResource(R.string.glucose_range_warning))
                    GlucoseLegendItem(color = GlucoseDanger, range = stringResource(R.string.glucose_range_danger))
                    Spacer(modifier = Modifier.width(8.dp))
                    if (glucoseValue != null) {
                        Text(text = String.format("%.1f", glucoseValue), color = glucoseColor, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(text = stringResource(R.string.glucose_current), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(start = 4.dp))
                    }
                }
            }

            item {
                val insulinOptions = listOf("NovoRapid", "Humalog", "Apidra")
                ExposedDropdownMenuBox(expanded = insulinExpanded, onExpandedChange = { insulinExpanded = !insulinExpanded }) {
                    OutlinedTextField(value = selectedInsulin, onValueChange = {}, readOnly = true, label = { Text(stringResource(R.string.insulin_label)) }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = insulinExpanded) }, modifier = Modifier.fillMaxWidth().menuAnchor(type = MenuAnchorType.PrimaryNotEditable, enabled = true))
                    ExposedDropdownMenu(expanded = insulinExpanded, onDismissRequest = { insulinExpanded = false }) {
                        insulinOptions.forEach { insulin ->
                            DropdownMenuItem(text = { Text(insulin) }, onClick = { viewModel.updateSelectedInsulin(insulin); insulinExpanded = false })
                        }
                    }
                }
            }
            item {
                val periods = listOf(
                    "Breakfast" to R.string.breakfast,
                    "Lunch" to R.string.lunch,
                    "Dinner" to R.string.dinner
                )
                val currentPeriodResId = periods.find { it.first == mealPeriod }?.second ?: R.string.lunch

                ExposedDropdownMenuBox(expanded = periodExpanded, onExpandedChange = { periodExpanded = !periodExpanded }) {
                    OutlinedTextField(
                        value = stringResource(currentPeriodResId),
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(stringResource(R.string.meal_period)) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = periodExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(type = MenuAnchorType.PrimaryNotEditable, enabled = true)
                    )
                    ExposedDropdownMenu(expanded = periodExpanded, onDismissRequest = { periodExpanded = false }) {
                        periods.forEach { (key, resId) ->
                            DropdownMenuItem(
                                text = { Text(stringResource(resId)) },
                                onClick = { viewModel.updateMealPeriod(key); periodExpanded = false }
                            )
                        }
                    }
                }
            }

            item {
                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                    Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "⚡", fontSize = 20.sp, modifier = Modifier.padding(end = 8.dp))
                            Text(stringResource(R.string.sports_mode), style = MaterialTheme.typography.bodyLarge)
                        }
                        Switch(checked = isSportsMode, onCheckedChange = { viewModel.toggleSportsMode() })
                    }
                }
            }

            item {
                Button(
                    onClick = {
                        val result = viewModel.calculateDose(
                            targetGlucose = settings.targetGlucose,
                            insulinSensitivity = settings.insulinSensitivity
                        )
                        doseResult = result
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    enabled = plateItems.isNotEmpty() && glucoseText.isNotEmpty(),
                    shape = MaterialTheme.shapes.large
                ) {
                    Text(stringResource(R.string.calculate_dose), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }

            doseResult?.let { result ->
                item {
                    if (result.isLowGlucose) {
                        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = WarningOrange)) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(text = stringResource(R.string.warning_title), fontWeight = FontWeight.Bold, color = WarningOrangeText)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = result.warning ?: "", color = WarningOrangeText)
                            }
                        }
                    } else {
                        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                Text(text = stringResource(R.string.meal_bolus, result.mealBolus), style = MaterialTheme.typography.bodyLarge)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = stringResource(R.string.correction, result.correction), style = MaterialTheme.typography.bodyLarge)
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(text = stringResource(R.string.total_dose, result.totalDose), style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontSize = 32.sp)
                            }
                        }
                    }
                }
            }

            if (doseResult != null && !doseResult!!.isLowGlucose) {
                item {
                    Button(
                        onClick = { viewModel.clearPlate(); doseResult = null },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = MaterialTheme.shapes.large,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text(stringResource(R.string.confirm_injection), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun GlucoseLegendItem(color: Color, range: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(color))
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = range, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun PlateItemCard(item: PlateItem, onRemove: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)) {
        Row(modifier = Modifier.fillMaxWidth().padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = item.dish.getDisplayName(), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(text = "${item.weightGrams}g • ${String.format("%.0f", item.carbsInPortion)}g carbs", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Text(text = String.format("%.2f XE", item.xe), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(end = 8.dp))
            IconButton(onClick = onRemove) {
                Icon(Icons.Default.Delete, contentDescription = "Remove", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}