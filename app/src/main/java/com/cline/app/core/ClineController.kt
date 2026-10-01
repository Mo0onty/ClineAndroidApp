// ClineController.kt - Main Orchestration Controller
package com.cline.app.core

import android.content.Context
import android.util.Log
import com.cline.app.ClineApp
import com.cline.app.data.KeyVault
import com.cline.app.util.Constants
import java.io.File

/**
 * Cline Controller - Main orchestration class for Cline Android
 * 
 * This class coordinates all major subsystems:
 * - Runtime environment setup
 * - Web UI process management
 * - Configuration management
 * - API key handling
 * - Lifecycle management
 */
class ClineController(
    private val context: Context,
    private val configStore: ConfigStore,
    private val runtimeHostPorts: RuntimeHostPorts,
    private val prootBootstrap: ProotBootstrap,
    private val containerRuntime: ContainerRuntime,
    private val webProcessManager: WebProcessManager,
    private val environmentAccess: EnvironmentAccess,
    private val keyVault: KeyVault
) {
    
    companion object {
        private const val TAG = "ClineController"
    }
    
    // State
    private var isStarting = false
    private var isRunning = false
    private var startError: String? = null
    
    // ========================================================================
    // LIFECYCLE
    // ========================================================================
    
    /**
     * Start the Cline runtime and Web UI
     */
    fun start() {
        if (isStarting || isRunning) {
            Log.i(TAG, "Already starting or running")
            return
        }
        
        isStarting = true
        startError = null
        
        try {
            // Step 1: Initialize proot
            if (!prootBootstrap.ensureProotAvailable()) {
                throw RuntimeException("proot not available")
            }
            
            // Step 2: Initialize runtime environment
            if (!containerRuntime.ensureRuntimeReady()) {
                throw RuntimeException("Runtime not ready")
            }
            
            // Step 3: Start Web UI
            startWebUi()
            
            isRunning = true
            isStarting = false
            
            Log.i(TAG, "Cline started successfully")
            
        } catch (e: Exception) {
            startError = e.message ?: "Unknown error"
            isStarting = false
            isRunning = false
            Log.e(TAG, "Failed to start Cline: ${e.message}")
            throw e
        }
    }
    
    /**
     * Stop the Cline runtime and Web UI
     */
    fun stop() {
        if (!isRunning && !isStarting) {
            Log.i(TAG, "Already stopped")
            return
        }
        
        try {
            // Step 1: Stop Web UI
            webProcessManager.stopWebProcess()
            
            // Step 2: Clean up runtime
            containerRuntime.cleanup()
            
            isRunning = false
            isStarting = false
            startError = null
            
            Log.i(TAG, "Cline stopped successfully")
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to stop Cline: ${e.message}")
            throw e
        }
    }
    
    /**
     * Restart the Cline runtime
     */
    fun restart() {
        stop()
        start()
    }
    
    // ========================================================================
    // WEB UI
    // ========================================================================
    
    /**
     * Start the Web UI process
     */
    private fun startWebUi() {
        val port = runtimeHostPorts.getWebPort()
        val host = runtimeHostPorts.getHost()
        
        // Configure and start web process
        webProcessManager.startWebProcess(
            host = host,
            port = port,
            runtimeDir = containerRuntime.getRuntimeDir(),
            onStarted = {
                Log.i(TAG, "Web UI started on $host:$port")
            },
            onError = { error ->
                Log.e(TAG, "Web UI error: $error")
            }
        )
    }
    
    /**
     * Get the Web UI URL
     */
    fun getWebUrl(): String {
        val port = runtimeHostPorts.getWebPort()
        val host = runtimeHostPorts.getHost()
        return "http://$host:$port"
    }
    
    // ========================================================================
    // STATE
    // ========================================================================
    
    /**
     * Check if Cline is currently starting
     */
    fun isStarting(): Boolean {
        return isStarting
    }
    
    /**
     * Check if Cline is currently running
     */
    fun isRunning(): Boolean {
        return isRunning
    }
    
    /**
     * Get the last start error
     */
    fun getStartError(): String? {
        return startError
    }
    
    // ========================================================================
    // CONFIGURATION
    // ========================================================================
    
    /**
     * Get the current configuration
     */
    fun getConfig(): ConfigStore {
        return configStore
    }
    
    /**
     * Update the web port
     */
    fun setWebPort(port: Int) {
        runtimeHostPorts.setWebPort(port)
        configStore.webPort = port
    }
    
    /**
     * Get the current web port
     */
    fun getWebPort(): Int {
        return runtimeHostPorts.getWebPort()
    }
    
    // ========================================================================
    // API KEY
    // ========================================================================
    
    /**
     * Set the API key (encrypted)
     */
    fun setApiKey(apiKey: String) {
        keyVault.setApiKey(apiKey)
    }
    
    /**
     * Get the API key (decrypted)
     */
    fun getApiKey(): String? {
        return keyVault.getApiKey()
    }
    
    /**
     * Clear the API key
     */
    fun clearApiKey() {
        keyVault.clearApiKey()
    }
    
    // ========================================================================
    // RUNTIME
    // ========================================================================
    
    /**
     * Get the runtime directory
     */
    fun getRuntimeDir(): File {
        return containerRuntime.getRuntimeDir()
    }
    
    /**
     * Check if runtime is ready
     */
    fun isRuntimeReady(): Boolean {
        return containerRuntime.isRuntimeReady()
    }
    
    /**
     * Check if proot is available
     */
    fun isProotAvailable(): Boolean {
        return prootBootstrap.isProotAvailable()
    }
    
    // ========================================================================
    // ENVIRONMENT
    // ========================================================================
    
    /**
     * Check if environment is accessible
     */
    fun isEnvironmentAccessible(): Boolean {
        return environmentAccess.isEnvironmentAccessible()
    }
    
    /**
     * Get the proot path
     */
    fun getProotPath(): String? {
        return prootBootstrap.getProotPath()
    }
    
    // ========================================================================
    // UTILITY
    // ========================================================================
    
    /**
     * Get the application context
     */
    fun getContext(): Context {
        return context
    }
    
    /**
     * Get the Cline application instance
     */
    fun getApp(): ClineApp {
        return context.applicationContext as ClineApp
    }
}
