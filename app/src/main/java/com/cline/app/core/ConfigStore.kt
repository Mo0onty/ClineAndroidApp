// ConfigStore.kt - Persistent Configuration Management
package com.cline.app.core

import android.content.Context
import android.content.SharedPreferences
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.cline.app.util.Constants
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/**
 * Configuration Store - Manages persistent configuration for Cline Android
 * 
 * This class provides secure storage for:
 * - User preferences (theme, language, etc.)
 * - Runtime configuration (ports, paths, etc.)
 * - API keys and sensitive data (encrypted)
 * 
 * Configuration is stored using:
 * - SharedPreferences for regular settings
 * - EncryptedSharedPreferences for sensitive data
 */
class ConfigStore(private val context: Context) {
    
    companion object {
        private const val TAG = "ConfigStore"
        
        // Preference file names
        private const val PREFS_NAME = "cline_prefs"
        private const val SECURE_PREFS_NAME = "cline_secure_prefs"
        
        // Keys
        private const val KEY_WEB_PORT = "web_port"
        private const val KEY_API_KEY = "api_key"
        private const val KEY_MODEL = "model"
        private const val KEY_WORKDIR = "workdir"
        private const val KEY_THEME = "theme"
        private const val KEY_LANGUAGE = "language"
        private const val KEY_DYNAMIC_COLOR = "dynamic_color"
        private const val KEY_DARK_MODE = "dark_mode"
        private const val KEY_TERMINAL_FONT_SIZE = "terminal_font_size"
        private const val KEY_ENABLE_PLUGINS = "enable_plugins"
        private const val KEY_AUTO_BACKUP = "auto_backup"
        private const val KEY_FIRST_LAUNCH = "first_launch"
        
        // Default values
        private const val DEFAULT_WEB_PORT = 3080
        private const val DEFAULT_MODEL = "cline"
        private const val DEFAULT_THEME = "system"
        private const val DEFAULT_LANGUAGE = "en"
        private const val DEFAULT_TERMINAL_FONT_SIZE = 14f
    }
    
    // Preferences
    private val prefs: SharedPreferences by lazy {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }
    
    // Secure preferences
    private val securePrefs: SharedPreferences by lazy {
        createEncryptedSharedPreferences()
    }
    
    // ========================================================================
    // INITIALIZATION
    // ========================================================================
    
    /**
     * Create encrypted shared preferences
     */
    private fun createEncryptedSharedPreferences(): SharedPreferences {
        try {
            val masterKey = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()
            
            return EncryptedSharedPreferences.create(
                context,
                SECURE_PREFS_NAME,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        } catch (e: Exception) {
            Log.e(TAG, "Failed to create encrypted preferences: ${e.message}")
            // Fallback to regular preferences
            return context.getSharedPreferences(SECURE_PREFS_NAME, Context.MODE_PRIVATE)
        }
    }
    
    // ========================================================================
    // RUNTIME CONFIGURATION
    // ========================================================================
    
    /**
     * Get the web port
     */
    var webPort: Int
        get() = prefs.getInt(KEY_WEB_PORT, DEFAULT_WEB_PORT)
        set(value) = prefs.edit().putInt(KEY_WEB_PORT, value).apply()
    
    /**
     * Get the selected model
     */
    var selectedModel: String
        get() = prefs.getString(KEY_MODEL, DEFAULT_MODEL) ?: DEFAULT_MODEL
        set(value) = prefs.edit().putString(KEY_MODEL, value).apply()
    
    /**
     * Get the working directory
     */
    var workdir: String
        get() = prefs.getString(KEY_WORKDIR, "") ?: ""
        set(value) = prefs.edit().putString(KEY_WORKDIR, value).apply()
    
    // ========================================================================
    // API KEY (Encrypted)
    // ========================================================================
    
    /**
     * Get the API key (encrypted storage)
     */
    var apiKey: String?
        get() = securePrefs.getString(KEY_API_KEY, null)
        set(value) = securePrefs.edit().putString(KEY_API_KEY, value).apply()
    
    /**
     * Check if API key is set
     */
    fun hasApiKey(): Boolean {
        return apiKey?.isNotBlank() == true
    }
    
    // ========================================================================
    // APPEARANCE SETTINGS
    // ========================================================================
    
    /**
     * Get the theme setting
     */
    var theme: String
        get() = prefs.getString(KEY_THEME, DEFAULT_THEME) ?: DEFAULT_THEME
        set(value) = prefs.edit().putString(KEY_THEME, value).apply()
    
    /**
     * Check if dark mode is enabled
     */
    var isDarkMode: Boolean
        get() = prefs.getBoolean(KEY_DARK_MODE, false)
        set(value) = prefs.edit().putBoolean(KEY_DARK_MODE, value).apply()
    
    /**
     * Check if dynamic color is enabled
     */
    var useDynamicColor: Boolean
        get() = prefs.getBoolean(KEY_DYNAMIC_COLOR, true)
        set(value) = prefs.edit().putBoolean(KEY_DYNAMIC_COLOR, value).apply()
    
    // ========================================================================
    // LANGUAGE SETTINGS
    // ========================================================================
    
    /**
     * Get the language setting
     */
    var language: String
        get() = prefs.getString(KEY_LANGUAGE, DEFAULT_LANGUAGE) ?: DEFAULT_LANGUAGE
        set(value) = prefs.edit().putString(KEY_LANGUAGE, value).apply()
    
    // ========================================================================
    // TERMINAL SETTINGS
    // ========================================================================
    
    /**
     * Get the terminal font size
     */
    var terminalFontSize: Float
        get() = prefs.getFloat(KEY_TERMINAL_FONT_SIZE, DEFAULT_TERMINAL_FONT_SIZE)
        set(value) = prefs.edit().putFloat(KEY_TERMINAL_FONT_SIZE, value).apply()
    
    // ========================================================================
    // PLUGIN SETTINGS
    // ========================================================================
    
    /**
     * Check if plugins are enabled
     */
    var enablePlugins: Boolean
        get() = prefs.getBoolean(KEY_ENABLE_PLUGINS, true)
        set(value) = prefs.edit().putBoolean(KEY_ENABLE_PLUGINS, value).apply()
    
    // ========================================================================
    // BACKUP SETTINGS
    // ========================================================================
    
    /**
     * Check if auto backup is enabled
     */
    var autoBackup: Boolean
        get() = prefs.getBoolean(KEY_AUTO_BACKUP, false)
        set(value) = prefs.edit().putBoolean(KEY_AUTO_BACKUP, value).apply()
    
    // ========================================================================
    // FIRST LAUNCH
    // ========================================================================
    
    /**
     * Check if this is the first launch
     */
    fun isFirstLaunch(): Boolean {
        return prefs.getBoolean(KEY_FIRST_LAUNCH, true)
    }
    
    /**
     * Mark first launch as complete
     */
    fun markFirstLaunchComplete() {
        prefs.edit().putBoolean(KEY_FIRST_LAUNCH, false).apply()
    }
    
    // ========================================================================
    // BULK OPERATIONS
    // ========================================================================
    
    /**
     * Clear all configuration
     */
    fun clearAll() {
        prefs.edit().clear().apply()
        securePrefs.edit().clear().apply()
    }
    
    /**
     * Clear only non-sensitive configuration
     */
    fun clearNonSecure() {
        prefs.edit().clear().apply()
    }
    
    /**
     * Clear only sensitive configuration
     */
    fun clearSecure() {
        securePrefs.edit().clear().apply()
    }
    
    // ========================================================================
    // EXPORT / IMPORT
    // ========================================================================
    
    /**
     * Export configuration to a map
     */
    fun exportConfig(): Map<String, Any?> {
        return mapOf(
            KEY_WEB_PORT to webPort,
            KEY_MODEL to selectedModel,
            KEY_WORKDIR to workdir,
            KEY_THEME to theme,
            KEY_LANGUAGE to language,
            KEY_DYNAMIC_COLOR to useDynamicColor,
            KEY_DARK_MODE to isDarkMode,
            KEY_TERMINAL_FONT_SIZE to terminalFontSize,
            KEY_ENABLE_PLUGINS to enablePlugins,
            KEY_AUTO_BACKUP to autoBackup
        )
    }
    
    /**
     * Import configuration from a map
     */
    fun importConfig(config: Map<String, Any?>) {
        config[KEY_WEB_PORT]?.let { webPort = it as Int }
        config[KEY_MODEL]?.let { selectedModel = it as String }
        config[KEY_WORKDIR]?.let { workdir = it as String }
        config[KEY_THEME]?.let { theme = it as String }
        config[KEY_LANGUAGE]?.let { language = it as String }
        config[KEY_DYNAMIC_COLOR]?.let { useDynamicColor = it as Boolean }
        config[KEY_DARK_MODE]?.let { isDarkMode = it as Boolean }
        config[KEY_TERMINAL_FONT_SIZE]?.let { terminalFontSize = it as Float }
        config[KEY_ENABLE_PLUGINS]?.let { enablePlugins = it as Boolean }
        config[KEY_AUTO_BACKUP]?.let { autoBackup = it as Boolean }
    }
}
