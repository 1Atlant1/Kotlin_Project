package com.example.kotlin_project.ui.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kotlin_project.R
import com.example.kotlin_project.ui.components.AppTopBar
import com.example.kotlin_project.ui.components.DecimalTextField
import com.example.kotlin_project.ui.components.InfoBanner
import com.example.kotlin_project.ui.theme.PillShape
import com.example.kotlin_project.ui.theme.extendedColors

@Composable
fun CarbRatiosScreen(
    viewModel: SettingsViewModel,
    onBackClick: () -> Unit
) {
    val settings by viewModel.settings.collectAsState()
    val normalColor = MaterialTheme.colorScheme.primary
    val normalContainer = MaterialTheme.colorScheme.primaryContainer
    val sportsColor = MaterialTheme.extendedColors.sports
    val sportsContainer = MaterialTheme.extendedColors.sportsContainer

    Scaffold(
        topBar = {
            AppTopBar(
                title = stringResource(R.string.app_name),
                subtitle = stringResource(R.string.carb_ratios_title),
                onBackClick = onBackClick
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
                shape = PillShape
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
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp)
        ) {
            item {
                Text(
                    text = stringResource(R.string.carb_ratios_subtitle),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            item {
                RatioGroupHeader(
                    icon = Icons.Default.Restaurant,
                    title = stringResource(R.string.normal_mode),
                    color = normalColor,
                    containerColor = normalContainer
                )
            }

            val normalMeals = listOf(
                Triple("Breakfast", R.string.breakfast, 1.5),
                Triple("Lunch", R.string.lunch, 1.3),
                Triple("Dinner", R.string.dinner, 1.4)
            )
            items(normalMeals) { (mealKey, mealResId, defaultValue) ->
                CarbRatioRow(
                    mealResId = mealResId,
                    value = settings.carbRatios[mealKey] ?: defaultValue,
                    onValueChange = { viewModel.updateCarbRatio(mealKey, it) },
                    accentColor = normalColor
                )
            }

            item {
                Spacer(modifier = Modifier.height(4.dp))
                RatioGroupHeader(
                    icon = Icons.Default.FitnessCenter,
                    title = stringResource(R.string.sports_mode_label),
                    color = sportsColor,
                    containerColor = sportsContainer
                )
            }

            val sportMeals = listOf(
                Triple("Breakfast_sport", R.string.breakfast, 1.0),
                Triple("Lunch_sport", R.string.lunch, 1.0),
                Triple("Dinner_sport", R.string.dinner, 1.1)
            )
            items(sportMeals) { (mealKey, mealResId, defaultValue) ->
                CarbRatioRow(
                    mealResId = mealResId,
                    value = settings.carbRatios[mealKey] ?: defaultValue,
                    onValueChange = { viewModel.updateCarbRatio(mealKey, it) },
                    accentColor = sportsColor
                )
            }

            item {
                InfoBanner(text = stringResource(R.string.sports_values_hint))
            }
        }
    }
}

@Composable
private fun RatioGroupHeader(
    icon: ImageVector,
    title: String,
    color: Color,
    containerColor: Color
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Surface(
            shape = MaterialTheme.shapes.small,
            color = containerColor,
            modifier = Modifier.size(36.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = stringResource(R.string.u_xe_unit),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun CarbRatioRow(
    mealResId: Int,
    value: Double,
    onValueChange: (Double) -> Unit,
    accentColor: Color = MaterialTheme.colorScheme.primary
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(mealResId),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )
            DecimalTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.width(100.dp),
                textStyle = LocalTextStyle.current.copy(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = accentColor
                )
            )
        }
    }
}
