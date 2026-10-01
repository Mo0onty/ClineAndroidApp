package com.cline.app.recovery

import android.content.Context
import android.util.Log
import com.cline.app.core.ContainerRuntime
import com.cline.app.core.ProotBootstrap
import com.cline.app.core.RuntimeHostPorts
import com.cline.app.data.BackupManager
import com.cline.app.util.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Controller for recovery operations.
 * Manages recovery mode, safe runtime initialization, and backup/restore operations.
 */
class RecoveryController(
    private val context: Context,
    private val runtimeHostPorts: RuntimeHostPorts,
    private val prootBootstrap: ProotBootstrap,
    private val containerRuntime: ContainerRuntime,
    private val backupManager: BackupManager
) {

    companion object {
        private const val TAG = "RecoveryController"
    }

    private val recoveryScope = CoroutineScope(Dispatchers.IO)
    private var isInRecoveryMode = false
    private var recoveryRuntime: RecoveryRuntime? = null

    /**
     * Initialize recovery controller.
     */
    fun initialize() {
        Log.d(TAG, "Initializing RecoveryController")
        recoveryRuntime = RecoveryRuntime(context, runtimeHostPorts, prootBootstrap)
    }

    /**
     * Enter recovery mode.
     */
    suspend fun enterRecoveryMode(): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Entering recovery mode")
                
                // Stop normal runtime
                containerRuntime.stop()
                
                // Initialize recovery runtime
                recoveryRuntime?.initialize()
                
                isInRecoveryMode = true
                true
            } catch (e: Exception) {
                Log.e(TAG, "Error entering recovery mode", e)
                false
            }
        }
    }

    /**
     * Exit recovery mode.
     */
    suspend fun exitRecoveryMode(): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Exiting recovery mode")
                
                // Stop recovery runtime
                recoveryRuntime?.stop()
                
                // Restart normal runtime
                containerRuntime.start()
                
                isInRecoveryMode = false
                true
            } catch (e: Exception) {
                Log.e(TAG, "Error exiting recovery mode", e)
                false
            }
        }
    }

    /**
     * Check if in recovery mode.
     */
    fun isInRecoveryMode(): Boolean = isInRecoveryMode

    /**
     * Perform recovery backup.
     */
    suspend fun performRecoveryBackup(backupPath: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Performing recovery backup to: $backupPath")
                
                // Create backup using backup manager
                backupManager.createBackup(backupPath)
                true
            } catch (e: Exception) {
                Log.e(TAG, "Error performing recovery backup", e)
                false
            }
        }
    }

    /**
     * Perform recovery restore.
     */
    suspend fun performRecoveryRestore(backupPath: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Performing recovery restore from: $backupPath")
                
                // Stop runtime
                recoveryRuntime?.stop()
                containerRuntime.stop()
                
                // Restore from backup
                backupManager.restoreBackup(backupPath)
                
                // Restart runtime
                recoveryRuntime?.initialize()
                
                true
            } catch (e: Exception) {
                Log.e(TAG, "Error performing recovery restore", e)
                false
            }
        }
    }

    /**
     * Verify runtime integrity.
     */
    suspend fun verifyRuntimeIntegrity(): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Verifying runtime integrity")
                
                // Check if runtime files exist
                val runtimeFiles = listOf(
                    Constants.RUNTIME_DIR,
                    Constants.PROOT_DIR,
                    Constants.ROOTFS_PATH,
                    Constants.CLINE_RUNTIME_PATH
                )
                
                for (filePath in runtimeFiles) {
                    if (!java.io.File(filePath).exists()) {
                        Log.e(TAG, "Runtime file missing: $filePath")
                        return@withContext false
                    }
                }
                
                // Check proot binary
                if (!prootBootstrap.isProotAvailable()) {
                    Log.e(TAG, "Proot binary not available")
                    return@withContext false
                }
                
                true
            } catch (e: Exception) {
                Log.e(TAG, "Error verifying runtime integrity", e)
                false
            }
        }
    }

    /**
     * Repair runtime environment.
     */
    suspend fun repairRuntime(): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Repairing runtime environment")
                
                // Extract proot if needed
                if (!prootBootstrap.isProotAvailable()) {
                    prootBootstrap.extractProot()
                }
                
                // Extract rootfs if needed
                if (!java.io.File(Constants.ROOTFS_PATH).exists()) {
                    containerRuntime.extractRootfs()
                }
                
                // Extract runtime if needed
                if (!java.io.File(Constants.CLINE_RUNTIME_PATH).exists()) {
                    containerRuntime.extractRuntime()
                }
                
                // Verify again
                verifyRuntimeIntegrity()
            } catch (e: Exception) {
                Log.e(TAG, "Error repairing runtime", e)
                false
            }
        }
    }

    /**
     * Get recovery status.
     */
    fun getRecoveryStatus(): RecoveryStatus {
        return RecoveryStatus(
            isInRecoveryMode = isInRecoveryMode,
            runtimeStatus = if (isInRecoveryMode) {
                recoveryRuntime?.getStatus() ?: RecoveryRuntime.Status.STOPPED
            } else {
                containerRuntime.getStatus()
            },
            lastError = null
        )
    }

    /**
     * Start recovery service.
     */
    fun startRecoveryService() {
        recoveryScope.launch {
            try {
                Log.d(TAG, "Starting recovery service")
                RecoveryService.start(context)
            } catch (e: Exception) {
                Log.e(TAG, "Error starting recovery service", e)
            }
        }
    }

    /**
     * Stop recovery service.
     */
    fun stopRecoveryService() {
        recoveryScope.launch {
            try {
                Log.d(TAG, "Stopping recovery service")
                RecoveryService.stop(context)
            } catch (e: Exception) {
                Log.e(TAG, "Error stopping recovery service", e)
            }
        }
    }

    /**
     * Clean up resources.
     */
    fun cleanup() {
        Log.d(TAG, "Cleaning up RecoveryController")
        recoveryScope.launch {
            recoveryRuntime?.cleanup()
            recoveryRuntime = null
        }
    }

    data class RecoveryStatus(
        val isInRecoveryMode: Boolean,
        val runtimeStatus: RecoveryRuntime.Status,
        val lastError: String?
    )
}
