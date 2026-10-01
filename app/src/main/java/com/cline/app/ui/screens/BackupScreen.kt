// BackupScreen.kt - Backup and Restore Interface
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Update
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.cline.app.design.components.ClineBackupCard
import com.cline.app.design.components.ClineIconButton
import com.cline.app.design.components.ClinePrimaryButton
import com.cline.app.design.components.ClineSettingsCard
import com.cline.app.design.tokens.DesignTokens
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Backup Screen - Backup and Restore Interface
 * 
 * This screen provides access to backup management features:
 * - Create new backups
 * - Restore from backups
 * - Delete backups
 * - Configure auto-backup
 */

// ========================================================================
// DATA CLASSES
// ========================================================================

/**
 * Backup data class
 */
data class Backup(
    val id: String,
    val name: String,
    val timestamp: Long = System.currentTimeMillis(),
    val size: Long = 0,
    val description: String? = null,
    val version: String = "1.0",
    val isEncrypted: Boolean = true
)

/**
 * Backup state
 */
data class BackupState(
    val backups: List<Backup> = emptyList(),
    val isLoading: Boolean = false,
    val isCreating: Boolean = false,
    val isRestoring: Boolean = false,
    val error: String? = null,
    val autoBackupEnabled: Boolean = false
)

/**
 * Backup status
 */
enum class BackupStatus {
    IDLE, CREATING, RESTORING, DELETING, ERROR, SUCCESS
}

// ========================================================================
// FORMATTING
// ========================================================================

/**
 * Format date for display
 */
fun formatDate(timestamp: Long): String {
    val date = Date(timestamp)
    val format = SimpleDateFormat("MMM dd, yyyy - HH:mm", Locale.getDefault())
    return format.format(date)
}

/**
 * Format file size for display
 */
fun formatSize(bytes: Long): String {
    return when {
        bytes >= 1024 * 1024 * 1024 -> "%.2f GB".format(bytes / (1024.0 * 1024.0 * 1024.0))
        bytes >= 1024 * 1024 -> "%.2f MB".format(bytes / (1024.0 * 1024.0))
        bytes >= 1024 -> "%.2f KB".format(bytes / 1024.0)
        else -> "$bytes B"
    }
}

// ========================================================================
// BACKUP CARD
// ========================================================================

/**
 * Backup card with actions
 */
@Composable
fun BackupCard(
    backup: Backup,
    onRestore: () -> Unit = {},
    onDelete: () -> Unit = {},
    onShare: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    ClineBackupCard(
        name = backup.name,
        date = formatDate(backup.timestamp),
        size = formatSize(backup.size),
        onRestore = onRestore,
        onDelete = onDelete
    )
}

// ========================================================================
// BACKUP LIST
// ========================================================================

/**
 * List of backups
 */
@Composable
fun BackupList(
    backups: List<Backup>,
    onRestore: (Backup) -> Unit = {},
    onDelete: (Backup) -> Unit = {},
    onShare: (Backup) -> Unit = {},
    modifier: Modifier = Modifier
) {
    if (backups.isEmpty()) {
        EmptyBackupList(
            onCreate = {},
            modifier = modifier.fillMaxSize()
        )
    } else {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(DesignTokens.Spacing.md),
            verticalArrangement = Arrangement.spacedBy(DesignTokens.Spacing.md)
        ) {
            items(backups) { backup ->
                BackupCard(
                    backup = backup,
                    onRestore = { onRestore(backup) },
                    onDelete = { onDelete(backup) },
                    onShare = { onShare(backup) }
                )
            }
        }
    }
}

/**
 * Empty backup list
 */
@Composable
fun EmptyBackupList(
    onCreate: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .background(MaterialTheme.colorScheme.surface)
            .padding(DesignTokens.Spacing.xl)
    ) {
        Icon(
            imageVector = Icons.Default.Backup,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(DesignTokens.Spacing.xxxl)
        )
        
        Spacer(modifier = Modifier.height(DesignTokens.Spacing.xl))
        
        Text(
            text = "No backups yet",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Spacer(modifier = Modifier.height(DesignTokens.Spacing.md))
        
        Text(
            text = "Create a backup to save your Cline configuration, chats, and plugins.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(DesignTokens.Spacing.xl))
        
        ClinePrimaryButton(
            onClick = onCreate,
            text = "Create Backup",
            icon = Icons.Default.Add
        )
    }
}

// ========================================================================
// BACKUP SCREEN
// ========================================================================

/**
 * Main backup screen
 */
@Composable
fun BackupScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Sample backups
    val backups = listOf(
        Backup(
            id = "1",
            name = "Full Backup - May 2024",
            timestamp = System.currentTimeMillis() - 86400000 * 5, // 5 days ago
            size = 150 * 1024 * 1024, // 150 MB
            description = "Complete backup with all data"
        ),
        Backup(
            id = "2",
            name = "Config Only - May 2024",
            timestamp = System.currentTimeMillis() - 86400000 * 2, // 2 days ago
            size = 50 * 1024 * 1024, // 50 MB
            description = "Configuration and settings"
        ),
        Backup(
            id = "3",
            name = "Quick Save - Today",
            timestamp = System.currentTimeMillis() - 3600000, // 1 hour ago
            size = 10 * 1024 * 1024, // 10 MB
            description = "Recent chats"
        )
    )
    
    var backupState by remember {
        mutableStateOf(
            BackupState(
                backups = backups,
                isLoading = false,
                isCreating = false,
                isRestoring = false,
                error = null,
                autoBackupEnabled = true
            )
        )
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
                text = "Backup & Restore",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        
        // Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Backup actions
            Row(
                horizontalArrangement = Arrangement.spacedBy(DesignTokens.Spacing.md),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(DesignTokens.Spacing.md)
            ) {
                ClinePrimaryButton(
                    onClick = {
                        backupState = backupState.copy(isCreating = true)
                        // Simulate backup creation
                        backupState = backupState.copy(
                            isCreating = false,
                            backups = backupState.backups + Backup(
                                id = (backupState.backups.size + 1).toString(),
                                name = "New Backup - ${formatDate(System.currentTimeMillis())}",
                                size = 100 * 1024 * 1024
                            )
                        )
                    },
                    text = "Create Backup",
                    icon = Icons.Default.Save,
                    modifier = Modifier.weight(1f),
                    enabled = !backupState.isCreating && !backupState.isRestoring
                )
                
                ClinePrimaryButton(
                    onClick = { /* Export backup */ },
                    text = "Export",
                    icon = Icons.Default.CloudUpload,
                    modifier = Modifier.weight(1f),
                    enabled = !backupState.isCreating && !backupState.isRestoring
                )
            }
            
            // Auto-backup setting
            ClineSettingsCard(
                title = "Auto Backup",
                subtitle = if (backupState.autoBackupEnabled) "Enabled" else "Disabled",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(DesignTokens.Spacing.md)
                    .clickable(onClick = {
                        backupState = backupState.copy(
                            autoBackupEnabled = !backupState.autoBackupEnabled
                        )
                    })
            )
            
            // Backup count
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(DesignTokens.Spacing.md)
            ) {
                Text(
                    text = "${backupState.backups.size} backups",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            // Backup list
            BackupList(
                backups = backupState.backups,
                onRestore = { backup ->
                    backupState = backupState.copy(isRestoring = true)
                    // Simulate restore
                    backupState = backupState.copy(isRestoring = false)
                },
                onDelete = { backup ->
                    backupState = backupState.copy(
                        backups = backupState.backups.filter { it.id != backup.id }
                    )
                },
                onShare = { backup ->
                    // Share backup
                }
            )
        }
    }
}

// ========================================================================
// CREATE BACKUP DIALOG
// ========================================================================

/**
 * Create backup dialog
 */
@Composable
fun CreateBackupDialog(
    onDismiss: () -> Unit,
    onCreate: (String, String?) -> Unit,
    modifier: Modifier = Modifier
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var isCreating by remember { mutableStateOf(false) }
    
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(DesignTokens.Spacing.md)
    ) {
        Text(
            text = "Create Backup",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Spacer(modifier = Modifier.height(DesignTokens.Spacing.md))
        
        // Name field
        com.cline.app.design.components.ClineTextField(
            value = name,
            onValueChange = { name = it },
            label = "Backup Name",
            placeholder = "My Backup - ${formatDate(System.currentTimeMillis())}",
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(DesignTokens.Spacing.md))
        
        // Description field
        com.cline.app.design.components.ClineTextField(
            value = description,
            onValueChange = { description = it },
            label = "Description (Optional)",
            placeholder = "Brief description of what's included",
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(DesignTokens.Spacing.xl))
        
        // Actions
        Row(
            horizontalArrangement = Arrangement.End,
            modifier = Modifier.fillMaxWidth()
        ) {
            ClinePrimaryButton(
                onClick = onDismiss,
                text = "Cancel",
                modifier = Modifier.padding(end = DesignTokens.Spacing.md),
                enabled = !isCreating
            )
            
            ClinePrimaryButton(
                onClick = {
                    isCreating = true
                    onCreate(name, description)
                },
                text = "Create",
                enabled = name.isNotBlank() && !isCreating
            )
        }
        
        if (isCreating) {
            Spacer(modifier = Modifier.height(DesignTokens.Spacing.md))
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}

// ========================================================================
// RESTORE BACKUP DIALOG
// ========================================================================

/**
 * Restore backup dialog
 */
@Composable
fun RestoreBackupDialog(
    backup: Backup,
    onDismiss: () -> Unit,
    onRestore: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isRestoring by remember { mutableStateOf(false) }
    
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(DesignTokens.Spacing.md)
    ) {
        Text(
            text = "Restore Backup",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Spacer(modifier = Modifier.height(DesignTokens.Spacing.md))
        
        // Backup info
        Column(
            modifier = Modifier
                .clip(MaterialTheme.shapes.medium)
                .background(MaterialTheme.colorScheme.surface)
                .padding(DesignTokens.Spacing.md)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Backup,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .size(DesignTokens.Spacing.lg)
                        .padding(end = DesignTokens.Spacing.md)
                )
                
                Column {
                    Text(
                        text = backup.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    Text(
                        text = "${formatDate(backup.timestamp)} - ${formatSize(backup.size)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            if (backup.description != null) {
                Spacer(modifier = Modifier.height(DesignTokens.Spacing.md))
                Text(
                    text = backup.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        Spacer(modifier = Modifier.height(DesignTokens.Spacing.xl))
        
        // Warning
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clip(MaterialTheme.shapes.small)
                .background(MaterialTheme.colorScheme.errorContainer)
                .padding(DesignTokens.Spacing.md)
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onErrorContainer
            )
            
            Spacer(modifier = Modifier.width(DesignTokens.Spacing.sm))
            
            Text(
                text = "This will overwrite your current data. This action cannot be undone.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
        }
        
        Spacer(modifier = Modifier.height(DesignTokens.Spacing.xl))
        
        // Actions
        Row(
            horizontalArrangement = Arrangement.End,
            modifier = Modifier.fillMaxWidth()
        ) {
            ClinePrimaryButton(
                onClick = onDismiss,
                text = "Cancel",
                modifier = Modifier.padding(end = DesignTokens.Spacing.md),
                enabled = !isRestoring
            )
            
            ClinePrimaryButton(
                onClick = {
                    isRestoring = true
                    onRestore()
                },
                text = "Restore",
                enabled = !isRestoring
            )
        }
        
        if (isRestoring) {
            Spacer(modifier = Modifier.height(DesignTokens.Spacing.md))
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}

// ========================================================================
// DELETE BACKUP DIALOG
// ========================================================================

/**
 * Delete backup confirmation dialog
 */
@Composable
fun DeleteBackupDialog(
    backup: Backup,
    onDismiss: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isDeleting by remember { mutableStateOf(false) }
    
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(DesignTokens.Spacing.md)
    ) {
        Text(
            text = "Delete Backup",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Spacer(modifier = Modifier.height(DesignTokens.Spacing.md))
        
        Text(
            text = "Are you sure you want to delete "${backup.name}"?",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Spacer(modifier = Modifier.height(DesignTokens.Spacing.md))
        
        Text(
            text = "This action cannot be undone.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(DesignTokens.Spacing.xl))
        
        // Actions
        Row(
            horizontalArrangement = Arrangement.End,
            modifier = Modifier.fillMaxWidth()
        ) {
            ClinePrimaryButton(
                onClick = onDismiss,
                text = "Cancel",
                modifier = Modifier.padding(end = DesignTokens.Spacing.md),
                enabled = !isDeleting
            )
            
            ClinePrimaryButton(
                onClick = {
                    isDeleting = true
                    onDelete()
                },
                text = "Delete",
                enabled = !isDeleting
            )
        }
        
        if (isDeleting) {
            Spacer(modifier = Modifier.height(DesignTokens.Spacing.md))
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}

// ========================================================================
// BACKUP STATUS INDICATOR
// ========================================================================

/**
 * Backup status indicator
 */
@Composable
fun BackupStatusIndicator(
    status: BackupStatus,
    modifier: Modifier = Modifier
) {
    val (text, color, icon) = when (status) {
        BackupStatus.IDLE -> "Ready" to MaterialTheme.colorScheme.onSurfaceVariant to Icons.Default.Check
        BackupStatus.CREATING -> "Creating..." to MaterialTheme.colorScheme.primary to Icons.Default.CloudUpload
        BackupStatus.RESTORING -> "Restoring..." to MaterialTheme.colorScheme.primary to Icons.Default.CloudDownload
        BackupStatus.DELETING -> "Deleting..." to MaterialTheme.colorScheme.error to Icons.Default.Delete
        BackupStatus.ERROR -> "Error" to MaterialTheme.colorScheme.error to Icons.Default.Info
        BackupStatus.SUCCESS -> "Success!" to MaterialTheme.colorScheme.primary to Icons.Default.Check
    }
    
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clip(MaterialTheme.shapes.full)
            .background(
                when (status) {
                    BackupStatus.IDLE -> MaterialTheme.colorScheme.surfaceVariant
                    BackupStatus.CREATING, BackupStatus.RESTORING -> MaterialTheme.colorScheme.primaryContainer
                    BackupStatus.DELETING, BackupStatus.ERROR -> MaterialTheme.colorScheme.errorContainer
                    BackupStatus.SUCCESS -> MaterialTheme.colorScheme.primaryContainer
                }
            )
            .padding(
                horizontal = DesignTokens.Spacing.md,
                vertical = DesignTokens.Spacing.sm
            )
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color
        )
        
        Spacer(modifier = Modifier.width(DesignTokens.Spacing.sm))
        
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = color
        )
    }
}

// ========================================================================
// PREVIEW
// ========================================================================

/**
 * Preview for backup screen
 */
@Composable
fun BackupScreenPreview() {
    MaterialTheme {
        BackupScreen(
            onBack = {}
        )
    }
}

/**
 * Preview for create backup dialog
 */
@Composable
fun CreateBackupDialogPreview() {
    MaterialTheme {
        CreateBackupDialog(
            onDismiss = {},
            onCreate = { _, _ -> }
        )
    }
}

/**
 * Preview for restore backup dialog
 */
@Composable
fun RestoreBackupDialogPreview() {
    MaterialTheme {
        val backup = Backup(
            id = "1",
            name = "Full Backup",
            timestamp = System.currentTimeMillis(),
            size = 100 * 1024 * 1024,
            description = "Complete backup"
        )
        RestoreBackupDialog(
            backup = backup,
            onDismiss = {},
            onRestore = {}
        )
    }
}

/**
 * Preview for delete backup dialog
 */
@Composable
fun DeleteBackupDialogPreview() {
    MaterialTheme {
        val backup = Backup(
            id = "1",
            name = "Old Backup",
            timestamp = System.currentTimeMillis(),
            size = 100 * 1024 * 1024
        )
        DeleteBackupDialog(
            backup = backup,
            onDismiss = {},
            onDelete = {}
        )
    }
}
