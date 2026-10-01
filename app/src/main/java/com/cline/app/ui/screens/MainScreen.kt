// MainScreen.kt - Primary App Screen with Bottom Navigation
package com.cline.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.cline.app.design.components.BottomNavigationBar
import com.cline.app.design.components.ClineExtendedFAB
import com.cline.app.design.components.ClineNavHost
import com.cline.app.design.tokens.DesignTokens
import com.cline.app.ui.navigation.Screen

/**
 * Main Screen - Primary app screen with bottom navigation
 * 
 * This screen hosts the main navigation graph and provides:
 * - Bottom navigation bar
 * - Floating action button
 * - Scaffold for consistent layout
 */

// ========================================================================
// SCREEN STATE
// ========================================================================

/**
 * Main screen state
 */
@Composable
fun MainScreen(
    onChatClick: () -> Unit,
    onTerminalClick: () -> Unit,
    onPluginsClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onFileBrowserClick: () -> Unit,
    onVoiceInputClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE

    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }

    // Track bottom bar visibility
    var bottomBarVisible by remember { mutableStateOf(true) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            if (bottomBarVisible && !isLandscape) {
                BottomNavigationBar(
                    currentScreen = getCurrentScreen(navController),
                    onScreenSelected = { screen ->
                        when (screen) {
                            Screen.Chat -> onChatClick()
                            Screen.Terminal -> onTerminalClick()
                            Screen.Plugins -> onPluginsClick()
                            Screen.Settings -> onSettingsClick()
                            Screen.FileBrowser -> onFileBrowserClick()
                            Screen.VoiceInput -> onVoiceInputClick()
                            else -> {}
                        }
                    }
                )
            }
        },
        floatingActionButton = {
            if (!isLandscape) {
                ClineExtendedFAB(
                    onClick = onChatClick,
                    text = "New Chat",
                    icon = Icons.Default.Add,
                    modifier = Modifier
                        .padding(DesignTokens.Spacing.md)
                        .animateContentSize(
                            animationSpec = SpringSpec(
                                dampingRatio = DesignTokens.Motion.Spring.dampingRatio,
                                stiffness = DesignTokens.Motion.Spring.stiffness
                            )
                        )
                )
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            ClineNavHost(
                navController = navController,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

/**
 * Get current screen from navigation controller
 */
@Composable
private fun getCurrentScreen(navController: NavHostController): Screen {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val destination = navBackStackEntry?.destination
    
    return when (destination?.route) {
        Screen.Chat.route -> Screen.Chat
        Screen.Terminal.route -> Screen.Terminal
        Screen.Plugins.route -> Screen.Plugins
        Screen.Settings.route -> Screen.Settings
        Screen.FileBrowser.route -> Screen.FileBrowser
        Screen.VoiceInput.route -> Screen.VoiceInput
        else -> Screen.Chat
    }
}

// ========================================================================
// HOME SCREEN
// ========================================================================

/**
 * Home screen shown when app first loads
 */
@Composable
fun HomeScreen(
    onChatClick: () -> Unit,
    onTerminalClick: () -> Unit,
    onPluginsClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onFileBrowserClick: () -> Unit,
    onVoiceInputClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .padding(DesignTokens.Spacing.md)
    ) {
        // Welcome message
        Text(
            text = "Welcome to Cline",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(DesignTokens.Spacing.md))

        // Description
        Text(
            text = "Your AI coding agent is ready to help. Choose an option below to get started.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(DesignTokens.Spacing.xl))

        // Quick actions
        Text(
            text = "Quick Actions",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(DesignTokens.Spacing.md))

        // Action cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(DesignTokens.Spacing.md)
        ) {
            ActionCard(
                icon = Icons.Default.Chat,
                title = "New Chat",
                description = "Start a conversation with Cline",
                onClick = onChatClick,
                modifier = Modifier.weight(1f)
            )

            ActionCard(
                icon = Icons.Default.Terminal,
                title = "Terminal",
                description = "Open terminal session",
                onClick = onTerminalClick,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(DesignTokens.Spacing.md))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(DesignTokens.Spacing.md)
        ) {
            ActionCard(
                icon = Icons.Default.Extension,
                title = "Plugins",
                description = "Browse and install plugins",
                onClick = onPluginsClick,
                modifier = Modifier.weight(1f)
            )

            ActionCard(
                icon = Icons.Default.Folder,
                title = "Files",
                description = "Browse app files",
                onClick = onFileBrowserClick,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(DesignTokens.Spacing.md))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(DesignTokens.Spacing.md)
        ) {
            ActionCard(
                icon = Icons.Default.Mic,
                title = "Voice",
                description = "Use voice input",
                onClick = onVoiceInputClick,
                modifier = Modifier.weight(1f)
            )

            ActionCard(
                icon = Icons.Default.Settings,
                title = "Settings",
                description = "Configure app preferences",
                onClick = onSettingsClick,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

/**
 * Action card for home screen
 */
@Composable
fun ActionCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
    var isPressed by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(DesignTokens.Spacing.md),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.medium)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(DesignTokens.Spacing.xl)
            )

            Spacer(modifier = Modifier.height(DesignTokens.Spacing.sm))

            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(DesignTokens.Spacing.xs))

            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// ========================================================================
// DASHBOARD SCREEN
// ========================================================================

/**
 * Dashboard screen with recent items
 */
@Composable
fun DashboardScreen(
    onChatClick: () -> Unit,
    onTerminalClick: () -> Unit,
    onPluginsClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onFileBrowserClick: () -> Unit,
    onVoiceInputClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .padding(DesignTokens.Spacing.md)
    ) {
        // Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Dashboard",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Text(
                    text = "Welcome back!",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(DesignTokens.Spacing.xl))

        // Recent chats
        SectionHeader(
            title = "Recent Chats",
            actionText = "See all",
            onActionClick = onChatClick
        )

        Spacer(modifier = Modifier.height(DesignTokens.Spacing.md))

        // Placeholder for recent chats
        RecentItemPlaceholder()

        Spacer(modifier = Modifier.height(DesignTokens.Spacing.xl))

        // Quick actions
        SectionHeader(
            title = "Quick Actions",
            actionText = null,
            onActionClick = null
        )

        Spacer(modifier = Modifier.height(DesignTokens.Spacing.md))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(DesignTokens.Spacing.md)
        ) {
            QuickActionButton(
                icon = Icons.Default.Chat,
                title = "New Chat",
                onClick = onChatClick,
                modifier = Modifier.weight(1f)
            )

            QuickActionButton(
                icon = Icons.Default.Terminal,
                title = "Terminal",
                onClick = onTerminalClick,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(DesignTokens.Spacing.md))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(DesignTokens.Spacing.md)
        ) {
            QuickActionButton(
                icon = Icons.Default.Extension,
                title = "Plugins",
                onClick = onPluginsClick,
                modifier = Modifier.weight(1f)
            )

            QuickActionButton(
                icon = Icons.Default.Settings,
                title = "Settings",
                onClick = onSettingsClick,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

/**
 * Section header with action
 */
@Composable
fun SectionHeader(
    title: String,
    actionText: String?,
    onActionClick: (() -> Unit)?,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )

        if (actionText != null && onActionClick != null) {
            Spacer(modifier = Modifier.weight(1f))
            
            Text(
                text = actionText,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .clip(MaterialTheme.shapes.small)
                    .padding(
                        horizontal = DesignTokens.Spacing.sm,
                        vertical = DesignTokens.Spacing.xs
                    )
            )
        }
    }
}

/**
 * Quick action button
 */
@Composable
fun QuickActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(DesignTokens.Spacing.md)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(DesignTokens.Spacing.xl)
        )

        Spacer(modifier = Modifier.height(DesignTokens.Spacing.sm))

        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

/**
 * Placeholder for recent items
 */
@Composable
fun RecentItemPlaceholder() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(DesignTokens.Spacing.md),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Chat,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(DesignTokens.Spacing.xl)
        )

        Spacer(modifier = Modifier.height(DesignTokens.Spacing.sm))

        Text(
            text = "No recent chats yet",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(DesignTokens.Spacing.xs))

        Text(
            text = "Start a new chat to see it here",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
        )
    }
}

// ========================================================================
// PREVIEW
// ========================================================================

/**
 * Preview for main screen
 */
@Composable
fun MainScreenPreview() {
    MaterialTheme {
        MainScreen(
            onChatClick = {},
            onTerminalClick = {},
            onPluginsClick = {},
            onSettingsClick = {}
        )
    }
}

/**
 * Preview for home screen
 */
@Composable
fun HomeScreenPreview() {
    MaterialTheme {
        HomeScreen(
            onChatClick = {},
            onTerminalClick = {},
            onPluginsClick = {},
            onSettingsClick = {}
        )
    }
}

/**
 * Preview for dashboard screen
 */
@Composable
fun DashboardScreenPreview() {
    MaterialTheme {
        DashboardScreen(
            onChatClick = {},
            onTerminalClick = {},
            onPluginsClick = {},
            onSettingsClick = {}
        )
    }
}
