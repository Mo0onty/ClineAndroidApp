// DesignTokens.kt - Material Design 3 Expressive Design Tokens
package com.cline.app.design.tokens

import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt

/**
 * Design Tokens System for Material Design 3 Expressive
 * 
 * This system provides a centralized, type-safe way to access design values
 * including colors, typography, spacing, shapes, elevation, and motion.
 */
object DesignTokens {

    // ========================================================================
    // COLOR SYSTEM - Cline AI Official Green Palette
    // ========================================================================
    
    object Colors {
        // Seed color (Cline AI official primary green brand color)
        val Primary = Color(0xFF00D4AA)
        val PrimaryContainer = Color(0xFF00F5C2)
        val OnPrimary = Color(0xFF00382B)
        val OnPrimaryContainer = Color(0xFF002015)
        
        // Secondary colors (Cline AI secondary palette - teal/blue-green)
        val Secondary = Color(0xFF00B8A3)
        val SecondaryContainer = Color(0xFF00E9C2)
        val OnSecondary = Color(0xFF00382B)
        val OnSecondaryContainer = Color(0xFF002015)
        
        // Tertiary colors (Cline AI accent - lighter green)
        val Tertiary = Color(0xFF00E5B8)
        val TertiaryContainer = Color(0xFF00F5E5)
        val OnTertiary = Color(0xFF00382B)
        val OnTertiaryContainer = Color(0xFF002015)
        
        // Surface colors
        val Surface = Color(0xFF1A1C1E)
        val SurfaceVariant = Color(0xFF2A2D30)
        val SurfaceTint = Color(0xFF00D4AA)
        val SurfaceContainer = Color(0xFF212528)
        val SurfaceContainerLow = Color(0xFF1F2326)
        val SurfaceContainerHigh = Color(0xFF2B3034)
        val SurfaceDim = Color(0xFF14171A)
        val SurfaceBright = Color(0xFF35393C)
        
        // On Surface colors
        val OnSurface = Color(0xFFE0E3E6)
        val OnSurfaceVariant = Color(0xFFBFC4C8)
        val OnSurfaceInverse = Color(0xFF1A1C1E)
        
        // Background colors
        val Background = Color(0xFF121416)
        val OnBackground = Color(0xFFE0E3E6)
        
        // Error colors
        val Error = Color(0xFFFF5555)
        val ErrorContainer = Color(0xFF420000)
        val OnError = Color(0xFFFFFFFF)
        val OnErrorContainer = Color(0xFFFFB3B3)
        
        // Outline colors
        val Outline = Color(0xFF5A5F64)
        val OutlineVariant = Color(0xFF3F4449)
        
        // Scrim
        val Scrim = Color(0xFF000000)
        
        // Inverse colors
        val InversePrimary = Color(0xFF006B54)
        val InverseSurface = Color(0xFFE0E3E6)
        
        // Dynamic color support
        @Composable
        @ReadOnlyComposable
        fun getDynamicScheme(): android.content.res.ColorStateList {
            val context = LocalContext.current
            return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                val dynamicColors = androidx.compose.material3.dynamicDarkColorScheme(context)
                android.content.res.ColorStateList.valueOf(dynamicColors.primary.toArgb())
            } else {
                android.content.res.ColorStateList.valueOf(Primary.toArgb())
            }
        }
    }

    // ========================================================================
    // TYPOGRAPHY
    // ========================================================================
    
    object Typography {
        // Display styles
        val DisplayLarge = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Normal,
            fontSize = 57.sp,
            lineHeight = 64.sp,
            letterSpacing = (-0.25).sp
        )
        
        val DisplayMedium = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Normal,
            fontSize = 45.sp,
            lineHeight = 52.sp,
            letterSpacing = 0.sp
        )
        
        val DisplaySmall = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Normal,
            fontSize = 36.sp,
            lineHeight = 44.sp,
            letterSpacing = 0.sp
        )
        
        // Headline styles
        val HeadlineLarge = TextStyle(
            fontWeight = FontWeight.Normal,
            fontSize = 32.sp,
            lineHeight = 40.sp,
            letterSpacing = 0.sp
        )
        
        val HeadlineMedium = TextStyle(
            fontWeight = FontWeight.Normal,
            fontSize = 28.sp,
            lineHeight = 36.sp,
            letterSpacing = 0.sp
        )
        
        val HeadlineSmall = TextStyle(
            fontWeight = FontWeight.Normal,
            fontSize = 24.sp,
            lineHeight = 32.sp,
            letterSpacing = 0.sp
        )
        
        // Title styles
        val TitleLarge = TextStyle(
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            lineHeight = 28.sp,
            letterSpacing = 0.sp
        )
        
        val TitleMedium = TextStyle(
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            lineHeight = 24.sp,
            letterSpacing = 0.1.sp
        )
        
        val TitleSmall = TextStyle(
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            letterSpacing = 0.1.sp
        )
        
        // Body styles
        val BodyLarge = TextStyle(
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            lineHeight = 24.sp,
            letterSpacing = 0.5.sp
        )
        
        val BodyMedium = TextStyle(
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            letterSpacing = 0.25.sp
        )
        
        val BodySmall = TextStyle(
            fontWeight = FontWeight.Normal,
            fontSize = 12.sp,
            lineHeight = 16.sp,
            letterSpacing = 0.4.sp
        )
        
        // Label styles
        val LabelLarge = TextStyle(
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            letterSpacing = 0.1.sp
        )
        
        val LabelMedium = TextStyle(
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp,
            lineHeight = 16.sp,
            letterSpacing = 0.5.sp
        )
        
        val LabelSmall = TextStyle(
            fontWeight = FontWeight.Medium,
            fontSize = 11.sp,
            lineHeight = 16.sp,
            letterSpacing = 0.5.sp
        )
    }

    // ========================================================================
    // MOTION
    // ========================================================================
    
    object Motion {
        // Duration constants
        val Standard = 300
        val Extended = 400
        val Short = 200
        val ExtraShort = 150
        val Long = 500
        
        // Easing functions
        val StandardEasing = androidx.compose.animation.core.CubicBezierEasing(
            0.4f, 0.0f, 0.2f, 1.0f
        )
        
        val ExtendedEasing = androidx.compose.animation.core.CubicBezierEasing(
            0.2f, 0.0f, 0.0f, 1.0f
        )
        
        val EmphasizedEasing = androidx.compose.animation.core.CubicBezierEasing(
            0.2f, 0.0f, 0.0f, 1.0f
        )
        
        // Spring physics
        val Spring = androidx.compose.animation.core.SpringSpec(
            dampingRatio = androidx.compose.animation.core.Spring.DampingRatioMediumBouncy,
            stiffness = androidx.compose.animation.core.Spring.StiffnessMedium
        )
        
        val StiffSpring = androidx.compose.animation.core.SpringSpec(
            dampingRatio = androidx.compose.animation.core.Spring.DampingRatioMediumBouncy,
            stiffness = androidx.compose.animation.core.Spring.StiffnessHigh
        )
        
        val BouncySpring = androidx.compose.animation.core.SpringSpec(
            dampingRatio = androidx.compose.animation.core.Spring.DampingRatioNoBouncy,
            stiffness = androidx.compose.animation.core.Spring.StiffnessLow
        )
    }

    // ========================================================================
    // SPACING
    // ========================================================================
    
    object Spacing {
        val xs: Dp = 4.dp
        val sm: Dp = 8.dp
        val md: Dp = 16.dp
        val lg: Dp = 24.dp
        val xl: Dp = 32.dp
        val xxl: Dp = 48.dp
        val xxxl: Dp = 64.dp
        
        // Touch targets
        val TouchTargetMin: Dp = 48.dp
        val TouchTarget: Dp = 48.dp
        
        // Content padding
        val ContentPadding: Dp = 16.dp
        val ContentPaddingHorizontal: Dp = 24.dp
        val ContentPaddingVertical: Dp = 16.dp
    }

    // ========================================================================
    // SHAPE
    // ========================================================================
    
    object Shape {
        val None: CornerBasedShape = RoundedCornerShape(0.dp)
        val Small: CornerBasedShape = RoundedCornerShape(4.dp)
        val Medium: CornerBasedShape = RoundedCornerShape(8.dp)
        val Large: CornerBasedShape = RoundedCornerShape(12.dp)
        val Full: CornerBasedShape = RoundedCornerShape(16.dp)
        val ExtraLarge: CornerBasedShape = RoundedCornerShape(28.dp)
        
        // Custom shapes for specific components
        val ButtonShape: CornerBasedShape = RoundedCornerShape(8.dp)
        val CardShape: CornerBasedShape = RoundedCornerShape(12.dp)
        val DialogShape: CornerBasedShape = RoundedCornerShape(28.dp)
        val BottomSheetShape: CornerBasedShape = RoundedCornerShape(16.dp)
        val ChipShape: CornerBasedShape = RoundedCornerShape(16.dp)
        val TextFieldShape: CornerBasedShape = RoundedCornerShape(8.dp)
    }

    // ========================================================================
    // ELEVATION
    // ========================================================================
    
    object Elevation {
        val Level0: Dp = 0.dp
        val Level1: Dp = 1.dp
        val Level2: Dp = 3.dp
        val Level3: Dp = 6.dp
        val Level4: Dp = 8.dp
        val Level5: Dp = 12.dp
        val Level6: Dp = 16.dp
        
        // Component-specific elevations
        val CardElevation: Dp = 2.dp
        val DialogElevation: Dp = 6.dp
        val BottomSheetElevation: Dp = 8.dp
        val ButtonElevation: Dp = 2.dp
        val ButtonPressedElevation: Dp = 8.dp
        val FABElevation: Dp = 6.dp
        val FABPressedElevation: Dp = 12.dp
    }

    // ========================================================================
    // OPACITY
    // ========================================================================
    
    object Opacity {
        val Full: Float = 1.0f
        val High: Float = 0.87f
        val Medium: Float = 0.60f
        val Low: Float = 0.38f
        val Disabled: Float = 0.38f
        val Hint: Float = 0.38f
        val Divider: Float = 0.12f
    }

    // ========================================================================
    // Z-INDEX
    // ========================================================================
    
    object ZIndex {
        val Background: Float = 0f
        val Surface: Float = 1f
        val Floating: Float = 2f
        val Dialog: Float = 3f
        val Snackbar: Float = 4f
        val Tooltip: Float = 5f
        val Modal: Float = 6f
    }
}
