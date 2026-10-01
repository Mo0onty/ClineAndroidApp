// Animations.kt - Animation System for MD3 Expressive
package com.cline.app.design.animations

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.cline.app.design.tokens.DesignTokens

/**
 * Animation System for Cline Android
 * 
 * Provides a comprehensive set of animations following Material Design 3 Expressive
 * principles with spring physics, easing curves, and content transitions.
 */

// ========================================================================
// SPRING ANIMATIONS
// ========================================================================

/**
 * Create a spring spec with custom parameters
 */
fun springSpec(
    dampingRatio: Float = Spring.DampingRatioMediumBouncy,
    stiffness: Float = Spring.StiffnessMedium
): SpringSpec<Float> = SpringSpec(
    dampingRatio = dampingRatio,
    stiffness = stiffness
)

/**
 * Standard spring animation
 */
val StandardSpring = SpringSpec<Float>(
    dampingRatio = Spring.DampingRatioMediumBouncy,
    stiffness = Spring.StiffnessMedium
)

/**
 * Bouncy spring animation
 */
val BouncySpring = SpringSpec<Float>(
    dampingRatio = Spring.DampingRatioNoBouncy,
    stiffness = Spring.StiffnessLow
)

/**
 * Stiff spring animation
 */
val StiffSpring = SpringSpec<Float>(
    dampingRatio = Spring.DampingRatioMediumBouncy,
    stiffness = Spring.StiffnessHigh
)

// ========================================================================
// ENTER TRANSITIONS
// ========================================================================

/**
 * Slide in from start (left side)
 */
fun slideInFromStart(duration: Int = DesignTokens.Motion.Standard): ContentTransform {
    return slideInHorizontally(
        initialOffsetX = { -it / 2 },
        animationSpec = tween(
            durationMillis = duration,
            easing = DesignTokens.Motion.StandardEasing
        )
    ) + fadeIn(
        animationSpec = tween(
            durationMillis = duration,
            easing = DesignTokens.Motion.StandardEasing
        )
    )
}

/**
 * Slide in from end (right side)
 */
fun slideInFromEnd(duration: Int = DesignTokens.Motion.Standard): ContentTransform {
    return slideInHorizontally(
        initialOffsetX = { it / 2 },
        animationSpec = tween(
            durationMillis = duration,
            easing = DesignTokens.Motion.StandardEasing
        )
    ) + fadeIn(
        animationSpec = tween(
            durationMillis = duration,
            easing = DesignTokens.Motion.StandardEasing
        )
    )
}

/**
 * Slide in from top
 */
fun slideInFromTop(duration: Int = DesignTokens.Motion.Standard): ContentTransform {
    return slideInVertically(
        initialOffsetY = { -it / 2 },
        animationSpec = tween(
            durationMillis = duration,
            easing = DesignTokens.Motion.StandardEasing
        )
    ) + fadeIn(
        animationSpec = tween(
            durationMillis = duration,
            easing = DesignTokens.Motion.StandardEasing
        )
    )
}

/**
 * Slide in from bottom
 */
fun slideInFromBottom(duration: Int = DesignTokens.Motion.Standard): ContentTransform {
    return slideInVertically(
        initialOffsetY = { it / 2 },
        animationSpec = tween(
            durationMillis = duration,
            easing = DesignTokens.Motion.StandardEasing
        )
    ) + fadeIn(
        animationSpec = tween(
            durationMillis = duration,
            easing = DesignTokens.Motion.StandardEasing
        )
    )
}

/**
 * Simple fade in animation
 */
fun fadeIn(duration: Int = DesignTokens.Motion.Standard): ContentTransform {
    return fadeIn(
        animationSpec = tween(
            durationMillis = duration,
            easing = DesignTokens.Motion.StandardEasing
        )
    )
}

/**
 * Scale in animation
 */
fun scaleIn(duration: Int = DesignTokens.Motion.Standard): ContentTransform {
    return androidx.compose.animation.scaleIn(
        animationSpec = tween(
            durationMillis = duration,
            easing = DesignTokens.Motion.StandardEasing
        )
    ) + fadeIn(
        animationSpec = tween(
            durationMillis = duration,
            easing = DesignTokens.Motion.StandardEasing
        )
    )
}

// ========================================================================
// EXIT TRANSITIONS
// ========================================================================

/**
 * Slide out to start (left side)
 */
fun slideOutToStart(duration: Int = DesignTokens.Motion.Short): ContentTransform {
    return slideOutHorizontally(
        targetOffsetX = { -it / 2 },
        animationSpec = tween(
            durationMillis = duration,
            easing = DesignTokens.Motion.StandardEasing
        )
    ) + fadeOut(
        animationSpec = tween(
            durationMillis = duration,
            easing = DesignTokens.Motion.StandardEasing
        )
    )
}

/**
 * Slide out to end (right side)
 */
fun slideOutToEnd(duration: Int = DesignTokens.Motion.Short): ContentTransform {
    return slideOutHorizontally(
        targetOffsetX = { it / 2 },
        animationSpec = tween(
            durationMillis = duration,
            easing = DesignTokens.Motion.StandardEasing
        )
    ) + fadeOut(
        animationSpec = tween(
            durationMillis = duration,
            easing = DesignTokens.Motion.StandardEasing
        )
    )
}

/**
 * Slide out to top
 */
fun slideOutToTop(duration: Int = DesignTokens.Motion.Short): ContentTransform {
    return slideOutVertically(
        targetOffsetY = { -it / 2 },
        animationSpec = tween(
            durationMillis = duration,
            easing = DesignTokens.Motion.StandardEasing
        )
    ) + fadeOut(
        animationSpec = tween(
            durationMillis = duration,
            easing = DesignTokens.Motion.StandardEasing
        )
    )
}

/**
 * Slide out to bottom
 */
fun slideOutToBottom(duration: Int = DesignTokens.Motion.Short): ContentTransform {
    return slideOutVertically(
        targetOffsetY = { it / 2 },
        animationSpec = tween(
            durationMillis = duration,
            easing = DesignTokens.Motion.StandardEasing
        )
    ) + fadeOut(
        animationSpec = tween(
            durationMillis = duration,
            easing = DesignTokens.Motion.StandardEasing
        )
    )
}

/**
 * Simple fade out animation
 */
fun fadeOut(duration: Int = DesignTokens.Motion.Short): ContentTransform {
    return fadeOut(
        animationSpec = tween(
            durationMillis = duration,
            easing = DesignTokens.Motion.StandardEasing
        )
    )
}

/**
 * Scale out animation
 */
fun scaleOut(duration: Int = DesignTokens.Motion.Short): ContentTransform {
    return androidx.compose.animation.scaleOut(
        animationSpec = tween(
            durationMillis = duration,
            easing = DesignTokens.Motion.StandardEasing
        )
    ) + fadeOut(
        animationSpec = tween(
            durationMillis = duration,
            easing = DesignTokens.Motion.StandardEasing
        )
    )
}

// ========================================================================
// CONTENT TRANSITIONS
// ========================================================================

/**
 * Crossfade transition between content
 */
fun crossfade(duration: Int = DesignTokens.Motion.Standard): ContentTransform {
    return fadeIn(
        animationSpec = tween(duration, easing = DesignTokens.Motion.StandardEasing)
    ) + fadeOut(
        animationSpec = tween(duration, easing = DesignTokens.Motion.StandardEasing)
    )
}

/**
 * Slide left transition (content slides left when changing)
 */
fun slideLeft(duration: Int = DesignTokens.Motion.Standard): ContentTransform {
    return slideInFromEnd(duration) + slideOutToStart(duration)
}

/**
 * Slide right transition (content slides right when changing)
 */
fun slideRight(duration: Int = DesignTokens.Motion.Standard): ContentTransform {
    return slideInFromStart(duration) + slideOutToEnd(duration)
}

/**
 * Slide up transition
 */
fun slideUp(duration: Int = DesignTokens.Motion.Standard): ContentTransform {
    return slideInFromBottom(duration) + slideOutToTop(duration)
}

/**
 * Slide down transition
 */
fun slideDown(duration: Int = DesignTokens.Motion.Standard): ContentTransform {
    return slideInFromTop(duration) + slideOutToBottom(duration)
}

// ========================================================================
// SPECIAL ANIMATIONS
// ========================================================================

/**
 * Content size animation with spring
 */
fun contentSizeSpring(): ContentTransform {
    return androidx.compose.animation.core.ContentScaleAnimation(
        animationSpec = StandardSpring
    ) + fadeIn(
        animationSpec = StandardSpring
    )
}

/**
 * Typing indicator animation
 */
@Composable
fun TypingIndicator(
    modifier: Modifier = Modifier,
    dotSize: Dp = DesignTokens.Spacing.sm,
    color: Color = MaterialTheme.colorScheme.onSurface
) {
    val infiniteTransition = rememberInfiniteTransition(label = "typingIndicator")
    
    val dot1 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "dot1"
    )
    
    val dot2 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = LinearEasing, delayMillis = 200),
            repeatMode = RepeatMode.Restart
        ),
        label = "dot2"
    )
    
    val dot3 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = LinearEasing, delayMillis = 400),
            repeatMode = RepeatMode.Restart
        ),
        label = "dot3"
    )
    
    androidx.compose.foundation.layout.Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Dot(opacity = dot1, size = dotSize, color = color)
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.width(DesignTokens.Spacing.xs))
        Dot(opacity = dot2, size = dotSize, color = color)
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.width(DesignTokens.Spacing.xs))
        Dot(opacity = dot3, size = dotSize, color = color)
    }
}

/**
 * Individual dot for typing indicator
 */
@Composable
fun Dot(
    opacity: Float,
    size: Dp = DesignTokens.Spacing.sm,
    color: Color = MaterialTheme.colorScheme.onSurface
) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(androidx.compose.foundation.shape.CircleShape)
            .background(color.copy(alpha = opacity))
    )
}

/**
 * Loading spinner animation
 */
@Composable
fun LoadingSpinner(
    modifier: Modifier = Modifier,
    size: Dp = DesignTokens.Spacing.xl,
    color: Color = MaterialTheme.colorScheme.primary,
    strokeWidth: Dp = 3.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "loadingSpinner")
    
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )
    
    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        androidx.compose.foundation.Canvas(
            modifier = Modifier.size(size)
        ) {
            drawCircle(
                color = color,
                radius = size.toPx() / 2 - strokeWidth.toPx() / 2,
                center = center,
                style = androidx.compose.ui.graphics.draw.Stroke(strokeWidth.toPx())
            )
        }
    }
}

/**
 * Pulse animation for attention
 */
@Composable
fun PulseAnimation(
    isActive: Boolean,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary
) {
    val animatedColor by animateFloatAsState(
        targetValue = if (isActive) 1f else 0.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )
    
    Box(
        modifier = modifier
            .size(DesignTokens.Spacing.md)
            .clip(androidx.compose.foundation.shape.CircleShape)
            .background(color.copy(alpha = animatedColor))
    )
}

/**
 * Shake animation for error states
 */
fun shakeAnimation(): ContentTransform {
    return androidx.compose.animation.core.updateTransition(
        transitionSpec = {
            if (targetState > initialState) {
                // Shake right
                (tween(50) + tween(50) + tween(50))
            } else {
                // Shake left
                (tween(50) + tween(50) + tween(50))
            }
        }
    )
}

// ========================================================================
// ANIMATED CONTENT
// ========================================================================

/**
 * Animated content with Cline transitions
 */
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun <T> ClineAnimatedContent(
    targetState: T,
    modifier: Modifier = Modifier,
    transitionSpec: AnimatedContentTransitionScope<T>.() -> ContentTransform = { crossfade() },
    label: String = "animatedContent",
    content: @Composable (T) -> Unit
) {
    AnimatedContent(
        targetState = targetState,
        modifier = modifier,
        transitionSpec = transitionSpec,
        label = label
    ) { state ->
        content(state)
    }
}

/**
 * Animated visibility with fade and slide
 */
@Composable
fun ClineAnimatedVisibility(
    visible: Boolean,
    modifier: Modifier = Modifier,
    enter: ContentTransform = fadeIn() + slideInFromTop(),
    exit: ContentTransform = fadeOut() + slideOutToBottom(),
    content: @Composable () -> Unit
) {
    if (visible) {
        content()
    }
}

// ========================================================================
// UTILITY FUNCTIONS
// ========================================================================

/**
 * Animate a Dp value
 */
@Composable
fun animateDp(
    targetValue: Dp,
    animationSpec: androidx.compose.animation.core.AnimationSpec<Dp> = tween(DesignTokens.Motion.Standard)
): Dp {
    val animatedValue by animateDpAsState(
        targetValue = targetValue,
        animationSpec = animationSpec,
        label = "animateDp"
    )
    return animatedValue
}

/**
 * Animate a Float value
 */
@Composable
fun animateFloat(
    targetValue: Float,
    animationSpec: androidx.compose.animation.core.AnimationSpec<Float> = tween(DesignTokens.Motion.Standard)
): Float {
    val animatedValue by animateFloatAsState(
        targetValue = targetValue,
        animationSpec = animationSpec,
        label = "animateFloat"
    )
    return animatedValue
}

/**
 * Get the appropriate animation duration based on importance
 */
fun getAnimationDuration(importance: AnimationImportance = AnimationImportance.Standard): Int {
    return when (importance) {
        AnimationImportance.Instant -> DesignTokens.Motion.ExtraShort
        AnimationImportance.Fast -> DesignTokens.Motion.Short
        AnimationImportance.Standard -> DesignTokens.Motion.Standard
        AnimationImportance.Slow -> DesignTokens.Motion.Extended
        AnimationImportance.ExtraSlow -> DesignTokens.Motion.Long
    }
}

/**
 * Animation importance levels
 */
enum class AnimationImportance {
    Instant, Fast, Standard, Slow, ExtraSlow
}
