package com.cline.app.recovery

import android.content.Context
import android.util.Log
import com.cline.app.core.ProotBootstrap
import com.cline.app.core.RuntimeHostPorts
import com.cline.app.util.Constants
import com.cline.app.util.ShellExecutor
import java.io.File

/**
 * Recovery runtime environment.
 * Provides a minimal, safe runtime for recovery operations.
 */
class RecoveryRuntime(
    private val context: Context,
    private val runtimeHostPorts: RuntimeHostPorts,
    private val prootBootstrap: ProotBootstrap
) {

    companion object {
        private const val TAG = "RecoveryRuntime"
    }

    private var process: Process? = null
    private var isRunning = false
    private var currentStatus = Status.STOPPED

    /**
     * Initialize the recovery runtime.
     */
    fun initialize() {
        Log.d(TAG, "Initializing RecoveryRuntime")
        
        // Ensure proot is available
        if (!prootBootstrap.isProotAvailable()) {
            prootBootstrap.extractProot()
        }
        
        // Ensure rootfs is available
        ensureRootfsAvailable()
        
        currentStatus = Status.READY
    }

    /**
     * Ensure rootfs is available for recovery.
     */
    private fun ensureRootfsAvailable() {
        val rootfsFile = File(Constants.ROOTFS_PATH)
        if (!rootfsFile.exists()) {
            Log.w(TAG, "Rootfs not found, recovery may be limited")
        }
    }

    /**
     * Start the recovery runtime.
     */
    fun start(): Boolean {
        return try {
            Log.d(TAG, "Starting recovery runtime")
            
            // Build proot command
            val prootPath = File(Constants.PROOT_DIR, "libproot.so").absolutePath
            val rootfsPath = Constants.ROOTFS_PATH
            val runtimeDir = Constants.RUNTIME_DIR
            
            val command = arrayOf(
                "proot",
                "-S", prootPath,
                "-b", "$rootfsPath:/rootfs",
                "-w", "/rootfs",
                "-b", "/dev",
                "-b", "/proc",
                "-b", "/sys",
                "-b", "$runtimeDir:/runtime",
                "/bin/bash"
            )
            
            // Execute with environment
            val builder = ProcessBuilder(*command)
                .directory(File(runtimeDir))
                .redirectErrorStream(true)
            
            // Set environment
            val env = HashMap<String, String>()
            env["PATH"] = "/bin:/sbin:/usr/bin:/usr/sbin:/runtime/bin"
            env["HOME"] = "/root"
            env["USER"] = "root"
            env["TERM"] = "xterm-256color"
            env["LANG"] = "en_US.UTF-8"
            env["LC_ALL"] = "en_US.UTF-8"
            
            builder.environment().putAll(env)
            
            process = builder.start()
            isRunning = true
            currentStatus = Status.RUNNING
            
            Log.d(TAG, "Recovery runtime started successfully")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error starting recovery runtime", e)
            currentStatus = Status.ERROR
            false
        }
    }

    /**
     * Stop the recovery runtime.
     */
    fun stop(): Boolean {
        return try {
            Log.d(TAG, "Stopping recovery runtime")
            
            process?.destroy()
            process?.waitFor()
            process = null
            isRunning = false
            currentStatus = Status.STOPPED
            
            Log.d(TAG, "Recovery runtime stopped")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping recovery runtime", e)
            false
        }
    }

    /**
     * Execute a command in the recovery runtime.
     */
    fun executeCommand(command: String): String {
        if (!isRunning) {
            start()
        }
        
        return try {
            val executor = ShellExecutor()
            val prootPath = File(Constants.PROOT_DIR, "libproot.so").absolutePath
            val rootfsPath = Constants.ROOTFS_PATH
            val runtimeDir = Constants.RUNTIME_DIR
            
            val fullCommand = "proot -S $prootPath -b $rootfsPath:/rootfs -w /rootfs -b /dev -b /proc -b /sys -b $runtimeDir:/runtime /bin/bash -c \"$command\""
            
            executor.execute(fullCommand)
        } catch (e: Exception) {
            Log.e(TAG, "Error executing command in recovery runtime", e)
            ""
        }
    }

    /**
     * Get the current status.
     */
    fun getStatus(): Status = currentStatus

    /**
     * Check if runtime is running.
     */
    fun isRunning(): Boolean = isRunning

    /**
     * Get process ID if available.
     */
    fun getProcessId(): Int? = process?.pid()

    /**
     * Clean up resources.
     */
    fun cleanup() {
        Log.d(TAG, "Cleaning up RecoveryRuntime")
        stop()
    }

    /**
     * Get runtime configuration.
     */
    fun getConfiguration(): RecoveryConfiguration {
        return RecoveryConfiguration(
            prootPath = File(Constants.PROOT_DIR, "libproot.so").absolutePath,
            rootfsPath = Constants.ROOTFS_PATH,
            runtimeDir = Constants.RUNTIME_DIR,
            hostPorts = runtimeHostPorts.getPorts(),
            isSafeMode = true,
            hasNetworkAccess = false
        )
    }

    enum class Status {
        STOPPED, READY, RUNNING, ERROR
    }

    data class RecoveryConfiguration(
        val prootPath: String,
        val rootfsPath: String,
        val runtimeDir: String,
        val hostPorts: RuntimeHostPorts.Ports,
        val isSafeMode: Boolean,
        val hasNetworkAccess: Boolean
    )
}
