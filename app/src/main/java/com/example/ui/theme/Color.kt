package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// Poster-Scale Achromatic Technical-Product Palette
val AchromaticGround = Color(0xFFEFEFEE)      // #EFEFEE Cool achromatic ground
val DimmerStageGround = Color(0xFFE4E4E2)     // #E4E4E2 Dimmer stage ground (Hero / Alternate sections)
val InkPrimary = Color(0xFF0D0D0F)            // #0D0D0F Near-black ink & inverted section ground
val InkSecondary = Color(0xFF43444A)          // #43444A Secondary body ink
val InkMuted = Color(0xFF6E6F76)              // #6E6F76 Monospaced technical metadata muted ink
val SignalAccent = Color(0xFF2F5BFF)          // #2F5BFF Saturated signal accent (small type, dot, rule, trace)
val SignalAccentLift = Color(0xFF7C97FF)      // #7C97FF Signal lift on dark (#0D0D0F) grounds
val HairlineRule = Color(0x1F0D0D0F)          // rgba(13,13,15,.12) 1px hairline border
val InvertedHairlineRule = Color(0x26FFFFFF)  // rgba(255,255,255,.15) 1px hairline on #0D0D0F ground

// Runtime Field-Swap Presets
val FieldPresetRmgGround = Color(0xFFEFEFEE)
val FieldPresetRmgInk = Color(0xFF0D0D0F)

val FieldPresetCncGround = Color(0xFF0D0D0F)
val FieldPresetCncInk = Color(0xFFEFEFEE)

val FieldPresetDokanGround = Color(0xFFE4E4E2)
val FieldPresetDokanInk = Color(0xFF0D0D0F)

// Legacy aliases mapped to the achromatic + signal accent palette for sub-drawers
val AuralisCyan = SignalAccent
val AuralisCyanDark = SignalAccent
val AuralisAmber = InkPrimary
val AuralisAmberDark = InkSecondary
val AuralisBkashPink = SignalAccent
val AuralisGreen = SignalAccent
val AuralisRed = InkPrimary

val DarkBackground = InkPrimary
val DarkSurface = Color(0xFF141418)
val DarkSurfaceVariant = Color(0xFF1C1C22)
val DarkOutline = InvertedHairlineRule

val TextPrimary = AchromaticGround
val TextSecondary = Color(0xFFA8A9B2)
val TextMuted = InkMuted

val LightBackground = AchromaticGround
val LightSurface = DimmerStageGround
val LightSurfaceVariant = Color(0xFFDADAD7)
val LightOutline = HairlineRule
val LightTextPrimary = InkPrimary
val LightTextSecondary = InkSecondary
