package com.example.assabil.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ─── PALETTE VINTAGE ──────────────────────────────────────────────
object AsSabilColors {
    val Espresso    = Color(0xFF482E1D)
    val Caramel     = Color(0xFF895D2B)
    val Leafy       = Color(0xFFA3966A)
    val SandStorm   = Color(0xFFF0DAAE)
    val Cinnamon    = Color(0xFF90553C)
    val Cream       = Color(0xFFFAF3E6)
    val LightCream  = Color(0xFFFDF6E8)
    val DarkBg      = Color(0xFF1A0F0A)
    val DarkSurface = Color(0xFF2C1A10)
    val DarkCard    = Color(0xFF3A2218)
    val Gold        = Color(0xFFD4A017)
    val GoldLight   = Color(0xFFE8C96A)
    val White       = Color(0xFFFFFDF8)
    val TextDark    = Color(0xFF2A1505)
    val TextMedium  = Color(0xFF5C3D1E)
    val TextLight   = Color(0xFF8A6644)
    val Divider     = Color(0xFFE0C8A0)
}

// ─── TYPOGRAPHY ───────────────────────────────────────────────────
// Amiri for Arabic & display titles, Lato for body
val AsSabilTypography = Typography(
    displayLarge = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        letterSpacing = 0.sp
    ),
    displayMedium = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
        letterSpacing = 0.sp
    ),
    headlineLarge = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        letterSpacing = 0.2.sp
    ),
    headlineMedium = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        letterSpacing = 0.1.sp
    ),
    titleLarge = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 17.sp,
        letterSpacing = 0.15.sp
    ),
    titleMedium = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 15.sp,
        letterSpacing = 0.1.sp
    ),
    bodyLarge = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp
    ),
    bodyMedium = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    labelLarge = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        letterSpacing = 0.5.sp
    ),
    labelMedium = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 11.sp,
        letterSpacing = 0.4.sp
    )
)

// ─── LIGHT COLOR SCHEME ───────────────────────────────────────────
private val LightColorScheme = lightColorScheme(
    primary          = AsSabilColors.Caramel,
    onPrimary        = AsSabilColors.White,
    primaryContainer = AsSabilColors.SandStorm,
    onPrimaryContainer = AsSabilColors.Espresso,
    secondary        = AsSabilColors.Cinnamon,
    onSecondary      = AsSabilColors.White,
    secondaryContainer = AsSabilColors.SandStorm,
    onSecondaryContainer = AsSabilColors.Espresso,
    tertiary         = AsSabilColors.Leafy,
    onTertiary       = AsSabilColors.White,
    background       = AsSabilColors.Cream,
    onBackground     = AsSabilColors.Espresso,
    surface          = AsSabilColors.LightCream,
    onSurface        = AsSabilColors.Espresso,
    surfaceVariant   = AsSabilColors.SandStorm,
    onSurfaceVariant = AsSabilColors.TextMedium,
    outline          = AsSabilColors.Divider,
    outlineVariant   = AsSabilColors.SandStorm,
    error            = Color(0xFFB00020),
    onError          = AsSabilColors.White,
)

// ─── DARK COLOR SCHEME ────────────────────────────────────────────
private val DarkColorScheme = darkColorScheme(
    primary          = AsSabilColors.GoldLight,
    onPrimary        = AsSabilColors.DarkBg,
    primaryContainer = AsSabilColors.DarkCard,
    onPrimaryContainer = AsSabilColors.GoldLight,
    secondary        = AsSabilColors.Cinnamon,
    onSecondary      = AsSabilColors.White,
    background       = AsSabilColors.DarkBg,
    onBackground     = AsSabilColors.SandStorm,
    surface          = AsSabilColors.DarkSurface,
    onSurface        = AsSabilColors.SandStorm,
    surfaceVariant   = AsSabilColors.DarkCard,
    onSurfaceVariant = AsSabilColors.Leafy,
    outline          = Color(0xFF5C3D1E),
)

// ─── SHAPES ───────────────────────────────────────────────────────
val AsSabilShapes = Shapes(
    extraSmall = androidx.compose.foundation.shape.RoundedCornerShape(6.dp),
    small      = androidx.compose.foundation.shape.RoundedCornerShape(10.dp),
    medium     = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
    large      = androidx.compose.foundation.shape.RoundedCornerShape(20.dp),
    extraLarge = androidx.compose.foundation.shape.RoundedCornerShape(28.dp)
)

// ─── MAIN THEME ───────────────────────────────────────────────────
@Composable
fun AsSabilTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AsSabilTypography,
        shapes = AsSabilShapes,
        content = content
    )
}
