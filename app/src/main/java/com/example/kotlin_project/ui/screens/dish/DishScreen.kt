package com.example.kotlin_project.ui.screens.dish

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.example.kotlin_project.ui.utils.getDisplayName
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DishScreen(
    dishDao: DishDao,
    dishId: Int,
    onBackClick: () -> Unit,
    onAddToPlate: (Dish, Int) -> Unit
) {
    val dish by dishDao.getDishById(dishId).collectAsState(initial = null)

    var weightText by remember { mutableStateOf("") }
    val weight = weightText.toDoubleOrNull() ?: 0.0

    val carbsInPortion = if (dish != null) dish!!.carbsPer100g * weight / 100.0 else 0.0
    val xe = carbsInPortion / 12.0

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(dish?.getDisplayName() ?: "Dish", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        if (dish == null) {
            Box(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
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
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                }

                NutritionCard(dish = dish!!)

                OutlinedTextField(
                    value = weightText,
                    onValueChange = { weightText = it.filter { ch -> ch.isDigit() || ch == '.' } },
                    label = { Text(stringResource(R.string.portion_weight_g)) },
                    placeholder = { Text(stringResource(R.string.portion_example)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
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

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        if (weight > 0 && dish != null) {
                            onAddToPlate(dish!!, weight.toInt())
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = weight > 0
                ) {
                    Text(stringResource(R.string.add_to_plate), fontSize = 16.sp)
                }
            }
        }
    }
}

@Composable
fun NutritionCard(dish: Dish) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = stringResource(R.string.nutrition_per_100g),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                NutritionItem(stringResource(R.string.carbs), "${dish.carbsPer100g} g")
                NutritionItem(stringResource(R.string.protein), "${dish.proteinPer100g} g")
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                NutritionItem(stringResource(R.string.fat), "${dish.fatPer100g} g")
                NutritionItem(stringResource(R.string.kcal), "${dish.caloriesPer100g}")
            }
        }
    }
}

@Composable
fun NutritionItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Text(text = label, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
fun ResultCard(weight: Double, carbs: Double, xe: Double) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.your_portion, weight.toInt()),
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.carbs_in_portion, carbs),
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = stringResource(R.string.xe_result, xe),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}