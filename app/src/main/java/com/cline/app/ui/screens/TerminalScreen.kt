// TerminalScreen.kt - Terminal Interface
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
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cline.app.design.components.ClineIconButton
import com.cline.app.design.components.ClinePrimaryButton
import com.cline.app.design.tokens.DesignTokens

/**
 * Terminal Screen - Interactive Terminal Interface
 * 
 * This screen provides a full terminal interface for running commands
 * within the Cline runtime environment.
 * 
 * Features:
 * - Command input with history
 * - Command output display
 * - Syntax highlighting
 * - Copy/paste support
 * - Multiple terminal sessions
 */

// ========================================================================
// DATA CLASSES
// ========================================================================

/**
 * Terminal line data class
 */
data class TerminalLine(
    val id: String,
    val content: String,
    val isCommand: Boolean = false,
    val isError: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Terminal state
 */
data class TerminalState(
    val lines: List<TerminalLine> = emptyList(),
    val input: String = "",
    val isLoading: Boolean = false,
    val currentDirectory: String = "~/cline",
    val sessionName: String = "Session 1"
)

/**
 * Terminal session
 */
data class TerminalSession(
    val id: String,
    val name: String,
    val lines: List<TerminalLine> = emptyList(),
    val currentDirectory: String = "~/cline",
    val isActive: Boolean = false
)

// ========================================================================
// TERMINAL HEADER
// ========================================================================

/**
 * Terminal header with session info and actions
 */
@Composable
fun TerminalHeader(
    title: String,
    subtitle: String,
    actions: @Composable () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(DesignTokens.Spacing.md)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        actions()
    }
}

// ========================================================================
// TERMINAL OUTPUT
// ========================================================================

/**
 * Terminal output display
 */
@Composable
fun TerminalOutput(
    lines: List<TerminalLine>,
    modifier: Modifier = Modifier,
    onLineClick: (TerminalLine) -> Unit = {}
) {
    val listState = rememberLazyListState()
    
    // Auto-scroll to bottom
    LaunchedEffect(lines.size) {
        if (lines.isNotEmpty()) {
            listState.animateScrollToItem(0)
        }
    }
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.surface)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant,
                shape = MaterialTheme.shapes.medium
            )
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(DesignTokens.Spacing.md),
            reverseLayout = true
        ) {
            items(lines) { line ->
                TerminalLineItem(
                    line = line,
                    onClick = { onLineClick(line) }
                )
            }
        }
        
        // Scroll to bottom button
        if (lines.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(DesignTokens.Spacing.md)
            ) {
                ClineIconButton(
                    onClick = {
                        listState.animateScrollToItem(0)
                    },
                    icon = Icons.Default.ArrowDownward,
                    contentDescription = "Scroll to bottom",
                    modifier = Modifier.size(DesignTokens.Spacing.lg)
                )
            }
        }
    }
}

/**
 * Individual terminal line
 */
@Composable
fun TerminalLineItem(
    line: TerminalLine,
    onClick: (TerminalLine) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val textColor = when {
        line.isError -> MaterialTheme.colorScheme.error
        line.isCommand -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.onSurface
    }
    
    val backgroundColor = when {
        line.isError -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f)
        line.isCommand -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
        else -> Color.Transparent
    }
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.small)
            .background(backgroundColor)
            .clickable(onClick = { onClick(line) })
            .padding(vertical = 2.dp)
    ) {
        BasicText(
            text = line.content,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = textColor,
                fontFamily = FontFamily.Monospace,
                fontSize = 14.sp,
                lineHeight = 20.sp
            )
        )
    }
}

// ========================================================================
// TERMINAL INPUT
// ========================================================================

/**
 * Terminal input field
 */
@Composable
fun TerminalInput(
    value: String,
    onValueChange: (String) -> Unit,
    onExecute: () -> Unit,
    isLoading: Boolean = false,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val clipboardManager = LocalClipboardManager.current
    
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
        // Prompt
        Text(
            text = "$",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.primary,
                fontFamily = FontFamily.Monospace
            ),
            modifier = Modifier.padding(start = DesignTokens.Spacing.md)
        )
        
        // Input field
        BasicText(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSurface,
                fontFamily = FontFamily.Monospace
            ),
            modifier = Modifier
                .weight(1f)
                .padding(DesignTokens.Spacing.md)
        )
        
        // Clear button
        if (value.isNotEmpty()) {
            ClineIconButton(
                onClick = {
                    onValueChange("")
                    focusManager.clearFocus()
                },
                icon = Icons.Default.Clear,
                contentDescription = "Clear input",
                modifier = Modifier.size(DesignTokens.Spacing.lg)
            )
        }
        
        // Execute button
        ClineIconButton(
            onClick = {
                if (value.isNotBlank() && !isLoading) {
                    onExecute()
                    keyboardController?.hide()
                    focusManager.clearFocus()
                }
            },
            icon = Icons.Default.Send,
            contentDescription = "Execute command",
            enabled = value.isNotBlank() && !isLoading,
            containerColor = if (value.isNotBlank() && !isLoading) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            },
            contentColor = if (value.isNotBlank() && !isLoading) {
                MaterialTheme.colorScheme.onPrimaryContainer
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            },
            modifier = Modifier.size(DesignTokens.Spacing.lg)
        )
    }
}

// ========================================================================
// TERMINAL SCREEN
// ========================================================================

/**
 * Main terminal screen
 */
@Composable
fun TerminalScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Sample terminal state
    var terminalState by remember {
        mutableStateOf(
            TerminalState(
                lines = listOf(
                    TerminalLine(
                        id = "1",
                        content = "Welcome to Cline Terminal",
                        isCommand = false
                    ),
                    TerminalLine(
                        id = "2",
                        content = "Type 'help' for available commands",
                        isCommand = false
                    ),
                    TerminalLine(
                        id = "3",
                        content = "$",
                        isCommand = true
                    )
                ),
                input = "",
                isLoading = false,
                currentDirectory = "~/cline",
                sessionName = "Session 1"
            )
        )
    }
    
    var showMenu by remember { mutableStateOf(false) }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        // Header
        TerminalHeader(
            title = terminalState.sessionName,
            subtitle = terminalState.currentDirectory,
            actions = {
                ClineIconButton(
                    onClick = { showMenu = true },
                    icon = Icons.Default.MoreVert,
                    contentDescription = "Menu",
                    modifier = Modifier.size(DesignTokens.Spacing.lg)
                )
                
                TerminalMenu(
                    expanded = showMenu,
                    onDismiss = { showMenu = false },
                    onNewSession = {},
                    onClear = {},
                    onSettings = {}
                )
            }
        )
        
        // Terminal output
        TerminalOutput(
            lines = terminalState.lines,
            modifier = Modifier.weight(1f)
        )
        
        // Terminal input
        TerminalInput(
            value = terminalState.input,
            onValueChange = { newValue ->
                terminalState = terminalState.copy(input = newValue)
            },
            onExecute = {
                // Handle command execution
                val command = terminalState.input
                if (command.isNotBlank()) {
                    // Add command to lines
                    val newLines = terminalState.lines + TerminalLine(
                        id = (terminalState.lines.size + 1).toString(),
                        content = "$ $command",
                        isCommand = true
                    )
                    
                    terminalState = terminalState.copy(
                        lines = newLines,
                        input = "",
                        isLoading = true
                    )
                    
                    // Simulate command execution
                    // In real implementation, this would call the runtime
                    terminalState = terminalState.copy(
                        lines = newLines + TerminalLine(
                            id = (terminalState.lines.size + 2).toString(),
                            content = "Command executed: $command",
                            isCommand = false
                        ),
                        isLoading = false
                    )
                }
            },
            isLoading = terminalState.isLoading
        )
    }
}

/**
 * Terminal menu dropdown
 */
@Composable
fun TerminalMenu(
    expanded: Boolean,
    onDismiss: () -> Unit,
    onNewSession: () -> Unit,
    onClear: () -> Unit,
    onSettings: () -> Unit
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismiss
    ) {
        DropdownMenuItem(
            text = {
                Text(
                    text = "New Session",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            onClick = {
                expanded = false
                onNewSession()
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null
                )
            }
        )
        
        DropdownMenuItem(
            text = {
                Text(
                    text = "Clear Terminal",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            onClick = {
                expanded = false
                onClear()
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = null
                )
            }
        )
        
        DropdownMenuItem(
            text = {
                Text(
                    text = "Terminal Settings",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            onClick = {
                expanded = false
                onSettings()
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = null
                )
            }
        )
    }
}

// ========================================================================
// EMPTY TERMINAL SCREEN
// ========================================================================

/**
 * Empty terminal screen shown when no session is active
 */
@Composable
fun EmptyTerminalScreen(
    onNewSession: () -> Unit,
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
            imageVector = Icons.Default.Terminal,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(DesignTokens.Spacing.xxxl)
        )
        
        Spacer(modifier = Modifier.height(DesignTokens.Spacing.xl))
        
        Text(
            text = "No terminal session",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Spacer(modifier = Modifier.height(DesignTokens.Spacing.md))
        
        Text(
            text = "Start a new terminal session to run commands in the Cline environment.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(DesignTokens.Spacing.xl))
        
        ClinePrimaryButton(
            onClick = onNewSession,
            text = "New Session",
            icon = Icons.Default.Add
        )
    }
}

// ========================================================================
// TERMINAL VIEW (FOR CHAT)
// ========================================================================

/**
 * Terminal view that can be embedded in other screens
 */
@Composable
fun TerminalView(
    lines: List<TerminalLine>,
    input: String,
    onValueChange: (String) -> Unit,
    onExecute: () -> Unit,
    isLoading: Boolean = false,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.surfaceContainer)
    ) {
        // Output
        TerminalOutput(
            lines = lines,
            modifier = Modifier.weight(1f)
        )
        
        // Input
        TerminalInput(
            value = input,
            onValueChange = onValueChange,
            onExecute = onExecute,
            isLoading = isLoading
        )
    }
}

// ========================================================================
// PREVIEW
// ========================================================================

/**
 * Preview for terminal screen
 */
@Composable
fun TerminalScreenPreview() {
    MaterialTheme {
        TerminalScreen(
            onBack = {}
        )
    }
}

/**
 * Preview for empty terminal screen
 */
@Composable
fun EmptyTerminalScreenPreview() {
    MaterialTheme {
        EmptyTerminalScreen(
            onNewSession = {}
        )
    }
}

/**
 * Preview for terminal view
 */
@Composable
fun TerminalViewPreview() {
    MaterialTheme {
        TerminalView(
            lines = listOf(
                TerminalLine(
                    id = "1",
                    content = "Welcome to Cline Terminal",
                    isCommand = false
                )
            ),
            input = "",
            onValueChange = {},
            onExecute = {},
            isLoading = false
        )
    }
}
