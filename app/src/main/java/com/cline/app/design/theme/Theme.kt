// Theme.kt - Material Design 3 Expressive Theme System
package com.cline.app.design.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.cline.app.design.tokens.DesignTokens

/**
 * Cline Theme System with Material Design 3 Expressive support
 * 
 * Features:
 * - Dynamic color support (Android 12+)
 * - Light/Dark theme switching
 * - Custom color schemes based on design tokens
 */
object ClineTheme {
    
    // ========================================================================
    // Color Schemes
    // ========================================================================
    
    private val LightColorScheme = lightColorScheme(
        primary = DesignTokens.Colors.Primary,
        primaryContainer = DesignTokens.Colors.PrimaryContainer,
        onPrimary = DesignTokens.Colors.OnPrimary,
        onPrimaryContainer = DesignTokens.Colors.OnPrimaryContainer,
        
        secondary = DesignTokens.Colors.Secondary,
        secondaryContainer = DesignTokens.Colors.SecondaryContainer,
        onSecondary = DesignTokens.Colors.OnSecondary,
        onSecondaryContainer = DesignTokens.Colors.OnSecondaryContainer,
        
        tertiary = DesignTokens.Colors.Tertiary,
        tertiaryContainer = DesignTokens.Colors.TertiaryContainer,
        onTertiary = DesignTokens.Colors.OnTertiary,
        onTertiaryContainer = DesignTokens.Colors.OnTertiaryContainer,
        
        background = DesignTokens.Colors.Background,
        onBackground = DesignTokens.Colors.OnBackground,
        
        surface = DesignTokens.Colors.Surface,
        onSurface = DesignTokens.Colors.OnSurface,
        surfaceVariant = DesignTokens.Colors.SurfaceVariant,
        onSurfaceVariant = DesignTokens.Colors.OnSurfaceVariant,
        surfaceTint = DesignTokens.Colors.SurfaceTint,
        
        error = DesignTokens.Colors.Error,
        errorContainer = DesignTokens.Colors.ErrorContainer,
        onError = DesignTokens.Colors.OnError,
        onErrorContainer = DesignTokens.Colors.OnErrorContainer,
        
        outline = DesignTokens.Colors.Outline,
        outlineVariant = DesignTokens.Colors.OutlineVariant,
        
        scrim = DesignTokens.Colors.Scrim,
        
        inversePrimary = DesignTokens.Colors.InversePrimary,
        inverseSurface = DesignTokens.Colors.InverseSurface,
        inverseOnSurface = DesignTokens.Colors.OnSurfaceInverse,
        
        surfaceContainer = DesignTokens.Colors.SurfaceContainer,
        surfaceContainerLow = DesignTokens.Colors.SurfaceContainerLow,
        surfaceContainerHigh = DesignTokens.Colors.SurfaceContainerHigh,
        surfaceDim = DesignTokens.Colors.SurfaceDim,
        surfaceBright = DesignTokens.Colors.SurfaceBright
    )
    
    private val DarkColorScheme = darkColorScheme(
        primary = DesignTokens.Colors.Primary,
        primaryContainer = DesignTokens.Colors.PrimaryContainer,
        onPrimary = DesignTokens.Colors.OnPrimary,
        onPrimaryContainer = DesignTokens.Colors.OnPrimaryContainer,
        
        secondary = DesignTokens.Colors.Secondary,
        secondaryContainer = DesignTokens.Colors.SecondaryContainer,
        onSecondary = DesignTokens.Colors.OnSecondary,
        onSecondaryContainer = DesignTokens.Colors.OnSecondaryContainer,
        
        tertiary = DesignTokens.Colors.Tertiary,
        tertiaryContainer = DesignTokens.Colors.TertiaryContainer,
        onTertiary = DesignTokens.Colors.OnTertiary,
        onTertiaryContainer = DesignTokens.Colors.OnTertiaryContainer,
        
        background = DesignTokens.Colors.Surface,
        onBackground = DesignTokens.Colors.OnSurface,
        
        surface = DesignTokens.Colors.Surface,
        onSurface = DesignTokens.Colors.OnSurface,
        surfaceVariant = DesignTokens.Colors.SurfaceVariant,
        onSurfaceVariant = DesignTokens.Colors.OnSurfaceVariant,
        surfaceTint = DesignTokens.Colors.SurfaceTint,
        
        error = DesignTokens.Colors.Error,
        errorContainer = DesignTokens.Colors.ErrorContainer,
        onError = DesignTokens.Colors.OnError,
        onErrorContainer = DesignTokens.Colors.OnErrorContainer,
        
        outline = DesignTokens.Colors.Outline,
        outlineVariant = DesignTokens.Colors.OutlineVariant,
        
        scrim = DesignTokens.Colors.Scrim,
        
        inversePrimary = DesignTokens.Colors.InversePrimary,
        inverseSurface = DesignTokens.Colors.InverseSurface,
        inverseOnSurface = DesignTokens.Colors.OnSurfaceInverse,
        
        surfaceContainer = DesignTokens.Colors.SurfaceContainer,
        surfaceContainerLow = DesignTokens.Colors.SurfaceContainerLow,
        surfaceContainerHigh = DesignTokens.Colors.SurfaceContainerHigh,
        surfaceDim = DesignTokens.Colors.SurfaceDim,
        surfaceBright = DesignTokens.Colors.SurfaceBright
    )
    
    // ========================================================================
    // Typography
    // ========================================================================
    
    private val ClineTypography = androidx.compose.material3.Typography(
        displayLarge = DesignTokens.Typography.DisplayLarge,
        displayMedium = DesignTokens.Typography.DisplayMedium,
        displaySmall = DesignTokens.Typography.DisplaySmall,
        
        headlineLarge = DesignTokens.Typography.HeadlineLarge,
        headlineMedium = DesignTokens.Typography.HeadlineMedium,
        headlineSmall = DesignTokens.Typography.HeadlineSmall,
        
        titleLarge = DesignTokens.Typography.TitleLarge,
        titleMedium = DesignTokens.Typography.TitleMedium,
        titleSmall = DesignTokens.Typography.TitleSmall,
        
        bodyLarge = DesignTokens.Typography.BodyLarge,
        bodyMedium = DesignTokens.Typography.BodyMedium,
        bodySmall = DesignTokens.Typography.BodySmall,
        
        labelLarge = DesignTokens.Typography.LabelLarge,
        labelMedium = DesignTokens.Typography.LabelMedium,
        labelSmall = DesignTokens.Typography.LabelSmall
    )
    
    // ========================================================================
    // Shapes
    // ========================================================================
    
    private val ClineShapes = androidx.compose.material3.Shapes(
        extraSmall = DesignTokens.Shape.Small,
        small = DesignTokens.Shape.Small,
        medium = DesignTokens.Shape.Medium,
        large = DesignTokens.Shape.Large,
        extraLarge = DesignTokens.Shape.ExtraLarge
    )
}

/**
 * Main theme composable function
 * 
 * @param darkTheme Whether to use dark theme (defaults to system preference)
 * @param dynamicColor Whether to use dynamic color (Android 12+)
 * @param content The content to be themed
 */
@Composable
fun ClineTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    // Determine color scheme
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> ClineTheme.DarkColorScheme
        else -> ClineTheme.LightColorScheme
    }
    
    // Apply status bar styling
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.surface.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }
    
    // Apply Material Theme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = ClineTheme.ClineTypography,
        shapes = ClineTheme.ClineShapes,
        content = content
    )
}

/**
 * Extended theme with additional Cline-specific styling
 */
@Composable
fun ClineThemeExtended(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    ClineTheme(darkTheme = darkTheme, dynamicColor = dynamicColor) {
        // Add any Cline-specific theme extensions here
        content()
    }
}

/**
 * Theme preview for design tools
 */
@Composable
fun ClineThemePreview(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    ClineTheme(darkTheme = darkTheme, dynamicColor = false) {
        content()
    }
}

/**
 * Get the current color scheme from the theme
 */
@Composable
fun currentColorScheme(): ColorScheme {
    return MaterialTheme.colorScheme
}

/**
 * Get the current typography from the theme
 */
@Composable
fun currentTypography(): androidx.compose.material3.Typography {
    return MaterialTheme.typography
}

/**
 * Get the current shapes from the theme
 */
@Composable
fun currentShapes(): androidx.compose.material3.Shapes {
    return MaterialTheme.shapes
}
