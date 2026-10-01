package com.cline.app.ui.viewmodels

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cline.app.util.Constants
import com.cline.app.util.FileType
import com.cline.app.util.ShellExecutor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FileBrowserViewModel : ViewModel() {

    // State
    var currentPath by mutableStateOf("")
        private set

    var fileItems by mutableStateOf(listOf<FileItem>())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var searchQuery by mutableStateOf("")
        private set

    var showSearch by mutableStateOf(false)
        private set

    var selectedFile by mutableStateOf<FileItem?>(null)
        internal set

    // Root directory
    private val rootDirectory: File by lazy {
        File(Constants.AppDataDir)
    }

    // Filtered items based on search
    val filteredFileItems: List<FileItem>
        get() = if (searchQuery.isBlank()) {
            fileItems
        } else {
            fileItems.filter {
                it.name.contains(searchQuery, ignoreCase = true)
            }
        }

    init {
        currentPath = rootDirectory.absolutePath
    }

    fun loadFiles() {
        viewModelScope.launch {
            isLoading = true
            try {
                val currentDir = File(currentPath)
                if (currentDir.exists() && currentDir.isDirectory) {
                    val files = currentDir.listFiles()?.toList() ?: emptyList()
                    fileItems = files.map { file ->
                        createFileItem(file)
                    }.sortedWith(compareBy(
                        { it.type == FileType.DIRECTORY },
                        { it.name.lowercase() }
                    ))
                } else {
                    fileItems = emptyList()
                }
            } catch (e: Exception) {
                fileItems = emptyList()
            } finally {
                isLoading = false
            }
        }
    }

    fun navigateTo(path: String) {
        currentPath = path
    }

    fun navigateUp() {
        val parent = File(currentPath).parentFile
        if (parent != null && parent.exists()) {
            currentPath = parent.absolutePath
        } else {
            currentPath = rootDirectory.absolutePath
        }
    }

    fun toggleSearch() {
        showSearch = !showSearch
        if (!showSearch) {
            searchQuery = ""
        }
    }

    fun setSearchQuery(query: String) {
        searchQuery = query
    }

    fun createFile(name: String) {
        viewModelScope.launch {
            isLoading = true
            try {
                val file = File(currentPath, name)
                if (!file.exists()) {
                    file.createNewFile()
                    loadFiles()
                }
            } catch (e: Exception) {
                // Handle error
            } finally {
                isLoading = false
            }
        }
    }

    fun createFolder(name: String) {
        viewModelScope.launch {
            isLoading = true
            try {
                val dir = File(currentPath, name)
                if (!dir.exists()) {
                    dir.mkdirs()
                    loadFiles()
                }
            } catch (e: Exception) {
                // Handle error
            } finally {
                isLoading = false
            }
        }
    }

    fun deleteFile(path: String) {
        viewModelScope.launch {
            isLoading = true
            try {
                val file = File(path)
                if (file.exists()) {
                    if (file.isDirectory) {
                        file.deleteRecursively()
                    } else {
                        file.delete()
                    }
                    loadFiles()
                }
            } catch (e: Exception) {
                // Handle error
            } finally {
                isLoading = false
            }
        }
    }

    fun deleteFiles(paths: List<String>) {
        viewModelScope.launch {
            isLoading = true
            try {
                paths.forEach { path ->
                    val file = File(path)
                    if (file.exists()) {
                        if (file.isDirectory) {
                            file.deleteRecursively()
                        } else {
                            file.delete()
                        }
                    }
                }
                loadFiles()
            } catch (e: Exception) {
                // Handle error
            } finally {
                isLoading = false
            }
        }
    }

    fun renameFile(oldPath: String, newName: String) {
        viewModelScope.launch {
            isLoading = true
            try {
                val oldFile = File(oldPath)
                val newFile = File(oldFile.parentFile, newName)
                if (oldFile.exists() && !newFile.exists()) {
                    oldFile.renameTo(newFile)
                    loadFiles()
                }
            } catch (e: Exception) {
                // Handle error
            } finally {
                isLoading = false
            }
        }
    }

    fun copyFiles(paths: List<String>, context: Context) {
        viewModelScope.launch {
            isLoading = true
            try {
                // For now, just copy to clipboard
                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                val clip = android.content.ClipData.newPlainText("Files", paths.joinToString(", "))
                clipboard.setPrimaryClip(clip)
            } catch (e: Exception) {
                // Handle error
            } finally {
                isLoading = false
            }
        }
    }

    fun handleExternalFile(uri: Uri, context: Context) {
        viewModelScope.launch {
            isLoading = true
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                if (inputStream != null) {
                    val fileName = getFileNameFromUri(uri, context)
                    val outputFile = File(currentPath, fileName)
                    val outputStream = FileOutputStream(outputFile)
                    inputStream.copyTo(outputStream)
                    outputStream.close()
                    inputStream.close()
                    loadFiles()
                }
            } catch (e: Exception) {
                // Handle error
            } finally {
                isLoading = false
            }
        }
    }

    private fun getFileNameFromUri(uri: Uri, context: Context): String {
        var result = ""
        if (uri.scheme == "content") {
            context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndexOrThrow(android.provider.OpenableColumns.DISPLAY_NAME))
                }
            }
        }
        if (result.isEmpty()) {
            result = uri.pathSegments?.lastOrNull() ?: "file_${System.currentTimeMillis()}"
        }
        return result
    }

    private fun createFileItem(file: File): FileItem {
        val isDirectory = file.isDirectory
        val size = if (isDirectory) "" else formatFileSize(file.length())
        val extension = if (isDirectory) "" else getFileExtension(file.name)
        val childCount = if (isDirectory) {
            file.listFiles()?.size ?: 0
        } else {
            0
        }

        return FileItem(
            name = file.name,
            path = file.absolutePath,
            type = if (isDirectory) FileType.DIRECTORY else FileType.FILE,
            size = size,
            extension = extension,
            childCount = childCount,
            modifiedDate = formatDate(file.lastModified()),
            createdDate = formatDate(file.lastModified()) // Approximation
        )
    }

    private fun formatFileSize(bytes: Long): String {
        return when {
            bytes >= 1024 * 1024 * 1024 -> "${"%.2f".format(bytes.toDouble() / (1024 * 1024 * 1024))} GB"
            bytes >= 1024 * 1024 -> "${"%.2f".format(bytes.toDouble() / (1024 * 1024))} MB"
            bytes >= 1024 -> "${"%.2f".format(bytes.toDouble() / 1024)} KB"
            else -> "$bytes B"
        }
    }

    private fun getFileExtension(filename: String): String {
        val index = filename.lastIndexOf('.')
        return if (index > 0 && index < filename.length - 1) {
            filename.substring(index + 1)
        } else {
            ""
        }
    }

    private fun formatDate(timestamp: Long): String {
        return try {
            val date = Date(timestamp)
            val format = SimpleDateFormat("MMM dd, yyyy, hh:mm a", Locale.getDefault())
            format.format(date)
        } catch (e: Exception) {
            "Unknown"
        }
    }

    data class FileItem(
        val name: String,
        val path: String,
        val type: FileType,
        val size: String,
        val extension: String,
        val childCount: Int,
        val modifiedDate: String,
        val createdDate: String
    )
}
