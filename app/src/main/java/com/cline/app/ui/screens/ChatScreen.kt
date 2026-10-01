// ChatScreen.kt - AI Chat Interface
package com.cline.app.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarOutline
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cline.app.design.animations.TypingIndicator
import com.cline.app.design.components.ClineIconButton
import com.cline.app.design.components.ClineTextField
import com.cline.app.design.tokens.DesignTokens
import kotlinx.coroutines.launch

/**
 * Chat Screen - AI Conversation Interface
 * 
 * This screen provides the main chat interface for interacting with Cline AI.
 * Features:
 * - Message list with sender/receiver bubbles
 * - Input field with send button
 * - Typing indicators
 * - Message actions (copy, delete, etc.)
 */

// ========================================================================
// DATA CLASSES
// ========================================================================

/**
 * Message role enum
 */
enum class MessageRole {
    USER, ASSISTANT, SYSTEM
}

/**
 * Message data class
 */
data class ChatMessage(
    val id: String,
    val role: MessageRole,
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isFavorite: Boolean = false
) {
    val displayName: String
        get() = when (role) {
            MessageRole.USER -> "You"
            MessageRole.ASSISTANT -> "Cline"
            MessageRole.SYSTEM -> "System"
        }
}

/**
 * Chat state
 */
data class ChatState(
    val messages: List<ChatMessage> = emptyList(),
    val input: String = "",
    val isLoading: Boolean = false,
    val selectedModel: String = "cline",
    val suggestions: List<String> = emptyList()
)

// ========================================================================
// MESSAGE BUBBLE
// ========================================================================

/**
 * Chat message bubble
 */
@Composable
fun ChatMessageBubble(
    message: ChatMessage,
    isLoading: Boolean = false,
    onCopyClick: () -> Unit = {},
    onFavoriteClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val isUser = message.role == MessageRole.USER
    
    val bubbleColor = if (isUser) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.surfaceContainer
    }
    
    val textColor = if (isUser) {
        MaterialTheme.colorScheme.onPrimaryContainer
    } else {
        MaterialTheme.colorScheme.onSurface
    }
    
    val borderColor = if (isUser) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.outlineVariant
    }
    
    val alignment = if (isUser) {
        Alignment.End
    } else {
        Alignment.Start
    }
    
    Column(
        horizontalAlignment = alignment,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = DesignTokens.Spacing.sm)
    ) {
        // Message header (sender info)
        if (!isUser) {
            Text(
                text = message.displayName,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = DesignTokens.Spacing.xs)
            )
        }
        
        // Message bubble
        Box(
            modifier = Modifier
                .clip(MaterialTheme.shapes.medium)
                .background(bubbleColor)
                .border(
                    width = 1.dp,
                    color = borderColor,
                    shape = MaterialTheme.shapes.medium
                )
                .padding(DesignTokens.Spacing.md)
        ) {
            if (isLoading) {
                TypingIndicator()
            } else {
                // Message content
                BasicText(
                    text = message.content,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = textColor,
                        lineHeight = 24.sp
                    )
                )
            }
        }
        
        // Message actions
        if (!isLoading) {
            Row(
                horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = DesignTokens.Spacing.xs)
            ) {
                if (isUser) {
                    // User message actions
                    ClineIconButton(
                        onClick = onDeleteClick,
                        icon = Icons.Default.Delete,
                        contentDescription = "Delete",
                        modifier = Modifier.size(DesignTokens.Spacing.md)
                    )
                } else {
                    // Assistant message actions
                    ClineIconButton(
                        onClick = onCopyClick,
                        icon = Icons.Default.ContentCopy,
                        contentDescription = "Copy",
                        modifier = Modifier.size(DesignTokens.Spacing.md)
                    )
                    
                    Spacer(modifier = Modifier.size(DesignTokens.Spacing.xs))
                    
                    ClineIconButton(
                        onClick = onFavoriteClick,
                        icon = if (message.isFavorite) Icons.Default.Star else Icons.Default.StarOutline,
                        contentDescription = "Favorite",
                        modifier = Modifier.size(DesignTokens.Spacing.md)
                    )
                }
            }
        }
    }
}

// ========================================================================
// CHAT INPUT
// ========================================================================

/**
 * Chat input area
 */
@Composable
fun ChatInputArea(
    value: String,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit,
    isLoading: Boolean = false,
    suggestions: List<String> = emptyList(),
    onSuggestionClick: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    val clipboardManager = LocalClipboardManager.current
    
    Column(modifier = modifier) {
        // Suggestions
        if (suggestions.isNotEmpty() && value.isEmpty()) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(DesignTokens.Spacing.sm),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = DesignTokens.Spacing.sm)
            ) {
                suggestions.forEach { suggestion ->
                    SuggestionChip(
                        text = suggestion,
                        onClick = { onSuggestionClick(suggestion) }
                    )
                }
            }
        }
        
        // Input row
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.large)
                .background(MaterialTheme.colorScheme.surfaceContainer)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant,
                    shape = MaterialTheme.shapes.large
                )
                .padding(DesignTokens.Spacing.sm)
        ) {
            // Input field
            BasicText(
                text = value,
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = if (value.isEmpty()) {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    }
                ),
                modifier = Modifier
                    .weight(1f)
                    .padding(DesignTokens.Spacing.sm)
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
            
            // Send button
            ClineIconButton(
                onClick = {
                    if (value.isNotBlank() && !isLoading) {
                        onSend()
                        focusManager.clearFocus()
                    }
                },
                icon = Icons.Default.Send,
                contentDescription = "Send message",
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
}

/**
 * Suggestion chip
 */
@Composable
fun SuggestionChip(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(MaterialTheme.shapes.full)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable(onClick = onClick)
            .padding(
                horizontal = DesignTokens.Spacing.md,
                vertical = DesignTokens.Spacing.sm
            )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// ========================================================================
// CHAT SCREEN
// ========================================================================

/**
 * Main chat screen
 */
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ChatScreen(
    onBack: () -> Unit,
    onOpenTerminal: () -> Unit,
    onChatItemClick: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    // Sample chat state
    var chatState by remember {
        mutableStateOf(
            ChatState(
                messages = listOf(
                    ChatMessage(
                        id = "1",
                        role = MessageRole.USER,
                        content = "Hello! Can you help me write a Kotlin function to sort a list?"
                    ),
                    ChatMessage(
                        id = "2",
                        role = MessageRole.ASSISTANT,
                        content = "Of course! Here's a Kotlin function that sorts a list of integers in ascending order:\n\n```kotlin\nfun sortList(list: List<Int>): List<Int> {\n    return list.sorted()\n}\n```\n\nYou can use it like this:\n\n```kotlin\nval numbers = listOf(5, 2, 8, 1, 3)\nval sorted = sortList(numbers)\nprintln(sorted) // [1, 2, 3, 5, 8]\n```"
                    )
                ),
                input = "",
                isLoading = false,
                selectedModel = "cline",
                suggestions = listOf(
                    "Write a function",
                    "Debug my code",
                    "Explain this code",
                    "Optimize this"
                )
            )
        )
    }
    
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    
    // Auto-scroll to bottom when messages change
    LaunchedEffect(chatState.messages.size) {
        if (chatState.messages.isNotEmpty()) {
            coroutineScope.launch {
                listState.animateScrollToItem(0)
            }
        }
    }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        // Top app bar
        ChatTopAppBar(
            title = "Cline AI",
            onBack = onBack,
            onModelChange = {},
            onOpenTerminal = onOpenTerminal
        )
        
        // Chat messages
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = DesignTokens.Spacing.md,
                    end = DesignTokens.Spacing.md,
                    top = DesignTokens.Spacing.md,
                    bottom = DesignTokens.Spacing.xl
                ),
                reverseLayout = true
            ) {
                items(chatState.messages.size) { index ->
                    val message = chatState.messages[index]
                    val prevMessage = chatState.messages.getOrNull(index + 1)
                    val showAvatar = prevMessage == null || prevMessage.role != message.role
                    
                    ChatMessageBubble(
                        message = message,
                        isLoading = chatState.isLoading && index == 0,
                        onCopyClick = { /* Copy to clipboard */ },
                        onFavoriteClick = { /* Toggle favorite */ },
                        onDeleteClick = { /* Delete message */ }
                    )
                }
            }
        }
        
        // Input area
        ChatInputArea(
            value = chatState.input,
            onValueChange = { newValue ->
                chatState = chatState.copy(input = newValue)
            },
            onSend = {
                // Handle send
            },
            isLoading = chatState.isLoading,
            suggestions = chatState.suggestions,
            onSuggestionClick = { suggestion ->
                chatState = chatState.copy(input = suggestion)
            }
        )
    }
}

/**
 * Chat top app bar
 */
@Composable
fun ChatTopAppBar(
    title: String,
    onBack: () -> Unit,
    onModelChange: () -> Unit,
    onOpenTerminal: () -> Unit,
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
        // Back button
        ClineIconButton(
            onClick = onBack,
            icon = Icons.Default.ArrowBack,
            contentDescription = "Back",
            modifier = Modifier.size(DesignTokens.Spacing.lg)
        )
        
        Spacer(modifier = Modifier.width(DesignTokens.Spacing.sm))
        
        // Title
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
        
        // Actions
        ClineIconButton(
            onClick = onOpenTerminal,
            icon = Icons.Default.Terminal,
            contentDescription = "Open terminal",
            modifier = Modifier.size(DesignTokens.Spacing.lg)
        )
        
        Spacer(modifier = Modifier.width(DesignTokens.Spacing.xs))
        
        // Model selector
        ModelSelector(
            currentModel = "cline",
            onModelChange = onModelChange
        )
    }
}

/**
 * Model selector dropdown
 */
@Composable
fun ModelSelector(
    currentModel: String,
    onModelChange: () -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    
    Box(modifier = modifier) {
        ClineIconButton(
            onClick = { expanded = true },
            icon = Icons.Default.MoreVert,
            contentDescription = "Select model",
            modifier = Modifier.size(DesignTokens.Spacing.lg)
        )
        
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = {
                    Text(
                        text = "Cline (Default)",
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                onClick = {
                    expanded = false
                    onModelChange()
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null
                    )
                }
            )
            
            DropdownMenuItem(
                text = {
                    Text(
                        text = "Cline Pro",
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                onClick = {
                    expanded = false
                    onModelChange()
                }
            )
            
            DropdownMenuItem(
                text = {
                    Text(
                        text = "Custom",
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                onClick = {
                    expanded = false
                    onModelChange()
                }
            )
        }
    }
}

// ========================================================================
// CHAT DETAIL SCREEN
// ========================================================================

/**
 * Chat detail screen for viewing a specific chat
 */
@Composable
fun ChatDetailScreen(
    chatId: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        // Top app bar
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
                text = "Chat #$chatId",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        
        // Chat content
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(DesignTokens.Spacing.md),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Chat detail for: $chatId",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// ========================================================================
// EMPTY CHAT SCREEN
// ========================================================================

/**
 * Empty chat screen shown when no chat is selected
 */
@Composable
fun EmptyChatScreen(
    onNewChat: () -> Unit,
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
            imageVector = Icons.Default.Chat,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(DesignTokens.Spacing.xxxl)
        )
        
        Spacer(modifier = Modifier.height(DesignTokens.Spacing.xl))
        
        Text(
            text = "No chat selected",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Spacer(modifier = Modifier.height(DesignTokens.Spacing.md))
        
        Text(
            text = "Start a new conversation with Cline to get coding assistance.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(DesignTokens.Spacing.xl))
        
        com.cline.app.design.components.ClinePrimaryButton(
            onClick = onNewChat,
            text = "New Chat",
            icon = Icons.Default.Add
        )
    }
}

// ========================================================================
// PREVIEW
// ========================================================================

/**
 * Preview for chat screen
 */
@Composable
fun ChatScreenPreview() {
    MaterialTheme {
        ChatScreen(
            onBack = {},
            onOpenTerminal = {}
        )
    }
}

/**
 * Preview for chat detail screen
 */
@Composable
fun ChatDetailScreenPreview() {
    MaterialTheme {
        ChatDetailScreen(
            chatId = "123",
            onBack = {}
        )
    }
}

/**
 * Preview for empty chat screen
 */
@Composable
fun EmptyChatScreenPreview() {
    MaterialTheme {
        EmptyChatScreen(
            onNewChat = {}
        )
    }
}
