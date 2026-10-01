// ProotBootstrap.kt - proot Binary Management
package com.cline.app.core

import android.content.Context
import android.os.Build
import android.util.Log
import com.cline.app.util.Constants
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/**
 * Proot Bootstrap - Manages proot binary for Cline Android
 * 
 * This class handles:
 * - proot binary location and availability
 * - proot binary extraction from assets
 * - proot binary validation
 * - proot command execution
 */
class ProotBootstrap(private val context: Context) {
    
    companion object {
        private const val TAG = "ProotBootstrap"
        
        // proot binary names
        private const val PROOT_BINARY = "proot"
        private const val PROOTLOADER_BINARY = "prootloader"
        private const val TERMUX_BINARY = "libtermux.so"
        
        // Binary directories
        private const val BIN_DIR = "proot-bin"
        private const val LIB_DIR = "lib"
    }
    
    // State
    private var prootPath: String? = null
    private var isProotAvailableChecked = false
    private var isProotAvailableValue = false
    
    // ========================================================================
    // PROOT AVAILABILITY
    // ========================================================================
    
    /**
     * Check if proot is available
     */
    fun isProotAvailable(): Boolean {
        if (!isProotAvailableChecked) {
            isProotAvailableValue = checkProotAvailability()
            isProotAvailableChecked = true
        }
        return isProotAvailableValue
    }
    
    /**
     * Ensure proot is available (extract if needed)
     */
    fun ensureProotAvailable(): Boolean {
        if (isProotAvailable()) {
            return true
        }
        
        try {
            extractProotBinary()
            isProotAvailableValue = checkProotAvailability()
            isProotAvailableChecked = true
            return isProotAvailableValue
        } catch (e: Exception) {
            Log.e(TAG, "Failed to ensure proot available: ${e.message}")
            return false
        }
    }
    
    /**
     * Check proot availability
     */
    private fun checkProotAvailability(): Boolean {
        try {
            val path = findProotPath()
            if (path != null) {
                prootPath = path
                return true
            }
            return false
        } catch (e: Exception) {
            Log.e(TAG, "Error checking proot availability: ${e.message}")
            return false
        }
    }
    
    // ========================================================================
    // PROOT PATH
    // ========================================================================
    
    /**
     * Get the proot path
     */
    fun getProotPath(): String? {
        if (prootPath == null && !isProotAvailableChecked) {
            isProotAvailable()
        }
        return prootPath
    }
    
    /**
     * Find proot path in various locations
     */
    private fun findProotPath(): String? {
        // Check app files directory
        val filesDir = context.filesDir
        val prootFile = File(filesDir, "$BIN_DIR/$PROOT_BINARY")
        if (prootFile.exists() && prootFile.canExecute()) {
            return prootFile.absolutePath
        }
        
        // Check app lib directory
        val libDir = File(filesDir, LIB_DIR)
        val prootLib = File(libDir, PROOT_BINARY)
        if (prootLib.exists() && prootLib.canExecute()) {
            return prootLib.absolutePath
        }
        
        // Check data directory
        val dataDir = context.applicationInfo.dataDir
        val dataProot = File(dataDir, "$BIN_DIR/$PROOT_BINARY")
        if (dataProot.exists() && dataProot.canExecute()) {
            return dataProot.absolutePath
        }
        
        // Check if proot is in PATH
        val path = System.getenv("PATH")
        if (path != null) {
            val paths = path.split(":")
            for (p in paths) {
                val prootInPath = File(p, PROOT_BINARY)
                if (prootInPath.exists() && prootInPath.canExecute()) {
                    return prootInPath.absolutePath
                }
            }
        }
        
        // Check common system paths
        val systemPaths = listOf(
            "/system/bin/$PROOT_BINARY",
            "/system/xbin/$PROOT_BINARY",
            "/data/data/com.termux/files/usr/bin/$PROOT_BINARY",
            "/data/adb/termux/files/usr/bin/$PROOT_BINARY"
        )
        
        for (systemPath in systemPaths) {
            val file = File(systemPath)
            if (file.exists() && file.canExecute()) {
                return systemPath
            }
        }
        
        return null
    }
    
    // ========================================================================
    // PROOT EXTRACTION
    // ========================================================================
    
    /**
     * Extract proot binary from assets
     */
    private fun extractProotBinary() {
        try {
            // Create bin directory
            val binDir = File(context.filesDir, BIN_DIR)
            if (!binDir.exists()) {
                binDir.mkdirs()
            }
            
            // Copy proot binary
            val prootFile = File(binDir, PROOT_BINARY)
            if (!prootFile.exists()) {
                copyAssetToFile("proot", prootFile)
                prootFile.setExecutable(true)
            }
            
            // Copy prootloader if available
            val prootloaderFile = File(binDir, PROOTLOADER_BINARY)
            if (!prootloaderFile.exists()) {
                copyAssetToFile("prootloader", prootloaderFile)
                prootloaderFile.setExecutable(true)
            }
            
            // Copy termux library if available
            val libDir = File(context.filesDir, LIB_DIR)
            if (!libDir.exists()) {
                libDir.mkdirs()
            }
            val termuxLib = File(libDir, TERMUX_BINARY)
            if (!termuxLib.exists()) {
                copyAssetToFile("libtermux.so", termuxLib)
                termuxLib.setExecutable(true)
            }
            
            // Update proot path
            prootPath = prootFile.absolutePath
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to extract proot binary: ${e.message}")
            throw e
        }
    }
    
    /**
     * Copy asset to file
     */
    private fun copyAssetToFile(assetName: String, outputFile: File) {
        try {
            val inputStream = context.assets.open(assetName)
            val outputStream = FileOutputStream(outputFile)
            
            inputStream.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }
            
            Log.i(TAG, "Copied asset $assetName to ${outputFile.absolutePath}")
            
        } catch (e: IOException) {
            Log.e(TAG, "Failed to copy asset $assetName: ${e.message}")
            throw e
        }
    }
    
    // ========================================================================
    // PROOT VALIDATION
    // ========================================================================
    
    /**
     * Validate proot binary
     */
    fun validateProot(): Boolean {
        val path = getProotPath()
        if (path == null) {
            return false
        }
        
        try {
            // Check if file exists
            val file = File(path)
            if (!file.exists() || !file.canExecute()) {
                return false
            }
            
            // Try to execute proot --version
            val process = Runtime.getRuntime().exec("$path --version")
            val exitCode = process.waitFor()
            
            return exitCode == 0
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to validate proot: ${e.message}")
            return false
        }
    }
    
    // ========================================================================
    // PROOT COMMAND
    // ========================================================================
    
    /**
     * Execute a proot command
     */
    fun executeProotCommand(vararg args: String): Process {
        val path = getProotPath() ?: throw RuntimeException("proot not available")
        
        val command = arrayOf(path) + args
        val process = Runtime.getRuntime().exec(command)
        
        return process
    }
    
    /**
     * Execute a proot command with environment
     */
    fun executeProotCommand(
        env: Map<String, String>,
        vararg args: String
    ): Process {
        val path = getProotPath() ?: throw RuntimeException("proot not available")
        
        val command = arrayOf(path) + args
        val processBuilder = ProcessBuilder(*command)
        
        // Set environment
        val environment = processBuilder.environment()
        env.forEach { (key, value) ->
            environment[key] = value
        }
        
        // Set working directory
        processBuilder.directory(context.filesDir)
        
        return processBuilder.start()
    }
    
    // ========================================================================
    // UTILITY
    // ========================================================================
    
    /**
     * Get proot version
     */
    fun getProotVersion(): String? {
        try {
            val path = getProotPath() ?: return null
            val process = Runtime.getRuntime().exec("$path --version")
            val input = process.inputStream.bufferedReader()
            val output = input.readText()
            return output.trim()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get proot version: ${e.message}")
            return null
        }
    }
    
    /**
     * Get proot architecture
     */
    fun getProotArchitecture(): String {
        return if (Build.SUPPORTED_ABIS.contains("arm64-v8a")) {
            "arm64"
        } else if (Build.SUPPORTED_ABIS.contains("armeabi-v7a")) {
            "arm"
        } else if (Build.SUPPORTED_ABIS.contains("x86_64")) {
            "x86_64"
        } else {
            "unknown"
        }
    }
}
