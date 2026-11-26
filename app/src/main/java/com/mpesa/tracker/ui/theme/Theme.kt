package com.mpesa.tracker.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.mpesa.tracker.data.repository.ThemeMode

// Legacy / Fallback Palette Tokens
val PrimaryGreen = Color(0xFF00C853)
val GreenDark = Color(0xFF009624)
val GreenGlow = Color(0xFF00E676)
val EmeraldGradientStart = Color(0xFF0D3B2E)
val EmeraldGradientEnd = Color(0xFF1B5E3B)
val BackgroundDark = Color(0xFF0F1311)
val SurfaceDark = Color(0xFF171D1A)
val SurfaceVariantDark = Color(0xFF212925)
val OutlineDark = Color(0xFF2D3833)
val SpentRed = Color(0xFFFF5252)
val IncomeGreen = Color(0xFF00E676)
val FulizaOrange = Color(0xFFFF9800)
val NeutralGray = Color(0xFF9E9E9E)

@Immutable
data class BillerSemanticColors(
    val income: Color,
    val expense: Color,
    val transfer: Color,
    val warning: Color,
    val heroStart: Color,
    val heroEnd: Color,
    val onHero: Color,
    val isDark: Boolean
)

private val LightSemanticColors = BillerSemanticColors(
    income = Color(0xFF137333),
    expense = Color(0xFFC5221F),
    transfer = Color(0xFF0277BD),
    warning = Color(0xFFB06000),
    heroStart = Color(0xFF0B4F38),
    heroEnd = Color(0xFF176B4B),
    onHero = Color.White,
    isDark = false
)

private val DarkSemanticColors = BillerSemanticColors(
    income = Color(0xFF4ADE80),
    expense = Color(0xFFFF6B6B),
    transfer = Color(0xFF38BDF8),
    warning = Color(0xFFFBBF24),
    heroStart = Color(0xFF0D3B2E),
    heroEnd = Color(0xFF16523A),
    onHero = Color(0xFFF5FAF7),
    isDark = true
)

val LocalBillerColors = staticCompositionLocalOf { DarkSemanticColors }

object BillerTheme {
    val colors: BillerSemanticColors
        @Composable
        @ReadOnlyComposable
        get() = LocalBillerColors.current
}

private val FallbackDarkColorScheme = darkColorScheme(
    primary = Color(0xFF4ADE80),
    onPrimary = Color(0xFF00391B),
    primaryContainer = Color(0xFF0D4A30),
    onPrimaryContainer = Color(0xFFC8F7D6),
    secondary = Color(0xFF7DD3FC),
    onSecondary = Color(0xFF00344C),
    secondaryContainer = Color(0xFF15384B),
    onSecondaryContainer = Color(0xFFC8EFFF),
    tertiary = Color(0xFFFBBF24),
    background = Color(0xFF0F1311),
    onBackground = Color(0xFFEAEFEA),
    surface = Color(0xFF171C19),
    onSurface = Color(0xFFE6ECE7),
    surfaceVariant = Color(0xFF222925),
    onSurfaceVariant = Color(0xFFB3BEB6),
    outline = Color(0xFF3A4640),
    outlineVariant = Color(0xFF28312C),
    error = Color(0xFFFF6B6B),
    onError = Color(0xFF560005)
)

private val FallbackLightColorScheme = lightColorScheme(
    primary = Color(0xFF0B6E43),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD0F4DF),
    onPrimaryContainer = Color(0xFF002111),
    secondary = Color(0xFF026AA7),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFD6EEFF),
    onSecondaryContainer = Color(0xFF001E33),
    tertiary = Color(0xFF9E5A00),
    background = Color(0xFFF6F8F7),
    onBackground = Color(0xFF141A16),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF181E1A),
    surfaceVariant = Color(0xFFE9EFEA),
    onSurfaceVariant = Color(0xFF4B584F),
    outline = Color(0xFFBAC5BD),
    outlineVariant = Color(0xFFDCE3DD),
    error = Color(0xFFC5221F),
    onError = Color.White
)

@Composable
fun MpesaTrackerTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val systemDark = isSystemInDarkTheme()
    val useDark = when (themeMode) {
        ThemeMode.SYSTEM -> systemDark
        ThemeMode.DARK -> true
        ThemeMode.LIGHT -> false
    }

    val context = LocalContext.current
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (useDark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        useDark -> FallbackDarkColorScheme
        else -> FallbackLightColorScheme
    }

    val semanticColors = if (useDark) {
        DarkSemanticColors.copy(
            heroStart = colorScheme.primaryContainer,
            heroEnd = colorScheme.surfaceVariant,
            onHero = colorScheme.onPrimaryContainer
        )
    } else {
        LightSemanticColors
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.surfaceContainer.toArgb()
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !useDark
                isAppearanceLightNavigationBars = !useDark
            }
        }
    }

    CompositionLocalProvider(LocalBillerColors provides semanticColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            content = content
        )
    }
}
