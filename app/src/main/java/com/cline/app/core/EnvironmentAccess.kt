// EnvironmentAccess.kt - Environment Access Management
package com.cline.app.core

import android.content.Context
import android.os.Environment
import android.util.Log
import java.io.File

/**
 * Environment Access - Manages access to external storage and environment
 * 
 * This class handles:
 * - External storage access
 * - Environment state checks
 * - Directory permissions
 */
class EnvironmentAccess(private val context: Context) {
    
    companion object {
        private const val TAG = "EnvironmentAccess"
        
        // Environment states
        private const val STATE_UNKNOWN = 0
        private const val STATE_AVAILABLE = 1
        private const val STATE_UNAVAILABLE = 2
        private const val STATE_READ_ONLY = 3
    }
    
    // State
    private var environmentState: Int = STATE_UNKNOWN
    private var lastCheckTime: Long = 0
    
    // ========================================================================
    // ENVIRONMENT STATE
    // ========================================================================
    
    /**
     * Check if environment is accessible
     */
    fun isEnvironmentAccessible(): Boolean {
        checkEnvironmentState()
        return environmentState == STATE_AVAILABLE
    }
    
    /**
     * Check if environment is read-only
     */
    fun isEnvironmentReadOnly(): Boolean {
        checkEnvironmentState()
        return environmentState == STATE_READ_ONLY
    }
    
    /**
     * Get environment state
     */
    fun getEnvironmentState(): Int {
        checkEnvironmentState()
        return environmentState
    }
    
    /**
     * Check environment state
     */
    private fun checkEnvironmentState() {
        val now = System.currentTimeMillis()
        
        // Only check every 5 seconds
        if (now - lastCheckTime < 5000) {
            return
        }
        
        lastCheckTime = now
        
        try {
            // Check external storage state
            val state = Environment.getExternalStorageState()
            
            environmentState = when (state) {
                Environment.MEDIA_MOUNTED -> STATE_AVAILABLE
                Environment.MEDIA_MOUNTED_READ_ONLY -> STATE_READ_ONLY
                else -> STATE_UNAVAILABLE
            }
            
            Log.i(TAG, "Environment state: ${stateToString(environmentState)}")
            
        } catch (e: Exception) {
            Log.e(TAG, "Error checking environment state: ${e.message}")
            environmentState = STATE_UNAVAILABLE
        }
    }
    
    /**
     * Convert state to string
     */
    private fun stateToString(state: Int): String {
        return when (state) {
            STATE_UNKNOWN -> "Unknown"
            STATE_AVAILABLE -> "Available"
            STATE_UNAVAILABLE -> "Unavailable"
            STATE_READ_ONLY -> "Read-Only"
            else -> "Unknown"
        }
    }
    
    // ========================================================================
    // DIRECTORIES
    // ========================================================================
    
    /**
     * Get external storage directory
     */
    fun getExternalStorageDir(): File? {
        return if (isEnvironmentAccessible()) {
            context.getExternalFilesDir(null)
        } else {
            null
        }
    }
    
    /**
     * Get app-specific external storage directory
     */
    fun getAppExternalDir(): File? {
        return if (isEnvironmentAccessible()) {
            context.getExternalFilesDir(null)
        } else {
            null
        }
    }
    
    /**
     * Get downloads directory
     */
    fun getDownloadsDir(): File? {
        return if (isEnvironmentAccessible()) {
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        } else {
            null
        }
    }
    
    /**
     * Get documents directory
     */
    fun getDocumentsDir(): File? {
        return if (isEnvironmentAccessible()) {
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
        } else {
            null
        }
    }
    
    // ========================================================================
    // PERMISSIONS
    // ========================================================================
    
    /**
     * Check if we have external storage write permission
     */
    fun hasExternalStoragePermission(): Boolean {
        // In a real implementation, we would check the permission
        // For now, we'll assume we have it
        return true
    }
    
    /**
     * Check if we have external storage read permission
     */
    fun hasExternalStorageReadPermission(): Boolean {
        // In a real implementation, we would check the permission
        // For now, we'll assume we have it
        return true
    }
    
    // ========================================================================
    // FILE OPERATIONS
    // ========================================================================
    
    /**
     * Check if a file is writable
     */
    fun isFileWritable(file: File): Boolean {
        try {
            if (!file.exists()) {
                file.createNewFile()
                file.delete()
                return true
            }
            return file.canWrite()
        } catch (e: Exception) {
            return false
        }
    }
    
    /**
     * Check if a file is readable
     */
    fun isFileReadable(file: File): Boolean {
        return file.exists() && file.canRead()
    }
    
    /**
     * Check if a directory is accessible
     */
    fun isDirectoryAccessible(dir: File): Boolean {
        return dir.exists() && dir.canRead()
    }
    
    // ========================================================================
    // STORAGE INFO
    // ========================================================================
    
    /**
     * Get available storage space
     */
    fun getAvailableSpace(): Long {
        try {
            val stat = Environment.getDataDirectory().statFs
            return stat.availableBytes
        } catch (e: Exception) {
            return 0
        }
    }
    
    /**
     * Get total storage space
     */
    fun getTotalSpace(): Long {
        try {
            val stat = Environment.getDataDirectory().statFs
            return stat.totalBytes
        } catch (e: Exception) {
            return 0
        }
    }
    
    /**
     * Get used storage space
     */
    fun getUsedSpace(): Long {
        return getTotalSpace() - getAvailableSpace()
    }
    
    // ========================================================================
    // UTILITY
    // ========================================================================
    
    /**
     * Get the app cache directory
     */
    fun getCacheDir(): File {
        return context.cacheDir
    }
    
    /**
     * Get the app files directory
     */
    fun getFilesDir(): File {
        return context.filesDir
    }
    
    /**
     * Get the app external cache directory
     */
    fun getExternalCacheDir(): File? {
        return if (isEnvironmentAccessible()) {
            context.externalCacheDir
        } else {
            null
        }
    }
    
    /**
     * Check if a path is on external storage
     */
    fun isOnExternalStorage(path: String): Boolean {
        val externalDir = getAppExternalDir()?.absolutePath ?: return false
        return path.startsWith(externalDir)
    }
}
