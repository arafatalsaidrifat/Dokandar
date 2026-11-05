package com.example.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val AchromaticTechnicalLightScheme = lightColorScheme(
    primary = SignalAccent,
    onPrimary = Color.White,
    primaryContainer = DimmerStageGround,
    onPrimaryContainer = InkPrimary,
    secondary = InkPrimary,
    onSecondary = AchromaticGround,
    secondaryContainer = DimmerStageGround,
    onSecondaryContainer = InkPrimary,
    tertiary = SignalAccent,
    onTertiary = Color.White,
    tertiaryContainer = DimmerStageGround,
    onTertiaryContainer = InkPrimary,
    background = AchromaticGround,
    onBackground = InkPrimary,
    surface = AchromaticGround,
    onSurface = InkPrimary,
    surfaceVariant = DimmerStageGround,
    onSurfaceVariant = InkSecondary,
    outline = HairlineRule
)

private val AchromaticTechnicalDarkScheme = darkColorScheme(
    primary = SignalAccentLift,
    onPrimary = InkPrimary,
    primaryContainer = Color(0xFF1A1B20),
    onPrimaryContainer = AchromaticGround,
    secondary = AchromaticGround,
    onSecondary = InkPrimary,
    secondaryContainer = Color(0xFF1A1B20),
    onSecondaryContainer = AchromaticGround,
    tertiary = SignalAccentLift,
    onTertiary = InkPrimary,
    background = InkPrimary,
    onBackground = AchromaticGround,
    surface = InkPrimary,
    onSurface = AchromaticGround,
    surfaceVariant = Color(0xFF18191E),
    onSurfaceVariant = Color(0xFFA8A9B2),
    outline = InvertedHairlineRule
)

// Border-radius 0 everywhere except pill buttons (handled explicitly on Button components)
val AchromaticShapes = Shapes(
    extraSmall = RoundedCornerShape(0.dp),
    small = RoundedCornerShape(0.dp),
    medium = RoundedCornerShape(0.dp),
    large = RoundedCornerShape(0.dp),
    extraLarge = RoundedCornerShape(0.dp)
)

@Composable
fun AuralisOSTheme(
    darkTheme: Boolean = false,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) AchromaticTechnicalDarkScheme else AchromaticTechnicalLightScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = AchromaticShapes,
        content = content
    )
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = false,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    AuralisOSTheme(darkTheme = darkTheme, dynamicColor = dynamicColor, content = content)
}
