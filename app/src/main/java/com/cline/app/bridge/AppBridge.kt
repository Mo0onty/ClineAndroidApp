package com.cline.app.bridge

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.annotation.RequiresPermission

/**
 * App-level bridge for communication between Cline runtime and Android app.
 * Handles intents, file sharing, and app-level operations.
 */
class AppBridge(private val context: Context) {

    private val localNetworkAccess by lazy { LocalNetworkAccess(context) }

    /**
     * Initialize the bridge.
     */
    fun initialize() {
        localNetworkAccess.requestLocalNetworkAccess()
    }

    /**
     * Clean up resources.
     */
    fun cleanup() {
        localNetworkAccess.releaseLocalNetworkAccess()
    }

    /**
     * Open a URL in the default browser.
     */
    fun openUrl(url: String): Boolean {
        return try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
            true
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Share text content.
     */
    fun shareText(text: String, subject: String = ""): Boolean {
        return try {
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, text)
                if (subject.isNotEmpty()) {
                    putExtra(Intent.EXTRA_SUBJECT, subject)
                }
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(Intent.createChooser(intent, "Share"))
            true
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Share a file.
     */
    fun shareFile(fileUri: Uri, mimeType: String = "application/octet-stream"): Boolean {
        return try {
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = mimeType
                putExtra(Intent.EXTRA_STREAM, fileUri)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(intent, "Share"))
            true
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Check if local network access is available.
     */
    fun hasLocalNetworkAccess(): Boolean {
        return localNetworkAccess.hasLocalNetworkAccess()
    }

    /**
     * Check if we can access local network resources.
     */
    @RequiresPermission(android.Manifest.permission.LOCAL_NETWORK)
    fun canAccessLocalNetwork(): Boolean {
        return localNetworkAccess.canAccessLocalNetwork()
    }

    /**
     * Get the current network type.
     */
    fun getNetworkType(): LocalNetworkAccess.NetworkType {
        return localNetworkAccess.getNetworkType()
    }

    /**
     * Send a broadcast intent.
     */
    fun sendBroadcast(action: String, extras: Map<String, Any> = emptyMap()): Boolean {
        return try {
            val intent = Intent(action).apply {
                extras.forEach { (key, value) ->
                    when (value) {
                        is String -> putExtra(key, value)
                        is Int -> putExtra(key, value)
                        is Boolean -> putExtra(key, value)
                        is Float -> putExtra(key, value)
                        is Double -> putExtra(key, value)
                        is Long -> putExtra(key, value)
                        else -> putExtra(key, value.toString())
                    }
                }
            }
            context.sendBroadcast(intent)
            true
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Start a new activity.
     */
    fun startActivity(className: String, extras: Map<String, Any> = emptyMap()): Boolean {
        return try {
            val intent = Intent(context, Class.forName(className)).apply {
                extras.forEach { (key, value) ->
                    when (value) {
                        is String -> putExtra(key, value)
                        is Int -> putExtra(key, value)
                        is Boolean -> putExtra(key, value)
                        is Float -> putExtra(key, value)
                        is Double -> putExtra(key, value)
                        is Long -> putExtra(key, value)
                        else -> putExtra(key, value.toString())
                    }
                }
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            true
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Get app version information.
     */
    fun getAppVersion(): String {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            packageInfo.versionName ?: "1.0.0"
        } catch (e: Exception) {
            "1.0.0"
        }
    }

    /**
     * Get app version code.
     */
    fun getAppVersionCode(): Int {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            packageInfo.versionCode
        } catch (e: Exception) {
            1
        }
    }
}
