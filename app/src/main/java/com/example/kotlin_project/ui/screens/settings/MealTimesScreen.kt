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
fun MealTimesScreen(
    viewModel: SettingsViewModel,
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
                            text = stringResource(R.string.meal_times_title),
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
        },
        bottomBar = {
            Button(
                onClick = {
                    viewModel.saveSettings()
                    onBackClick()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(56.dp),
                shape = MaterialTheme.shapes.large
            ) {
                Text(stringResource(R.string.save), fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp)
        ) {
            item {
                Text(
                    text = stringResource(R.string.meal_times_subtitle),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            val meals = listOf("Breakfast", "Lunch", "Dinner")
            val mealResIds = mapOf(
                "Breakfast" to R.string.breakfast,
                "Lunch" to R.string.lunch,
                "Dinner" to R.string.dinner
            )
            val mealIcons = mapOf(
                "Breakfast" to Icons.Default.WbSunny,
                "Lunch" to Icons.Default.WbSunny,
                "Dinner" to Icons.Default.NightsStay
            )

            items(meals) { meal ->
                MealTimeCard(
                    mealResId = mealResIds[meal] ?: R.string.breakfast,
                    icon = mealIcons[meal] ?: Icons.Default.Schedule,
                    startTime = settings.mealTimes[meal]?.first ?: "06:00",
                    endTime = settings.mealTimes[meal]?.second ?: "11:00",
                    onStartChange = { start ->
                        viewModel.updateMealTime(
                            meal,
                            start,
                            settings.mealTimes[meal]?.second ?: "11:00"
                        )
                    },
                    onEndChange = { end ->
                        viewModel.updateMealTime(
                            meal,
                            settings.mealTimes[meal]?.first ?: "06:00",
                            end
                        )
                    }
                )
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
                            text = stringResource(R.string.time_selects_factor),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MealTimeCard(
    mealResId: Int,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    startTime: String,
    endTime: String,
    onStartChange: (String) -> Unit,
    onEndChange: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
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
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = stringResource(mealResId),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = "$startTime-$endTime",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                TimeInputField(
                    label = stringResource(R.string.start_time),
                    value = startTime,
                    onValueChange = onStartChange,
                    modifier = Modifier.weight(1f)
                )
                TimeInputField(
                    label = stringResource(R.string.end_time),
                    value = endTime,
                    onValueChange = onEndChange,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun TimeInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = { newValue ->
            val filtered = newValue.filter { it.isDigit() || it == ':' }
            if (filtered.length <= 5) {
                onValueChange(filtered)
            }
        },
        label = { Text(label) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = modifier,
        singleLine = true,
        trailingIcon = {
            Icon(Icons.Default.Schedule, contentDescription = null)
        }
    )
}