// Cards.kt - Custom Card Components with MD3 Expressive
package com.cline.app.design.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.cline.app.design.tokens.DesignTokens

/**
 * Custom Card Components for Cline Android
 * 
 * Cards provide surfaces for displaying content with elevation and shadows.
 * All cards follow Material Design 3 Expressive principles.
 */

// ========================================================================
// STANDARD CARDS
// ========================================================================

/**
 * Standard card with Cline styling
 * 
 * @param onClick Optional click handler (makes card clickable)
 * @param modifier Modifier for the card
 * @param elevation Elevation for the card
 * @param shape Shape of the card
 * @param border Optional border stroke
 * @param colors Card color configuration
 * @param content The card content
 */
@Composable
fun ClineCard(
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    elevation: Dp = DesignTokens.Elevation.Level2,
    shape: Shape = MaterialTheme.shapes.medium,
    border: BorderStroke? = null,
    colors: androidx.compose.material3.CardColors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        contentColor = MaterialTheme.colorScheme.onSurface
    ),
    content: @Composable () -> Unit
) {
    val interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val elevationValue by animateDpAsState(
        targetValue = if (isPressed) elevation + DesignTokens.Elevation.Level1 else elevation,
        animationSpec = DesignTokens.Motion.StandardEasing
    )
    
    Card(
        onClick = onClick,
        modifier = modifier
            .clip(shape)
            .animateContentSize(
                animationSpec = DesignTokens.Motion.Spring
            ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = elevationValue
        ),
        shape = shape,
        border = border,
        colors = colors,
        interactionSource = interactionSource,
        indicator = if (onClick != null) {
            {
                CardDefaults.indicator(
                    interactionSource = interactionSource,
                    colors = colors
                )
            }
        } else null
    ) {
        content()
    }
}

/**
 * Elevated card for more prominent content
 */
@Composable
fun ClineElevatedCard(
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    elevation: Dp = DesignTokens.Elevation.Level3,
    shape: Shape = MaterialTheme.shapes.medium,
    content: @Composable () -> Unit
) {
    ElevatedCard(
        onClick = onClick,
        modifier = modifier
            .clip(shape)
            .animateContentSize(
                animationSpec = DesignTokens.Motion.Spring
            ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = elevation
        ),
        shape = shape,
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        content()
    }
}

/**
 * Outlined card with border
 */
@Composable
fun ClineOutlinedCard(
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    elevation: Dp = DesignTokens.Elevation.Level0,
    shape: Shape = MaterialTheme.shapes.medium,
    border: BorderStroke = BorderStroke(
        width = 1.dp,
        color = MaterialTheme.colorScheme.outlineVariant
    ),
    content: @Composable () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = modifier
            .clip(shape)
            .animateContentSize(
                animationSpec = DesignTokens.Motion.Spring
            ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = elevation
        ),
        shape = shape,
        border = border,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        content()
    }
}

// ========================================================================
// SPECIALIZED CARDS
// ========================================================================

/**
 * Chat message card (bubble)
 */
@Composable
fun ClineChatBubble(
    message: String,
    isUser: Boolean = false,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    val containerColor = if (isUser) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.surfaceContainer
    }
    
    val contentColor = if (isUser) {
        MaterialTheme.colorScheme.onPrimaryContainer
    } else {
        MaterialTheme.colorScheme.onSurface
    }
    
    val borderColor = if (isUser) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.outlineVariant
    }
    
    Card(
        onClick = onClick,
        modifier = modifier
            .clip(MaterialTheme.shapes.medium)
            .animateContentSize(
                animationSpec = DesignTokens.Motion.Spring
            ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = DesignTokens.Elevation.Level1
        ),
        shape = MaterialTheme.shapes.medium,
        border = BorderStroke(
            width = 1.dp,
            color = borderColor
        ),
        colors = CardDefaults.cardColors(
            containerColor = containerColor,
            contentColor = contentColor
        )
    ) {
        androidx.compose.material3.Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = contentColor,
            modifier = Modifier.padding(DesignTokens.Spacing.md)
        )
    }
}

/**
 * Terminal card for displaying terminal output
 */
@Composable
fun ClineTerminalCard(
    content: String,
    modifier: Modifier = Modifier,
    isError: Boolean = false
) {
    val textColor = if (isError) {
        MaterialTheme.colorScheme.error
    } else {
        MaterialTheme.colorScheme.onSurface
    }
    
    Card(
        modifier = modifier
            .clip(MaterialTheme.shapes.medium)
            .animateContentSize(
                animationSpec = DesignTokens.Motion.Spring
            ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = DesignTokens.Elevation.Level1
        ),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant
        )
    ) {
        androidx.compose.material3.Text(
            text = content,
            style = MaterialTheme.typography.bodyMedium,
            color = textColor,
            modifier = Modifier.padding(DesignTokens.Spacing.md)
        )
    }
}

/**
 * Plugin card for displaying plugin information
 */
@Composable
fun ClinePluginCard(
    name: String,
    description: String,
    version: String,
    isInstalled: Boolean = false,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Card(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .animateContentSize(
                animationSpec = DesignTokens.Motion.Spring
            ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = DesignTokens.Elevation.Level2
        ),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Column(
            modifier = Modifier
                .padding(DesignTokens.Spacing.md)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                androidx.compose.material3.Text(
                    text = name,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                if (isInstalled) {
                    androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(DesignTokens.Spacing.sm))
                    androidx.compose.material3.Icon(
                        imageVector = androidx.compose.material.icons.Icons.Default.CheckCircle,
                        contentDescription = "Installed",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(DesignTokens.Spacing.xs))
            
            androidx.compose.material3.Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(DesignTokens.Spacing.sm))
            
            androidx.compose.material3.Text(
                text = "Version: $version",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Settings card for settings items
 */
@Composable
fun ClineSettingsCard(
    title: String,
    subtitle: String? = null,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Card(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .animateContentSize(
                animationSpec = DesignTokens.Motion.Spring
            ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = DesignTokens.Elevation.Level1
        ),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Column(
            modifier = Modifier
                .padding(DesignTokens.Spacing.md)
        ) {
            androidx.compose.material3.Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )
            
            if (subtitle != null) {
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(DesignTokens.Spacing.xs))
                androidx.compose.material3.Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * Backup card for displaying backup information
 */
@Composable
fun ClineBackupCard(
    name: String,
    date: String,
    size: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    onRestore: () -> Unit = {},
    onDelete: () -> Unit = {}
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .animateContentSize(
                animationSpec = DesignTokens.Motion.Spring
            ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = DesignTokens.Elevation.Level2
        ),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(DesignTokens.Spacing.md)
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                androidx.compose.material3.Text(
                    text = name,
                    style = MaterialTheme.typography.titleMedium
                )
                
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(DesignTokens.Spacing.xs))
                
                androidx.compose.material3.Text(
                    text = "$date - $size",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Row {
                ClineIconButton(
                    onClick = onRestore,
                    icon = androidx.compose.material.icons.Icons.Default.Restore,
                    contentDescription = "Restore",
                    modifier = Modifier.size(DesignTokens.Spacing.lg)
                )
                
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(DesignTokens.Spacing.xs))
                
                ClineIconButton(
                    onClick = onDelete,
                    icon = androidx.compose.material.icons.Icons.Default.Delete,
                    contentDescription = "Delete",
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.size(DesignTokens.Spacing.lg)
                )
            }
        }
    }
}

// ========================================================================
// CARD UTILITIES
// ========================================================================

/**
 * Create a border stroke for cards
 */
@Composable
fun cardBorder(
    width: Dp = 1.dp,
    color: Color = MaterialTheme.colorScheme.outlineVariant
): BorderStroke {
    return BorderStroke(width, color)
}

/**
 * Get the appropriate elevation for a card based on its importance
 */
@Composable
fun getCardElevation(importance: CardImportance = CardImportance.Standard): Dp {
    return when (importance) {
        CardImportance.Low -> DesignTokens.Elevation.Level1
        CardImportance.Standard -> DesignTokens.Elevation.Level2
        CardImportance.High -> DesignTokens.Elevation.Level3
        CardImportance.Elevated -> DesignTokens.Elevation.Level4
    }
}

/**
 * Card importance levels
 */
enum class CardImportance {
    Low, Standard, High, Elevated
}
