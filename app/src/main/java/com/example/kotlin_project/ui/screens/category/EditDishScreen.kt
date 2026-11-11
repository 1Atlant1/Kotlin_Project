package com.example.kotlin_project.ui.screens.category

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.kotlin_project.R
import com.example.kotlin_project.data.db.DishDao
import com.example.kotlin_project.ui.components.AppTopBar
import com.example.kotlin_project.ui.utils.ImageHelper
import com.example.kotlin_project.ui.utils.getCategoryDisplayName
import com.example.kotlin_project.ui.utils.toDecimalOrNull
import kotlinx.coroutines.launch
import java.io.File

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
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var showValidationError by remember { mutableStateOf(false) }
    var showCategoryDropdown by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val imagePath = ImageHelper.copyImageToInternalStorage(context, it)
            selectedImageUri = imagePath?.let { path -> Uri.fromFile(File(path)) }
        }
    }

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
            if (!it.imagePath.isNullOrBlank()) {
                selectedImageUri = Uri.parse(it.imagePath)
            }
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            AppTopBar(
                title = stringResource(R.string.app_name),
                subtitle = stringResource(R.string.edit_dish_title),
                onBackClick = onBackClick
            )
        },
        bottomBar = {
            DishFormBottomBar(
                onCancel = onBackClick,
                onSave = {
                    val currentDish = dish
                    if (nameEn.isNotBlank() && nameRu.isNotBlank() && carbsText.toDecimalOrNull() != null && currentDish != null) {
                        if (currentDish.imagePath != selectedImageUri?.path) {
                            ImageHelper.deleteImage(context, currentDish.imagePath)
                        }
                        val updatedDish = currentDish.copy(
                            name = nameEn.trim(),
                            nameRu = nameRu.trim(),
                            category = selectedCategory,
                            carbsPer100g = carbsText.toDecimalOrNull() ?: 0.0,
                            proteinPer100g = proteinText.toDecimalOrNull() ?: 0.0,
                            fatPer100g = fatText.toDecimalOrNull() ?: 0.0,
                            caloriesPer100g = caloriesText.toDecimalOrNull() ?: 0.0,
                            isDrink = isDrink,
                            imagePath = selectedImageUri?.path
                        )
                        scope.launch {
                            dishDao.insertDish(updatedDish)
                            onDishSaved()
                        }
                    } else {
                        showValidationError = true
                    }
                }
            )
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
                DishPhotoPicker(
                    imageModel = selectedImageUri,
                    onClick = { imagePickerLauncher.launch("image/*") }
                )

                OutlinedTextField(
                    value = nameEn,
                    onValueChange = { nameEn = it },
                    label = { Text(stringResource(R.string.dish_name_en)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = MaterialTheme.shapes.medium,
                    isError = showValidationError && nameEn.isBlank()
                )
                OutlinedTextField(
                    value = nameRu,
                    onValueChange = { nameRu = it },
                    label = { Text(stringResource(R.string.dish_name_ru)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = MaterialTheme.shapes.medium,
                    isError = showValidationError && nameRu.isBlank()
                )

                ExposedDropdownMenuBox(
                    expanded = showCategoryDropdown,
                    onExpandedChange = { showCategoryDropdown = !showCategoryDropdown }
                ) {
                    OutlinedTextField(
                        value = getCategoryDisplayName(selectedCategory),
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(stringResource(R.string.category)) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showCategoryDropdown) },
                        shape = MaterialTheme.shapes.medium,
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(
                                type = ExposedDropdownMenuAnchorType.PrimaryNotEditable,
                                enabled = true
                            )
                    )
                    ExposedDropdownMenu(
                        expanded = showCategoryDropdown,
                        onDismissRequest = { showCategoryDropdown = false }
                    ) {
                        dishCategories.forEach { category ->
                            DropdownMenuItem(
                                text = { Text(getCategoryDisplayName(category)) },
                                onClick = {
                                    selectedCategory = category
                                    showCategoryDropdown = false
                                }
                            )
                        }
                    }
                }

                DishTypeSelector(
                    isDrink = isDrink,
                    onTypeChange = { isDrink = it }
                )

                NutritionInputs(
                    proteinText = proteinText,
                    onProteinChange = { proteinText = it },
                    fatText = fatText,
                    onFatChange = { fatText = it },
                    carbsText = carbsText,
                    onCarbsChange = { carbsText = it },
                    caloriesText = caloriesText,
                    onCaloriesChange = { caloriesText = it },
                    carbsError = showValidationError && carbsText.toDecimalOrNull() == null
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
