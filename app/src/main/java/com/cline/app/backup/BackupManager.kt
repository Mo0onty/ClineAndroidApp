// BackupManager.kt - Backup and Restore Management
package com.cline.app.backup

import android.content.Context
import android.util.Log
import com.cline.app.core.ConfigStore
import com.cline.app.core.ContainerRuntime
import com.cline.app.core.EnvironmentAccess
import com.cline.app.core.ProotBootstrap
import com.cline.app.core.RuntimeHostPorts
import com.cline.app.core.WebProcessManager
import com.cline.app.data.KeyVault
import com.cline.app.util.Constants
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

/**
 * Backup Manager - Manages backup and restore operations
 * 
 * This class provides:
 * - Full backup creation
 * - Backup restoration
 * - Backup listing
 * - Backup deletion
 * - Progress tracking
 */
class BackupManager(
    private val context: Context,
    private val configStore: ConfigStore = ConfigStore(context),
    private val runtimeHostPorts: RuntimeHostPorts = RuntimeHostPorts(context),
    private val prootBootstrap: ProotBootstrap = ProotBootstrap(context),
    private val containerRuntime: ContainerRuntime = ContainerRuntime(context),
    private val webProcessManager: WebProcessManager = WebProcessManager(context),
    private val environmentAccess: EnvironmentAccess = EnvironmentAccess(context),
    private val keyVault: KeyVault = KeyVault(context)
) {
    
    companion object {
        private const val TAG = "BackupManager"
        
        // Backup directory
        private const val BACKUP_DIR = "backups"
        private const val BACKUP_EXTENSION = ".clinebk"
        private const val MANIFEST_FILE = "backup.manifest.json"
        
        // Backup state
        enum class BackupState {
            IDLE, CREATING, RESTORING, DELETING, ERROR, SUCCESS
        }
    }
    
    // State
    private var state: BackupState = BackupState.IDLE
    private var progress: Int = 0
    private var error: String? = null
    private var currentBackup: BackupInfo? = null
    
    // Callbacks
    private var onProgress: ((Int) -> Unit)? = null
    private var onStateChange: ((BackupState) -> Unit)? = null
    private var onError: ((String) -> Unit)? = null
    private var onComplete: (() -> Unit)? = null
    
    // ========================================================================
    // BACKUP INFO
    // ========================================================================
    
    /**
     * Backup information
     */
    data class BackupInfo(
        val id: String,
        val name: String,
        val timestamp: Long,
        val size: Long,
        val description: String? = null,
        val version: String = "1.0",
        val isEncrypted: Boolean = true
    )
    
    // ========================================================================
    // BACKUP DIRECTORY
    // ========================================================================
    
    /**
     * Get backup directory
     */
    private fun getBackupDir(): File {
        val dir = File(context.filesDir, BACKUP_DIR)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return dir
    }
    
    // ========================================================================
    // BACKUP CREATION
    // ========================================================================
    
    /**
     * Create a new backup
     */
    fun createBackup(
        name: String,
        description: String? = null,
        onProgress: ((Int) -> Unit)? = null,
        onStateChange: ((BackupState) -> Unit)? = null,
        onError: ((String) -> Unit)? = null,
        onComplete: (() -> Unit)? = null
    ): BackupInfo? {
        if (state != BackupState.IDLE) {
            Log.w(TAG, "Backup operation already in progress")
            return null
        }
        
        state = BackupState.CREATING
        progress = 0
        error = null
        
        this.onProgress = onProgress
        this.onStateChange = onStateChange
        this.onError = onError
        this.onComplete = onComplete
        
        onStateChange?.invoke(state)
        
        try {
            val backupId = System.currentTimeMillis().toString()
            val backupDir = File(getBackupDir(), backupId)
            
            if (!backupDir.exists()) {
                backupDir.mkdirs()
            }
            
            // Save configuration
            saveConfig(backupDir)
            updateProgress(10)
            
            // Save API key
            saveApiKey(backupDir)
            updateProgress(20)
            
            // Save runtime data
            saveRuntimeData(backupDir)
            updateProgress(50)
            
            // Save web process state
            saveWebProcessState(backupDir)
            updateProgress(60)
            
            // Create backup archive
            val backupFile = createBackupArchive(backupDir, name, description)
            updateProgress(90)
            
            // Clean up temp directory
            deleteRecursive(backupDir)
            updateProgress(100)
            
            // Create backup info
            val backupInfo = BackupInfo(
                id = backupId,
                name = name,
                timestamp = System.currentTimeMillis(),
                size = backupFile.length(),
                description = description
            )
            
            currentBackup = backupInfo
            state = BackupState.SUCCESS
            onStateChange?.invoke(state)
            onComplete?.invoke()
            
            Log.i(TAG, "Backup created: ${backupInfo.name}")
            
            return backupInfo
            
        } catch (e: Exception) {
            state = BackupState.ERROR
            error = e.message ?: "Unknown error"
            onStateChange?.invoke(state)
            onError?.invoke(error!!)
            
            Log.e(TAG, "Failed to create backup: ${e.message}")
            return null
        }
    }
    
    /**
     * Save configuration
     */
    private fun saveConfig(backupDir: File) {
        try {
            val configFile = File(backupDir, "config.json")
            val config = configStore.exportConfig()
            configFile.writeText(Constants.gson.toJson(config))
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save config: ${e.message}")
            throw e
        }
    }
    
    /**
     * Save API key
     */
    private fun saveApiKey(backupDir: File) {
        try {
            val apiKey = keyVault.getApiKey()
            if (apiKey != null) {
                val apiKeyFile = File(backupDir, "api_key.enc")
                val encrypted = keyVault.encrypt(apiKey)
                apiKeyFile.writeBytes(encrypted)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save API key: ${e.message}")
            // Don't throw, API key is optional
        }
    }
    
    /**
     * Save runtime data
     */
    private fun saveRuntimeData(backupDir: File) {
        try {
            val runtimeDir = containerRuntime.getRuntimeDir()
            val runtimeBackupDir = File(backupDir, "runtime")
            
            copyDirectory(runtimeDir, runtimeBackupDir)
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save runtime data: ${e.message}")
            throw e
        }
    }
    
    /**
     * Save web process state
     */
    private fun saveWebProcessState(backupDir: File) {
        try {
            val stateFile = File(backupDir, "web_state.json")
            val state = mapOf(
                "isRunning" to webProcessManager.isRunning(),
                "port" to webProcessManager.getPort(),
                "host" to webProcessManager.getHost()
            )
            stateFile.writeText(Constants.gson.toJson(state))
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save web process state: ${e.message}")
            // Don't throw, web state is optional
        }
    }
    
    /**
     * Create backup archive
     */
    private fun createBackupArchive(
        sourceDir: File,
        name: String,
        description: String?
    ): File {
        try {
            val backupFile = File(getBackupDir(), "$name$BACKUP_EXTENSION")
            
            ZipOutputStream(FileOutputStream(backupFile)).use { zos ->
                // Add manifest
                val manifest = mapOf(
                    "version" to "1.0",
                    "name" to name,
                    "timestamp" to System.currentTimeMillis(),
                    "description" to description,
                    "isEncrypted" to true
                )
                
                val manifestEntry = ZipEntry(MANIFEST_FILE)
                zos.putNextEntry(manifestEntry)
                zos.write(Constants.gson.toJson(manifest).toByteArray(Charsets.UTF_8))
                zos.closeEntry()
                
                // Add all files from source directory
                addFilesToZip(sourceDir, sourceDir, zos)
            }
            
            return backupFile
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to create backup archive: ${e.message}")
            throw e
        }
    }
    
    /**
     * Add files to zip recursively
     */
    private fun addFilesToZip(sourceDir: File, currentDir: File, zos: ZipOutputStream) {
        val files = currentDir.listFiles() ?: return
        
        for (file in files) {
            if (file.isDirectory) {
                addFilesToZip(sourceDir, file, zos)
            } else {
                val relativePath = sourceDir.toPath().relativize(file.toPath()).toString()
                val entry = ZipEntry(relativePath)
                zos.putNextEntry(entry)
                file.inputStream().use { fis ->
                    fis.copyTo(zos)
                }
                zos.closeEntry()
            }
        }
    }
    
    // ========================================================================
    // BACKUP RESTORATION
    // ========================================================================
    
    /**
     * Restore from a backup
     */
    fun restoreBackup(
        backupInfo: BackupInfo,
        onProgress: ((Int) -> Unit)? = null,
        onStateChange: ((BackupState) -> Unit)? = null,
        onError: ((String) -> Unit)? = null,
        onComplete: (() -> Unit)? = null
    ): Boolean {
        if (state != BackupState.IDLE) {
            Log.w(TAG, "Backup operation already in progress")
            return false
        }
        
        state = BackupState.RESTORING
        progress = 0
        error = null
        
        this.onProgress = onProgress
        this.onStateChange = onStateChange
        this.onError = onError
        this.onComplete = onComplete
        
        onStateChange?.invoke(state)
        
        try {
            // Stop running processes
            webProcessManager.stopWebProcess()
            updateProgress(10)
            
            // Extract backup archive
            val tempDir = File(context.cacheDir, "restore_${System.currentTimeMillis()}")
            extractBackupArchive(backupInfo, tempDir)
            updateProgress(30)
            
            // Restore configuration
            restoreConfig(tempDir)
            updateProgress(40)
            
            // Restore API key
            restoreApiKey(tempDir)
            updateProgress(50)
            
            // Restore runtime data
            restoreRuntimeData(tempDir)
            updateProgress(70)
            
            // Restore web process state
            restoreWebProcessState(tempDir)
            updateProgress(80)
            
            // Clean up temp directory
            deleteRecursive(tempDir)
            updateProgress(100)
            
            state = BackupState.SUCCESS
            onStateChange?.invoke(state)
            onComplete?.invoke()
            
            Log.i(TAG, "Backup restored: ${backupInfo.name}")
            return true
            
        } catch (e: Exception) {
            state = BackupState.ERROR
            error = e.message ?: "Unknown error"
            onStateChange?.invoke(state)
            onError?.invoke(error!!)
            
            Log.e(TAG, "Failed to restore backup: ${e.message}")
            return false
        }
    }
    
    /**
     * Extract backup archive
     */
    private fun extractBackupArchive(backupInfo: BackupInfo, targetDir: File) {
        try {
            if (!targetDir.exists()) {
                targetDir.mkdirs()
            }
            
            val backupFile = File(getBackupDir(), "${backupInfo.name}$BACKUP_EXTENSION")
            if (!backupFile.exists()) {
                throw IOException("Backup file not found")
            }
            
            ZipInputStream(FileInputStream(backupFile)).use { zis ->
                var entry: ZipEntry? = zis.nextEntry
                
                while (entry != null) {
                    val outputFile = File(targetDir, entry.name)
                    
                    if (entry.isDirectory) {
                        outputFile.mkdirs()
                    } else {
                        outputFile.parentFile?.mkdirs()
                        FileOutputStream(outputFile).use { fos ->
                            zis.copyTo(fos)
                        }
                    }
                    
                    entry = zis.nextEntry
                }
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to extract backup archive: ${e.message}")
            throw e
        }
    }
    
    /**
     * Restore configuration
     */
    private fun restoreConfig(tempDir: File) {
        try {
            val configFile = File(tempDir, "config.json")
            if (configFile.exists()) {
                val json = configFile.readText()
                val config = Constants.gson.fromJson(json, Map::class.java)
                configStore.importConfig(config as Map<String, Any?>)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to restore config: ${e.message}")
            throw e
        }
    }
    
    /**
     * Restore API key
     */
    private fun restoreApiKey(tempDir: File) {
        try {
            val apiKeyFile = File(tempDir, "api_key.enc")
            if (apiKeyFile.exists()) {
                val encrypted = apiKeyFile.readBytes()
                val apiKey = keyVault.decrypt(encrypted)
                keyVault.setApiKey(apiKey)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to restore API key: ${e.message}")
            // Don't throw, API key is optional
        }
    }
    
    /**
     * Restore runtime data
     */
    private fun restoreRuntimeData(tempDir: File) {
        try {
            val runtimeBackupDir = File(tempDir, "runtime")
            val runtimeDir = containerRuntime.getRuntimeDir()
            
            // Clear existing runtime
            deleteRecursive(runtimeDir)
            runtimeDir.mkdirs()
            
            // Copy backup to runtime
            copyDirectory(runtimeBackupDir, runtimeDir)
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to restore runtime data: ${e.message}")
            throw e
        }
    }
    
    /**
     * Restore web process state
     */
    private fun restoreWebProcessState(tempDir: File) {
        try {
            val stateFile = File(tempDir, "web_state.json")
            if (stateFile.exists()) {
                val json = stateFile.readText()
                val state = Constants.gson.fromJson(json, Map::class.java)
                
                val isRunning = state["isRunning"] as? Boolean ?: false
                val port = state["port"] as? Int
                val host = state["host"] as? String
                
                if (port != null) {
                    runtimeHostPorts.setWebPort(port)
                }
                if (host != null) {
                    runtimeHostPorts.setHost(host)
                }
                
                // Note: We don't automatically restart the web process
                // The user should start it manually
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to restore web process state: ${e.message}")
            // Don't throw, web state is optional
        }
    }
    
    // ========================================================================
    // BACKUP LISTING
    // ========================================================================
    
    /**
     * List all backups
     */
    fun listBackups(): List<BackupInfo> {
        val backups = mutableListOf<BackupInfo>()
        
        try {
            val backupDir = getBackupDir()
            val files = backupDir.listFiles { file ->
                file.name.endsWith(BACKUP_EXTENSION)
            } ?: return backups
            
            for (file in files) {
                try {
                    val backupInfo = readBackupInfo(file)
                    backups.add(backupInfo)
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to read backup info from ${file.name}: ${e.message}")
                }
            }
            
            // Sort by timestamp (newest first)
            backups.sortByDescending { it.timestamp }
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to list backups: ${e.message}")
        }
        
        return backups
    }
    
    /**
     * Read backup info from file
     */
    private fun readBackupInfo(file: File): BackupInfo {
        try {
            ZipInputStream(FileInputStream(file)).use { zis ->
                var entry: ZipEntry? = zis.nextEntry
                
                while (entry != null) {
                    if (entry.name == MANIFEST_FILE) {
                        val json = zis.bufferedReader().readText()
                        val manifest = Constants.gson.fromJson(json, Map::class.java)
                        
                        return BackupInfo(
                            id = (manifest["timestamp"] as? Long)?.toString() ?: System.currentTimeMillis().toString(),
                            name = manifest["name"] as? String ?: file.nameWithoutExtension,
                            timestamp = manifest["timestamp"] as? Long ?: System.currentTimeMillis(),
                            size = file.length(),
                            description = manifest["description"] as? String,
                            version = manifest["version"] as? String ?: "1.0",
                            isEncrypted = manifest["isEncrypted"] as? Boolean ?: true
                        )
                    }
                    
                    entry = zis.nextEntry
                }
            }
            
            // Fallback if manifest not found
            return BackupInfo(
                id = System.currentTimeMillis().toString(),
                name = file.nameWithoutExtension,
                timestamp = file.lastModified(),
                size = file.length()
            )
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to read backup manifest: ${e.message}")
            throw e
        }
    }
    
    // ========================================================================
    // BACKUP DELETION
    // ========================================================================
    
    /**
     * Delete a backup
     */
    fun deleteBackup(
        backupInfo: BackupInfo,
        onProgress: ((Int) -> Unit)? = null,
        onStateChange: ((BackupState) -> Unit)? = null,
        onError: ((String) -> Unit)? = null,
        onComplete: (() -> Unit)? = null
    ): Boolean {
        if (state != BackupState.IDLE) {
            Log.w(TAG, "Backup operation already in progress")
            return false
        }
        
        state = BackupState.DELETING
        progress = 0
        error = null
        
        this.onProgress = onProgress
        this.onStateChange = onStateChange
        this.onError = onError
        this.onComplete = onComplete
        
        onStateChange?.invoke(state)
        
        try {
            val backupFile = File(getBackupDir(), "${backupInfo.name}$BACKUP_EXTENSION")
            
            if (backupFile.exists()) {
                backupFile.delete()
            }
            
            updateProgress(100)
            
            state = BackupState.SUCCESS
            onStateChange?.invoke(state)
            onComplete?.invoke()
            
            Log.i(TAG, "Backup deleted: ${backupInfo.name}")
            return true
            
        } catch (e: Exception) {
            state = BackupState.ERROR
            error = e.message ?: "Unknown error"
            onStateChange?.invoke(state)
            onError?.invoke(error!!)
            
            Log.e(TAG, "Failed to delete backup: ${e.message}")
            return false
        }
    }
    
    // ========================================================================
    // STATE
    // ========================================================================
    
    /**
     * Get current state
     */
    fun getState(): BackupState {
        return state
    }
    
    /**
     * Get current progress (0-100)
     */
    fun getProgress(): Int {
        return progress
    }
    
    /**
     * Get current backup
     */
    fun getCurrentBackup(): BackupInfo? {
        return currentBackup
    }
    
    /**
     * Get last error
     */
    fun getError(): String? {
        return error
    }
    
    // ========================================================================
    // CALLBACKS
    // ========================================================================
    
    /**
     * Set progress callback
     */
    fun setOnProgress(callback: (Int) -> Unit) {
        onProgress = callback
    }
    
    /**
     * Set state change callback
     */
    fun setOnStateChange(callback: (BackupState) -> Unit) {
        onStateChange = callback
    }
    
    /**
     * Set error callback
     */
    fun setOnError(callback: (String) -> Unit) {
        onError = callback
    }
    
    /**
     * Set complete callback
     */
    fun setOnComplete(callback: () -> Unit) {
        onComplete = callback
    }
    
    // ========================================================================
    // UTILITY
    // ========================================================================
    
    /**
     * Update progress and notify callback
     */
    private fun updateProgress(newProgress: Int) {
        progress = newProgress.coerceIn(0, 100)
        onProgress?.invoke(progress)
    }
    
    /**
     * Copy directory recursively
     */
    private fun copyDirectory(source: File, target: File) {
        if (!source.exists()) {
            return
        }
        
        if (!target.exists()) {
            target.mkdirs()
        }
        
        if (source.isDirectory) {
            val files = source.listFiles() ?: return
            for (file in files) {
                copyDirectory(file, File(target, file.name))
            }
        } else {
            Files.copy(
                source.toPath(),
                target.toPath(),
                StandardCopyOption.REPLACE_EXISTING
            )
        }
    }
    
    /**
     * Delete directory recursively
     */
    private fun deleteRecursive(file: File) {
        if (file.isDirectory) {
            file.listFiles()?.forEach { child ->
                deleteRecursive(child)
            }
        }
        file.delete()
    }
}
