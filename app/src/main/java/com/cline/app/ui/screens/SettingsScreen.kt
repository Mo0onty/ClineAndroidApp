// SettingsScreen.kt - Configuration and Preferences
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DataObject
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.FormatColorText
import androidx.compose.material.icons.filled.Globe
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.cline.app.design.components.ClineIconButton
import com.cline.app.design.components.ClinePrimaryButton
import com.cline.app.design.components.ClineSettingsCard
import com.cline.app.design.tokens.DesignTokens

/**
 * Settings Screen - Configuration and Preferences
 * 
 * This screen provides access to all app configuration options:
 * - Appearance settings
 * - Runtime configuration
 * - API keys and authentication
 * - Backup and restore
 * - About information
 */

// ========================================================================
// SETTINGS CATEGORIES
// ========================================================================

/**
 * Settings category
 */
data class SettingsCategory(
    val id: String,
    val title: String,
    val icon: ImageVector,
    val items: List<SettingsItem>
)

/**
 * Settings item
 */
data class SettingsItem(
    val id: String,
    val title: String,
    val description: String? = null,
    val icon: ImageVector,
    val type: SettingsItemType = SettingsItemType.NAVIGATION,
    val action: () -> Unit = {}
)

/**
 * Settings item types
 */
enum class SettingsItemType {
    NAVIGATION, TOGGLE, SELECTOR, INPUT, BUTTON
}

// ========================================================================
// SETTINGS DATA
// ========================================================================

/**
 * Appearance settings
 */
fun getAppearanceSettings(onAction: (String) -> Unit = {}): SettingsCategory {
    return SettingsCategory(
        id = "appearance",
        title = "Appearance",
        icon = Icons.Default.Palette,
        items = listOf(
            SettingsItem(
                id = "theme",
                title = "Theme",
                description = "Dark, Light, or System",
                icon = Icons.Default.ColorLens,
                type = SettingsItemType.SELECTOR,
                action = { onAction("theme") }
            ),
            SettingsItem(
                id = "dynamic_color",
                title = "Dynamic Color",
                description = "Use system accent color",
                icon = Icons.Default.Brush,
                type = SettingsItemType.TOGGLE,
                action = { onAction("dynamic_color") }
            ),
            SettingsItem(
                id = "font_size",
                title = "Font Size",
                description = "Adjust text size",
                icon = Icons.Default.FormatColorText,
                type = SettingsItemType.SELECTOR,
                action = { onAction("font_size") }
            )
        )
    )
}

/**
 * Runtime settings
 */
fun getRuntimeSettings(onAction: (String) -> Unit = {}): SettingsCategory {
    return SettingsCategory(
        id = "runtime",
        title = "Runtime",
        icon = Icons.Default.Terminal,
        items = listOf(
            SettingsItem(
                id = "model",
                title = "AI Model",
                description = "Select AI model",
                icon = Icons.Default.Code,
                type = SettingsItemType.SELECTOR,
                action = { onAction("model") }
            ),
            SettingsItem(
                id = "runtime_version",
                title = "Runtime Version",
                description = "Cline runtime version",
                icon = Icons.Default.Memory,
                type = SettingsItemType.NAVIGATION,
                action = { onAction("runtime_version") }
            ),
            SettingsItem(
                id = "storage",
                title = "Storage Location",
                description = "Where runtime files are stored",
                icon = Icons.Default.Folder,
                type = SettingsItemType.NAVIGATION,
                action = { onAction("storage") }
            )
        )
    )
}

/**
 * API settings
 */
fun getApiSettings(onAction: (String) -> Unit = {}): SettingsCategory {
    return SettingsCategory(
        id = "api",
        title = "API Keys",
        icon = Icons.Default.Key,
        items = listOf(
            SettingsItem(
                id = "api_key",
                title = "API Key",
                description = "Configure your API key",
                icon = Icons.Default.Security,
                type = SettingsItemType.NAVIGATION,
                action = { onAction("api_key") }
            ),
            SettingsItem(
                id = "authentication",
                title = "Authentication",
                description = "Sign in to sync settings",
                icon = Icons.Default.VerifiedUser,
                type = SettingsItemType.NAVIGATION,
                action = { onAction("authentication") }
            )
        )
    )
}

/**
 * Backup settings
 */
fun getBackupSettings(onAction: (String) -> Unit = {}): SettingsCategory {
    return SettingsCategory(
        id = "backup",
        title = "Backup & Restore",
        icon = Icons.Default.Backup,
        items = listOf(
            SettingsItem(
                id = "backup",
                title = "Create Backup",
                description = "Backup your data",
                icon = Icons.Default.Backup,
                type = SettingsItemType.BUTTON,
                action = { onAction("backup") }
            ),
            SettingsItem(
                id = "restore",
                title = "Restore Backup",
                description = "Restore from backup",
                icon = Icons.Default.Storage,
                type = SettingsItemType.BUTTON,
                action = { onAction("restore") }
            ),
            SettingsItem(
                id = "auto_backup",
                title = "Auto Backup",
                description = "Automatically backup on exit",
                icon = Icons.Default.Settings,
                type = SettingsItemType.TOGGLE,
                action = { onAction("auto_backup") }
            )
        )
    )
}

/**
 * About settings
 */
fun getAboutSettings(onAction: (String) -> Unit = {}): SettingsCategory {
    return SettingsCategory(
        id = "about",
        title = "About",
        icon = Icons.Default.Info,
        items = listOf(
            SettingsItem(
                id = "version",
                title = "Version",
                description = "1.0.0",
                icon = Icons.Default.Info,
                type = SettingsItemType.NAVIGATION,
                action = { onAction("version") }
            ),
            SettingsItem(
                id = "documentation",
                title = "Documentation",
                description = "View documentation",
                icon = Icons.Default.DataObject,
                type = SettingsItemType.NAVIGATION,
                action = { onAction("documentation") }
            ),
            SettingsItem(
                id = "feedback",
                title = "Feedback",
                description = "Send feedback",
                icon = Icons.Default.Face,
                type = SettingsItemType.NAVIGATION,
                action = { onAction("feedback") }
            )
        )
    )
}

// ========================================================================
// SETTINGS CATEGORY
// ========================================================================

/**
 * Settings category header
 */
@Composable
fun SettingsCategoryHeader(
    category: SettingsCategory,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(DesignTokens.Spacing.md)
    ) {
        Icon(
            imageVector = category.icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .size(DesignTokens.Spacing.lg)
                .padding(end = DesignTokens.Spacing.md)
        )
        
        Text(
            text = category.title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

// ========================================================================
// SETTINGS ITEM
// ========================================================================

/**
 * Settings item row
 */
@Composable
fun SettingsItemRow(
    item: SettingsItem,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .clickable(onClick = item.action)
            .padding(DesignTokens.Spacing.md)
    ) {
        Icon(
            imageVector = item.icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .size(DesignTokens.Spacing.lg)
                .padding(end = DesignTokens.Spacing.md)
        )
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            item.description?.let { description ->
                Spacer(modifier = Modifier.height(DesignTokens.Spacing.xs))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        // Item type indicator
        when (item.type) {
            SettingsItemType.NAVIGATION -> {
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            SettingsItemType.TOGGLE -> {
                Switch(
                    checked = false,
                    onCheckedChange = { item.action() }
                )
            }
            SettingsItemType.SELECTOR -> {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Value",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            SettingsItemType.BUTTON -> {
                // No indicator for buttons
            }
            SettingsItemType.INPUT -> {
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

// ========================================================================
// SETTINGS SCREEN
// ========================================================================

/**
 * Main settings screen
 */
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onBackupClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    
    // Settings state
    val categories = listOf(
        getAppearanceSettings { action ->
            // Handle appearance settings
        },
        getRuntimeSettings { action ->
            // Handle runtime settings
        },
        getApiSettings { action ->
            // Handle API settings
        },
        getBackupSettings { action ->
            when (action) {
                "backup" -> onBackupClick()
                "restore" -> onBackupClick()
                else -> {}
            }
        },
        getAboutSettings { action ->
            // Handle about settings
        }
    )
    
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
                text = "Settings",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        
        // Settings content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            categories.forEach { category ->
                SettingsCategoryHeader(category = category)
                
                category.items.forEach { item ->
                    SettingsItemRow(item = item)
                }
                
                Spacer(modifier = Modifier.height(DesignTokens.Spacing.sm))
            }
            
            Spacer(modifier = Modifier.height(DesignTokens.Spacing.md))
        }
    }
}

// ========================================================================
// APPEARANCE SETTINGS SCREEN
// ========================================================================

/**
 * Appearance settings screen
 */
@Composable
fun AppearanceSettingsScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var darkTheme by remember { mutableStateOf(false) }
    var dynamicColor by remember { mutableStateOf(true) }
    
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
                text = "Appearance",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        
        // Settings content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(DesignTokens.Spacing.md)
        ) {
            // Theme selection
            SettingSection(
                title = "Theme",
                description = "Choose your preferred color theme"
            ) {
                ThemeOption(
                    title = "System",
                    description = "Match system settings",
                    selected = !darkTheme,
                    onClick = { darkTheme = false }
                )
                
                ThemeOption(
                    title = "Dark",
                    description = "Dark theme",
                    selected = darkTheme,
                    onClick = { darkTheme = true }
                )
                
                ThemeOption(
                    title = "Light",
                    description = "Light theme",
                    selected = !darkTheme,
                    onClick = { darkTheme = false }
                )
            }
            
            Spacer(modifier = Modifier.height(DesignTokens.Spacing.md))
            
            // Dynamic color
            SettingSection(
                title = "Dynamic Color",
                description = "Use system accent color for theming"
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(MaterialTheme.shapes.medium)
                        .clickable(onClick = { dynamicColor = !dynamicColor })
                        .padding(DesignTokens.Spacing.md)
                ) {
                    Text(
                        text = "Dynamic Color",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )
                    
                    Switch(
                        checked = dynamicColor,
                        onCheckedChange = { dynamicColor = it }
                    )
                }
            }
        }
    }
}

/**
 * Setting section
 */
@Composable
fun SettingSection(
    title: String,
    description: String? = null,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Column(modifier = modifier) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        description?.let { desc ->
            Spacer(modifier = Modifier.height(DesignTokens.Spacing.xs))
            Text(
                text = desc,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        Spacer(modifier = Modifier.height(DesignTokens.Spacing.md))
        
        content()
    }
}

/**
 * Theme option
 */
@Composable
fun ThemeOption(
    title: String,
    description: String,
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
                    MaterialTheme.colorScheme.surfaceContainer
                }
            )
            .clickable(onClick = onClick)
            .padding(DesignTokens.Spacing.md)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(DesignTokens.Spacing.xs))
            
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        if (selected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Selected",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

// ========================================================================
// API KEY SCREEN
// ========================================================================

/**
 * API key configuration screen
 */
@Composable
fun ApiKeySettingsScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var apiKey by remember { mutableStateOf("") }
    var showKey by remember { mutableStateOf(false) }
    
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
                text = "API Key",
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
            Text(
                text = "Configure your API key for accessing Cline services.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(DesignTokens.Spacing.xl))
            
            // API key input
            com.cline.app.design.components.ClineTextField(
                value = apiKey,
                onValueChange = { apiKey = it },
                label = "API Key",
                placeholder = "Enter your API key",
                visualTransformation = if (showKey) {
                    androidx.compose.ui.text.input.VisualTransformation.None
                } else {
                    androidx.compose.ui.text.input.PasswordVisualTransformation()
                },
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(DesignTokens.Spacing.md))
            
            // Show/hide key
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.medium)
                    .clickable(onClick = { showKey = !showKey })
                    .padding(DesignTokens.Spacing.md)
            ) {
                Icon(
                    imageVector = if (showKey) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.width(DesignTokens.Spacing.sm))
                
                Text(
                    text = if (showKey) "Hide key" else "Show key",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Spacer(modifier = Modifier.height(DesignTokens.Spacing.xl))
            
            // Save button
            ClinePrimaryButton(
                onClick = { /* Save API key */ },
                text = "Save API Key",
                modifier = Modifier.fillMaxWidth(),
                enabled = apiKey.isNotBlank()
            )
            
            Spacer(modifier = Modifier.height(DesignTokens.Spacing.md))
            
            // Info
            ClineSettingsCard(
                title = "Need an API key?",
                subtitle = "Get your API key from the Cline website",
                onClick = { /* Open website */ }
            )
        }
    }
}

// ========================================================================
// PREVIEW
// ========================================================================

/**
 * Preview for settings screen
 */
@Composable
fun SettingsScreenPreview() {
    MaterialTheme {
        SettingsScreen(
            onBack = {},
            onBackupClick = {}
        )
    }
}

/**
 * Preview for appearance settings screen
 */
@Composable
fun AppearanceSettingsScreenPreview() {
    MaterialTheme {
        AppearanceSettingsScreen(
            onBack = {}
        )
    }
}

/**
 * Preview for API key settings screen
 */
@Composable
fun ApiKeySettingsScreenPreview() {
    MaterialTheme {
        ApiKeySettingsScreen(
            onBack = {}
        )
    }
}
