// Buttons.kt - Custom Button Components with MD3 Expressive
package com.cline.app.design.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.SpringSpec
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonElevation
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.cline.app.design.tokens.DesignTokens

/**
 * Custom Button Components for Cline Android
 * 
 * All buttons follow Material Design 3 Expressive principles with:
 * - Spring animations for interactive elements
 * - Dynamic color support
 * - Consistent spacing and sizing
 * - Accessibility support
 */

// ========================================================================
// PRIMARY BUTTONS
// ========================================================================

/**
 * Primary button with Cline styling
 * 
 * @param onClick Callback when button is clicked
 * @param modifier Modifier for the button
 * @param enabled Whether the button is enabled
 * @param contentPadding Padding for the button content
 * @param content The button content
 */
@Composable
fun ClinePrimaryButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    contentPadding: PaddingValues = PaddingValues(
        horizontal = DesignTokens.Spacing.lg,
        vertical = DesignTokens.Spacing.md
    ),
    content: @Composable RowScope.() -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .clip(MaterialTheme.shapes.medium)
            .animateContentSize(
                animationSpec = SpringSpec(
                    dampingRatio = DesignTokens.Motion.Spring.dampingRatio,
                    stiffness = DesignTokens.Motion.Spring.stiffness
                )
            ),
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = DesignTokens.Elevation.Level1,
            pressedElevation = DesignTokens.Elevation.Level2
        ),
        shape = MaterialTheme.shapes.medium,
        contentPadding = contentPadding
    ) {
        content()
    }
}

/**
 * Primary button with text only
 */
@Composable
fun ClinePrimaryButton(
    onClick: () -> Unit,
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    ClinePrimaryButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge
        )
    }
}

/**
 * Primary button with icon and text
 */
@Composable
fun ClinePrimaryButton(
    onClick: () -> Unit,
    text: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    iconPosition: IconPosition = IconPosition.Start
) {
    ClinePrimaryButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled
    ) {
        when (iconPosition) {
            IconPosition.Start -> {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(DesignTokens.Spacing.md)
                )
                androidx.compose.foundation.layout.Spacer(
                    modifier = Modifier.size(DesignTokens.Spacing.sm)
                )
                Text(
                    text = text,
                    style = MaterialTheme.typography.labelLarge
                )
            }
            IconPosition.End -> {
                Text(
                    text = text,
                    style = MaterialTheme.typography.labelLarge
                )
                androidx.compose.foundation.layout.Spacer(
                    modifier = Modifier.size(DesignTokens.Spacing.sm)
                )
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(DesignTokens.Spacing.md)
                )
            }
        }
    }
}

// ========================================================================
// SECONDARY BUTTONS
// ========================================================================

/**
 * Secondary button (tonal variant)
 */
@Composable
fun ClineSecondaryButton(
    onClick: () -> Unit,
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    FilledTonalButton(
        onClick = onClick,
        modifier = modifier
            .clip(MaterialTheme.shapes.medium)
            .animateContentSize(
                animationSpec = SpringSpec(
                    dampingRatio = DesignTokens.Motion.Spring.dampingRatio,
                    stiffness = DesignTokens.Motion.Spring.stiffness
                )
            ),
        enabled = enabled,
        shape = MaterialTheme.shapes.medium
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge
        )
    }
}

/**
 * Outlined secondary button
 */
@Composable
fun ClineOutlinedButton(
    onClick: () -> Unit,
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .clip(MaterialTheme.shapes.medium)
            .animateContentSize(
                animationSpec = SpringSpec(
                    dampingRatio = DesignTokens.Motion.Spring.dampingRatio,
                    stiffness = DesignTokens.Motion.Spring.stiffness
                )
            ),
        enabled = enabled,
        shape = MaterialTheme.shapes.medium,
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outline
        )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge
        )
    }
}

// ========================================================================
// ICON BUTTONS
// ========================================================================

/**
 * Icon button with Cline styling
 */
@Composable
fun ClineIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector,
    contentDescription: String? = null,
    enabled: Boolean = true,
    containerColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    contentColor: Color = MaterialTheme.colorScheme.onSurfaceVariant
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
            .size(DesignTokens.Spacing.xl)
            .clip(CircleShape)
            .animateContentSize(
                animationSpec = SpringSpec(
                    dampingRatio = DesignTokens.Motion.Spring.dampingRatio,
                    stiffness = DesignTokens.Motion.Spring.stiffness
                )
            ),
        enabled = enabled,
        colors = IconButtonDefaults.iconButtonColors(
            containerColor = containerColor,
            contentColor = contentColor,
            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = contentColor
        )
    }
}

/**
 * Filled icon button
 */
@Composable
fun ClineFilledIconButton(
    onClick: () -> Unit,
    icon: ImageVector,
    contentDescription: String? = null,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    FilledIconButton(
        onClick = onClick,
        modifier = modifier
            .size(DesignTokens.Spacing.xl)
            .clip(CircleShape)
            .animateContentSize(
                animationSpec = SpringSpec(
                    dampingRatio = DesignTokens.Motion.Spring.dampingRatio,
                    stiffness = DesignTokens.Motion.Spring.stiffness
                )
            ),
        enabled = enabled
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription
        )
    }
}

/**
 * Outlined icon button
 */
@Composable
fun ClineOutlinedIconButton(
    onClick: () -> Unit,
    icon: ImageVector,
    contentDescription: String? = null,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    OutlinedIconButton(
        onClick = onClick,
        modifier = modifier
            .size(DesignTokens.Spacing.xl)
            .clip(CircleShape)
            .animateContentSize(
                animationSpec = SpringSpec(
                    dampingRatio = DesignTokens.Motion.Spring.dampingRatio,
                    stiffness = DesignTokens.Motion.Spring.stiffness
                )
            ),
        enabled = enabled
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription
        )
    }
}

// ========================================================================
// FLOATING ACTION BUTTONS
// ========================================================================

/**
 * Floating Action Button with Cline styling
 */
@Composable
fun ClineFAB(
    onClick: () -> Unit,
    icon: ImageVector = Icons.Default.Add,
    contentDescription: String? = null,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    FloatingActionButton(
        onClick = onClick,
        modifier = modifier
            .size(DesignTokens.Spacing.xxl)
            .animateContentSize(
                animationSpec = SpringSpec(
                    dampingRatio = DesignTokens.Motion.Spring.dampingRatio,
                    stiffness = DesignTokens.Motion.Spring.stiffness
                )
            ),
        enabled = enabled,
        shape = MaterialTheme.shapes.large,
        elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(
            elevation = DesignTokens.Elevation.Level3
        )
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription
        )
    }
}

/**
 * Extended Floating Action Button
 */
@Composable
fun ClineExtendedFAB(
    onClick: () -> Unit,
    text: String,
    icon: ImageVector = Icons.Default.Add,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    ExtendedFloatingActionButton(
        onClick = onClick,
        modifier = modifier
            .padding(DesignTokens.Spacing.md)
            .animateContentSize(
                animationSpec = SpringSpec(
                    dampingRatio = DesignTokens.Motion.Spring.dampingRatio,
                    stiffness = DesignTokens.Motion.Spring.stiffness
                )
            ),
        enabled = enabled,
        shape = MaterialTheme.shapes.large,
        elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(
            elevation = DesignTokens.Elevation.Level3
        )
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.padding(end = DesignTokens.Spacing.sm)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge
        )
    }
}

// ========================================================================
// SEGMENTED BUTTONS
// ========================================================================

/**
 * Segmented button row for tab-like selection
 */
@Composable
fun ClineSegmentedButtons(
    options: List<String>,
    selectedIndex: Int,
    onSelectionChanged: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    SingleChoiceSegmentedButtonRow(
        modifier = modifier
            .clip(MaterialTheme.shapes.medium)
            .animateContentSize(
                animationSpec = SpringSpec(
                    dampingRatio = DesignTokens.Motion.Spring.dampingRatio,
                    stiffness = DesignTokens.Motion.Spring.stiffness
                )
            )
    ) {
        options.forEachIndexed { index, label ->
            SegmentedButton(
                shape = SegmentedButtonDefaults.itemShape(
                    index = index,
                    count = options.size
                ),
                onClick = { onSelectionChanged(index) },
                selected = index == selectedIndex
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    }
}

// ========================================================================
// ICON POSITION
// ========================================================================

enum class IconPosition {
    Start, End
}
