// TerminalProcess.kt - Terminal Process Management
package com.cline.app.runtime

import android.util.Log
import java.io.BufferedReader
import java.io.File
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream

/**
 * Terminal Process - Manages terminal processes with PTY support
 * 
 * This class provides:
 * - Terminal process execution
 * - Input/output streaming
 * - Process lifecycle management
 */
class TerminalProcess {
    
    companion object {
        private const val TAG = "TerminalProcess"
    }
    
    // Process
    private var process: Process? = null
    private var inputStream: InputStream? = null
    private var outputStream: OutputStream? = null
    private var errorStream: InputStream? = null
    
    // Callbacks
    private var onOutput: ((String) -> Unit)? = null
    private var onError: ((String) -> Unit)? = null
    private var onExit: ((Int) -> Unit)? = null
    
    // Threads
    private var outputThread: Thread? = null
    private var errorThread: Thread? = null
    
    // State
    private var isRunning = false
    private var exitCode: Int? = null
    
    // ========================================================================
    // PROCESS LIFECYCLE
    // ========================================================================
    
    /**
     * Start a terminal process
     */
    fun start(command: String, workingDir: File? = null): Boolean {
        if (isRunning) {
            Log.w(TAG, "Process already running")
            return false
        }
        
        try {
            val processBuilder = ProcessBuilder("/system/bin/sh", "-c", command)
            
            if (workingDir != null) {
                processBuilder.directory(workingDir)
            }
            
            // Set up environment
            val env = processBuilder.environment()
            env["TERM"] = "xterm-256color"
            env["LANG"] = "en_US.UTF-8"
            env["LC_ALL"] = "en_US.UTF-8"
            
            process = processBuilder.start()
            inputStream = process?.inputStream
            outputStream = process?.outputStream
            errorStream = process?.errorStream
            
            isRunning = true
            exitCode = null
            
            // Start output reader
            startOutputReader()
            
            // Start error reader
            startErrorReader()
            
            // Start exit monitor
            startExitMonitor()
            
            Log.i(TAG, "Process started: $command")
            return true
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start process: ${e.message}")
            cleanup()
            return false
        }
    }
    
    /**
     * Stop the terminal process
     */
    fun stop() {
        if (!isRunning) {
            return
        }
        
        try {
            process?.destroy()
            isRunning = false
            
            // Wait for threads to finish
            outputThread?.join(1000)
            errorThread?.join(1000)
            
            cleanup()
            Log.i(TAG, "Process stopped")
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to stop process: ${e.message}")
        }
    }
    
    /**
     * Force stop the terminal process
     */
    fun forceStop() {
        if (!isRunning) {
            return
        }
        
        try {
            process?.destroyForcibly()
            isRunning = false
            
            // Wait for threads to finish
            outputThread?.join(1000)
            errorThread?.join(1000)
            
            cleanup()
            Log.i(TAG, "Process force stopped")
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to force stop process: ${e.message}")
        }
    }
    
    // ========================================================================
    // OUTPUT HANDLING
    // ========================================================================
    
    /**
     * Start output reader thread
     */
    private fun startOutputReader() {
        outputThread = Thread {
            try {
                val reader = BufferedReader(InputStreamReader(inputStream))
                var line: String?
                
                while (isRunning) {
                    line = reader.readLine()
                    if (line == null) {
                        break
                    }
                    
                    onOutput?.invoke(line)
                }
                
            } catch (e: Exception) {
                Log.e(TAG, "Error reading output: ${e.message}")
            }
        }
        
        outputThread?.start()
    }
    
    /**
     * Start error reader thread
     */
    private fun startErrorReader() {
        errorThread = Thread {
            try {
                val reader = BufferedReader(InputStreamReader(errorStream))
                var line: String?
                
                while (isRunning) {
                    line = reader.readLine()
                    if (line == null) {
                        break
                    }
                    
                    onError?.invoke(line)
                }
                
            } catch (e: Exception) {
                Log.e(TAG, "Error reading error: ${e.message}")
            }
        }
        
        errorThread?.start()
    }
    
    // ========================================================================
    // EXIT MONITORING
    // ========================================================================
    
    /**
     * Start exit monitor thread
     */
    private fun startExitMonitor() {
        Thread {
            try {
                val exitCode = process?.waitFor()
                this.exitCode = exitCode
                isRunning = false
                
                onExit?.invoke(exitCode ?: -1)
                
            } catch (e: Exception) {
                Log.e(TAG, "Error monitoring exit: ${e.message}")
            }
        }.start()
    }
    
    // ========================================================================
    // INPUT HANDLING
    // ========================================================================
    
    /**
     * Write input to the process
     */
    fun writeInput(input: String): Boolean {
        if (!isRunning || outputStream == null) {
            return false
        }
        
        try {
            outputStream?.write((input + "\n").toByteArray(Charsets.UTF_8))
            outputStream?.flush()
            return true
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to write input: ${e.message}")
            return false
        }
    }
    
    /**
     * Write raw bytes to the process
     */
    fun writeBytes(bytes: ByteArray): Boolean {
        if (!isRunning || outputStream == null) {
            return false
        }
        
        try {
            outputStream?.write(bytes)
            outputStream?.flush()
            return true
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to write bytes: ${e.message}")
            return false
        }
    }
    
    // ========================================================================
    // CALLBACKS
    // ========================================================================
    
    /**
     * Set output callback
     */
    fun setOnOutput(callback: (String) -> Unit) {
        onOutput = callback
    }
    
    /**
     * Set error callback
     */
    fun setOnError(callback: (String) -> Unit) {
        onError = callback
    }
    
    /**
     * Set exit callback
     */
    fun setOnExit(callback: (Int) -> Unit) {
        onExit = callback
    }
    
    // ========================================================================
    // STATE
    // ========================================================================
    
    /**
     * Check if process is running
     */
    fun isRunning(): Boolean {
        return isRunning
    }
    
    /**
     * Get exit code
     */
    fun getExitCode(): Int? {
        return exitCode
    }
    
    /**
     * Get process
     */
    fun getProcess(): Process? {
        return process
    }
    
    // ========================================================================
    // CLEANUP
    // ========================================================================
    
    /**
     * Clean up resources
     */
    private fun cleanup() {
        try {
            inputStream?.close()
            outputStream?.close()
            errorStream?.close()
            
            inputStream = null
            outputStream = null
            errorStream = null
            
        } catch (e: Exception) {
            Log.e(TAG, "Error cleaning up: ${e.message}")
        }
    }
    
    // ========================================================================
    // UTILITY
    // ========================================================================
    
    /**
     * Execute a command and get output
     */
    fun executeAndGetOutput(command: String, timeout: Long = 5000): String {
        var output = ""
        val lock = Any()
        
        setOnOutput { line ->
            synchronized(lock) {
                output += line + "\n"
            }
        }
        
        start(command)
        
        // Wait for command to complete or timeout
        val startTime = System.currentTimeMillis()
        while (isRunning && System.currentTimeMillis() - startTime < timeout) {
            Thread.sleep(100)
        }
        
        if (isRunning) {
            stop()
        }
        
        return output
    }
}
