package com.example.kotlin_project.ui.screens.category

import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.kotlin_project.R
import com.example.kotlin_project.data.db.DishDao
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditDishScreen(
    dishDao: DishDao,
    dishId: Int,
    onBackClick: () -> Unit,
    onDishSaved: () -> Unit
) {
    val dish by dishDao.getDishById(dishId).collectAsState(initial = null)
    val scope = rememberCoroutineScope()

    var nameEn by remember { mutableStateOf("") }
    var nameRu by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("") }
    var isDrink by remember { mutableStateOf(false) }
    var proteinText by remember { mutableStateOf("") }
    var fatText by remember { mutableStateOf("") }
    var carbsText by remember { mutableStateOf("") }
    var caloriesText by remember { mutableStateOf("") }

    var showValidationError by remember { mutableStateOf(false) }

    LaunchedEffect(dish) {
        dish?.let {
            nameEn = it.name
            nameRu = it.nameRu
            selectedCategory = it.category
            isDrink = it.isDrink
            proteinText = it.proteinPer100g.toString()
            fatText = it.fatPer100g.toString()
            carbsText = it.carbsPer100g.toString()
            caloriesText = it.caloriesPer100g.toString()
        }
    }

    val categories = listOf("Soups", "Hot dishes", "Sweets", "Drinks", "Fruits", "Vegetables", "Dairy", "Bread & grains")
    var showCategoryDropdown by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(text = stringResource(R.string.app_name), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Text(text = stringResource(R.string.edit_dish_title), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                navigationIcon = { IconButton(onClick = onBackClick) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        bottomBar = {
            Surface(shadowElevation = 8.dp, color = MaterialTheme.colorScheme.surface) {
                Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(onClick = onBackClick, modifier = Modifier.weight(1f)) {
                        Text(stringResource(R.string.cancel))
                    }
                    Button(
                        onClick = {
                            if (nameEn.isNotBlank() && nameRu.isNotBlank() && carbsText.isNotBlank() && dish != null) {
                                val updatedDish = dish!!.copy(
                                    name = nameEn.trim(),
                                    nameRu = nameRu.trim(),
                                    category = selectedCategory,
                                    carbsPer100g = carbsText.toDoubleOrNull() ?: 0.0,
                                    proteinPer100g = proteinText.toDoubleOrNull() ?: 0.0,
                                    fatPer100g = fatText.toDoubleOrNull() ?: 0.0,
                                    caloriesPer100g = caloriesText.toDoubleOrNull() ?: 0.0,
                                    isDrink = isDrink
                                )
                                scope.launch {
                                    dishDao.insertDish(updatedDish)
                                    onDishSaved()
                                }
                            } else {
                                showValidationError = true
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text(stringResource(R.string.save))
                    }
                }
            }
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
                modifier = Modifier.fillMaxSize().padding(paddingValues).verticalScroll(rememberScrollState()).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(value = nameEn, onValueChange = { nameEn = it }, label = { Text(stringResource(R.string.dish_name_en)) }, modifier = Modifier.fillMaxWidth(), singleLine = true, isError = showValidationError && nameEn.isBlank())
                OutlinedTextField(value = nameRu, onValueChange = { nameRu = it }, label = { Text(stringResource(R.string.dish_name_ru)) }, modifier = Modifier.fillMaxWidth(), singleLine = true, isError = showValidationError && nameRu.isBlank())

                ExposedDropdownMenuBox(expanded = showCategoryDropdown, onExpandedChange = { showCategoryDropdown = !showCategoryDropdown }) {
                    OutlinedTextField(value = selectedCategory, onValueChange = {}, readOnly = true, label = { Text(stringResource(R.string.category)) }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showCategoryDropdown) }, modifier = Modifier.fillMaxWidth().menuAnchor(type = MenuAnchorType.PrimaryNotEditable, enabled = true))
                    ExposedDropdownMenu(expanded = showCategoryDropdown, onDismissRequest = { showCategoryDropdown = false }) {
                        categories.forEach { category ->
                            DropdownMenuItem(text = { Text(category) }, onClick = { selectedCategory = category; showCategoryDropdown = false })
                        }
                    }
                }

                Row(modifier = Modifier.fillMaxWidth().padding(4.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    FilterChip(selected = !isDrink, onClick = { isDrink = false }, label = { Text(stringResource(R.string.food)) }, modifier = Modifier.weight(1f))
                    FilterChip(selected = isDrink, onClick = { isDrink = true }, label = { Text(stringResource(R.string.drink)) }, modifier = Modifier.weight(1f))
                }

                Text(text = stringResource(R.string.nutrition_per_100g), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)

                OutlinedTextField(value = proteinText, onValueChange = { proteinText = it.filter { ch -> ch.isDigit() || ch == '.' } }, label = { Text(stringResource(R.string.protein)) }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), modifier = Modifier.fillMaxWidth(), singleLine = true)
                OutlinedTextField(value = fatText, onValueChange = { fatText = it.filter { ch -> ch.isDigit() || ch == '.' } }, label = { Text(stringResource(R.string.fat)) }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), modifier = Modifier.fillMaxWidth(), singleLine = true)
                OutlinedTextField(value = carbsText, onValueChange = { carbsText = it.filter { ch -> ch.isDigit() || ch == '.' } }, label = { Text("${stringResource(R.string.carbs)} *") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), modifier = Modifier.fillMaxWidth(), singleLine = true, isError = showValidationError && carbsText.isBlank(), supportingText = { if (showValidationError && carbsText.isBlank()) Text(stringResource(R.string.carbs_required)) })
                OutlinedTextField(value = caloriesText, onValueChange = { caloriesText = it.filter { ch -> ch.isDigit() || ch == '.' } }, label = { Text(stringResource(R.string.kcal)) }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), modifier = Modifier.fillMaxWidth(), singleLine = true)

                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}