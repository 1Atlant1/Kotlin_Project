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
import com.example.kotlin_project.R
import com.example.kotlin_project.ui.components.AppTopBar
import com.example.kotlin_project.ui.components.DecimalTextField
import com.example.kotlin_project.ui.components.InfoBanner
import com.example.kotlin_project.ui.components.SegmentedSelector

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
            AppTopBar(
                title = stringResource(R.string.app_name),
                subtitle = stringResource(R.string.settings_title),
                onBackClick = onBackClick
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
                    DecimalTextField(
                        value = settings.targetGlucose,
                        onValueChange = viewModel::updateTargetGlucose,
                        label = { Text(stringResource(R.string.target_glucose)) },
                        modifier = Modifier.weight(1f)
                    )
                    DecimalTextField(
                        value = settings.insulinSensitivity,
                        onValueChange = viewModel::updateInsulinSensitivity,
                        label = { Text(stringResource(R.string.insulin_sensitivity)) },
                        modifier = Modifier.weight(1f)
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
                    text = stringResource(R.string.insulin_list),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
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
                InfoBanner(text = stringResource(R.string.consult_doctor))
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
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
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
                    label = { Text(stringResource(R.string.insulin_list)) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Vaccines,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(type = ExposedDropdownMenuAnchorType.PrimaryNotEditable, enabled = true)
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
                        text = {
                            Text(
                                stringResource(R.string.add_custom_insulin),
                                color = MaterialTheme.colorScheme.primary
                            )
                        },
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
            shape = MaterialTheme.shapes.extraLarge,
            title = { Text(stringResource(R.string.add_insulin_title)) },
            text = {
                OutlinedTextField(
                    value = newInsulinName,
                    onValueChange = { newInsulinName = it },
                    label = { Text(stringResource(R.string.insulin_name_label)) },
                    singleLine = true,
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
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
    val lightLabel = stringResource(R.string.light_theme)
    val darkLabel = stringResource(R.string.dark_theme)

    SettingsOptionCard(
        icon = if (settings.isDarkTheme) Icons.Default.DarkMode else Icons.Default.LightMode,
        title = stringResource(R.string.theme_label)
    ) {
        SegmentedSelector(
            options = listOf(false, true),
            selected = settings.isDarkTheme,
            onSelect = viewModel::setDarkTheme,
            label = { isDark -> if (isDark) darkLabel else lightLabel }
        )
    }
}

@Composable
fun LanguageSelector(viewModel: SettingsViewModel) {
    val settings by viewModel.settings.collectAsState()

    SettingsOptionCard(
        icon = Icons.Default.Language,
        title = stringResource(R.string.language_label)
    ) {
        SegmentedSelector(
            options = listOf("EN", "RU"),
            selected = settings.language,
            onSelect = viewModel::updateLanguage,
            label = { it }
        )
    }
}

@Composable
private fun SettingsOptionCard(
    icon: ImageVector,
    title: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
            content()
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
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
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
