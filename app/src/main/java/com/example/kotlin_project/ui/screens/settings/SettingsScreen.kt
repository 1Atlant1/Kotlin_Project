package com.example.kotlin_project.ui.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kotlin_project.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onMealTimesClick: () -> Unit,
    onCarbRatiosClick: () -> Unit,
    onBackClick: () -> Unit
) {
    val settings by viewModel.settings.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = stringResource(R.string.app_name),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = stringResource(R.string.settings_title),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Text("←", fontSize = 24.sp)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp)
        ) {

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = settings.targetGlucose.toString(),
                        onValueChange = { newText ->
                            val newValue = newText.toDoubleOrNull()
                            if (newValue != null) {
                                viewModel.updateTargetGlucose(newValue)
                            }
                        },
                        label = { Text(stringResource(R.string.target_glucose)) },
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                            keyboardType = androidx.compose.ui.text.input.KeyboardType.Decimal
                        ),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = settings.insulinSensitivity.toString(),
                        onValueChange = { newText ->
                            val newValue = newText.toDoubleOrNull()
                            if (newValue != null) {
                                viewModel.updateInsulinSensitivity(newValue)
                            }
                        },
                        label = { Text(stringResource(R.string.insulin_sensitivity)) },
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                            keyboardType = androidx.compose.ui.text.input.KeyboardType.Decimal
                        ),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }
            }

            item {
                SettingsNavigationCard(
                    icon = Icons.Default.Schedule,
                    title = stringResource(R.string.meal_times),
                    subtitle = formatMealTimesLocalized(settings.mealTimes),
                    onClick = onMealTimesClick
                )
            }

            item {
                SettingsNavigationCard(
                    icon = Icons.Default.Calculate,
                    title = stringResource(R.string.carb_ratios),
                    subtitle = formatCarbRatiosLocalized(settings.carbRatios),
                    onClick = onCarbRatiosClick
                )
            }

            item {
                Text(
                    text = stringResource(R.string.selected_insulin),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            item {
                InsulinSelector(viewModel = viewModel)
            }


            item {
                ThemeSelector(viewModel = viewModel)
            }

            item {
                LanguageSelector(viewModel = viewModel)
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = stringResource(R.string.consult_doctor),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InsulinSelector(viewModel: SettingsViewModel) {
    val settings by viewModel.settings.collectAsState()
    var expanded by remember { mutableStateOf(false) }
    var showAddDialog by remember { mutableStateOf(false) }
    var newInsulinName by remember { mutableStateOf("") }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = settings.selectedInsulin,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(R.string.selected_insulin)) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(type = MenuAnchorType.PrimaryNotEditable, enabled = true)
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    settings.availableInsulins.forEach { insulin ->
                        DropdownMenuItem(
                            text = { Text(insulin) },
                            onClick = {
                                viewModel.updateSelectedInsulin(insulin)
                                expanded = false
                            },
                            leadingIcon = {
                                if (settings.selectedInsulin == insulin) {
                                    Icon(Icons.Default.Check, contentDescription = null)
                                }
                            }
                        )
                    }
                    HorizontalDivider()
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.add_custom_insulin), color = MaterialTheme.colorScheme.primary) },
                        onClick = {
                            expanded = false
                            showAddDialog = true
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Add, contentDescription = null)
                        }
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text(stringResource(R.string.add_insulin_title)) },
            text = {
                OutlinedTextField(
                    value = newInsulinName,
                    onValueChange = { newInsulinName = it },
                    label = { Text(stringResource(R.string.insulin_name_label)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.addCustomInsulin(newInsulinName.trim())
                        newInsulinName = ""
                        showAddDialog = false
                    },
                    enabled = newInsulinName.isNotBlank()
                ) {
                    Text(stringResource(R.string.add))
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showAddDialog = false
                    newInsulinName = ""
                }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}

@Composable
fun ThemeSelector(viewModel: SettingsViewModel) {
    val settings by viewModel.settings.collectAsState()

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(stringResource(R.string.theme_label), style = MaterialTheme.typography.bodyLarge)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = !settings.isDarkTheme,
                    onClick = { viewModel.setDarkTheme(false) },
                    label = { Text(stringResource(R.string.light_theme)) },
                    leadingIcon = {
                        if (!settings.isDarkTheme) Icon(Icons.Default.Check, null, modifier = Modifier.size(18.dp))
                    }
                )
                FilterChip(
                    selected = settings.isDarkTheme,
                    onClick = { viewModel.setDarkTheme(true) },
                    label = { Text(stringResource(R.string.dark_theme)) },
                    leadingIcon = {
                        if (settings.isDarkTheme) Icon(Icons.Default.Check, null, modifier = Modifier.size(18.dp))
                    }
                )
            }
        }
    }
}

@Composable
fun LanguageSelector(viewModel: SettingsViewModel) {
    val settings by viewModel.settings.collectAsState()

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(stringResource(R.string.language_label), style = MaterialTheme.typography.bodyLarge)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = settings.language == "EN",
                    onClick = { viewModel.updateLanguage("EN") },
                    label = { Text("EN") },
                    leadingIcon = {
                        if (settings.language == "EN") Icon(Icons.Default.Check, null, modifier = Modifier.size(18.dp))
                    }
                )
                FilterChip(
                    selected = settings.language == "RU",
                    onClick = { viewModel.updateLanguage("RU") },
                    label = { Text("RU") },
                    leadingIcon = {
                        if (settings.language == "RU") Icon(Icons.Default.Check, null, modifier = Modifier.size(18.dp))
                    }
                )
            }
        }
    }
}

@Composable
fun SettingsNavigationCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun formatMealTimesLocalized(mealTimes: Map<String, Pair<String, String>>): String {

    val breakfastName = stringResource(R.string.breakfast)
    val lunchName = stringResource(R.string.lunch)
    val dinnerName = stringResource(R.string.dinner)
    val mealNames = mapOf(
        "Breakfast" to breakfastName,
        "Lunch" to lunchName,
        "Dinner" to dinnerName
    )
    return mealTimes.entries.joinToString(", ") { (meal, times) ->
        val name = mealNames[meal] ?: meal
        "$name: ${times.first}-${times.second}"
    }
}


@Composable
fun formatCarbRatiosLocalized(carbRatios: Map<String, Double>): String {

    val breakfastName = stringResource(R.string.breakfast)
    val lunchName = stringResource(R.string.lunch)
    val dinnerName = stringResource(R.string.dinner)
    val unitString = stringResource(R.string.u_xe_unit)

    val mealNames = mapOf(
        "Breakfast" to breakfastName,
        "Lunch" to lunchName,
        "Dinner" to dinnerName
    )

    return mealNames.entries.joinToString(", ") { (mealKey, mealName) ->
        val value = carbRatios[mealKey] ?: 1.5
        "$mealName: $value $unitString"
    }
}