// Navigation.kt - Navigation Components for MD3 Expressive
package com.cline.app.design.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.cline.app.design.tokens.DesignTokens

/**
 * Navigation Components for Cline Android
 * 
 * Provides Material Design 3 Expressive navigation patterns including:
 * - Bottom navigation
 * - Navigation rail (for tablets/landscape)
 * - Top app bars
 * - FAB integration
 */

// ========================================================================
// NAVIGATION SEALS
// ========================================================================

/**
 * Screen definitions for navigation
 */
sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Welcome : Screen("welcome", "Welcome", Icons.Default.Home)
    object Main : Screen("main", "Main", Icons.Default.Home)
    object Chat : Screen("chat", "Chat", Icons.Default.Chat)
    object Terminal : Screen("terminal", "Terminal", Icons.Default.Terminal)
    object Plugins : Screen("plugins", "Plugins", Icons.Default.Extension)
    object Settings : Screen("settings", "Settings", Icons.Default.Settings)
    object Backup : Screen("backup", "Backup", Icons.Default.Settings)
}

/**
 * Leaf screen definitions (screens with parameters)
 */
sealed class LeafScreen(val route: String) {
    object ChatDetail : LeafScreen("chat/{chatId}")
    object PluginDetail : LeafScreen("plugin/{pluginId}")
}

// ========================================================================
// BOTTOM NAVIGATION
// ========================================================================

/**
 * Bottom navigation bar with Cline styling
 * 
 * @param currentScreen Current selected screen
 * @param onScreenSelected Callback when a screen is selected
 * @param modifier Modifier for the navigation bar
 */
@Composable
fun BottomNavigationBar(
    currentScreen: Screen,
    onScreenSelected: (Screen) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        Screen.Chat,
        Screen.Terminal,
        Screen.Plugins,
        Screen.Settings
    )

    NavigationBar(
        modifier = modifier
            .clip(MaterialTheme.shapes.large)
            .animateContentSize(
                animationSpec = SpringSpec(
                    dampingRatio = DesignTokens.Motion.Spring.dampingRatio,
                    stiffness = DesignTokens.Motion.Spring.stiffness
                )
            ),
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        items.forEach { screen ->
            val selected = currentScreen == screen

            NavigationBarItem(
                selected = selected,
                onClick = { onScreenSelected(screen) },
                icon = {
                    Icon(
                        imageVector = screen.icon,
                        contentDescription = screen.title,
                        tint = if (selected) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                },
                label = {
                    Text(
                        text = screen.title,
                        color = if (selected) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    }
}

/**
 * Bottom navigation with FAB
 */
@Composable
fun BottomNavigationWithFAB(
    currentScreen: Screen,
    onScreenSelected: (Screen) -> Unit,
    onFABClick: () -> Unit,
    fabIcon: ImageVector = Icons.Default.Add,
    fabContentDescription: String = "New Chat",
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        BottomNavigationBar(
            currentScreen = currentScreen,
            onScreenSelected = onScreenSelected,
            modifier = Modifier.fillMaxWidth()
        )

        ExtendedFloatingActionButton(
            onClick = onFABClick,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = DesignTokens.Elevation.Level3)
                .animateContentSize(
                    animationSpec = SpringSpec(
                        dampingRatio = DesignTokens.Motion.Spring.dampingRatio,
                        stiffness = DesignTokens.Motion.Spring.stiffness
                    )
                ),
            shape = MaterialTheme.shapes.large,
            elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(
                elevation = DesignTokens.Elevation.Level3
            )
        ) {
            Icon(
                imageVector = fabIcon,
                contentDescription = fabContentDescription,
                modifier = Modifier.padding(end = DesignTokens.Spacing.sm)
            )
            Text(fabContentDescription)
        }
    }
}

// ========================================================================
// NAVIGATION RAIL
// ========================================================================

/**
 * Navigation rail for tablets and landscape mode
 */
@Composable
fun NavigationRail(
    currentScreen: Screen,
    onScreenSelected: (Screen) -> Unit,
    modifier: Modifier = Modifier,
    header: @Composable () -> Unit = {}
) {
    val items = listOf(
        Screen.Chat,
        Screen.Terminal,
        Screen.Plugins,
        Screen.Settings
    )

    NavigationRail(
        modifier = modifier
            .clip(MaterialTheme.shapes.medium)
            .animateContentSize(
                animationSpec = SpringSpec(
                    dampingRatio = DesignTokens.Motion.Spring.dampingRatio,
                    stiffness = DesignTokens.Motion.Spring.stiffness
                )
            ),
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        contentColor = MaterialTheme.colorScheme.onSurface,
        header = header
    ) {
        items.forEach { screen ->
            val selected = currentScreen == screen

            NavigationRailItem(
                selected = selected,
                onClick = { onScreenSelected(screen) },
                icon = {
                    Icon(
                        imageVector = screen.icon,
                        contentDescription = screen.title,
                        tint = if (selected) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                },
                label = {
                    Text(
                        text = screen.title,
                        color = if (selected) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                },
                colors = NavigationRailItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    }
}

// ========================================================================
// TOP APP BARS
// ========================================================================

/**
 * Standard top app bar with Cline styling
 */
@Composable
fun ClineTopAppBar(
    title: String,
    modifier: Modifier = Modifier,
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {}
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        modifier = modifier
            .clip(MaterialTheme.shapes.large)
            .animateContentSize(
                animationSpec = SpringSpec(
                    dampingRatio = DesignTokens.Motion.Spring.dampingRatio,
                    stiffness = DesignTokens.Motion.Spring.stiffness
                )
            ),
        navigationIcon = navigationIcon,
        actions = actions,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
            actionIconContentColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    )
}

/**
 * Top app bar with back button
 */
@Composable
fun ClineBackAppBar(
    title: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    actions: @Composable RowScope.() -> Unit = {}
) {
    ClineTopAppBar(
        title = title,
        modifier = modifier,
        navigationIcon = {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .size(DesignTokens.Spacing.xl)
                    .clip(androidx.compose.foundation.shape.CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back"
                )
            }
        },
        actions = actions
    )
}

/**
 * Top app bar with search
 */
@Composable
fun ClineSearchAppBar(
    title: String,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier,
    navigationIcon: @Composable () -> Unit = {}
) {
    var isSearchActive by remember { mutableStateOf(false) }

    if (isSearchActive) {
        // Search mode
        ClineTopAppBar(
            title = "",
            modifier = modifier,
            navigationIcon = {
                IconButton(
                    onClick = {
                        isSearchActive = false
                        onClear()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            },
            actions = {
                IconButton(
                    onClick = {
                        onSearch()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search"
                    )
                }
            }
        )
    } else {
        // Normal mode
        ClineTopAppBar(
            title = title,
            modifier = modifier,
            navigationIcon = navigationIcon,
            actions = {
                IconButton(
                    onClick = { isSearchActive = true }
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search"
                    )
                }
            }
        )
    }
}

/**
 * Contextual action bar
 */
@Composable
fun ClineContextualActionBar(
    title: String,
    actions: List<ContextualAction>,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge
            )
        },
        modifier = modifier
            .clip(MaterialTheme.shapes.large)
            .background(MaterialTheme.colorScheme.primaryContainer),
        navigationIcon = {
            IconButton(
                onClick = onDismiss
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close"
                )
            }
        },
        actions = {
            actions.forEach { action ->
                IconButton(
                    onClick = action.onClick
                ) {
                    Icon(
                        imageVector = action.icon,
                        contentDescription = action.contentDescription
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    )
}

/**
 * Contextual action data class
 */
data class ContextualAction(
    val icon: ImageVector,
    val contentDescription: String,
    val onClick: () -> Unit
)

// ========================================================================
// SCAFFOLD
// ========================================================================

/**
 * Cline scaffold with coordinated navigation
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClineScaffold(
    navController: NavHostController,
    currentScreen: Screen,
    onScreenSelected: (Screen) -> Unit,
    modifier: Modifier = Modifier,
    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    floatingActionButtonPosition: androidx.compose.material3.FabPosition = androidx.compose.material3.FabPosition.End,
    snackbarHost: @Composable () -> Unit = {
        SnackbarHost(hostState = remember { SnackbarHostState() })
    },
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        modifier = modifier,
        topBar = topBar,
        bottomBar = bottomBar,
        floatingActionButton = floatingActionButton,
        floatingActionButtonPosition = floatingActionButtonPosition,
        snackbarHost = snackbarHost,
        content = content
    )
}

/**
 * Get current screen from navigation controller
 */
@Composable
fun currentScreen(navController: NavHostController): Screen? {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val destination = navBackStackEntry?.destination
    
    return when (destination?.route) {
        Screen.Welcome.route -> Screen.Welcome
        Screen.Main.route -> Screen.Main
        Screen.Chat.route -> Screen.Chat
        Screen.Terminal.route -> Screen.Terminal
        Screen.Plugins.route -> Screen.Plugins
        Screen.Settings.route -> Screen.Settings
        Screen.Backup.route -> Screen.Backup
        else -> null
    }
}

/**
 * Check if current destination matches the screen
 */
@Composable
fun isCurrentDestination(navController: NavHostController, screen: Screen): Boolean {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val destination = navBackStackEntry?.destination
    
    return destination?.hierarchy?.any { it.route == screen.route } == true
}

// ========================================================================
// TAB NAVIGATION
// ========================================================================

/**
 * Tab row with Cline styling
 */
@Composable
fun ClineTabRow(
    selectedTabIndex: Int,
    tabs: List<TabItem>,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    androidx.compose.material3.TabRow(
        selectedTabIndex = selectedTabIndex,
        modifier = modifier
            .clip(MaterialTheme.shapes.medium)
            .animateContentSize(
                animationSpec = SpringSpec(
                    dampingRatio = DesignTokens.Motion.Spring.dampingRatio,
                    stiffness = DesignTokens.Motion.Spring.stiffness
                )
            ),
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        contentColor = MaterialTheme.colorScheme.onSurface,
        indicator = {
            androidx.compose.material3.TabRowDefaults.Indicator(
                modifier = Modifier
                    .clip(MaterialTheme.shapes.small),
                color = MaterialTheme.colorScheme.primary
            )
        },
        divider = {
            androidx.compose.foundation.layout.Spacer(
                modifier = Modifier
                    .height(1.dp)
                    .background(MaterialTheme.colorScheme.outlineVariant)
            )
        }
    ) {
        tabs.forEachIndexed { index, tab ->
            androidx.compose.material3.Tab(
                selected = selectedTabIndex == index,
                onClick = { onTabSelected(index) },
                modifier = Modifier
                    .clip(MaterialTheme.shapes.small),
                selectedContentColor = MaterialTheme.colorScheme.primary,
                unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                text = {
                    Text(
                        text = tab.text,
                        style = MaterialTheme.typography.labelMedium
                    )
                },
                icon = tab.icon?.let { icon ->
                    {
                        Icon(
                            imageVector = icon,
                            contentDescription = null
                        )
                    }
                }
            )
        }
    }
}

/**
 * Tab item data class
 */
data class TabItem(
    val text: String,
    val icon: ImageVector? = null
)

// ========================================================================
// DRAWER
// ========================================================================

/**
 * Navigation drawer
 */
@Composable
fun ClineNavigationDrawer(
    currentScreen: Screen,
    onScreenSelected: (Screen) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    header: @Composable () -> Unit = {}
) {
    val items = listOf(
        Screen.Chat,
        Screen.Terminal,
        Screen.Plugins,
        Screen.Settings,
        Screen.Backup
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(DesignTokens.Spacing.md)
    ) {
        header()
        
        Spacer(modifier = Modifier.height(DesignTokens.Spacing.md))
        
        items.forEach { screen ->
            val selected = currentScreen == screen
            
            NavigationDrawerItem(
                screen = screen,
                selected = selected,
                onClick = {
                    onScreenSelected(screen)
                    onDismiss()
                }
            )
            
            if (screen != items.last()) {
                Spacer(modifier = Modifier.height(DesignTokens.Spacing.xs))
            }
        }
    }
}

/**
 * Navigation drawer item
 */
@Composable
fun NavigationDrawerItem(
    screen: Screen,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .background(
                if (selected) {
                    MaterialTheme.colorScheme.primaryContainer
                } else {
                    Color.Transparent
                }
            )
            .padding(DesignTokens.Spacing.md)
            .selectable(
                selected = selected,
                onClick = onClick,
                role = Role.RadioButton
            )
    ) {
        Icon(
            imageVector = screen.icon,
            contentDescription = screen.title,
            tint = if (selected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            }
        )
        
        Spacer(modifier = Modifier.width(DesignTokens.Spacing.md))
        
        Text(
            text = screen.title,
            style = MaterialTheme.typography.bodyLarge,
            color = if (selected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurface
            }
        )
    }
}
