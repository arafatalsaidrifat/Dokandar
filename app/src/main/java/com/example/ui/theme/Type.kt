package com.example.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.example.R

@OptIn(ExperimentalTextApi::class)
val ArchivoDisplayFamily = FontFamily(
    Font(
        resId = R.font.archivo,
        weight = FontWeight.Normal,
        variationSettings = FontVariation.Settings(
            FontVariation.weight(400),
            FontVariation.width(100f)
        )
    ),
    Font(
        resId = R.font.archivo,
        weight = FontWeight.Medium,
        variationSettings = FontVariation.Settings(
            FontVariation.weight(500),
            FontVariation.width(100f)
        )
    ),
    Font(
        resId = R.font.archivo,
        weight = FontWeight.Bold,
        variationSettings = FontVariation.Settings(
            FontVariation.weight(700),
            FontVariation.width(100f)
        )
    ),
    Font(
        resId = R.font.archivo,
        weight = FontWeight.ExtraBold,
        variationSettings = FontVariation.Settings(
            FontVariation.weight(800),
            FontVariation.width(100f)
        )
    ),
    Font(
        resId = R.font.archivo,
        weight = FontWeight.Black,
        variationSettings = FontVariation.Settings(
            FontVariation.weight(900),
            FontVariation.width(104f)
        )
    )
)

@OptIn(ExperimentalTextApi::class)
val IbmPlexMonoFamily = FontFamily(
    Font(
        resId = R.font.ibm_plex_mono,
        weight = FontWeight.Normal,
        variationSettings = FontVariation.Settings(
            FontVariation.weight(400)
        )
    ),
    Font(
        resId = R.font.ibm_plex_mono,
        weight = FontWeight.Medium,
        variationSettings = FontVariation.Settings(
            FontVariation.weight(500)
        )
    ),
    Font(
        resId = R.font.ibm_plex_mono,
        weight = FontWeight.SemiBold,
        variationSettings = FontVariation.Settings(
            FontVariation.weight(600)
        )
    ),
    Font(
        resId = R.font.ibm_plex_mono,
        weight = FontWeight.Bold,
        variationSettings = FontVariation.Settings(
            FontVariation.weight(700)
        )
    )
)

val Typography = Typography(
    displayLarge = TextStyle(
        fontFamily = ArchivoDisplayFamily,
        fontWeight = FontWeight.Black,
        fontSize = 64.sp,
        lineHeight = 50.sp,
        letterSpacing = (-0.05).em
    ),
    displayMedium = TextStyle(
        fontFamily = ArchivoDisplayFamily,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 42.sp,
        lineHeight = 38.sp,
        letterSpacing = (-0.045).em
    ),
    displaySmall = TextStyle(
        fontFamily = ArchivoDisplayFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 31.sp,
        letterSpacing = (-0.04).em
    ),
    headlineLarge = TextStyle(
        fontFamily = ArchivoDisplayFamily,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 28.sp,
        lineHeight = 27.sp,
        letterSpacing = (-0.04).em
    ),
    headlineMedium = TextStyle(
        fontFamily = ArchivoDisplayFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 22.sp,
        letterSpacing = (-0.035).em
    ),
    titleLarge = TextStyle(
        fontFamily = ArchivoDisplayFamily,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 18.sp,
        lineHeight = 20.sp,
        letterSpacing = (-0.035).em
    ),
    titleMedium = TextStyle(
        fontFamily = ArchivoDisplayFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 15.sp,
        lineHeight = 19.sp,
        letterSpacing = (-0.02).em
    ),
    bodyLarge = TextStyle(
        fontFamily = ArchivoDisplayFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        lineHeight = 22.sp,
        letterSpacing = (-0.01).em
    ),
    bodyMedium = TextStyle(
        fontFamily = ArchivoDisplayFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp,
        lineHeight = 19.sp,
        letterSpacing = 0.sp
    ),
    labelLarge = TextStyle(
        fontFamily = IbmPlexMonoFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 15.sp,
        letterSpacing = 0.14.em
    ),
    labelMedium = TextStyle(
        fontFamily = IbmPlexMonoFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 10.sp,
        lineHeight = 14.sp,
        letterSpacing = 0.15.em
    ),
    labelSmall = TextStyle(
        fontFamily = IbmPlexMonoFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 10.sp,
        lineHeight = 13.sp,
        letterSpacing = 0.14.em
    )
)
