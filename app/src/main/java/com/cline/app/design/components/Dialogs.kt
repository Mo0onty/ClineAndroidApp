// Dialogs.kt - Custom Dialog Components with MD3 Expressive
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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.cline.app.design.tokens.DesignTokens
import kotlinx.coroutines.launch

/**
 * Custom Dialog Components for Cline Android
 * 
 * All dialogs follow Material Design 3 Expressive principles with:
 * - Spring animations for appearance
 * - Dynamic color support
 * - Consistent spacing and sizing
 * - Accessibility support
 */

// ========================================================================
// ALERT DIALOGS
// ========================================================================

/**
 * Standard alert dialog with Cline styling
 * 
 * @param title Dialog title
 * @param message Dialog message
 * @param confirmText Text for confirm button
 * @param dismissText Text for dismiss button (null to hide)
 * @param onConfirm Callback when confirm button is clicked
 * @param onDismiss Callback when dismiss button is clicked or dialog is dismissed
 * @param icon Optional icon to display
 * @param iconColor Color for the icon
 */
@Composable
fun ClineAlertDialog(
    title: String,
    message: String,
    confirmText: String = "OK",
    dismissText: String? = "Cancel",
    onConfirm: () -> Unit,
    onDismiss: () -> Unit = {},
    icon: ImageVector? = null,
    iconColor: Color = MaterialTheme.colorScheme.primary,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            ClinePrimaryButton(
                onClick = {
                    onConfirm()
                    onDismiss()
                },
                text = confirmText
            )
        },
        dismissButton = dismissText?.let { text ->
            {
                TextButton(
                    onClick = onDismiss
                ) {
                    Text(
                        text = text,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                icon?.let { img ->
                    Icon(
                        imageVector = img,
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier
                            .size(DesignTokens.Spacing.xl)
                            .padding(end = DesignTokens.Spacing.sm)
                    )
                }
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall
                )
            }
        },
        text = {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        shape = MaterialTheme.shapes.large,
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        titleContentColor = MaterialTheme.colorScheme.onSurface,
        textContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier
    )
}

/**
 * Info dialog
 */
@Composable
fun ClineInfoDialog(
    title: String,
    message: String,
    onDismiss: () -> Unit = {}
) {
    ClineAlertDialog(
        title = title,
        message = message,
        confirmText = "OK",
        dismissText = null,
        onConfirm = onDismiss,
        onDismiss = onDismiss,
        icon = Icons.Default.Info,
        iconColor = MaterialTheme.colorScheme.primary
    )
}

/**
 * Warning dialog
 */
@Composable
fun ClineWarningDialog(
    title: String,
    message: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit = {}
) {
    ClineAlertDialog(
        title = title,
        message = message,
        confirmText = "Continue",
        dismissText = "Cancel",
        onConfirm = onConfirm,
        onDismiss = onDismiss,
        icon = Icons.Default.Warning,
        iconColor = MaterialTheme.colorScheme.error
    )
}

/**
 * Error dialog
 */
@Composable
fun ClineErrorDialog(
    title: String,
    message: String,
    onDismiss: () -> Unit = {}
) {
    ClineAlertDialog(
        title = title,
        message = message,
        confirmText = "OK",
        dismissText = null,
        onConfirm = onDismiss,
        onDismiss = onDismiss,
        icon = Icons.Default.Error,
        iconColor = MaterialTheme.colorScheme.error
    )
}

/**
 * Success dialog
 */
@Composable
fun ClineSuccessDialog(
    title: String,
    message: String,
    onDismiss: () -> Unit = {}
) {
    ClineAlertDialog(
        title = title,
        message = message,
        confirmText = "OK",
        dismissText = null,
        onConfirm = onDismiss,
        onDismiss = onDismiss,
        icon = Icons.Default.Check,
        iconColor = MaterialTheme.colorScheme.primary
    )
}

/**
 * Confirmation dialog with destructive action
 */
@Composable
fun ClineConfirmationDialog(
    title: String,
    message: String,
    confirmText: String = "Delete",
    onConfirm: () -> Unit,
    onDismiss: () -> Unit = {}
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm()
                    onDismiss()
                },
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text(confirmText)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text(
                    "Cancel",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        shape = MaterialTheme.shapes.large,
        containerColor = MaterialTheme.colorScheme.surfaceContainer
    )
}

// ========================================================================
// CUSTOM DIALOGS
// ========================================================================

/**
 * Custom dialog with full control over content
 */
@Composable
fun ClineDialog(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    title: String? = null,
    content: @Composable () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        Card(
            modifier = modifier
                .clip(MaterialTheme.shapes.large)
                .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.5f)),
            elevation = CardDefaults.cardElevation(
                defaultElevation = DesignTokens.Elevation.Level3
            ),
            shape = MaterialTheme.shapes.large,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer,
                contentColor = MaterialTheme.colorScheme.onSurface
            )
        ) {
            Column(
                modifier = Modifier
                    .padding(DesignTokens.Spacing.md)
                    .verticalScroll(rememberScrollState())
            ) {
                title?.let { t ->
                    Text(
                        text = t,
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = DesignTokens.Spacing.md)
                    )
                }
                
                content()
            }
        }
    }
}

/**
 * Input dialog for collecting user input
 */
@Composable
fun ClineInputDialog(
    title: String,
    value: String,
    onValueChange: (String) -> Unit,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit = {},
    label: String = "Input",
    placeholder: String = "",
    confirmText: String = "OK",
    dismissText: String = "Cancel"
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            ClinePrimaryButton(
                onClick = {
                    onConfirm(value)
                    onDismiss()
                },
                text = confirmText,
                enabled = value.isNotBlank()
            )
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text(
                    text = dismissText,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Column {
                ClineTextField(
                    value = value,
                    onValueChange = onValueChange,
                    label = label,
                    placeholder = placeholder,
                    singleLine = true
                )
            }
        },
        shape = MaterialTheme.shapes.large,
        containerColor = MaterialTheme.colorScheme.surfaceContainer
    )
}

// ========================================================================
// BOTTOM SHEETS
// ========================================================================

/**
 * Modal bottom sheet with Cline styling
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClineBottomSheet(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    title: String? = null,
    content: @Composable () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    val scope = rememberCoroutineScope()
    
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        contentColor = MaterialTheme.colorScheme.onSurface,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(DesignTokens.Spacing.md)
                    .size(width = DesignTokens.Spacing.xl, height = DesignTokens.Spacing.sm)
                    .clip(RoundedCornerShape(DesignTokens.Spacing.xs))
                    .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f))
            )
        }
    ) {
        Column(
            modifier = Modifier
                .padding(DesignTokens.Spacing.md)
                .verticalScroll(rememberScrollState())
        ) {
            title?.let { t ->
                Text(
                    text = t,
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = DesignTokens.Spacing.md)
                )
            }
            
            content()
            
            Spacer(modifier = Modifier.height(DesignTokens.Spacing.md))
        }
    }
}

/**
 * Action bottom sheet with list of actions
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClineActionBottomSheet(
    onDismiss: () -> Unit,
    title: String,
    actions: List<ActionItem>,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        Column(
            modifier = Modifier
                .padding(DesignTokens.Spacing.md)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = DesignTokens.Spacing.md)
            )
            
            actions.forEach { action ->
                ActionItemRow(
                    action = action,
                    onClick = {
                        action.onClick()
                        onDismiss()
                    }
                )
                
                if (action != actions.last()) {
                    Spacer(modifier = Modifier.height(DesignTokens.Spacing.xs))
                }
            }
            
            Spacer(modifier = Modifier.height(DesignTokens.Spacing.md))
        }
    }
}

/**
 * Action item data class
 */
data class ActionItem(
    val text: String,
    val icon: ImageVector? = null,
    val onClick: () -> Unit
)

/**
 * Action item row
 */
@Composable
fun ActionItemRow(
    action: ActionItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.surface)
            .padding(DesignTokens.Spacing.md)
    ) {
        action.icon?.let { icon ->
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .size(DesignTokens.Spacing.lg)
                    .padding(end = DesignTokens.Spacing.md)
            )
        }
        
        Text(
            text = action.text,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
        
        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Select",
            tint = MaterialTheme.colorScheme.primary
        )
    }
}

// ========================================================================
// LOADING INDICATORS
// ========================================================================

/**
 * Full-screen loading overlay
 */
@Composable
fun ClineLoadingOverlay(
    message: String = "Loading...",
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.5f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterVertically,
            verticalArrangement = Arrangement.Center
        ) {
            LoadingSpinner(size = DesignTokens.Spacing.xxl)
            
            Spacer(modifier = Modifier.height(DesignTokens.Spacing.md))
            
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

/**
 * Inline loading indicator
 */
@Composable
fun ClineInlineLoading(
    message: String = "Loading...",
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = modifier
    ) {
        LoadingSpinner(size = DesignTokens.Spacing.lg)
        
        Spacer(modifier = Modifier.width(DesignTokens.Spacing.sm))
        
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// ========================================================================
// TOAST / SNACKBAR
// ========================================================================

/**
 * Simple toast message
 */
@Composable
fun ClineToast(
    message: String,
    modifier: Modifier = Modifier,
    duration: Int = 3000
) {
    var visible by remember { mutableStateOf(true) }
    
    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically() + fadeIn(),
        exit = slideOutVertically() + fadeOut(),
        modifier = modifier
    ) {
        Card(
            modifier = Modifier
                .padding(DesignTokens.Spacing.md)
                .clip(MaterialTheme.shapes.medium),
            elevation = CardDefaults.cardElevation(
                defaultElevation = DesignTokens.Elevation.Level3
            ),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer,
                contentColor = MaterialTheme.colorScheme.onSurface
            )
        ) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(DesignTokens.Spacing.md)
            )
        }
    }
}

/**
 * Snackbar with action
 */
@Composable
fun ClineSnackbar(
    message: String,
    actionText: String,
    onAction: () -> Unit,
    modifier: Modifier = Modifier
) {
    var visible by remember { mutableStateOf(true) }
    
    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically() + fadeIn(),
        exit = slideOutVertically() + fadeOut(),
        modifier = modifier
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(DesignTokens.Spacing.md)
                .clip(MaterialTheme.shapes.medium),
            elevation = CardDefaults.cardElevation(
                defaultElevation = DesignTokens.Elevation.Level3
            ),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer,
                contentColor = MaterialTheme.colorScheme.onSurface
            )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(DesignTokens.Spacing.md)
            ) {
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f)
                )
                
                Spacer(modifier = Modifier.width(DesignTokens.Spacing.md))
                
                TextButton(
                    onClick = {
                        onAction()
                        visible = false
                    }
                ) {
                    Text(
                        text = actionText,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}
