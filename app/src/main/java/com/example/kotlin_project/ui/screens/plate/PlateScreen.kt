package com.example.kotlin_project.ui.screens.plate

import android.text.format.DateFormat
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kotlin_project.R
import com.example.kotlin_project.domain.calculator.MealPeriodDetector
import com.example.kotlin_project.domain.model.CgmReading
import com.example.kotlin_project.domain.model.Glucose
import com.example.kotlin_project.domain.model.PlateItem
import com.example.kotlin_project.ui.components.AppTopBar
import com.example.kotlin_project.ui.screens.settings.SettingsViewModel
import com.example.kotlin_project.ui.theme.DoseTextStyle
import com.example.kotlin_project.ui.theme.PillShape
import com.example.kotlin_project.ui.theme.extendedColors
import com.example.kotlin_project.ui.utils.getDisplayName
import kotlinx.coroutines.delay
import java.time.LocalTime
import java.util.Date

private const val TIME_REFRESH_INTERVAL_MS = 60_000L

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlateScreen(
    viewModel: PlateViewModel,
    settingsViewModel: SettingsViewModel,
    onBackClick: () -> Unit
) {
    val plateItems by viewModel.plateItems.collectAsState()
    val glucose by viewModel.glucose.collectAsState()
    val cgmSyncResult by viewModel.cgmSyncResult.collectAsState()
    val manualMealPeriod by viewModel.manualMealPeriod.collectAsState()
    val isSportsMode by viewModel.isSportsMode.collectAsState()
    val settings by settingsViewModel.settings.collectAsState()
    val currentTime by rememberCurrentTime()
    val mealPeriod = manualMealPeriod ?: MealPeriodDetector.detect(settings.mealTimes, currentTime)

    var doseResult by remember {
        mutableStateOf<com.example.kotlin_project.domain.calculator.DoseResult?>(
            null
        )
    }
    var insulinExpanded by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    val lowGlucoseWarningText = stringResource(R.string.low_glucose_warning)
    val borderlineWarningText = stringResource(R.string.borderline_warning)

    val cgmSyncMessage = cgmSyncResult?.let { cgmSyncMessage(it) }
    LaunchedEffect(cgmSyncResult) {
        if (cgmSyncMessage != null) {
            snackbarHostState.showSnackbar(cgmSyncMessage)
            viewModel.onCgmSyncResultShown()
        }
    }

    LaunchedEffect(plateItems, glucose, mealPeriod, isSportsMode) {
        doseResult = null
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            AppTopBar(
                title = stringResource(R.string.app_name),
                subtitle = stringResource(R.string.my_plate_title),
                onBackClick = onBackClick
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp)
        ) {
            if (plateItems.isNotEmpty()) {
                val totalCarbs = plateItems.sumOf { it.carbsInPortion }
                val totalXE = plateItems.sumOf { it.xe }
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.large,
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = stringResource(R.string.plate_total, totalCarbs, totalXE),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = pluralStringResource(
                                        R.plurals.dishes_count,
                                        plateItems.size,
                                        plateItems.size
                                    ),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            if (plateItems.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Inbox,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            stringResource(R.string.plate_empty),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                itemsIndexed(plateItems) { index, item ->
                    PlateItemCard(item = item, onRemove = { viewModel.removeDishFromPlate(index) })
                }
            }

            item {
                GlucoseSection(
                    glucose = glucose,
                    onGlucoseChange = viewModel::updateGlucose,
                    onSyncClick = viewModel::syncGlucoseFromCgm
                )
            }

            item {
                val insulinOptions = settings.availableInsulins

                ExposedDropdownMenuBox(
                    expanded = insulinExpanded,
                    onExpandedChange = { insulinExpanded = !insulinExpanded }) {
                    OutlinedTextField(
                        value = settings.selectedInsulin,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(stringResource(R.string.insulin_label)) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = insulinExpanded) },
                        shape = MaterialTheme.shapes.medium,
                        modifier = Modifier.fillMaxWidth().menuAnchor(
                            type = ExposedDropdownMenuAnchorType.PrimaryNotEditable,
                            enabled = true
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = insulinExpanded,
                        onDismissRequest = { insulinExpanded = false }) {
                        insulinOptions.forEach { insulin ->
                            DropdownMenuItem(
                                text = { Text(insulin) },
                                onClick = {
                                    settingsViewModel.updateSelectedInsulin(insulin)
                                    insulinExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            item {
                MealPeriodSelector(
                    mealPeriod = mealPeriod,
                    isAutoDetected = manualMealPeriod == null,
                    onPeriodSelected = viewModel::updateMealPeriod,
                    onAutoDetectClick = viewModel::resetMealPeriod
                )
            }

            item {
                val sportsColor = MaterialTheme.extendedColors.sports
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.large,
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSportsMode) {
                            MaterialTheme.extendedColors.sportsContainer
                        } else {
                            MaterialTheme.colorScheme.surface
                        }
                    )
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Bolt,
                                contentDescription = null,
                                tint = sportsColor,
                                modifier = Modifier.padding(end = 8.dp)
                            )
                            Text(
                                stringResource(R.string.sports_mode),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                        Switch(
                            checked = isSportsMode,
                            onCheckedChange = { viewModel.toggleSportsMode() },
                            colors = SwitchDefaults.colors(
                                checkedTrackColor = sportsColor,
                                checkedThumbColor = MaterialTheme.colorScheme.surface
                            )
                        )
                    }
                }
            }

            item {
                Button(
                    onClick = {
                        val carbRatioKey = if (isSportsMode) "${mealPeriod}_sport" else mealPeriod
                        val carbRatio = settings.carbRatios[carbRatioKey] ?: 1.3

                        val result = viewModel.calculateDose(
                            targetGlucose = settings.targetGlucose,
                            insulinSensitivity = settings.insulinSensitivity,
                            carbRatio = carbRatio,
                            lowGlucoseWarning = lowGlucoseWarningText,
                            borderlineWarning = borderlineWarningText
                        )
                        doseResult = result
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    enabled = plateItems.isNotEmpty() && glucose.valueMmol != null,
                    shape = PillShape
                ) {
                    Text(
                        stringResource(R.string.calculate_dose),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

            }

            doseResult?.let { result ->
                item {
                    if (result.isLowGlucose) {
                        val warningContent = MaterialTheme.extendedColors.onWarningContainer
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = MaterialTheme.shapes.large,
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.extendedColors.warningContainer
                            )
                        ) {
                            Row(modifier = Modifier.padding(16.dp)) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = warningContent
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = stringResource(R.string.warning_title),
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = warningContent
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = result.warning ?: "",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = warningContent
                                    )
                                }
                            }
                        }
                    } else {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = MaterialTheme.shapes.large,
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                Text(
                                    text = stringResource(R.string.meal_bolus, result.mealBolus),
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = stringResource(R.string.correction, result.correction),
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = stringResource(R.string.total_dose, result.totalDose),
                                    style = DoseTextStyle,
                                    color = MaterialTheme.colorScheme.primary
                                )

                                result.warning?.let { warning ->
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        text = warning,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.extendedColors.onWarningContainer
                                    )
                                }
                            }
                        }
                    }
                }
            }

            if (doseResult != null && !doseResult!!.isLowGlucose) {
                item {
                    Button(
                        onClick = { viewModel.clearPlate(); doseResult = null },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = PillShape,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text(
                            stringResource(R.string.confirm_injection),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun rememberCurrentTime(): State<LocalTime> = produceState(LocalTime.now()) {
    while (true) {
        delay(TIME_REFRESH_INTERVAL_MS)
        value = LocalTime.now()
    }
}

@Composable
private fun cgmSyncMessage(result: CgmSyncResult): String = when (result) {
    is CgmSyncResult.Success -> {
        val measuredAt = DateFormat.getTimeFormat(LocalContext.current)
            .format(Date(result.reading.timestampMillis))
        stringResource(
            R.string.cgm_sync_success,
            Glucose.format(result.reading.glucoseMmol),
            measuredAt
        )
    }

    CgmSyncResult.NoData -> stringResource(R.string.cgm_sync_no_data)

    CgmSyncResult.Outdated -> stringResource(R.string.cgm_sync_outdated, CgmReading.MAX_AGE_MINUTES)
}

@Composable
fun PlateItemCard(item: PlateItem, onRemove: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.dish.getDisplayName(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = stringResource(
                        R.string.plate_item_details,
                        item.weightGrams,
                        item.carbsInPortion
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Surface(
                shape = PillShape,
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.padding(end = 4.dp)
            ) {
                Text(
                    text = stringResource(R.string.xe_result, item.xe),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
            IconButton(onClick = onRemove) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = stringResource(R.string.delete),
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}