package com.example.kotlin_project.ui.screens.dish

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kotlin_project.data.db.Dish
import com.example.kotlin_project.data.db.DishDao
import androidx.compose.runtime.collectAsState
import androidx.compose.foundation.text.KeyboardOptions


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DishScreen(
    dishDao: DishDao,
    dishId: Int,
    onBackClick: () -> Unit,
    onAddToPlate: (Dish, Int) -> Unit
) {
    // Загружаем блюдо из БД
    val dish by dishDao.getDishById(dishId).collectAsState(initial = null)

    // Состояние поля ввода веса
    var weightText by remember { mutableStateOf("") }
    val weight = weightText.toDoubleOrNull() ?: 0.0

    // Расчёты
    val carbsInPortion = if (dish != null) dish!!.carbsPer100g * weight / 100.0 else 0.0
    val xe = carbsInPortion / 12.0

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(dish?.name ?: "Блюдо", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
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

                NutritionCard(dish = dish!!)


                OutlinedTextField(
                    value = weightText,
                    onValueChange = { weightText = it.filter { ch -> ch.isDigit() || ch == '.' } },
                    label = { Text("Вес порции (г)") },
                    placeholder = { Text("Например, 250") },
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
                    Text("Добавить в тарелку", fontSize = 16.sp)
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
                text = "Пищевая ценность на 100 г",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                NutritionItem("Углеводы", "${dish.carbsPer100g} г")
                NutritionItem("Белки", "${dish.proteinPer100g} г")
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                NutritionItem("Жиры", "${dish.fatPer100g} г")
                NutritionItem("Ккал", "${dish.caloriesPer100g}")
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
                text = "Ваша порция: ${weight.toInt()} г",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Углеводы: ${String.format("%.1f", carbs)} г",
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = String.format("%.2f ХЕ", xe),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}