package com.example.kotlin_project.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class ExtendedColors(
    val glucoseNormal: Color,
    val glucoseWarning: Color,
    val glucoseDanger: Color,
    val infoContainer: Color,
    val onInfoContainer: Color,
    val warningContainer: Color,
    val onWarningContainer: Color,
    val sports: Color,
    val sportsContainer: Color,
    val onSportsContainer: Color
)

val LightExtendedColors = ExtendedColors(
    glucoseNormal = GlucoseNormal,
    glucoseWarning = GlucoseWarning,
    glucoseDanger = GlucoseDanger,
    infoContainer = InfoGreen,
    onInfoContainer = InfoGreenText,
    warningContainer = WarningOrange,
    onWarningContainer = WarningOrangeText,
    sports = MintGreen,
    sportsContainer = Mint90,
    onSportsContainer = Mint10
)

val DarkExtendedColors = ExtendedColors(
    glucoseNormal = Color(0xFF81C784),
    glucoseWarning = Color(0xFFFFD54F),
    glucoseDanger = Color(0xFFEF9A9A),
    infoContainer = InfoGreenDark,
    onInfoContainer = InfoGreenTextDark,
    warningContainer = WarningOrangeDark,
    onWarningContainer = WarningOrangeTextDark,
    sports = Mint80,
    sportsContainer = MintDarkContainer,
    onSportsContainer = Mint90
)

val LocalExtendedColors = staticCompositionLocalOf { LightExtendedColors }

val MaterialTheme.extendedColors: ExtendedColors
    @Composable
    @ReadOnlyComposable
    get() = LocalExtendedColors.current
