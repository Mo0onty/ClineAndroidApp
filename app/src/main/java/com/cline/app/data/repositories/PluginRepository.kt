package com.cline.app.data.repositories

import android.content.Context
import android.util.Log
import com.cline.app.core.ProotBootstrap
import com.cline.app.util.Constants
import com.cline.app.util.ShellExecutor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

/**
 * Repository for managing plugins in the Cline runtime.
 * 
 * Handles:
 * - Plugin installation
 * - Plugin uninstallation
 * - Plugin listing
 * - Plugin state management
 * - Plugin updates
 */
class PluginRepository(
    private val context: Context,
    private val prootBootstrap: ProotBootstrap
) {

    companion object {
        private const val TAG = "PluginRepository"
        private const val PLUGINS_DIR = "plugins"
    }

    private val scope = CoroutineScope(Dispatchers.IO)
    private val shellExecutor = ShellExecutor()
    private val pluginsDir by lazy { File(Constants.RUNTIME_DIR, PLUGINS_DIR) }

    /**
     * Initialize the plugin repository.
     */
    fun initialize() {
        scope.launch {
            ensurePluginsDirectory()
        }
    }

    /**
     * Ensure the plugins directory exists.
     */
    private suspend fun ensurePluginsDirectory() {
        withContext(Dispatchers.IO) {
            if (!pluginsDir.exists()) {
                pluginsDir.mkdirs()
                Log.d(TAG, "Created plugins directory: ${pluginsDir.absolutePath}")
            }
        }
    }

    /**
     * List all installed plugins.
     */
    suspend fun listPlugins(): List<Plugin> {
        return withContext(Dispatchers.IO) {
            try {
                ensurePluginsDirectory()
                
                val plugins = mutableListOf<Plugin>()
                
                if (pluginsDir.exists() && pluginsDir.isDirectory) {
                    pluginsDir.listFiles()?.forEach { pluginDir ->
                        if (pluginDir.isDirectory) {
                            val manifestFile = File(pluginDir, "manifest.json")
                            if (manifestFile.exists()) {
                                // Parse plugin manifest
                                val plugin = parsePluginManifest(manifestFile)
                                plugins.add(plugin)
                            }
                        }
                    }
                }
                
                plugins
            } catch (e: Exception) {
                Log.e(TAG, "Error listing plugins", e)
                emptyList()
            }
        }
    }

    /**
     * Parse a plugin manifest file.
     */
    private fun parsePluginManifest(manifestFile: File): Plugin {
        return try {
            val json = manifestFile.readText()
            // Simple JSON parsing (would use Gson in production)
            val plugin = Plugin(
                id = manifestFile.parentFile?.name ?: "unknown",
                name = extractJsonValue(json, "name") ?: "Unknown",
                version = extractJsonValue(json, "version") ?: "1.0.0",
                description = extractJsonValue(json, "description") ?: "",
                author = extractJsonValue(json, "author") ?: "Unknown",
                state = PluginState.INSTALLED,
                enabled = true
            )
            plugin
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing plugin manifest: ${manifestFile.name}", e)
            Plugin(
                id = manifestFile.parentFile?.name ?: "unknown",
                name = "Unknown",
                version = "1.0.0",
                description = "",
                author = "Unknown",
                state = PluginState.ERROR,
                enabled = false
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
     * Install a plugin from a URL.
     */
    suspend fun installPlugin(pluginUrl: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Installing plugin from: $pluginUrl")
                
                // Download plugin archive
                val pluginFile = File(pluginsDir, "temp_plugin.zip")
                if (!downloadPlugin(pluginUrl, pluginFile)) {
                    Log.e(TAG, "Failed to download plugin")
                    return@withContext false
                }
                
                // Extract plugin
                if (!extractPlugin(pluginFile)) {
                    Log.e(TAG, "Failed to extract plugin")
                    pluginFile.delete()
                    return@withContext false
                }
                
                // Clean up
                pluginFile.delete()
                true
            } catch (e: Exception) {
                Log.e(TAG, "Error installing plugin", e)
                false
            }
        }
    }

    /**
     * Download a plugin archive.
     */
    private suspend fun downloadPlugin(url: String, destination: File): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                // Use curl or wget to download
                val result = shellExecutor.execute("curl -L -o ${destination.absolutePath} $url")
                destination.exists() && destination.length() > 0
            } catch (e: Exception) {
                false
            }
        }
    }

    /**
     * Extract a plugin archive.
     */
    private suspend fun extractPlugin(pluginFile: File): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                // Extract using unzip
                val result = shellExecutor.execute("unzip -o ${pluginFile.absolutePath} -d ${pluginsDir.absolutePath}")
                result.isNotEmpty()
            } catch (e: Exception) {
                false
            }
        }
    }

    /**
     * Uninstall a plugin.
     */
    suspend fun uninstallPlugin(pluginId: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val pluginDir = File(pluginsDir, pluginId)
                if (pluginDir.exists()) {
                    deleteRecursively(pluginDir)
                    true
                } else {
                    false
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error uninstalling plugin: $pluginId", e)
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
     * Enable a plugin.
     */
    suspend fun enablePlugin(pluginId: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                // Update plugin state in manifest
                val pluginDir = File(pluginsDir, pluginId)
                val manifestFile = File(pluginDir, "manifest.json")
                
                if (manifestFile.exists()) {
                    val json = manifestFile.readText()
                    val updatedJson = json.replace("\"enabled\":false", "\"enabled\":true")
                    manifestFile.writeText(updatedJson)
                    true
                } else {
                    false
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error enabling plugin: $pluginId", e)
                false
            }
        }
    }

    /**
     * Disable a plugin.
     */
    suspend fun disablePlugin(pluginId: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                // Update plugin state in manifest
                val pluginDir = File(pluginsDir, pluginId)
                val manifestFile = File(pluginDir, "manifest.json")
                
                if (manifestFile.exists()) {
                    val json = manifestFile.readText()
                    val updatedJson = json.replace("\"enabled\":true", "\"enabled\":false")
                    manifestFile.writeText(updatedJson)
                    true
                } else {
                    false
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error disabling plugin: $pluginId", e)
                false
            }
        }
    }

    /**
     * Get plugin by ID.
     */
    suspend fun getPlugin(pluginId: String): Plugin? {
        return withContext(Dispatchers.IO) {
            try {
                val pluginDir = File(pluginsDir, pluginId)
                if (pluginDir.exists() && pluginDir.isDirectory) {
                    val manifestFile = File(pluginDir, "manifest.json")
                    if (manifestFile.exists()) {
                        return@withContext parsePluginManifest(manifestFile)
                    }
                }
                null
            } catch (e: Exception) {
                Log.e(TAG, "Error getting plugin: $pluginId", e)
                null
            }
        }
    }

    /**
     * Check if a plugin is installed.
     */
    suspend fun isPluginInstalled(pluginId: String): Boolean {
        return withContext(Dispatchers.IO) {
            File(pluginsDir, pluginId).exists()
        }
    }

    /**
     * Check if a plugin is enabled.
     */
    suspend fun isPluginEnabled(pluginId: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val plugin = getPlugin(pluginId)
                plugin?.enabled ?: false
            } catch (e: Exception) {
                false
            }
        }
    }

    /**
     * Get plugin state.
     */
    suspend fun getPluginState(pluginId: String): PluginState {
        return withContext(Dispatchers.IO) {
            try {
                val pluginDir = File(pluginsDir, pluginId)
                if (!pluginDir.exists()) {
                    return@withContext PluginState.NOT_INSTALLED
                }
                
                val manifestFile = File(pluginDir, "manifest.json")
                if (!manifestFile.exists()) {
                    return@withContext PluginState.ERROR
                }
                
                val plugin = parsePluginManifest(manifestFile)
                if (plugin.enabled) {
                    PluginState.ENABLED
                } else {
                    PluginState.INSTALLED
                }
            } catch (e: Exception) {
                PluginState.ERROR
            }
        }
    }

    data class Plugin(
        val id: String,
        val name: String,
        val version: String,
        val description: String,
        val author: String,
        val state: PluginState,
        val enabled: Boolean,
        val permissions: List<String> = emptyList(),
        val dependencies: List<String> = emptyList()
    )

    enum class PluginState {
        NOT_INSTALLED,
        INSTALLED,
        ENABLED,
        DISABLED,
        ERROR
    }
}
