// PluginsScreen.kt - Plugin Management Interface
package com.cline.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarOutline
import androidx.compose.material.icons.filled.Update
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.cline.app.design.components.ClineIconButton
import com.cline.app.design.components.ClinePrimaryButton
import com.cline.app.design.components.ClinePluginCard
import com.cline.app.design.components.ClineSearchField
import com.cline.app.design.components.ClineTextField
import com.cline.app.design.tokens.DesignTokens

/**
 * Plugins Screen - Plugin Management Interface
 * 
 * This screen provides access to plugin management features:
 * - Browse available plugins
 * - Install/uninstall plugins
 * - View plugin details
 * - Configure plugins
 */

// ========================================================================
// DATA CLASSES
// ========================================================================

/**
 * Plugin data class
 */
data class Plugin(
    val id: String,
    val name: String,
    val description: String,
    val version: String,
    val author: String,
    val icon: ImageVector? = null,
    val category: PluginCategory = PluginCategory.OTHER,
    val isInstalled: Boolean = false,
    val isEnabled: Boolean = false,
    val isFavorite: Boolean = false,
    val rating: Float = 0f
)

/**
 * Plugin category
 */
enum class PluginCategory {
    AI, TERMINAL, EDITOR, UTILITY, THEME, LANGUAGE, OTHER
}

/**
 * Plugin state
 */
enum class PluginState {
    NOT_INSTALLED, INSTALLED, ENABLED, DISABLED, UPDATABLE
}

/**
 * Plugin list state
 */
data class PluginListState(
    val plugins: List<Plugin> = emptyList(),
    val searchQuery: String = "",
    val selectedCategory: PluginCategory? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

// ========================================================================
// PLUGIN CARD
// ========================================================================

/**
 * Plugin card with enhanced display
 */
@Composable
fun PluginCard(
    plugin: Plugin,
    state: PluginState,
    onClick: () -> Unit = {},
    onInstall: () -> Unit = {},
    onUninstall: () -> Unit = {},
    onToggle: () -> Unit = {},
    onFavorite: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val borderColor = when (state) {
        PluginState.NOT_INSTALLED -> MaterialTheme.colorScheme.outlineVariant
        PluginState.INSTALLED, PluginState.DISABLED -> MaterialTheme.colorScheme.primary
        PluginState.ENABLED -> MaterialTheme.colorScheme.primary
        PluginState.UPDATABLE -> MaterialTheme.colorScheme.secondary
    }
    
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .border(
                width = 1.dp,
                color = borderColor,
                shape = MaterialTheme.shapes.medium
            )
            .clickable(onClick = onClick)
            .padding(DesignTokens.Spacing.md)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Plugin icon
            Box(
                modifier = Modifier
                    .size(DesignTokens.Spacing.xl)
                    .clip(MaterialTheme.shapes.medium)
                    .background(
                        if (plugin.isFavorite) {
                            MaterialTheme.colorScheme.primaryContainer
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                plugin.icon?.let { icon ->
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (plugin.isFavorite) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                } ?: run {
                    Icon(
                        imageVector = Icons.Default.Extension,
                        contentDescription = null,
                        tint = if (plugin.isFavorite) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(DesignTokens.Spacing.md))
            
            // Plugin info
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = plugin.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    if (state == PluginState.UPDATABLE) {
                        Spacer(modifier = Modifier.width(DesignTokens.Spacing.xs))
                        Box(
                            modifier = Modifier
                                .size(DesignTokens.Spacing.sm)
                                .clip(MaterialTheme.shapes.full)
                                .background(MaterialTheme.colorScheme.secondaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "NEW",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(DesignTokens.Spacing.xs))
                
                Text(
                    text = plugin.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2
                )
                
                Spacer(modifier = Modifier.height(DesignTokens.Spacing.xs))
                
                // Plugin meta
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(DesignTokens.Spacing.sm)
                ) {
                    Text(
                        text = plugin.version,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Text(
                        text = "by ${plugin.author}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(DesignTokens.Spacing.md))
        
        // Plugin actions
        Row(
            horizontalArrangement = Arrangement.End
        ) {
            when (state) {
                PluginState.NOT_INSTALLED -> {
                    ClinePrimaryButton(
                        onClick = onInstall,
                        text = "Install",
                        modifier = Modifier.size(DesignTokens.Spacing.lg, DesignTokens.Spacing.md)
                    )
                }
                PluginState.INSTALLED, PluginState.DISABLED -> {
                    ClinePrimaryButton(
                        onClick = onToggle,
                        text = "Enable",
                        modifier = Modifier.size(DesignTokens.Spacing.lg, DesignTokens.Spacing.md)
                    )
                    Spacer(modifier = Modifier.width(DesignTokens.Spacing.sm))
                    ClineIconButton(
                        onClick = onUninstall,
                        icon = Icons.Default.Delete,
                        contentDescription = "Uninstall",
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.size(DesignTokens.Spacing.lg)
                    )
                }
                PluginState.ENABLED -> {
                    ClinePrimaryButton(
                        onClick = onToggle,
                        text = "Disable",
                        modifier = Modifier.size(DesignTokens.Spacing.lg, DesignTokens.Spacing.md)
                    )
                    Spacer(modifier = Modifier.width(DesignTokens.Spacing.sm))
                    ClineIconButton(
                        onClick = onUninstall,
                        icon = Icons.Default.Delete,
                        contentDescription = "Uninstall",
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.size(DesignTokens.Spacing.lg)
                    )
                }
                PluginState.UPDATABLE -> {
                    ClinePrimaryButton(
                        onClick = onInstall,
                        text = "Update",
                        modifier = Modifier.size(DesignTokens.Spacing.lg, DesignTokens.Spacing.md)
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(DesignTokens.Spacing.sm))
            
            ClineIconButton(
                onClick = onFavorite,
                icon = if (plugin.isFavorite) Icons.Default.Star else Icons.Default.StarOutline,
                contentDescription = "Favorite",
                contentColor = if (plugin.isFavorite) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
                modifier = Modifier.size(DesignTokens.Spacing.lg)
            )
        }
    }
}

// ========================================================================
// PLUGIN LIST
// ========================================================================

/**
 * Plugin list with categories and search
 */
@Composable
fun PluginList(
    plugins: List<Plugin>,
    stateMap: Map<String, PluginState> = emptyMap(),
    onPluginClick: (Plugin) -> Unit = {},
    onInstall: (Plugin) -> Unit = {},
    onUninstall: (Plugin) -> Unit = {},
    onToggle: (Plugin) -> Unit = {},
    onFavorite: (Plugin) -> Unit = {},
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(DesignTokens.Spacing.md),
        verticalArrangement = Arrangement.spacedBy(DesignTokens.Spacing.md)
    ) {
        items(plugins) { plugin ->
            val state = stateMap[plugin.id] ?: PluginState.NOT_INSTALLED
            
            PluginCard(
                plugin = plugin,
                state = state,
                onClick = { onPluginClick(plugin) },
                onInstall = { onInstall(plugin) },
                onUninstall = { onUninstall(plugin) },
                onToggle = { onToggle(plugin) },
                onFavorite = { onFavorite(plugin) }
            )
        }
    }
}

// ========================================================================
// CATEGORY FILTER
// ========================================================================

/**
 * Category filter chips
 */
@Composable
fun CategoryFilter(
    categories: List<PluginCategory>,
    selectedCategory: PluginCategory?,
    onCategorySelected: (PluginCategory?) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(DesignTokens.Spacing.sm),
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(vertical = DesignTokens.Spacing.sm)
    ) {
        // All category
        CategoryChip(
            category = null,
            title = "All",
            selected = selectedCategory == null,
            onClick = { onCategorySelected(null) }
        )
        
        // Other categories
        categories.forEach { category ->
            CategoryChip(
                category = category,
                title = category.displayName,
                selected = selectedCategory == category,
                onClick = { onCategorySelected(category) }
            )
        }
    }
}

/**
 * Category chip
 */
@Composable
fun CategoryChip(
    category: PluginCategory?,
    title: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(MaterialTheme.shapes.full)
            .background(
                if (selected) {
                    MaterialTheme.colorScheme.primaryContainer
                } else {
                    MaterialTheme.colorScheme.surfaceVariant
                }
            )
            .clickable(onClick = onClick)
            .padding(
                horizontal = DesignTokens.Spacing.md,
                vertical = DesignTokens.Spacing.sm
            )
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
            color = if (selected) {
                MaterialTheme.colorScheme.onPrimaryContainer
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            }
        )
    }
}

/**
 * Plugin category extensions
 */
val PluginCategory.displayName: String
    get() = when (this) {
        PluginCategory.AI -> "AI"
        PluginCategory.TERMINAL -> "Terminal"
        PluginCategory.EDITOR -> "Editor"
        PluginCategory.UTILITY -> "Utility"
        PluginCategory.THEME -> "Themes"
        PluginCategory.LANGUAGE -> "Languages"
        PluginCategory.OTHER -> "Other"
    }

// ========================================================================
// PLUGINS SCREEN
// ========================================================================

/**
 * Main plugins screen
 */
@Composable
fun PluginsScreen(
    onBack: () -> Unit,
    onPluginClick: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    // Sample plugins
    val plugins = listOf(
        Plugin(
            id = "ai-assistant",
            name = "AI Assistant",
            description = "Advanced AI-powered coding assistance with context-aware suggestions",
            version = "1.2.0",
            author = "Cline Team",
            icon = Icons.Default.Code,
            category = PluginCategory.AI,
            isInstalled = true,
            isEnabled = true
        ),
        Plugin(
            id = "terminal-plus",
            name = "Terminal Plus",
            description = "Enhanced terminal with syntax highlighting, autocomplete, and more",
            version = "1.1.0",
            author = "Cline Team",
            icon = Icons.Default.Terminal,
            category = PluginCategory.TERMINAL,
            isInstalled = true,
            isEnabled = true
        ),
        Plugin(
            id = "file-explorer",
            name = "File Explorer",
            description = "Browse and manage files in your Cline workspace",
            version = "1.0.0",
            author = "Cline Team",
            icon = Icons.Default.Folder,
            category = PluginCategory.UTILITY,
            isInstalled = true,
            isEnabled = false
        ),
        Plugin(
            id = "dark-theme",
            name = "Dark Theme",
            description = "Beautiful dark theme with multiple color variants",
            version = "1.0.0",
            author = "Community",
            category = PluginCategory.THEME,
            isInstalled = false
        ),
        Plugin(
            id = "python-support",
            name = "Python Support",
            description = "Enhanced Python support with linting and execution",
            version = "1.0.0",
            author = "Community",
            icon = Icons.Default.Code,
            category = PluginCategory.LANGUAGE,
            isInstalled = false
        )
    )
    
    val categories = PluginCategory.values().toList()
    var selectedCategory by remember { mutableStateOf<PluginCategory?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    
    val filteredPlugins = plugins.filter { plugin ->
        (selectedCategory == null || plugin.category == selectedCategory) &&
        (searchQuery.isEmpty() || 
         plugin.name.contains(searchQuery, ignoreCase = true) ||
         plugin.description.contains(searchQuery, ignoreCase = true))
    }
    
    val stateMap = remember(plugins) {
        plugins.associate { plugin ->
            plugin.id to when {
                plugin.isEnabled -> PluginState.ENABLED
                plugin.isInstalled -> PluginState.INSTALLED
                else -> PluginState.NOT_INSTALLED
            }
        }
    }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        // Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.large)
                .background(MaterialTheme.colorScheme.surfaceContainer)
                .padding(DesignTokens.Spacing.md)
        ) {
            ClineIconButton(
                onClick = onBack,
                icon = Icons.Default.ArrowBack,
                contentDescription = "Back",
                modifier = Modifier.size(DesignTokens.Spacing.lg)
            )
            
            Spacer(modifier = Modifier.width(DesignTokens.Spacing.sm))
            
            Text(
                text = "Plugins",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        
        // Search field
        ClineSearchField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = "Search plugins...",
            modifier = Modifier
                .fillMaxWidth()
                .padding(DesignTokens.Spacing.md)
        )
        
        // Category filter
        CategoryFilter(
            categories = categories,
            selectedCategory = selectedCategory,
            onCategorySelected = { selectedCategory = it },
            modifier = Modifier.padding(horizontal = DesignTokens.Spacing.md)
        )
        
        // Plugin count
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = DesignTokens.Spacing.md)
        ) {
            Text(
                text = "${filteredPlugins.size} plugins found",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        Spacer(modifier = Modifier.height(DesignTokens.Spacing.sm))
        
        // Plugin list
        PluginList(
            plugins = filteredPlugins,
            stateMap = stateMap,
            onPluginClick = { plugin -> onPluginClick(plugin.id) },
            onInstall = { plugin ->
                // Install plugin
            },
            onUninstall = { plugin ->
                // Uninstall plugin
            },
            onToggle = { plugin ->
                // Toggle plugin
            },
            onFavorite = { plugin ->
                // Toggle favorite
            }
        )
    }
}

// ========================================================================
// PLUGIN DETAIL SCREEN
// ========================================================================

/**
 * Plugin detail screen
 */
@Composable
fun PluginDetailScreen(
    pluginId: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Sample plugin (in real app, this would come from a repository)
    val plugin = Plugin(
        id = pluginId,
        name = "AI Assistant",
        description = "Advanced AI-powered coding assistance with context-aware suggestions. This plugin provides intelligent code completions, explanations, and debugging help.",
        version = "1.2.0",
        author = "Cline Team",
        icon = Icons.Default.Code,
        category = PluginCategory.AI,
        isInstalled = true,
        isEnabled = true
    )
    
    var state by remember { mutableStateOf(PluginState.ENABLED) }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        // Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.large)
                .background(MaterialTheme.colorScheme.surfaceContainer)
                .padding(DesignTokens.Spacing.md)
        ) {
            ClineIconButton(
                onClick = onBack,
                icon = Icons.Default.ArrowBack,
                contentDescription = "Back",
                modifier = Modifier.size(DesignTokens.Spacing.lg)
            )
            
            Spacer(modifier = Modifier.width(DesignTokens.Spacing.sm))
            
            Text(
                text = plugin.name,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        
        // Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(DesignTokens.Spacing.md)
        ) {
            // Plugin icon and info
            Row(
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(DesignTokens.Spacing.xxl)
                        .clip(MaterialTheme.shapes.large)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    plugin.icon?.let { icon ->
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(DesignTokens.Spacing.xl)
                        )
                    } ?: run {
                        Icon(
                            imageVector = Icons.Default.Extension,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(DesignTokens.Spacing.xl)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.width(DesignTokens.Spacing.md))
                
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(DesignTokens.Spacing.sm)
                    ) {
                        Text(
                            text = plugin.name,
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        
                        Box(
                            modifier = Modifier
                                .clip(MaterialTheme.shapes.full)
                                .background(MaterialTheme.colorScheme.primaryContainer)
                                .padding(
                                    horizontal = DesignTokens.Spacing.sm,
                                    vertical = DesignTokens.Spacing.xs
                                )
                        ) {
                            Text(
                                text = plugin.version,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(DesignTokens.Spacing.xs))
                    
                    Text(
                        text = "by ${plugin.author}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(DesignTokens.Spacing.xl))
            
            // Category badge
            Box(
                modifier = Modifier
                    .clip(MaterialTheme.shapes.full)
                    .background(MaterialTheme.colorScheme.secondaryContainer)
                    .padding(
                        horizontal = DesignTokens.Spacing.md,
                        vertical = DesignTokens.Spacing.sm
                    )
            ) {
                Text(
                    text = plugin.category.displayName,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
            
            Spacer(modifier = Modifier.height(DesignTokens.Spacing.xl))
            
            // Description
            Text(
                text = "Description",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(DesignTokens.Spacing.sm))
            
            Text(
                text = plugin.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(DesignTokens.Spacing.xl))
            
            // Status
            Text(
                text = "Status",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(DesignTokens.Spacing.sm))
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(DesignTokens.Spacing.md)
            ) {
                StatusBadge(
                    text = when (state) {
                        PluginState.NOT_INSTALLED -> "Not Installed"
                        PluginState.INSTALLED -> "Installed"
                        PluginState.ENABLED -> "Enabled"
                        PluginState.DISABLED -> "Disabled"
                        PluginState.UPDATABLE -> "Update Available"
                    },
                    color = when (state) {
                        PluginState.NOT_INSTALLED -> MaterialTheme.colorScheme.onSurfaceVariant
                        PluginState.INSTALLED -> MaterialTheme.colorScheme.primary
                        PluginState.ENABLED -> MaterialTheme.colorScheme.primary
                        PluginState.DISABLED -> MaterialTheme.colorScheme.onSurfaceVariant
                        PluginState.UPDATABLE -> MaterialTheme.colorScheme.secondary
                    },
                    backgroundColor = when (state) {
                        PluginState.NOT_INSTALLED -> MaterialTheme.colorScheme.surfaceVariant
                        PluginState.INSTALLED -> MaterialTheme.colorScheme.primaryContainer
                        PluginState.ENABLED -> MaterialTheme.colorScheme.primaryContainer
                        PluginState.DISABLED -> MaterialTheme.colorScheme.surfaceVariant
                        PluginState.UPDATABLE -> MaterialTheme.colorScheme.secondaryContainer
                    }
                )
            }
            
            Spacer(modifier = Modifier.height(DesignTokens.Spacing.xl))
            
            // Actions
            when (state) {
                PluginState.NOT_INSTALLED -> {
                    ClinePrimaryButton(
                        onClick = { state = PluginState.INSTALLED },
                        text = "Install Plugin",
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                PluginState.INSTALLED, PluginState.DISABLED -> {
                    ClinePrimaryButton(
                        onClick = { state = PluginState.ENABLED },
                        text = "Enable Plugin",
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Spacer(modifier = Modifier.height(DesignTokens.Spacing.md))
                    
                    ClinePrimaryButton(
                        onClick = { state = PluginState.NOT_INSTALLED },
                        text = "Uninstall",
                        modifier = Modifier.fillMaxWidth(),
                        enabled = true
                    )
                }
                PluginState.ENABLED -> {
                    ClinePrimaryButton(
                        onClick = { state = PluginState.DISABLED },
                        text = "Disable Plugin",
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Spacer(modifier = Modifier.height(DesignTokens.Spacing.md))
                    
                    ClinePrimaryButton(
                        onClick = { state = PluginState.NOT_INSTALLED },
                        text = "Uninstall",
                        modifier = Modifier.fillMaxWidth(),
                        enabled = true
                    )
                }
                PluginState.UPDATABLE -> {
                    ClinePrimaryButton(
                        onClick = { state = PluginState.ENABLED },
                        text = "Update Plugin",
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

/**
 * Status badge
 */
@Composable
fun StatusBadge(
    text: String,
    color: Color,
    backgroundColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(MaterialTheme.shapes.full)
            .background(backgroundColor)
            .padding(
                horizontal = DesignTokens.Spacing.md,
                vertical = DesignTokens.Spacing.sm
            )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = color
        )
    }
}

// ========================================================================
// INSTALL PLUGIN SCREEN
// ========================================================================

/**
 * Plugin installation screen
 */
@Composable
fun PluginInstallScreen(
    pluginId: String,
    onBack: () -> Unit,
    onComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        // Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.large)
                .background(MaterialTheme.colorScheme.surfaceContainer)
                .padding(DesignTokens.Spacing.md)
        ) {
            ClineIconButton(
                onClick = onBack,
                icon = Icons.Default.ArrowBack,
                contentDescription = "Back",
                modifier = Modifier.size(DesignTokens.Spacing.lg)
            )
            
            Spacer(modifier = Modifier.width(DesignTokens.Spacing.sm))
            
            Text(
                text = "Install Plugin",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        
        // Content
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(DesignTokens.Spacing.xl)
        ) {
            // Installation progress
            CircularProgressIndicator(
                modifier = Modifier.size(DesignTokens.Spacing.xxl)
            )
            
            Spacer(modifier = Modifier.height(DesignTokens.Spacing.xl))
            
            Text(
                text = "Installing plugin...",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(DesignTokens.Spacing.md))
            
            Text(
                text = "Plugin ID: $pluginId",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// ========================================================================
// EMPTY PLUGINS SCREEN
// ========================================================================

/**
 * Empty plugins screen
 */
@Composable
fun EmptyPluginsScreen(
    onRefresh: () -> Unit,
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
        Icon(
            imageVector = Icons.Default.Extension,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(DesignTokens.Spacing.xxxl)
        )
        
        Spacer(modifier = Modifier.height(DesignTokens.Spacing.xl))
        
        Text(
            text = "No plugins found",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Spacer(modifier = Modifier.height(DesignTokens.Spacing.md))
        
        Text(
            text = "Try refreshing the plugin list or check your connection.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(DesignTokens.Spacing.xl))
        
        ClinePrimaryButton(
            onClick = onRefresh,
            text = "Refresh",
            icon = Icons.Default.Refresh
        )
    }
}

// ========================================================================
// PREVIEW
// ========================================================================

/**
 * Preview for plugins screen
 */
@Composable
fun PluginsScreenPreview() {
    MaterialTheme {
        PluginsScreen(
            onBack = {},
            onPluginClick = {}
        )
    }
}

/**
 * Preview for plugin detail screen
 */
@Composable
fun PluginDetailScreenPreview() {
    MaterialTheme {
        PluginDetailScreen(
            pluginId = "ai-assistant",
            onBack = {}
        )
    }
}

/**
 * Preview for empty plugins screen
 */
@Composable
fun EmptyPluginsScreenPreview() {
    MaterialTheme {
        EmptyPluginsScreen(
            onRefresh = {}
        )
    }
}
