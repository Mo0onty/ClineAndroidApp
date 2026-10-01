// Inputs.kt - Custom Input Components with MD3 Expressive
package com.cline.app.design.components

import androidx.compose.animation.core.SpringSpec
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.cline.app.design.tokens.DesignTokens

/**
 * Custom Input Components for Cline Android
 * 
 * All input components follow Material Design 3 Expressive principles with:
 * - Spring animations for focus states
 * - Dynamic color support
 * - Consistent spacing and sizing
 * - Accessibility support
 */

// ========================================================================
// TEXT FIELDS
// ========================================================================

/**
 * Primary text field with Cline styling
 * 
 * @param value The current value of the text field
 * @param onValueChange Callback when the value changes
 * @param modifier Modifier for the text field
 * @param label Optional label for the text field
 * @param placeholder Optional placeholder text
 * @param leadingIcon Optional leading icon
 * @param trailingIcon Optional trailing icon
 * @param isError Whether the text field is in an error state
 * @param errorMessage Optional error message to display
 * @param enabled Whether the text field is enabled
 * @param readOnly Whether the text field is read-only
 * @param keyboardOptions Keyboard options for the text field
 * @param keyboardActions Keyboard actions for the text field
 * @param visualTransformation Visual transformation for the text
 */
@Composable
fun ClineTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String? = null,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
    isError: Boolean = false,
    errorMessage: String? = null,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    singleLine: Boolean = false
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    
    Column(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.medium)
                .onFocusChanged { focusState ->
                    // Handle focus changes
                },
            label = label?.let { { Text(it) } },
            placeholder = placeholder?.let { { Text(it) } },
            leadingIcon = leadingIcon?.let { 
                {
                    Icon(
                        imageVector = it,
                        contentDescription = null,
                        tint = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            trailingIcon = trailingIcon?.let { 
                {
                    Icon(
                        imageVector = it,
                        contentDescription = null,
                        tint = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            isError = isError,
            enabled = enabled,
            readOnly = readOnly,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            visualTransformation = visualTransformation,
            singleLine = singleLine,
            shape = MaterialTheme.shapes.medium,
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outline,
                disabledBorderColor = MaterialTheme.colorScheme.outlineVariant,
                errorBorderColor = MaterialTheme.colorScheme.error,
                focusedLabelColor = MaterialTheme.colorScheme.primary,
                unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                cursorColor = MaterialTheme.colorScheme.primary,
                focusedLeadingIconColor = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                unfocusedLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                focusedTrailingIconColor = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                unfocusedTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
        
        if (isError && errorMessage != null) {
            Spacer(modifier = Modifier.height(DesignTokens.Spacing.xs))
            Text(
                text = errorMessage,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(start = DesignTokens.Spacing.sm)
            )
        }
    }
}

/**
 * Filled text field (not outlined)
 */
@Composable
fun ClineFilledTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String? = null,
    isError: Boolean = false,
    enabled: Boolean = true
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium),
        label = label?.let { { Text(it) } },
        placeholder = placeholder?.let { { Text(it) } },
        isError = isError,
        enabled = enabled,
        shape = MaterialTheme.shapes.medium,
        colors = TextFieldDefaults.textFieldColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            focusedIndicatorColor = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
            unfocusedIndicatorColor = if (isError) MaterialTheme.colorScheme.error else Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            errorIndicatorColor = MaterialTheme.colorScheme.error,
            focusedLabelColor = MaterialTheme.colorScheme.primary,
            unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
            cursorColor = MaterialTheme.colorScheme.primary
        )
    )
}

/**
 * Search text field with search icon and clear button
 */
@Composable
fun ClineSearchField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Search...",
    onSearch: () -> Unit = {}
) {
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant,
                shape = MaterialTheme.shapes.large
            )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = DesignTokens.Spacing.md)
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.width(DesignTokens.Spacing.sm))
            
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier
                    .weight(1f)
                    .height(DesignTokens.Spacing.xxl),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Search
                ),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        onSearch()
                        keyboardController?.hide()
                        focusManager.clearFocus()
                    }
                ),
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurface
                ),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                singleLine = true
            )
            
            if (value.isNotEmpty()) {
                IconButton(
                    onClick = {
                        onValueChange("")
                        focusManager.clearFocus()
                    },
                    modifier = Modifier.size(DesignTokens.Spacing.lg)
                ) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Clear",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

/**
 * Password text field with visibility toggle
 */
@Composable
fun ClinePasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "Password",
    placeholder: String = "Enter password",
    isError: Boolean = false,
    errorMessage: String? = null
) {
    var passwordVisible by remember { mutableStateOf(false) }
    
    ClineTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        label = label,
        placeholder = placeholder,
        isError = isError,
        errorMessage = errorMessage,
        visualTransformation = if (passwordVisible) {
            VisualTransformation.None
        } else {
            PasswordVisualTransformation()
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password
        ),
        trailingIcon = if (passwordVisible) {
            androidx.compose.material.icons.Icons.Default.Visibility
        } else {
            androidx.compose.material.icons.Icons.Default.VisibilityOff
        }?.let { icon ->
            {
                IconButton(
                    onClick = { passwordVisible = !passwordVisible }
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = if (passwordVisible) "Hide password" else "Show password"
                    )
                }
            }
        }
    )
}

// ========================================================================
// MULTI-LINE INPUTS
// ========================================================================

/**
 * Multi-line text field for longer text input
 */
@Composable
fun ClineMultiLineTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String? = null,
    isError: Boolean = false,
    errorMessage: String? = null,
    minLines: Int = 3,
    maxLines: Int = Int.MAX_VALUE
) {
    ClineTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        label = label,
        placeholder = placeholder,
        isError = isError,
        errorMessage = errorMessage,
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Default
        ),
        keyboardActions = KeyboardActions.Default,
        singleLine = false,
        visualTransformation = VisualTransformation.None
    )
}

/**
 * Code editor input field with monospace font
 */
@Composable
fun ClineCodeEditor(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    language: String = "kotlin",
    isError: Boolean = false
) {
    val fontFamily = androidx.compose.ui.text.font.FontFamily(
        androidx.compose.ui.text.font.Font(
            resId = android.R.font.monospace_default,
            weight = androidx.compose.ui.text.font.FontWeight.Normal
        )
    )
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .border(
                width = 1.dp,
                color = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outlineVariant,
                shape = MaterialTheme.shapes.medium
            )
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(DesignTokens.Spacing.md)
                .verticalScroll(rememberScrollState()),
            textStyle = MaterialTheme.typography.bodyMedium.copy(
                fontFamily = fontFamily,
                color = MaterialTheme.colorScheme.onSurface
            ),
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Default
            )
        )
    }
}

// ========================================================================
// CHAT INPUT
// ========================================================================

/**
 * Chat input field with send button
 */
@Composable
fun ClineChatInput(
    value: String,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Type a message...",
    isLoading: Boolean = false,
    enabled: Boolean = true
) {
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant,
                shape = MaterialTheme.shapes.large
            )
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .weight(1f)
                .padding(DesignTokens.Spacing.md),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Send
            ),
            keyboardActions = KeyboardActions(
                onSend = {
                    if (value.isNotBlank() && enabled && !isLoading) {
                        onSend()
                        keyboardController?.hide()
                        focusManager.clearFocus()
                    }
                }
            ),
            textStyle = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSurface
            ),
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            placeholder = {
                Text(
                    text = placeholder,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            enabled = enabled && !isLoading,
            singleLine = false,
            maxLines = 4
        )
        
        if (value.isNotBlank() || isLoading) {
            IconButton(
                onClick = {
                    if (value.isNotBlank() && enabled && !isLoading) {
                        onSend()
                        keyboardController?.hide()
                        focusManager.clearFocus()
                    }
                },
                enabled = enabled && !isLoading && value.isNotBlank(),
                modifier = Modifier
                    .padding(end = DesignTokens.Spacing.sm)
                    .size(DesignTokens.Spacing.lg)
            ) {
                if (isLoading) {
                    // Show loading indicator
                    Box(
                        modifier = Modifier
                            .size(DesignTokens.Spacing.md)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Send",
                        tint = if (enabled && value.isNotBlank()) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                }
            }
        }
    }
}

// ========================================================================
// TERMINAL INPUT
// ========================================================================

/**
 * Terminal input field with command history support
 */
@Composable
fun ClineTerminalInput(
    value: String,
    onValueChange: (String) -> Unit,
    onExecute: () -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Enter command...",
    isLoading: Boolean = false
) {
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant,
                shape = MaterialTheme.shapes.medium
            )
    ) {
        Text(
            text = "$",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(start = DesignTokens.Spacing.md)
        )
        
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .weight(1f)
                .padding(DesignTokens.Spacing.md),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Go
            ),
            keyboardActions = KeyboardActions(
                onGo = {
                    if (value.isNotBlank() && !isLoading) {
                        onExecute()
                        keyboardController?.hide()
                        focusManager.clearFocus()
                    }
                }
            ),
            textStyle = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSurface
            ),
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            placeholder = {
                Text(
                    text = placeholder,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            enabled = !isLoading,
            singleLine = true
        )
        
        if (value.isNotBlank() || isLoading) {
            IconButton(
                onClick = {
                    if (value.isNotBlank() && !isLoading) {
                        onExecute()
                        keyboardController?.hide()
                        focusManager.clearFocus()
                    }
                },
                enabled = !isLoading && value.isNotBlank(),
                modifier = Modifier
                    .padding(end = DesignTokens.Spacing.sm)
                    .size(DesignTokens.Spacing.lg)
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "Execute",
                    tint = if (!isLoading && value.isNotBlank()) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }
        }
    }
}
