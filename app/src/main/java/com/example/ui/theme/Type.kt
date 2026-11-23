package com.example.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.R

val PlusJakartaSansFamily = FontFamily(
    Font(R.font.plus_jakarta_sans, FontWeight.Normal),
    Font(R.font.plus_jakarta_sans, FontWeight.Medium),
    Font(R.font.plus_jakarta_sans, FontWeight.SemiBold),
    Font(R.font.plus_jakarta_sans, FontWeight.Bold),
    Font(R.font.plus_jakarta_sans, FontWeight.ExtraBold)
)

val BarlowCondensedFamily = FontFamily(
    Font(R.font.barlow_condensed, FontWeight.Bold)
)

val AmiriArabicFamily = FontFamily(
    Font(R.font.amiri, FontWeight.Normal),
    Font(R.font.amiri, FontWeight.Bold)
)

val FrauncesFamily = FontFamily(
    Font(R.font.fraunces, FontWeight.Medium),
    Font(R.font.fraunces, FontWeight.Bold)
)

val SourceSerif4Family = FontFamily(
    Font(R.font.source_serif_4, FontWeight.Normal),
    Font(R.font.source_serif_4, FontWeight.Medium),
    Font(R.font.source_serif_4, FontWeight.SemiBold)
)

val AppTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = BarlowCondensedFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 60.sp,
        lineHeight = 62.sp,
        letterSpacing = (-0.5).sp
    ),
    headlineMedium = TextStyle(
        fontFamily = PlusJakartaSansFamily,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 21.sp,
        lineHeight = 25.sp
    ),
    titleLarge = TextStyle(
        fontFamily = PlusJakartaSansFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        lineHeight = 24.sp
    ),
    titleMedium = TextStyle(
        fontFamily = PlusJakartaSansFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 22.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = PlusJakartaSansFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        lineHeight = 22.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = PlusJakartaSansFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 13.sp,
        lineHeight = 19.sp
    ),
    labelMedium = TextStyle(
        fontFamily = PlusJakartaSansFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 12.sp,
        lineHeight = 15.sp
    ),
    labelSmall = TextStyle(
        fontFamily = PlusJakartaSansFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 14.sp
    )
)
