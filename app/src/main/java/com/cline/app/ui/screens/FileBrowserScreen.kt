package com.cline.app.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClick
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.InsertDriveFile
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cline.app.design.animations.Animations
import com.cline.app.design.components.ClineCard
import com.cline.app.design.components.ClineIconButton
import com.cline.app.design.components.ClinePrimaryButton
import com.cline.app.design.tokens.DesignTokens
import com.cline.app.ui.viewmodels.FileBrowserViewModel
import com.cline.app.util.FileType
import kotlinx.coroutines.launch
import java.io.File

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun FileBrowserScreen(
    viewModel: FileBrowserViewModel = viewModel(),
    onFileSelected: (File) -> Unit = {},
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()
    val listState = rememberLazyListState()
    
    var showCreateDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showRenameDialog by remember { mutableStateOf(false) }
    var showBottomSheet by remember { mutableStateOf(false) }
    var showContextMenu by remember { mutableStateOf(false) }
    var contextMenuPosition by remember { mutableStateOf<Pair<Int, Int>?>(null) }
    var selectedItems by remember { mutableStateOf(setOf<String>()) }
    
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { viewModel.handleExternalFile(it, context) }
    }

    LaunchedEffect(Unit) {
        viewModel.loadFiles()
    }

    LaunchedEffect(viewModel.currentPath) {
        viewModel.loadFiles()
        listState.animateScrollToItem(0)
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            FileBrowserTopBar(
                currentPath = viewModel.currentPath,
                onBack = onBack,
                onNavigateUp = { viewModel.navigateUp() },
                onRefresh = { viewModel.loadFiles() },
                onSearchToggle = { viewModel.toggleSearch() },
                searchQuery = viewModel.searchQuery,
                onSearchQueryChange = { viewModel.setSearchQuery(it) },
                onCreateClick = { showCreateDialog = true },
                onUploadClick = { 
                    filePickerLauncher.launch("*/*")
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            AnimatedVisibility(
                visible = selectedItems.isEmpty(),
                enter = fadeIn() + slideInVertically(),
                exit = fadeOut() + slideOutVertically()
            ) {
                FloatingActionButton(
                    onClick = { showCreateDialog = true },
                    shape = MaterialTheme.shapes.large,
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Create"
                    )
                }
            }
        },
        bottomBar = {
            if (selectedItems.isNotEmpty()) {
                FileBrowserSelectionBar(
                    selectedCount = selectedItems.size,
                    onSelectAll = { 
                        viewModel.fileItems.forEach { 
                            selectedItems = selectedItems + it.path
                        }
                    },
                    onDeselectAll = { selectedItems = emptySet() },
                    onDelete = { showDeleteDialog = true },
                    onCopy = { 
                        viewModel.copyFiles(selectedItems.toList(), context)
                        selectedItems = emptySet()
                    },
                    onMove = { 
                        // TODO: Implement move
                    }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (viewModel.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            } else if (viewModel.fileItems.isEmpty()) {
                EmptyFileBrowser(
                    modifier = Modifier.fillMaxSize(),
                    onCreateClick = { showCreateDialog = true }
                )
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(DesignTokens.Spacing.md)
                ) {
                    items(viewModel.fileItems) { item ->
                        val isSelected = selectedItems.contains(item.path)
                        
                        FileItem(
                            item = item,
                            isSelected = isSelected,
                            onClick = {
                                if (selectedItems.isNotEmpty()) {
                                    selectedItems = if (isSelected) {
                                        selectedItems - item.path
                                    } else {
                                        selectedItems + item.path
                                    }
                                } else {
                                    when (item.type) {
                                        FileType.DIRECTORY -> viewModel.navigateTo(item.path)
                                        FileType.FILE -> onFileSelected(File(item.path))
                                    }
                                }
                            },
                            onLongClick = {
                                selectedItems = if (isSelected) {
                                    selectedItems - item.path
                                } else {
                                    setOf(item.path)
                                }
                            },
                            onMenuClick = { 
                                viewModel.selectedFile = item
                                showContextMenu = true
                                // Position would be handled by dropdown
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = DesignTokens.Spacing.xs)
                        )
                    }
                }
            }

            // Context Menu
            if (showContextMenu && viewModel.selectedFile != null) {
                val file = viewModel.selectedFile!!
                DropdownMenu(
                    expanded = showContextMenu,
                    onDismissRequest = { showContextMenu = false },
                    modifier = Modifier.background(MaterialTheme.colorScheme.surfaceContainer)
                ) {
                    DropdownMenuItem(
                        text = { Text("Open") },
                        onClick = {
                            showContextMenu = false
                            when (file.type) {
                                FileType.DIRECTORY -> viewModel.navigateTo(file.path)
                                FileType.FILE -> onFileSelected(File(file.path))
                            }
                        },
                        leadingIcon = { Icon(Icons.Default.FolderOpen, null) }
                    )
                    DropdownMenuItem(
                        text = { Text("Rename") },
                        onClick = {
                            showContextMenu = false
                            showRenameDialog = true
                        },
                        leadingIcon = { Icon(Icons.Default.ContentCopy, null) }
                    )
                    DropdownMenuItem(
                        text = { Text("Delete") },
                        onClick = {
                            showContextMenu = false
                            viewModel.selectedFile = file
                            showDeleteDialog = true
                        },
                        leadingIcon = { Icon(Icons.Default.Delete, null) },
                        trailingIcon = { 
                            Icon(
                                Icons.Default.Delete,
                                null,
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    )
                    Divider()
                    DropdownMenuItem(
                        text = { Text("Properties") },
                        onClick = {
                            showContextMenu = false
                            showBottomSheet = true
                        },
                        leadingIcon = { Icon(Icons.Default.Star, null) }
                    )
                }
            }
        }

        // Create Dialog
        if (showCreateDialog) {
            CreateFileDialog(
                onDismiss = { showCreateDialog = false },
                onCreateFile = { name ->
                    viewModel.createFile(name)
                    showCreateDialog = false
                },
                onCreateFolder = { name ->
                    viewModel.createFolder(name)
                    showCreateDialog = false
                }
            )
        }

        // Delete Dialog
        if (showDeleteDialog) {
            val file = viewModel.selectedFile
            if (file != null) {
                DeleteFileDialog(
                    fileName = file.name,
                    isMultiple = false,
                    onDismiss = { showDeleteDialog = false },
                    onConfirm = {
                        viewModel.deleteFile(file.path)
                        showDeleteDialog = false
                    }
                )
            }
        }

        // Delete Multiple Dialog
        if (showDeleteDialog && selectedItems.isNotEmpty()) {
            DeleteFileDialog(
                fileName = "${selectedItems.size} items",
                isMultiple = true,
                onDismiss = { showDeleteDialog = false },
                onConfirm = {
                    viewModel.deleteFiles(selectedItems.toList())
                    selectedItems = emptySet()
                    showDeleteDialog = false
                }
            )
        }

        // Rename Dialog
        if (showRenameDialog) {
            val file = viewModel.selectedFile
            if (file != null) {
                RenameFileDialog(
                    currentName = file.name,
                    onDismiss = { showRenameDialog = false },
                    onRename = { newName ->
                        viewModel.renameFile(file.path, newName)
                        showRenameDialog = false
                    }
                )
            }
        }

        // File Properties Bottom Sheet
        if (showBottomSheet && viewModel.selectedFile != null) {
            val file = viewModel.selectedFile!!
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false },
                sheetState = sheetState
            ) {
                FilePropertiesContent(
                    file = file,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(DesignTokens.Spacing.md)
                )
            }
        }
    }
}

@Composable
fun FileBrowserTopBar(
    currentPath: String,
    onBack: () -> Unit,
    onNavigateUp: () -> Unit,
    onRefresh: () -> Unit,
    onSearchToggle: () -> Unit,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onCreateClick: () -> Unit,
    onUploadClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showSearch by remember { mutableStateOf(false) }

    TopAppBar(
        title = {
            if (showSearch) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    placeholder = { Text("Search files...") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.extraLarge
                )
            } else {
                Text(
                    text = currentPath.ifEmpty { "Storage" },
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Default.ArrowUpward,
                    contentDescription = "Back"
                )
            }
        },
        actions = {
            IconButton(onClick = { showSearch = !showSearch }) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search"
                )
            }
            IconButton(onClick = onRefresh) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Refresh"
                )
            }
            IconButton(onClick = onUploadClick) {
                Icon(
                    imageVector = Icons.Default.Upload,
                    contentDescription = "Upload"
                )
            }
            IconButton(onClick = onCreateClick) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Create"
                )
            }
        },
        modifier = modifier
    )
}

@Composable
fun FileBrowserSelectionBar(
    selectedCount: Int,
    onSelectAll: () -> Unit,
    onDeselectAll: () -> Unit,
    onDelete: () -> Unit,
    onCopy: () -> Unit,
    onMove: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(DesignTokens.Spacing.md),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onSelectAll) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Select all"
                )
            }
            Text("$selectedCount selected")
            IconButton(onClick = onDeselectAll) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Deselect all"
                )
            }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onCopy) {
                Icon(
                    imageVector = Icons.Default.ContentCopy,
                    contentDescription = "Copy"
                )
            }
            IconButton(onClick = onMove) {
                Icon(
                    imageVector = Icons.Default.Folder,
                    contentDescription = "Move"
                )
            }
            IconButton(
                onClick = onDelete,
                colors = IconButtonDefaults.iconButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete"
                )
            }
        }
    }
}

@Composable
fun EmptyFileBrowser(
    modifier: Modifier = Modifier,
    onCreateClick: () -> Unit
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .background(
                    color = MaterialTheme.colorScheme.surfaceContainer,
                    shape = RoundedCornerShape(24.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.FolderOpen,
                contentDescription = null,
                modifier = Modifier.size(60.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.height(DesignTokens.Spacing.lg))
        Text(
            text = "No files yet",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(DesignTokens.Spacing.sm))
        Text(
            text = "Tap the + button to create a new file or folder",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(DesignTokens.Spacing.lg))
        ClinePrimaryButton(
            onClick = onCreateClick,
            modifier = Modifier.padding(horizontal = DesignTokens.Spacing.lg)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(DesignTokens.Spacing.sm))
            Text("Create")
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FileItem(
    item: FileBrowserViewModel.FileItem,
    isSelected: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onMenuClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val icon = when (item.type) {
        FileType.DIRECTORY -> Icons.Default.Folder
        FileType.FILE -> when (item.extension.lowercase()) {
            "txt", "md", "json", "xml", "yaml", "yml", "csv" -> Icons.Default.InsertDriveFile
            "kt", "java", "py", "js", "ts", "cpp", "c", "h", "go", "rs", "swift" -> Icons.Default.InsertDriveFile
            "png", "jpg", "jpeg", "gif", "bmp", "webp", "svg" -> Icons.Default.InsertDriveFile
            else -> Icons.Default.InsertDriveFile
        }
    }

    val iconTint = when (item.type) {
        FileType.DIRECTORY -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    ClineCard(
        onClick = onClick,
        modifier = modifier.combinedClick(
            onClick = onClick,
            onLongClick = onLongClick
        ),
        elevation = if (isSelected) DesignTokens.Elevation.Level3 else DesignTokens.Elevation.Level1
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(DesignTokens.Spacing.md),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            color = MaterialTheme.colorScheme.primary,
                            shape = MaterialTheme.shapes.small
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
                Spacer(modifier = Modifier.width(DesignTokens.Spacing.sm))
            } else {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(40.dp)
                )
                Spacer(modifier = Modifier.width(DesignTokens.Spacing.md))
            }

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (item.type == FileType.FILE) {
                    Text(
                        text = "${item.size} • ${item.extension.uppercase()}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    Text(
                        text = "${item.childCount} items",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (item.type == FileType.FILE && item.extension.lowercase() in listOf("png", "jpg", "jpeg", "gif", "bmp", "webp")) {
                Spacer(modifier = Modifier.width(DesignTokens.Spacing.sm))
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(MaterialTheme.shapes.small)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    // Placeholder for thumbnail
                    Icon(
                        imageVector = Icons.Default.InsertDriveFile,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            IconButton(
                onClick = onMenuClick,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "More options",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun CreateFileDialog(
    onDismiss: () -> Unit,
    onCreateFile: (String) -> Unit,
    onCreateFolder: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var name by remember { mutableStateOf("") }
    var isFile by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    BasicAlertDialog(
        onDismissRequest = onDismiss,
        modifier = modifier
    ) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            color = MaterialTheme.colorScheme.surfaceContainer
        ) {
            Column(
                modifier = Modifier
                    .padding(DesignTokens.Spacing.lg)
                    .width(300.dp)
            ) {
                Text(
                    text = "Create New",
                    style = MaterialTheme.typography.headlineSmall
                )
                Spacer(modifier = Modifier.height(DesignTokens.Spacing.md))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    FilledTonalButton(
                        onClick = { isFile = true },
                        colors = if (isFile) {
                            ButtonDefaults.filledTonalButtonColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        } else {
                            ButtonDefaults.filledTonalButtonColors()
                        }
                    ) {
                        Text("File")
                    }
                    Spacer(modifier = Modifier.width(DesignTokens.Spacing.sm))
                    FilledTonalButton(
                        onClick = { isFile = false },
                        colors = if (!isFile) {
                            ButtonDefaults.filledTonalButtonColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        } else {
                            ButtonDefaults.filledTonalButtonColors()
                        }
                    ) {
                        Text("Folder")
                    }
                }

                Spacer(modifier = Modifier.height(DesignTokens.Spacing.md))

                OutlinedTextField(
                    value = name,
                    onValueChange = { 
                        name = it
                        error = null
                    },
                    label = { Text("Name") },
                    placeholder = { 
                        Text(if (isFile) "file.txt" else "New Folder") 
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    isError = error != null,
                    supportingText = {
                        if (error != null) {
                            Text(error!!, color = MaterialTheme.colorScheme.error)
                        }
                    }
                )

                Spacer(modifier = Modifier.height(DesignTokens.Spacing.lg))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(DesignTokens.Spacing.sm))
                    ClinePrimaryButton(
                        onClick = {
                            if (name.isBlank()) {
                                error = "Name cannot be empty"
                                return@ClinePrimaryButton
                            }
                            if (isFile) {
                                onCreateFile(name)
                            } else {
                                onCreateFolder(name)
                            }
                        },
                        enabled = name.isNotBlank()
                    ) {
                        Text("Create")
                    }
                }
            }
        }
    }
}

@Composable
fun DeleteFileDialog(
    fileName: String,
    isMultiple: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Delete")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        title = {
            Text(if (isMultiple) "Delete Multiple Items" else "Delete File")
        },
        text = {
            Text(
                if (isMultiple) "Are you sure you want to delete these ${fileName}? This cannot be undone." 
                else "Are you sure you want to delete '$fileName'? This cannot be undone."
            )
        },
        icon = {
            Icon(
                Icons.Default.Delete,
                null,
                tint = MaterialTheme.colorScheme.error
            )
        },
        modifier = modifier
    )
}

@Composable
fun RenameFileDialog(
    currentName: String,
    onDismiss: () -> Unit,
    onRename: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var newName by remember { mutableStateOf(currentName) }
    var error by remember { mutableStateOf<String?>(null) }

    BasicAlertDialog(
        onDismissRequest = onDismiss,
        modifier = modifier
    ) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            color = MaterialTheme.colorScheme.surfaceContainer
        ) {
            Column(
                modifier = Modifier
                    .padding(DesignTokens.Spacing.lg)
                    .width(300.dp)
            ) {
                Text(
                    text = "Rename",
                    style = MaterialTheme.typography.headlineSmall
                )
                Spacer(modifier = Modifier.height(DesignTokens.Spacing.md))

                OutlinedTextField(
                    value = newName,
                    onValueChange = { 
                        newName = it
                        error = null
                    },
                    label = { Text("New Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    isError = error != null,
                    supportingText = {
                        if (error != null) {
                            Text(error!!, color = MaterialTheme.colorScheme.error)
                        }
                    }
                )

                Spacer(modifier = Modifier.height(DesignTokens.Spacing.lg))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(DesignTokens.Spacing.sm))
                    ClinePrimaryButton(
                        onClick = {
                            if (newName.isBlank()) {
                                error = "Name cannot be empty"
                                return@ClinePrimaryButton
                            }
                            onRename(newName)
                        },
                        enabled = newName.isNotBlank() && newName != currentName
                    ) {
                        Text("Rename")
                    }
                }
            }
        }
    }
}

@Composable
fun FilePropertiesContent(
    file: FileBrowserViewModel.FileItem,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.Start
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            val icon = when (file.type) {
                FileType.DIRECTORY -> Icons.Default.Folder
                else -> Icons.Default.InsertDriveFile
            }
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.width(DesignTokens.Spacing.md))
            Column {
                Text(
                    text = file.name,
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = file.path,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(DesignTokens.Spacing.lg))
        Divider()
        Spacer(modifier = Modifier.height(DesignTokens.Spacing.md))

        Text(
            text = "Properties",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(DesignTokens.Spacing.sm))

        PropertyRow("Type", when (file.type) {
            FileType.DIRECTORY -> "Folder"
            FileType.FILE -> "File"
        })
        
        if (file.type == FileType.FILE) {
            PropertyRow("Size", file.size)
            PropertyRow("Extension", file.extension.uppercase())
        } else {
            PropertyRow("Items", "${file.childCount}")
        }

        PropertyRow("Modified", file.modifiedDate)
        PropertyRow("Created", file.createdDate)

        Spacer(modifier = Modifier.height(DesignTokens.Spacing.md))
        Divider()
        Spacer(modifier = Modifier.height(DesignTokens.Spacing.md))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = { /* TODO: Open file */ }) {
                Text("Open")
            }
        }
    }
}

@Composable
fun PropertyRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = DesignTokens.Spacing.xs),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

@Preview(showBackground = true)
@Composable
fun FileBrowserScreenPreview() {
    MaterialTheme {
        FileBrowserScreen(
            onFileSelected = {},
            onBack = {}
        )
    }
}
