package com.example.kotlin_project.ui.screens.category

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.kotlin_project.R
import com.example.kotlin_project.ui.components.SegmentedSelector
import com.example.kotlin_project.ui.components.dashedBorder
import com.example.kotlin_project.ui.theme.PillShape
import com.example.kotlin_project.ui.utils.filterDecimalInput

val dishCategories = listOf(
    "Soups",
    "Hot dishes",
    "Sweets",
    "Drinks",
    "Fruits",
    "Vegetables",
    "Dairy",
    "Bread & grains"
)

@Composable
fun FormSectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground
    )
}

@Composable
fun DishPhotoPicker(
    imageModel: Any?,
    onClick: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        FormSectionTitle(stringResource(R.string.photo_label))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(MaterialTheme.shapes.large)
                .background(MaterialTheme.colorScheme.surface)
                .dashedBorder(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f))
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            if (imageModel != null) {
                AsyncImage(
                    model = imageModel,
                    contentDescription = stringResource(R.string.photo_label),
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(MaterialTheme.shapes.large),
                    contentScale = ContentScale.Crop
                )
            } else {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.size(56.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.AddAPhoto,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.add_photo_hint),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun DishTypeSelector(
    isDrink: Boolean,
    onTypeChange: (Boolean) -> Unit
) {
    val foodLabel = stringResource(R.string.food)
    val drinkLabel = stringResource(R.string.drink)
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        FormSectionTitle(stringResource(R.string.dish_type_label))
        SegmentedSelector(
            options = listOf(false, true),
            selected = isDrink,
            onSelect = onTypeChange,
            label = { if (it) drinkLabel else foodLabel },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun NutritionInputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    supportingText: String? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = { onValueChange(it.filterDecimalInput()) },
        label = { Text(label) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        modifier = modifier,
        singleLine = true,
        shape = MaterialTheme.shapes.medium,
        isError = isError,
        supportingText = supportingText?.let { { Text(it) } }
    )
}

@Composable
fun NutritionInputs(
    proteinText: String,
    onProteinChange: (String) -> Unit,
    fatText: String,
    onFatChange: (String) -> Unit,
    carbsText: String,
    onCarbsChange: (String) -> Unit,
    caloriesText: String,
    onCaloriesChange: (String) -> Unit,
    carbsError: Boolean
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        FormSectionTitle(stringResource(R.string.nutrition_per_100g))
        NutritionInputField(
            value = carbsText,
            onValueChange = onCarbsChange,
            label = stringResource(R.string.carbs) + " *",
            modifier = Modifier.fillMaxWidth(),
            isError = carbsError,
            supportingText = stringResource(R.string.carbs_required)
        )
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            NutritionInputField(
                value = proteinText,
                onValueChange = onProteinChange,
                label = stringResource(R.string.protein),
                modifier = Modifier.weight(1f)
            )
            NutritionInputField(
                value = fatText,
                onValueChange = onFatChange,
                label = stringResource(R.string.fat),
                modifier = Modifier.weight(1f)
            )
        }
        NutritionInputField(
            value = caloriesText,
            onValueChange = onCaloriesChange,
            label = stringResource(R.string.kcal),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun DishFormBottomBar(
    onCancel: () -> Unit,
    onSave: () -> Unit
) {
    Surface(
        shadowElevation = 8.dp,
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onCancel,
                shape = PillShape,
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
            ) {
                Text(stringResource(R.string.cancel))
            }
            Button(
                onClick = onSave,
                shape = PillShape,
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.save),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
