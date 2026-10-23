package com.example.kotlin_project.ui.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kotlin_project.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarbRatiosScreen(
    viewModel: SettingsViewModel,
    onBackClick: () -> Unit
) {
    val settings by viewModel.settings.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(text = stringResource(R.string.app_name), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Text(text = stringResource(R.string.carb_ratios_title), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                navigationIcon = { IconButton(onClick = onBackClick) { Text("←", fontSize = 24.sp) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        bottomBar = {
            Button(
                onClick = { viewModel.saveSettings(); onBackClick() },
                modifier = Modifier.fillMaxWidth().padding(16.dp).height(56.dp),
                shape = MaterialTheme.shapes.large
            ) {
                Text(stringResource(R.string.save), fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp)
        ) {
            item {
                Text(text = stringResource(R.string.carb_ratios_subtitle), style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Restaurant, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = stringResource(R.string.normal_mode), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.weight(1f))
                    Text(text = stringResource(R.string.u_xe_unit), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            val normalMeals = listOf(
                Triple("Breakfast", R.string.breakfast, 1.5),
                Triple("Lunch", R.string.lunch, 1.3),
                Triple("Dinner", R.string.dinner, 1.4)
            )
            items(normalMeals) { (mealKey, mealResId, defaultValue) ->
                CarbRatioRow(
                    mealResId = mealResId,
                    value = settings.carbRatios[mealKey]?.toString() ?: defaultValue.toString(),
                    onValueChange = { newValue ->
                        val doubleValue = newValue.toDoubleOrNull() ?: defaultValue
                        viewModel.updateCarbRatio(mealKey, doubleValue)
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.FitnessCenter, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = stringResource(R.string.sports_mode_label), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.weight(1f))
                    Text(text = stringResource(R.string.u_xe_unit), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            val sportMeals = listOf(
                Triple("Breakfast_sport", R.string.breakfast, 1.0),
                Triple("Lunch_sport", R.string.lunch, 1.0),
                Triple("Dinner_sport", R.string.dinner, 1.1)
            )
            items(sportMeals) { (mealKey, mealResId, defaultValue) ->
                CarbRatioRow(
                    mealResId = mealResId,
                    value = settings.carbRatios[mealKey]?.toString() ?: defaultValue.toString(),
                    onValueChange = { newValue ->
                        val doubleValue = newValue.toDoubleOrNull() ?: defaultValue
                        viewModel.updateCarbRatio(mealKey, doubleValue)
                    }
                )
            }

            item {
                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(text = stringResource(R.string.sports_values_hint), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSecondaryContainer)
                    }
                }
            }
        }
    }
}

@Composable
fun CarbRatioRow(
    mealResId: Int,
    value: String,
    onValueChange: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = stringResource(mealResId), style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.width(100.dp),
            singleLine = true,
            textStyle = LocalTextStyle.current.copy(fontSize = 18.sp, fontWeight = FontWeight.Bold)
        )
    }
}