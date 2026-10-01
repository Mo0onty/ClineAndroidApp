// WelcomeScreen.kt - Onboarding Flow
package com.cline.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.cline.app.design.components.ClinePrimaryButton
import com.cline.app.design.tokens.DesignTokens

/**
 * Welcome/Onboarding screens for Cline Android
 * 
 * This file provides the onboarding flow with:
 * - Multi-page introduction
 * - Feature highlights
 * - Get started button
 */

// ========================================================================
// ONBOARDING DATA
// ========================================================================

/**
 * Onboarding page data
 */
data class OnboardingPage(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val iconColor: Color = MaterialTheme.colorScheme.primary
)

/**
 * List of onboarding pages
 */
val onboardingPages = listOf(
    OnboardingPage(
        title = "Welcome to Cline",
        description = "Your AI coding agent is ready to help you write, debug, and understand code faster than ever.",
        icon = Icons.Default.Code,
        iconColor = MaterialTheme.colorScheme.primary
    ),
    OnboardingPage(
        title = "Intelligent Assistance",
        description = "Get smart code completions, explanations, and suggestions powered by advanced AI models.",
        icon = Icons.Default.Lightbulb,
        iconColor = MaterialTheme.colorScheme.secondary
    ),
    OnboardingPage(
        title = "Full Development Environment",
        description = "Complete with terminal, file browser, and plugin system - everything you need in one app.",
        icon = Icons.Default.Terminal,
        iconColor = MaterialTheme.colorScheme.tertiary
    )
)

// ========================================================================
// ONBOARDING PAGE
// ========================================================================

/**
 * Individual onboarding page
 */
@Composable
fun OnboardingPage(
    page: OnboardingPage,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .fillMaxSize()
            .padding(DesignTokens.Spacing.xl)
            .verticalScroll(rememberScrollState())
    ) {
        // Icon
        Box(
            modifier = Modifier
                .size(DesignTokens.Spacing.xxxl)
                .clip(CircleShape)
                .background(page.iconColor.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = page.icon,
                contentDescription = null,
                tint = page.iconColor,
                modifier = Modifier.size(DesignTokens.Spacing.xxl)
            )
        }

        Spacer(modifier = Modifier.height(DesignTokens.Spacing.xl))

        // Title
        Text(
            text = page.title,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(DesignTokens.Spacing.md))

        // Description
        Text(
            text = page.description,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

// ========================================================================
// INDICATORS
// ========================================================================

/**
 * Pager indicator dots
 */
@Composable
fun PagerIndicators(
    pageCount: Int,
    currentPage: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(DesignTokens.Spacing.md),
        horizontalArrangement = Arrangement.Center
    ) {
        repeat(pageCount) { index ->
            val color = if (currentPage == index) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }

            Box(
                modifier = Modifier
                    .padding(DesignTokens.Spacing.xs)
                    .clip(CircleShape)
                    .background(color)
                    .size(DesignTokens.Spacing.sm)
            )
        }
    }
}

// ========================================================================
// GET STARTED BUTTON
// ========================================================================

/**
 * Get started button with animation
 */
@Composable
fun GetStartedButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isClicked by remember { mutableStateOf(false) }

    AnimatedVisibility(
        visible = !isClicked,
        enter = fadeIn() + slideInVertically(),
        exit = fadeOut() + slideOutVertically()
    ) {
        ClinePrimaryButton(
            onClick = {
                isClicked = true
                onClick()
            },
            modifier = modifier
                .fillMaxWidth()
                .padding(DesignTokens.Spacing.md),
            text = "Get Started"
        )
    }

    if (isClicked) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .padding(DesignTokens.Spacing.md),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Welcome complete",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(DesignTokens.Spacing.xl)
            )
        }
    }
}

// ========================================================================
// WELCOME SCREEN
// ========================================================================

/**
 * Main welcome screen with onboarding pager
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WelcomeScreen(
    onGetStarted: () -> Unit,
    modifier: Modifier = Modifier
) {
    val pageCount = onboardingPages.size
    val pagerState = rememberPagerState(pageCount = { pageCount })

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        // Pager with onboarding content
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { page ->
            OnboardingPage(page = onboardingPages[page])
        }

        // Indicators
        PagerIndicators(
            pageCount = pageCount,
            currentPage = pagerState.currentPage
        )

        // Get Started button
        GetStartedButton(
            onClick = onGetStarted,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

// ========================================================================
// SIMPLE WELCOME SCREEN
// ========================================================================

/**
 * Simple welcome screen (single page)
 */
@Composable
fun SimpleWelcomeScreen(
    onGetStarted: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .padding(DesignTokens.Spacing.xl)
    ) {
        // Logo placeholder
        Box(
            modifier = Modifier
                .size(DesignTokens.Spacing.xxxl)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Code,
                contentDescription = "Cline Logo",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(DesignTokens.Spacing.xxl)
            )
        }

        Spacer(modifier = Modifier.height(DesignTokens.Spacing.xl))

        // Title
        Text(
            text = "Cline",
            style = MaterialTheme.typography.displayMedium,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(DesignTokens.Spacing.md))

        // Subtitle
        Text(
            text = "Your AI Coding Agent",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(DesignTokens.Spacing.xl))

        // Description
        Text(
            text = "Write, debug, and understand code faster with intelligent AI assistance.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(DesignTokens.Spacing.xl))

        // Get Started button
        ClinePrimaryButton(
            onClick = onGetStarted,
            modifier = Modifier.fillMaxWidth(),
            text = "Get Started"
        )
    }
}

// ========================================================================
// SPLASH SCREEN
// ========================================================================

/**
 * Splash screen shown during app initialization
 */
@Composable
fun SplashScreen(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo
            Box(
                modifier = Modifier
                    .size(DesignTokens.Spacing.xxxl)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Code,
                    contentDescription = "Cline Logo",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(DesignTokens.Spacing.xxl)
                )
            }

            Spacer(modifier = Modifier.height(DesignTokens.Spacing.md))

            // Loading indicator
            com.cline.app.design.components.LoadingSpinner(
                size = DesignTokens.Spacing.lg
            )
        }
    }
}

// ========================================================================
// PREVIEW
// ========================================================================

/**
 * Preview for welcome screen
 */
@Composable
fun WelcomeScreenPreview() {
    MaterialTheme {
        WelcomeScreen(
            onGetStarted = {}
        )
    }
}

/**
 * Preview for simple welcome screen
 */
@Composable
fun SimpleWelcomeScreenPreview() {
    MaterialTheme {
        SimpleWelcomeScreen(
            onGetStarted = {}
        )
    }
}

/**
 * Preview for splash screen
 */
@Composable
fun SplashScreenPreview() {
    MaterialTheme {
        SplashScreen()
    }
}
