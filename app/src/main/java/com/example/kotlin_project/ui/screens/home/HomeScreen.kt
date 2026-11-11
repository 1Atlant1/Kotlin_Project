package com.example.kotlin_project.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.coerceIn
import androidx.compose.ui.unit.dp
import com.example.kotlin_project.R
import com.example.kotlin_project.ui.components.InfoBanner
import com.example.kotlin_project.ui.utils.getCategoryDisplayName

private const val GRID_COLUMNS = 2
private val GridSpacing = 12.dp
private val GridBottomPadding = 4.dp
private val MinCategoryCardHeight = 84.dp
private val MaxCategoryCardHeight = 120.dp

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onCategoryClick: (String) -> Unit,
    onPlateClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    val categories by viewModel.categories.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
        ) {
            Surface(
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.WaterDrop,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = stringResource(R.string.app_name),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = stringResource(R.string.app_subtitle),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.home_title),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = stringResource(R.string.home_subtitle),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (categories.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                val rows = (categories.size + GRID_COLUMNS - 1) / GRID_COLUMNS
                val cardHeight = categoryCardHeight(maxHeight, rows)

                LazyVerticalGrid(
                    columns = GridCells.Fixed(GRID_COLUMNS),
                    contentPadding = PaddingValues(bottom = GridBottomPadding),
                    horizontalArrangement = Arrangement.spacedBy(GridSpacing),
                    verticalArrangement = Arrangement.spacedBy(GridSpacing),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(categories) { category ->
                        CategoryCard(
                            category = category,
                            viewModel = viewModel,
                            height = cardHeight,
                            onClick = { onCategoryClick(category) }
                        )
                    }
                }
            }
        }

        InfoBanner(
            text = stringResource(R.string.xe_info),
            modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
        )
    }
}

private fun categoryCardHeight(availableHeight: Dp, rows: Int): Dp {
    if (rows <= 0) return MaxCategoryCardHeight
    val freeHeight = availableHeight - GridBottomPadding - GridSpacing * (rows - 1) - 1.dp
    return (freeHeight / rows).coerceIn(MinCategoryCardHeight, MaxCategoryCardHeight)
}

@Composable
fun CategoryCard(
    category: String,
    viewModel: HomeViewModel,
    height: Dp,
    onClick: () -> Unit
) {
    val count by viewModel.getDishCount(category).collectAsState()
    val icon = getIconForCategory(category)

    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(height),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = MaterialTheme.shapes.small,
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(36.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = getCategoryDisplayName(category),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = pluralStringResource(R.plurals.dishes_count, count, count),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
fun getIconForCategory(category: String): ImageVector = when (category) {
    "Soups" -> Icons.Default.LocalDining
    "Hot dishes" -> Icons.Default.Restaurant
    "Sweets" -> Icons.Default.Cake
    "Drinks" -> Icons.Default.LocalCafe
    "Fruits" -> Icons.Default.Eco
    "Vegetables" -> Icons.Default.Grass
    "Dairy" -> Icons.Default.WaterDrop
    "Bread & grains" -> Icons.Default.BakeryDining
    else -> Icons.Default.RestaurantMenu
}
