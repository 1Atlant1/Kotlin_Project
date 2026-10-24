package com.example.kotlin_project.ui.screens.category

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kotlin_project.R
import com.example.kotlin_project.data.db.Dish
import com.example.kotlin_project.data.db.DishDao
import com.example.kotlin_project.ui.theme.InfoGreen
import com.example.kotlin_project.ui.theme.InfoGreenText
import com.example.kotlin_project.ui.utils.getDisplayName
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryScreen(
    dishDao: DishDao,
    categoryName: String,
    onBackClick: () -> Unit,
    onDishClick: (Int) -> Unit,
    onAddDishClick: () -> Unit,
    onEditDishClick: (Int) -> Unit = {}
) {
    val dishes by dishDao.getDishesByCategory(categoryName)
        .collectAsState(initial = emptyList())

    var searchQuery by remember { mutableStateOf("") }
    val filteredDishes = dishes.filter {
        it.name.contains(searchQuery, ignoreCase = true) ||
                it.nameRu.contains(searchQuery, ignoreCase = true)
    }

    var selectedDish by remember { mutableStateOf<Dish?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = categoryName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddDishClick,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Add dish",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text(stringResource(R.string.search_dish)) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = MaterialTheme.shapes.extraLarge,
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                    focusedBorderColor = MaterialTheme.colorScheme.primary
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${dishes.size} dishes",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = stringResource(R.string.values_per_100g),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (filteredDishes.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        stringResource(R.string.no_dishes_found),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(filteredDishes) { dish ->
                        DishItemCard(
                            dish = dish,
                            onClick = { onDishClick(dish.id) },
                            onLongClick = {
                                selectedDish = dish
                                showEditDialog = true
                            }
                        )
                    }
                }
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 80.dp),
                colors = CardDefaults.cardColors(containerColor = InfoGreen)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = InfoGreenText,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.select_dish_hint),
                        color = InfoGreenText,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }

    if (showEditDialog && selectedDish != null) {
        AlertDialog(
            onDismissRequest = {
                showEditDialog = false
                selectedDish = null
            },
            title = { Text(selectedDish!!.getDisplayName()) },
            text = { Text(stringResource(R.string.dish_actions_hint)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showEditDialog = false
                        onEditDishClick(selectedDish!!.id)
                    }
                ) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(stringResource(R.string.edit))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showEditDialog = false
                        showDeleteDialog = true
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(stringResource(R.string.delete))
                }
            }
        )
    }

    if (showDeleteDialog && selectedDish != null) {
        AlertDialog(
            onDismissRequest = {
                showDeleteDialog = false
                selectedDish = null
            },
            title = { Text(stringResource(R.string.delete_dish_title)) },
            text = {
                Text(
                    stringResource(
                        R.string.delete_dish_confirmation,
                        selectedDish!!.getDisplayName()
                    )
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        scope.launch {
                            dishDao.deleteDish(selectedDish!!.id)
                            showDeleteDialog = false
                            selectedDish = null
                        }
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text(stringResource(R.string.delete))
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    selectedDish = null
                }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}

@Composable
fun DishItemCard(
    dish: Dish,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    val xe = dish.carbsPer100g / 12.0

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { onClick() },
                    onLongPress = { onLongClick() }
                )
            },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.size(60.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = dish.name.take(1),
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = dish.getDisplayName(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = stringResource(R.string.carbs_per_100g_format, dish.carbsPer100g),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Surface(
                shape = MaterialTheme.shapes.small,
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Text(
                    text = String.format("%.2fXE", xe),
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}