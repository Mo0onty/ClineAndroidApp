// RuntimeHostPorts.kt - Runtime Port Management
package com.cline.app.core

import android.content.Context
import android.content.SharedPreferences
import com.cline.app.util.Constants

/**
 * Runtime Host Ports - Manages port configuration for Cline runtime
 * 
 * This class handles:
 * - Web UI port configuration
 * - Port validation
 * - Default port management
 */
class RuntimeHostPorts(private val context: Context) {
    
    companion object {
        private const val PREFS_NAME = "runtime_ports_prefs"
        private const val KEY_WEB_PORT = "web_port"
        private const val KEY_HOST = "host"
        
        // Default ports
        private const val DEFAULT_WEB_PORT = 3080
        private const val DEFAULT_HOST = "127.0.0.1"
        
        // Port range
        private const val MIN_PORT = 1024
        private const val MAX_PORT = 65535
    }
    
    private val prefs: SharedPreferences by lazy {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }
    
    // ========================================================================
    // WEB PORT
    // ========================================================================
    
    /**
     * Get the web port
     */
    fun getWebPort(): Int {
        return prefs.getInt(KEY_WEB_PORT, DEFAULT_WEB_PORT)
    }
    
    /**
     * Set the web port
     */
    fun setWebPort(port: Int) {
        if (isValidPort(port)) {
            prefs.edit().putInt(KEY_WEB_PORT, port).apply()
        } else {
            throw IllegalArgumentException("Invalid port: $port")
        }
    }
    
    /**
     * Get the default web port
     */
    fun getDefaultWebPort(): Int {
        return DEFAULT_WEB_PORT
    }
    
    // ========================================================================
    // HOST
    // ========================================================================
    
    /**
     * Get the host address
     */
    fun getHost(): String {
        return prefs.getString(KEY_HOST, DEFAULT_HOST) ?: DEFAULT_HOST
    }
    
    /**
     * Set the host address
     */
    fun setHost(host: String) {
        prefs.edit().putString(KEY_HOST, host).apply()
    }
    
    /**
     * Get the default host
     */
    fun getDefaultHost(): String {
        return DEFAULT_HOST
    }
    
    // ========================================================================
    // PORT VALIDATION
    // ========================================================================
    
    /**
     * Check if a port is valid
     */
    fun isValidPort(port: Int): Boolean {
        return port >= MIN_PORT && port <= MAX_PORT
    }
    
    /**
     * Check if a port is available (not in use)
     * Note: This is a simple check and may not be 100% accurate
     */
    fun isPortAvailable(port: Int): Boolean {
        if (!isValidPort(port)) {
            return false
        }
        
        // In a real implementation, we would check if the port is in use
        // For now, we'll assume it's available
        return true
    }
    
    // ========================================================================
    // PORT UTILITIES
    // ========================================================================
    
    /**
     * Get a random available port
     */
    fun getRandomAvailablePort(): Int {
        // Try a few random ports
        val portsToTry = listOf(3080, 3081, 3082, 3083, 3084, 3085, 8080, 8081, 8082)
        
        for (port in portsToTry) {
            if (isPortAvailable(port)) {
                return port
            }
        }
        
        // Fallback to default
        return DEFAULT_WEB_PORT
    }
    
    /**
     * Get the Web UI URL
     */
    fun getWebUiUrl(): String {
        return "http://${getHost()}:${getWebPort()}"
    }
    
    // ========================================================================
    // RESET
    // ========================================================================
    
    /**
     * Reset to default ports
     */
    fun resetToDefaults() {
        prefs.edit()
            .putInt(KEY_WEB_PORT, DEFAULT_WEB_PORT)
            .putString(KEY_HOST, DEFAULT_HOST)
            .apply()
    }
}
