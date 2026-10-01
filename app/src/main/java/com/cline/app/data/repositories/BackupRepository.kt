package com.cline.app.data.repositories

import android.content.Context
import android.util.Log
import com.cline.app.util.Constants
import com.cline.app.util.ShellExecutor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Repository for managing backups.
 * 
 * Handles:
 * - Backup creation
 * - Backup restoration
 * - Backup listing
 * - Backup deletion
 * - Backup metadata
 */
class BackupRepository(
    private val context: Context
) {

    companion object {
        private const val TAG = "BackupRepository"
        private const val BACKUP_VERSION = 1
    }

    private val scope = CoroutineScope(Dispatchers.IO)
    private val shellExecutor = ShellExecutor()
    private val backupDir by lazy { File(Constants.BACKUP_DIR) }

    /**
     * Initialize the backup repository.
     */
    fun initialize() {
        scope.launch {
            ensureBackupDirectory()
        }
    }

    /**
     * Ensure the backup directory exists.
     */
    private suspend fun ensureBackupDirectory() {
        withContext(Dispatchers.IO) {
            if (!backupDir.exists()) {
                backupDir.mkdirs()
                Log.d(TAG, "Created backup directory: ${backupDir.absolutePath}")
            }
        }
    }

    /**
     * Create a new backup.
     */
    suspend fun createBackup(name: String = ""): Backup? {
        return withContext(Dispatchers.IO) {
            try {
                ensureBackupDirectory()
                
                val backupName = if (name.isNotEmpty()) {
                    name
                } else {
                    val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                    "backup_$timestamp"
                }
                
                val backupFile = File(backupDir, "$backupName.${Constants.BACKUP_EXTENSION}")
                
                // Create backup archive
                val result = createBackupArchive(backupFile)
                
                if (result) {
                    val metadataFile = File(backupDir, "$backupName.${Constants.BACKUP_EXTENSION}.meta")
                    saveBackupMetadata(metadataFile, backupName)
                    
                    Backup(
                        id = backupName,
                        file = backupFile,
                        metadataFile = metadataFile,
                        timestamp = System.currentTimeMillis(),
                        size = backupFile.length(),
                        version = BACKUP_VERSION
                    )
                } else {
                    backupFile.delete()
                    null
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error creating backup", e)
                null
            }
        }
    }

    /**
     * Create a backup archive.
     */
    private suspend fun createBackupArchive(backupFile: File): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Creating backup archive: ${backupFile.absolutePath}")
                
                // Create tar.gz archive of runtime directory
                val runtimeDir = File(Constants.RUNTIME_DIR)
                if (!runtimeDir.exists()) {
                    Log.e(TAG, "Runtime directory does not exist: ${runtimeDir.absolutePath}")
                    return@withContext false
                }
                
                // Use tar command to create archive
                val result = shellExecutor.execute(
                    "tar -czf ${backupFile.absolutePath} -C ${runtimeDir.parent} ${runtimeDir.name}"
                )
                
                backupFile.exists() && backupFile.length() > 0
            } catch (e: Exception) {
                Log.e(TAG, "Error creating backup archive", e)
                false
            }
        }
    }

    /**
     * Save backup metadata.
     */
    private fun saveBackupMetadata(metadataFile: File, backupName: String) {
        val metadata = BackupMetadata(
            id = backupName,
            timestamp = System.currentTimeMillis(),
            version = BACKUP_VERSION,
            appVersion = Constants.APP_VERSION,
            appVersionCode = Constants.APP_VERSION_CODE,
            runtimeStatus = "STOPPED",
            plugins = emptyList()
        )
        
        metadataFile.writeText(
            "{" +
            "\"id\":\"${metadata.id}\"," +
            "\"timestamp\":${metadata.timestamp}," +
            "\"version\":${metadata.version}," +
            "\"appVersion\":\"${metadata.appVersion}\"," +
            "\"appVersionCode\":${metadata.appVersionCode}," +
            "\"runtimeStatus\":\"${metadata.runtimeStatus}\"," +
            "\"plugins\":${metadata.plugins}" +
            "}"
        )
    }

    /**
     * Restore a backup.
     */
    suspend fun restoreBackup(backupId: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val backupFile = File(backupDir, "$backupId.${Constants.BACKUP_EXTENSION}")
                if (!backupFile.exists()) {
                    Log.e(TAG, "Backup file does not exist: ${backupFile.absolutePath}")
                    return@withContext false
                }
                
                // Stop runtime before restore
                // (Would be handled by caller)
                
                // Extract backup archive
                val result = extractBackupArchive(backupFile)
                
                if (result) {
                    // Verify extraction
                    val runtimeDir = File(Constants.RUNTIME_DIR)
                    runtimeDir.exists() && runtimeDir.isDirectory
                } else {
                    false
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error restoring backup: $backupId", e)
                false
            }
        }
    }

    /**
     * Extract a backup archive.
     */
    private suspend fun extractBackupArchive(backupFile: File): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Extracting backup archive: ${backupFile.absolutePath}")
                
                // Clean up existing runtime directory
                val runtimeDir = File(Constants.RUNTIME_DIR)
                if (runtimeDir.exists()) {
                    deleteRecursively(runtimeDir)
                }
                
                // Extract archive
                val result = shellExecutor.execute(
                    "tar -xzf ${backupFile.absolutePath} -C ${runtimeDir.parent}"
                )
                
                runtimeDir.exists()
            } catch (e: Exception) {
                Log.e(TAG, "Error extracting backup archive", e)
                false
            }
        }
    }

    /**
     * Delete a directory recursively.
     */
    private fun deleteRecursively(file: File): Boolean {
        if (file.isDirectory) {
            file.listFiles()?.forEach { child ->
                deleteRecursively(child)
            }
        }
        return file.delete()
    }

    /**
     * List all backups.
     */
    suspend fun listBackups(): List<Backup> {
        return withContext(Dispatchers.IO) {
            try {
                ensureBackupDirectory()
                
                val backups = mutableListOf<Backup>()
                
                if (backupDir.exists() && backupDir.isDirectory) {
                    backupDir.listFiles { file ->
                        file.name.endsWith(Constants.BACKUP_EXTENSION)
                    }?.forEach { backupFile ->
                        val backupName = backupFile.name.removeSuffix(Constants.BACKUP_EXTENSION)
                        val metadataFile = File(backupDir, "$backupName.${Constants.BACKUP_EXTENSION}.meta")
                        
                        val metadata = if (metadataFile.exists()) {
                            parseBackupMetadata(metadataFile)
                        } else {
                            BackupMetadata(
                                id = backupName,
                                timestamp = backupFile.lastModified(),
                                version = BACKUP_VERSION,
                                appVersion = "Unknown",
                                appVersionCode = 0,
                                runtimeStatus = "UNKNOWN",
                                plugins = emptyList()
                            )
                        }
                        
                        backups.add(Backup(
                            id = backupName,
                            file = backupFile,
                            metadataFile = metadataFile,
                            timestamp = metadata.timestamp,
                            size = backupFile.length(),
                            version = metadata.version
                        ))
                    }
                }
                
                backups.sortedByDescending { it.timestamp }
            } catch (e: Exception) {
                Log.e(TAG, "Error listing backups", e)
                emptyList()
            }
        }
    }

    /**
     * Parse backup metadata.
     */
    private fun parseBackupMetadata(metadataFile: File): BackupMetadata {
        return try {
            val json = metadataFile.readText()
            // Simple JSON parsing
            val id = extractJsonValue(json, "id") ?: ""
            val timestamp = extractJsonValue(json, "timestamp")?.toLong() ?: 0L
            val version = extractJsonValue(json, "version")?.toInt() ?: BACKUP_VERSION
            val appVersion = extractJsonValue(json, "appVersion") ?: "Unknown"
            val appVersionCode = extractJsonValue(json, "appVersionCode")?.toInt() ?: 0
            val runtimeStatus = extractJsonValue(json, "runtimeStatus") ?: "UNKNOWN"
            
            BackupMetadata(
                id = id,
                timestamp = timestamp,
                version = version,
                appVersion = appVersion,
                appVersionCode = appVersionCode,
                runtimeStatus = runtimeStatus,
                plugins = emptyList()
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing backup metadata", e)
            BackupMetadata(
                id = "",
                timestamp = 0L,
                version = BACKUP_VERSION,
                appVersion = "Unknown",
                appVersionCode = 0,
                runtimeStatus = "ERROR",
                plugins = emptyList()
            )
        }
    }

    /**
     * Extract a value from JSON string.
     */
    private fun extractJsonValue(json: String, key: String): String? {
        val start = json.indexOf("\"$key\":\"")
        if (start == -1) return null
        
        val valueStart = start + key.length + 4
        val valueEnd = json.indexOf("\"", valueStart)
        if (valueEnd == -1) return null
        
        return json.substring(valueStart, valueEnd)
    }

    /**
     * Delete a backup.
     */
    suspend fun deleteBackup(backupId: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val backupFile = File(backupDir, "$backupId.${Constants.BACKUP_EXTENSION}")
                val metadataFile = File(backupDir, "$backupId.${Constants.BACKUP_EXTENSION}.meta")
                
                var success = true
                
                if (backupFile.exists()) {
                    success = backupFile.delete()
                }
                
                if (metadataFile.exists()) {
                    success = success && metadataFile.delete()
                }
                
                success
            } catch (e: Exception) {
                Log.e(TAG, "Error deleting backup: $backupId", e)
                false
            }
        }
    }

    /**
     * Get backup by ID.
     */
    suspend fun getBackup(backupId: String): Backup? {
        return withContext(Dispatchers.IO) {
            try {
                val backups = listBackups()
                backups.find { it.id == backupId }
            } catch (e: Exception) {
                Log.e(TAG, "Error getting backup: $backupId", e)
                null
            }
        }
    }

    /**
     * Get the latest backup.
     */
    suspend fun getLatestBackup(): Backup? {
        return withContext(Dispatchers.IO) {
            try {
                val backups = listBackups()
                backups.firstOrNull()
            } catch (e: Exception) {
                Log.e(TAG, "Error getting latest backup", e)
                null
            }
        }
    }

    data class Backup(
        val id: String,
        val file: File,
        val metadataFile: File,
        val timestamp: Long,
        val size: Long,
        val version: Int
    )

    data class BackupMetadata(
        val id: String,
        val timestamp: Long,
        val version: Int,
        val appVersion: String,
        val appVersionCode: Int,
        val runtimeStatus: String,
        val plugins: List<String>
    )
}
