package com.example.kotlin_project.ui.screens.dish

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.kotlin_project.R
import com.example.kotlin_project.data.db.Dish
import com.example.kotlin_project.data.db.DishDao
import com.example.kotlin_project.ui.components.AppTopBar
import com.example.kotlin_project.ui.theme.DoseTextStyle
import com.example.kotlin_project.ui.theme.PillShape
import com.example.kotlin_project.ui.utils.filterDecimalInput
import com.example.kotlin_project.ui.utils.getDisplayName
import com.example.kotlin_project.ui.utils.toDecimalOrNull
import java.io.File

@Composable
fun DishScreen(
    dishDao: DishDao,
    dishId: Int,
    onBackClick: () -> Unit,
    onAddToPlate: (Dish, Int) -> Unit
) {
    val dish by dishDao.getDishById(dishId).collectAsState(initial = null)

    var weightText by remember { mutableStateOf("") }
    val weight = weightText.toDecimalOrNull() ?: 0.0

    val carbsInPortion = if (dish != null) dish!!.carbsPer100g * weight / 100.0 else 0.0
    val xe = carbsInPortion / 12.0

    Scaffold(
        topBar = {
            AppTopBar(
                title = stringResource(R.string.app_name),
                subtitle = dish?.getDisplayName(),
                onBackClick = onBackClick
            )
        },
        bottomBar = {
            if (dish != null) {
                Button(
                    onClick = {
                        if (weight > 0 && dish != null) {
                            onAddToPlate(dish!!, weight.toInt())
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(56.dp),
                    enabled = weight > 0,
                    shape = PillShape
                ) {
                    Icon(imageVector = Icons.Default.AddShoppingCart, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        stringResource(R.string.add_to_plate),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    ) { paddingValues ->
        if (dish == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (!dish!!.imagePath.isNullOrBlank()) {
                    AsyncImage(
                        model = File(dish!!.imagePath!!),
                        contentDescription = dish!!.getDisplayName(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(MaterialTheme.shapes.large),
                        contentScale = ContentScale.Crop
                    )
                }

                Text(
                    text = dish!!.getDisplayName(),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                NutritionCard(dish = dish!!)

                OutlinedTextField(
                    value = weightText,
                    onValueChange = { weightText = it.filterDecimalInput() },
                    label = { Text(stringResource(R.string.portion_weight_g)) },
                    placeholder = { Text(stringResource(R.string.portion_example)) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Scale,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                if (weight > 0) {
                    ResultCard(
                        weight = weight,
                        carbs = carbsInPortion,
                        xe = xe
                    )
                }
            }
        }
    }
}

@Composable
fun NutritionCard(dish: Dish) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = stringResource(R.string.nutrition_per_100g),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                NutritionItem(
                    label = stringResource(R.string.carbs),
                    value = "${dish.carbsPer100g} g",
                    highlighted = true,
                    modifier = Modifier.weight(1f)
                )
                NutritionItem(
                    label = stringResource(R.string.protein),
                    value = "${dish.proteinPer100g} g",
                    modifier = Modifier.weight(1f)
                )
                NutritionItem(
                    label = stringResource(R.string.fat),
                    value = "${dish.fatPer100g} g",
                    modifier = Modifier.weight(1f)
                )
                NutritionItem(
                    label = stringResource(R.string.kcal),
                    value = "${dish.caloriesPer100g}",
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun NutritionItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    highlighted: Boolean = false
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        color = if (highlighted) {
            MaterialTheme.colorScheme.primaryContainer
        } else {
            MaterialTheme.colorScheme.surfaceVariant
        }
    ) {
        Column(
            modifier = Modifier.padding(vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = if (highlighted) {
                    MaterialTheme.colorScheme.onPrimaryContainer
                } else {
                    MaterialTheme.colorScheme.onSurface
                }
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun ResultCard(weight: Double, carbs: Double, xe: Double) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.your_portion_label),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.your_portion, weight.toInt()),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.xe_result, xe),
                style = DoseTextStyle,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = stringResource(R.string.carbs_in_portion, carbs),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}
