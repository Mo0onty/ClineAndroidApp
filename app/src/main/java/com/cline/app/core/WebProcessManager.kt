// WebProcessManager.kt - Web Process Lifecycle Management
package com.cline.app.core

import android.content.Context
import android.util.Log
import com.cline.app.util.Constants
import java.io.File

/**
 * Web Process Manager - Manages the Cline Web UI process
 * 
 * This class handles:
 * - Web process lifecycle (start/stop)
 * - Process monitoring
 * - PID file management
 * - Port management
 */
class WebProcessManager(private val context: Context) {
    
    companion object {
        private const val TAG = "WebProcessManager"
        
        // PID file
        private const val PID_FILE = "web.pid"
        private const val LOG_FILE = "web.log"
        
        // Process state
        private const val STATE_RUNNING = "running"
        private const val STATE_STOPPED = "stopped"
        private const val STATE_STARTING = "starting"
        private const val STATE_STOPPING = "stopping"
    }
    
    // State
    private var process: Process? = null
    private var currentPid: Int? = null
    private var currentPort: Int? = null
    private var currentHost: String? = null
    private var state: String = STATE_STOPPED
    private var error: String? = null
    
    // Callbacks
    private var onStartedCallback: (() -> Unit)? = null
    private var onStoppedCallback: (() -> Unit)? = null
    private var onErrorCallback: ((String) -> Unit)? = null
    
    // ========================================================================
    // PROCESS LIFECYCLE
    // ========================================================================
    
    /**
     * Start the web process
     */
    fun startWebProcess(
        host: String,
        port: Int,
        runtimeDir: File,
        onStarted: (() -> Unit)? = null,
        onError: ((String) -> Unit)? = null
    ) {
        if (state != STATE_STOPPED) {
            Log.i(TAG, "Web process is not in stopped state: $state")
            return
        }
        
        state = STATE_STARTING
        error = null
        currentHost = host
        currentPort = port
        onStartedCallback = onStarted
        onErrorCallback = onError
        
        try {
            // Clean up any existing process
            stopWebProcess()
            
            // Build command
            val command = buildWebCommand(host, port, runtimeDir)
            
            // Start process
            val processBuilder = ProcessBuilder(*command.toTypedArray())
                .directory(runtimeDir)
                .redirectErrorStream(true)
            
            process = processBuilder.start()
            currentPid = getProcessPid(process!!)
            
            // Write PID file
            writePidFile(currentPid!!)
            
            // Monitor process
            startProcessMonitor()
            
            state = STATE_RUNNING
            onStartedCallback?.invoke()
            
            Log.i(TAG, "Web process started on $host:$port with PID $currentPid")
            
        } catch (e: Exception) {
            state = STATE_STOPPED
            error = e.message ?: "Unknown error"
            onErrorCallback?.invoke(error!!)
            Log.e(TAG, "Failed to start web process: ${e.message}")
        }
    }
    
    /**
     * Stop the web process
     */
    fun stopWebProcess() {
        if (state == STATE_STOPPED) {
            Log.i(TAG, "Web process already stopped")
            return
        }
        
        if (state == STATE_STARTING) {
            // Still starting, wait for it to start first
            state = STATE_STOPPING
            return
        }
        
        state = STATE_STOPPING
        
        try {
            // Kill process
            currentPid?.let { pid ->
                killProcess(pid)
            }
            
            // Clean up process reference
            process?.destroy()
            process = null
            
            // Remove PID file
            removePidFile()
            
            state = STATE_STOPPED
            currentPid = null
            onStoppedCallback?.invoke()
            
            Log.i(TAG, "Web process stopped")
            
        } catch (e: Exception) {
            state = STATE_STOPPED
            error = e.message ?: "Unknown error"
            Log.e(TAG, "Failed to stop web process: ${e.message}")
        }
    }
    
    /**
     * Restart the web process
     */
    fun restartWebProcess() {
        stopWebProcess()
        currentHost?.let { host ->
            currentPort?.let { port ->
                startWebProcess(
                    host = host,
                    port = port,
                    runtimeDir = File(context.filesDir, "runtime"),
                    onStarted = onStartedCallback,
                    onError = onErrorCallback
                )
            }
        }
    }
    
    // ========================================================================
    // COMMAND BUILDING
    // ========================================================================
    
    /**
     * Build the web process command
     */
    private fun buildWebCommand(host: String, port: Int, runtimeDir: File): List<String> {
        // In a real implementation, this would build the command to start
        // the Cline web server with the specified host and port
        
        // For now, we'll use a placeholder command
        // The actual command would depend on how Cline is packaged
        
        return listOf(
            "sh",
            "-c",
            "cd ${runtimeDir.absolutePath} && " +
            "node cline-runtime/bin/cline.js --host $host --port $port"
        )
    }
    
    // ========================================================================
    // PROCESS MONITORING
    // ========================================================================
    
    /**
     * Start process monitor
     */
    private fun startProcessMonitor() {
        // In a real implementation, we would monitor the process
        // and restart it if it crashes
        
        Thread {
            while (state == STATE_RUNNING) {
                try {
                    Thread.sleep(5000)
                    
                    currentPid?.let { pid ->
                        if (!isProcessAlive(pid)) {
                            Log.w(TAG, "Web process died unexpectedly, restarting...")
                            restartWebProcess()
                            return@Thread
                        }
                    }
                    
                } catch (e: Exception) {
                    Log.e(TAG, "Error monitoring web process: ${e.message}")
                }
            }
        }.start()
    }
    
    /**
     * Check if process is alive
     */
    private fun isProcessAlive(pid: Int): Boolean {
        try {
            // Check if /proc/pid exists
            val procFile = File("/proc/$pid")
            return procFile.exists()
        } catch (e: Exception) {
            return false
        }
    }
    
    // ========================================================================
    // PID MANAGEMENT
    // ========================================================================
    
    /**
     * Get process PID
     */
    private fun getProcessPid(process: Process): Int? {
        try {
            // Try to get PID from the process
            val pidField = process.javaClass.getDeclaredField("pid")
            pidField.isAccessible = true
            return pidField.getInt(process)
        } catch (e: Exception) {
            // Fallback: try to parse from /proc
            try {
                val cmd = "ps -o pid= -p ${process.pid()}"
                val p = Runtime.getRuntime().exec(cmd)
                val output = p.inputStream.bufferedReader().readText()
                return output.trim().toIntOrNull()
            } catch (e2: Exception) {
                return null
            }
        }
    }
    
    /**
     * Write PID file
     */
    private fun writePidFile(pid: Int) {
        try {
            val pidFile = File(context.filesDir, PID_FILE)
            pidFile.writeText(pid.toString())
        } catch (e: Exception) {
            Log.e(TAG, "Failed to write PID file: ${e.message}")
        }
    }
    
    /**
     * Read PID file
     */
    fun readPidFile(): Int? {
        try {
            val pidFile = File(context.filesDir, PID_FILE)
            if (pidFile.exists()) {
                return pidFile.readText().trim().toIntOrNull()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to read PID file: ${e.message}")
        }
        return null
    }
    
    /**
     * Remove PID file
     */
    private fun removePidFile() {
        try {
            val pidFile = File(context.filesDir, PID_FILE)
            if (pidFile.exists()) {
                pidFile.delete()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to remove PID file: ${e.message}")
        }
    }
    
    // ========================================================================
    // PROCESS CONTROL
    // ========================================================================
    
    /**
     * Kill a process by PID
     */
    private fun killProcess(pid: Int) {
        try {
            Runtime.getRuntime().exec("kill $pid")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to kill process $pid: ${e.message}")
        }
    }
    
    /**
     * Force kill a process by PID
     */
    fun forceKillProcess(pid: Int) {
        try {
            Runtime.getRuntime().exec("kill -9 $pid")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to force kill process $pid: ${e.message}")
        }
    }
    
    // ========================================================================
    // STATE
    // ========================================================================
    
    /**
     * Check if web process is running
     */
    fun isRunning(): Boolean {
        return state == STATE_RUNNING
    }
    
    /**
     * Check if web process is stopped
     */
    fun isStopped(): Boolean {
        return state == STATE_STOPPED
    }
    
    /**
     * Get current state
     */
    fun getState(): String {
        return state
    }
    
    /**
     * Get current PID
     */
    fun getPid(): Int? {
        return currentPid
    }
    
    /**
     * Get current port
     */
    fun getPort(): Int? {
        return currentPort
    }
    
    /**
     * Get current host
     */
    fun getHost(): String? {
        return currentHost
    }
    
    /**
     * Get last error
     */
    fun getError(): String? {
        return error
    }
    
    // ========================================================================
    // CALLBACKS
    // ========================================================================
    
    /**
     * Set on started callback
     */
    fun setOnStartedCallback(callback: () -> Unit) {
        onStartedCallback = callback
    }
    
    /**
     * Set on stopped callback
     */
    fun setOnStoppedCallback(callback: () -> Unit) {
        onStoppedCallback = callback
    }
    
    /**
     * Set on error callback
     */
    fun setOnErrorCallback(callback: (String) -> Unit) {
        onErrorCallback = callback
    }
    
    // ========================================================================
    // LOGGING
    // ========================================================================
    
    /**
     * Get web process log
     */
    fun getLog(): String {
        try {
            val logFile = File(context.filesDir, LOG_FILE)
            if (logFile.exists()) {
                return logFile.readText()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to read log file: ${e.message}")
        }
        return ""
    }
    
    /**
     * Clear web process log
     */
    fun clearLog() {
        try {
            val logFile = File(context.filesDir, LOG_FILE)
            if (logFile.exists()) {
                logFile.delete()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to clear log file: ${e.message}")
        }
    }
}
