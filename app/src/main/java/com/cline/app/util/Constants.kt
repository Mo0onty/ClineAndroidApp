// Constants.kt - Shared Constants and Configuration Values
package com.cline.app.util

import android.os.Build
import com.google.gson.Gson
import com.google.gson.GsonBuilder

/**
 * Constants - Shared constants and configuration values for Cline Android
 * 
 * This class provides a centralized location for all constants used throughout
 * the application, including:
 * - File paths and directories
 * - Configuration values
 * - Runtime settings
 * - Build information
 */
object Constants {
    
    // ========================================================================
    // APPLICATION
    // ========================================================================
    
    const val APP_NAME = "Cline"
    const val APP_PACKAGE = "com.cline.app"
    const val APP_VERSION = "1.0.0"
    const val APP_VERSION_CODE = 1
    
    // ========================================================================
    // FILE SYSTEM
    // ========================================================================
    
    // Root directories
    const val ROOT_DIR = "cline"
    const val RUNTIME_DIR = "runtime"
    const val ASSETS_DIR = "assets"
    const val BACKUP_DIR = "backups"
    const val CACHE_DIR = "cache"
    const val DATA_DIR = "data"
    const val TEMP_DIR = "temp"
    
    // Runtime files
    const val ROOTFS_FILE = "offline-rootfs.bin"
    const val RUNTIME_FILE = "cline-runtime.bin"
    const val PROOT_BINARY = "proot"
    const val PROOTLOADER_BINARY = "prootloader"
    const val TERMUX_LIBRARY = "libtermux.so"
    
    // Backup files
    const val BACKUP_EXTENSION = ".clinebk"
    const val BACKUP_MANIFEST = "backup.manifest.json"
    
    // ========================================================================
    // NETWORK
    // ========================================================================
    
    // Default ports
    const val DEFAULT_WEB_PORT = 3080
    const val DEFAULT_HOST = "127.0.0.1"
    const val DEFAULT_TIMEOUT = 30000 // 30 seconds
    
    // Network
    const val NETWORK_TIMEOUT = 30000
    const val NETWORK_RETRY_COUNT = 3
    
    // ========================================================================
    // RUNTIME
    // ========================================================================
    
    // Architecture
    const val ARCH_ARM64 = "arm64-v8a"
    const val ARCH_ARM = "armeabi-v7a"
    const val ARCH_X86_64 = "x86_64"
    const val ARCH_X86 = "x86"
    
    // Supported architectures
    val SUPPORTED_ARCHS = listOf(ARCH_ARM64, ARCH_ARM, ARCH_X86_64, ARCH_X86)
    
    // Current architecture
    val CURRENT_ARCH: String
        get() {
            return if (Build.SUPPORTED_ABIS.contains(ARCH_ARM64)) {
                ARCH_ARM64
            } else if (Build.SUPPORTED_ABIS.contains(ARCH_ARM)) {
                ARCH_ARM
            } else if (Build.SUPPORTED_ABIS.contains(ARCH_X86_64)) {
                ARCH_X86_64
            } else {
                ARCH_X86
            }
        }
    
    // ========================================================================
    // PROOT
    // ========================================================================
    
    const val PROOT_VERSION = "5.3.2"
    const val PROOTLOADER_VERSION = "1.0.0"
    
    // ========================================================================
    // UBUNTU ROOTFS
    // ========================================================================
    
    const val UBUNTU_VERSION = "24.04"
    const val UBUNTU_ARCH = "arm64"
    
    // ========================================================================
    // CLINE RUNTIME
    // ========================================================================
    
    const val CLINE_VERSION = "0.1.0"
    const val NODE_VERSION = "20.11.1"
    
    // ========================================================================
    // PLUGINS
    // ========================================================================
    
    const val PLUGIN_DIR = "plugins"
    const val PLUGIN_EXTENSION = ".dshp"
    const val PLUGIN_MANIFEST = "plugin.json"
    
    // ========================================================================
    // SETTINGS
    // ========================================================================
    
    // Default settings
    const val DEFAULT_THEME = "system"
    const val DEFAULT_LANGUAGE = "en"
    const val DEFAULT_FONT_SIZE = 14f
    const val DEFAULT_DARK_MODE = false
    const val DEFAULT_DYNAMIC_COLOR = true
    const val DEFAULT_ENABLE_PLUGINS = true
    const val DEFAULT_AUTO_BACKUP = false
    
    // Theme options
    val THEME_OPTIONS = listOf("system", "light", "dark")
    
    // Language options
    val LANGUAGE_OPTIONS = listOf("en", "zh", "ja", "ko", "es", "fr", "de")
    
    // ========================================================================
    // SECURITY
    // ========================================================================
    
    const val KEYSTORE_NAME = "AndroidKeyStore"
    const val KEY_ALIAS = "cline_api_key_encryption"
    const val CIPHER_ALGORITHM = "AES/GCM/NoPadding"
    const val KEY_SIZE = 256
    
    // ========================================================================
    // LOGGING
    // ========================================================================
    
    const val LOG_TAG = "ClineAndroid"
    const val LOG_LEVEL_VERBOSE = 2
    const val LOG_LEVEL_DEBUG = 3
    const val LOG_LEVEL_INFO = 4
    const val LOG_LEVEL_WARN = 5
    const val LOG_LEVEL_ERROR = 6
    
    // ========================================================================
    // BUILD
    // ========================================================================
    
    const val BUILD_TYPE_DEBUG = "debug"
    const val BUILD_TYPE_RELEASE = "release"
    
    // ========================================================================
    // JSON
    // ========================================================================
    
    val gson: Gson by lazy {
        GsonBuilder()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .create()
    }
    
    // ========================================================================
    // FILE SIZES
    // ========================================================================
    
    const val KB = 1024L
    const val MB = KB * 1024
    const val GB = MB * 1024
    
    // ========================================================================
    // TIME
    // ========================================================================
    
    const val SECOND = 1000L
    const val MINUTE = SECOND * 60
    const val HOUR = MINUTE * 60
    const val DAY = HOUR * 24
    
    // ========================================================================
    // REGEX
    // ========================================================================
    
    val EMAIL_REGEX = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\$[A-Za-z]\\$")
    val URL_REGEX = Regex("https?://[^\\s]+")
    val UUID_REGEX = Regex("[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}")
    
    // ========================================================================
    // INTENTS
    // ========================================================================
    
    const val INTENT_ACTION_MAIN = "${APP_PACKAGE}.action.MAIN"
    const val INTENT_ACTION_SETTINGS = "${APP_PACKAGE}.action.SETTINGS"
    const val INTENT_ACTION_BACKUP = "${APP_PACKAGE}.action.BACKUP"
    const val INTENT_ACTION_RESTORE = "${APP_PACKAGE}.action.RESTORE"
    
    // ========================================================================
    // PERMISSIONS
    // ========================================================================
    
    const val PERMISSION_STORAGE = android.Manifest.permission.READ_EXTERNAL_STORAGE
    const val PERMISSION_INTERNET = android.Manifest.permission.INTERNET
    const val PERMISSION_NETWORK_STATE = android.Manifest.permission.ACCESS_NETWORK_STATE
    const val PERMISSION_WIFI_STATE = android.Manifest.permission.ACCESS_WIFI_STATE
    const val PERMISSION_FOREGROUND_SERVICE = android.Manifest.permission.FOREGROUND_SERVICE
    const val PERMISSION_NOTIFICATION = android.Manifest.permission.POST_NOTIFICATIONS
    
    // ========================================================================
    // NOTIFICATIONS
    // ========================================================================
    
    const val NOTIFICATION_CHANNEL_ID = "${APP_PACKAGE}_channel"
    const val NOTIFICATION_CHANNEL_NAME = "Cline Notifications"
    const val NOTIFICATION_ID_RUNTIME = 1
    const val NOTIFICATION_ID_BACKUP = 2
    const val NOTIFICATION_ID_UPDATE = 3
}
