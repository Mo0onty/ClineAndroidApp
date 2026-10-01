// Navigation.kt - Navigation Graph for Cline Android
package com.cline.app.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.cline.app.design.animations.fadeIn
import com.cline.app.design.animations.fadeOut
import com.cline.app.design.animations.slideInFromEnd
import com.cline.app.design.animations.slideInFromStart
import com.cline.app.design.animations.slideOutToEnd
import com.cline.app.design.animations.slideOutToStart
import com.cline.app.design.tokens.DesignTokens
import com.cline.app.ui.screens.BackupScreen
import com.cline.app.ui.screens.ChatDetailScreen
import com.cline.app.ui.screens.ChatScreen
import com.cline.app.ui.screens.MainScreen
import com.cline.app.ui.screens.PluginDetailScreen
import com.cline.app.ui.screens.PluginsScreen
import com.cline.app.ui.screens.SettingsScreen
import com.cline.app.ui.screens.TerminalScreen

/**
 * Navigation Graph for Cline Android
 * 
 * This file defines the navigation structure and transitions for the app.
 * It uses Compose Navigation with animated transitions.
 */

// ========================================================================
// SCREEN DEFINITIONS
// ========================================================================

/**
 * Main screens in the app
 */
sealed class Screen(val route: String) {
    object Welcome : Screen("welcome")
    object Main : Screen("main")
    object Chat : Screen("chat")
    object Terminal : Screen("terminal")
    object Plugins : Screen("plugins")
    object Settings : Screen("settings")
    object Backup : Screen("backup")
}

/**
 * Screens with parameters
 */
sealed class LeafScreen(val route: String) {
    object ChatDetail : LeafScreen("chat/{chatId}")
    object PluginDetail : LeafScreen("plugin/{pluginId}")
}

// ========================================================================
// NAVIGATION ANIMATIONS
// ========================================================================

/**
 * Animation for entering a screen from the right (forward navigation)
 */
fun enterTransition(): ContentTransform {
    return slideInFromStart(DesignTokens.Motion.Standard) + fadeIn(DesignTokens.Motion.Standard)
}

/**
 * Animation for exiting a screen to the left (forward navigation)
 */
fun exitTransition(): ContentTransform {
    return slideOutToStart(DesignTokens.Motion.Short) + fadeOut(DesignTokens.Motion.Short)
}

/**
 * Animation for entering a screen from the left (back navigation)
 */
fun enterBackTransition(): ContentTransform {
    return slideInFromEnd(DesignTokens.Motion.Standard) + fadeIn(DesignTokens.Motion.Standard)
}

/**
 * Animation for exiting a screen to the right (back navigation)
 */
fun exitBackTransition(): ContentTransform {
    return slideOutToEnd(DesignTokens.Motion.Short) + fadeOut(DesignTokens.Motion.Short)
}

/**
 * Pop enter transition (when popping back stack)
 */
fun popEnterTransition(): ContentTransform {
    return slideInFromEnd(DesignTokens.Motion.Standard) + fadeIn(DesignTokens.Motion.Standard)
}

/**
 * Pop exit transition (when popping back stack)
 */
fun popExitTransition(): ContentTransform {
    return slideOutToStart(DesignTokens.Motion.Short) + fadeOut(DesignTokens.Motion.Short)
}

// ========================================================================
// NAVIGATION GRAPH
// ========================================================================

/**
 * Main navigation host for the Cline app
 * 
 * @param navController The navigation controller
 * @param modifier Modifier for the NavHost
 */
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ClineNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Main.route,
        modifier = modifier.fillMaxSize(),
        enterTransition = { enterTransition() },
        exitTransition = { exitTransition() },
        popEnterTransition = { popEnterTransition() },
        popExitTransition = { popExitTransition() }
    ) {
        // Main screen with bottom navigation
        composable(Screen.Main.route) {
            MainScreen(
                onChatClick = {
                    navController.navigate(Screen.Chat.route) {
                        launchSingleTop = true
                    }
                },
                onTerminalClick = {
                    navController.navigate(Screen.Terminal.route) {
                        launchSingleTop = true
                    }
                },
                onPluginsClick = {
                    navController.navigate(Screen.Plugins.route) {
                        launchSingleTop = true
                    }
                },
                onSettingsClick = {
                    navController.navigate(Screen.Settings.route) {
                        launchSingleTop = true
                    }
                }
            )
        }

        // Chat screen
        composable(Screen.Chat.route) {
            ChatScreen(
                onBack = { navController.popBackStack() },
                onOpenTerminal = {
                    navController.navigate(Screen.Terminal.route) {
                        launchSingleTop = true
                    }
                },
                onChatItemClick = { chatId ->
                    navController.navigate(LeafScreen.ChatDetail.route.replace("{chatId}", chatId)) {
                        launchSingleTop = true
                    }
                }
            )
        }

        // Chat detail screen
        composable(
            route = LeafScreen.ChatDetail.route,
            arguments = listOf(navArgument("chatId") { type = NavType.StringType })
        ) { backStackEntry ->
            val chatId = backStackEntry.arguments?.getString("chatId") ?: ""
            ChatDetailScreen(
                chatId = chatId,
                onBack = { navController.popBackStack() }
            )
        }

        // Terminal screen
        composable(Screen.Terminal.route) {
            TerminalScreen(
                onBack = { navController.popBackStack() }
            )
        }

        // Plugins screen
        composable(Screen.Plugins.route) {
            PluginsScreen(
                onBack = { navController.popBackStack() },
                onPluginClick = { pluginId ->
                    navController.navigate(LeafScreen.PluginDetail.route.replace("{pluginId}", pluginId)) {
                        launchSingleTop = true
                    }
                }
            )
        }

        // Plugin detail screen
        composable(
            route = LeafScreen.PluginDetail.route,
            arguments = listOf(navArgument("pluginId") { type = NavType.StringType })
        ) { backStackEntry ->
            val pluginId = backStackEntry.arguments?.getString("pluginId") ?: ""
            PluginDetailScreen(
                pluginId = pluginId,
                onBack = { navController.popBackStack() }
            )
        }

        // Settings screen
        composable(Screen.Settings.route) {
            SettingsScreen(
                onBack = { navController.popBackStack() },
                onBackupClick = {
                    navController.navigate(Screen.Backup.route) {
                        launchSingleTop = true
                    }
                }
            )
        }

        // Backup screen
        composable(Screen.Backup.route) {
            BackupScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}

// ========================================================================
// NAVIGATION UTILITIES
// ========================================================================

/**
 * Navigate to a screen with optional cleanup of back stack
 */
fun NavHostController.navigateToScreen(
    screen: Screen,
    inclusive: Boolean = false,
    popUpTo: Screen? = null
) {
    this.navigate(screen.route) {
        launchSingleTop = true
        popUpTo?.let { popUpToRoute ->
            popUpTo(popUpToRoute.route) {
                this.inclusive = inclusive
            }
        }
    }
}

/**
 * Navigate back with optional result
 */
fun NavHostController.navigateBack(result: Any? = null) {
    this.popBackStack()
}

/**
 * Get current route
 */
fun NavHostController.currentRoute(): String? {
    return this.currentBackStackEntry?.destination?.route
}

/**
 * Check if current destination is a specific screen
 */
fun NavHostController.isCurrentScreen(screen: Screen): Boolean {
    return this.currentBackStackEntry?.destination?.route == screen.route
}

// ========================================================================
// NAVIGATION ACTIONS
// ========================================================================

/**
 * Navigation actions interface
 */
interface NavigationActions {
    fun navigateToChat()
    fun navigateToTerminal()
    fun navigateToPlugins()
    fun navigateToSettings()
    fun navigateToBackup()
    fun navigateToChatDetail(chatId: String)
    fun navigateToPluginDetail(pluginId: String)
    fun navigateBack()
}

/**
 * Default implementation of navigation actions
 */
class DefaultNavigationActions(
    private val navController: NavHostController
) : NavigationActions {
    
    override fun navigateToChat() {
        navController.navigateToScreen(Screen.Chat)
    }
    
    override fun navigateToTerminal() {
        navController.navigateToScreen(Screen.Terminal)
    }
    
    override fun navigateToPlugins() {
        navController.navigateToScreen(Screen.Plugins)
    }
    
    override fun navigateToSettings() {
        navController.navigateToScreen(Screen.Settings)
    }
    
    override fun navigateToBackup() {
        navController.navigateToScreen(Screen.Backup)
    }
    
    override fun navigateToChatDetail(chatId: String) {
        navController.navigate(LeafScreen.ChatDetail.route.replace("{chatId}", chatId)) {
            launchSingleTop = true
        }
    }
    
    override fun navigateToPluginDetail(pluginId: String) {
        navController.navigate(LeafScreen.PluginDetail.route.replace("{pluginId}", pluginId)) {
            launchSingleTop = true
        }
    }
    
    override fun navigateBack() {
        navController.navigateBack()
    }
}

/**
 * Create navigation actions from a nav controller
 */
@Composable
fun rememberNavigationActions(
    navController: NavHostController
): NavigationActions {
    return remember(navController) {
        DefaultNavigationActions(navController)
    }
}
