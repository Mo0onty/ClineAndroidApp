package com.cline.app.bridge

import android.content.Context
import android.util.Log
import com.cline.app.util.Constants
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.IOException
import java.io.InputStreamReader

/**
 * ADB bridge for executing shell commands and managing ADB connections.
 * Provides shell command execution capabilities for the Cline runtime.
 */
class AdbBridge(private val context: Context) {

    companion object {
        private const val TAG = "AdbBridge"
    }

    private var isAdbConnected = false

    /**
     * Check if ADB is connected and available.
     */
    fun isAdbAvailable(): Boolean = isAdbConnected

    /**
     * Initialize ADB connection.
     */
    fun initializeAdb() {
        try {
            // Check if ADB is available
            val result = executeShellCommand("which adb")
            isAdbConnected = result.isNotEmpty()
            
            if (isAdbConnected) {
                Log.d(TAG, "ADB is available")
            } else {
                Log.w(TAG, "ADB is not available")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing ADB", e)
            isAdbConnected = false
        }
    }

    /**
     * Execute a shell command.
     */
    fun executeShellCommand(command: String): String {
        return try {
            val process = Runtime.getRuntime().exec(arrayOf("sh", "-c", command))
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            val errorReader = BufferedReader(InputStreamReader(process.errorStream))
            
            val output = StringBuilder()
            val errorOutput = StringBuilder()
            
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                output.append(line).append("\n")
            }
            
            while (errorReader.readLine().also { line = it } != null) {
                errorOutput.append(line).append("\n")
            }
            
            process.waitFor()
            
            if (errorOutput.isNotEmpty()) {
                Log.e(TAG, "Command error: $command\n$errorOutput")
            }
            
            output.toString().trim()
        } catch (e: IOException) {
            Log.e(TAG, "Error executing command: $command", e)
            ""
        } catch (e: InterruptedException) {
            Log.e(TAG, "Command interrupted: $command", e)
            ""
        }
    }

    /**
     * Execute a shell command with root privileges.
     */
    fun executeRootCommand(command: String): String {
        return try {
            val process = Runtime.getRuntime().exec("su")
            val outputStream = DataOutputStream(process.outputStream)
            
            outputStream.writeBytes("$command\n")
            outputStream.writeBytes("exit\n")
            outputStream.flush()
            
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            val errorReader = BufferedReader(InputStreamReader(process.errorStream))
            
            val output = StringBuilder()
            val errorOutput = StringBuilder()
            
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                output.append(line).append("\n")
            }
            
            while (errorReader.readLine().also { line = it } != null) {
                errorOutput.append(line).append("\n")
            }
            
            process.waitFor()
            
            if (errorOutput.isNotEmpty()) {
                Log.e(TAG, "Root command error: $command\n$errorOutput")
            }
            
            output.toString().trim()
        } catch (e: IOException) {
            Log.e(TAG, "Error executing root command: $command", e)
            ""
        } catch (e: InterruptedException) {
            Log.e(TAG, "Root command interrupted: $command", e)
            ""
        }
    }

    /**
     * Check if the current process has root access.
     */
    fun hasRootAccess(): Boolean {
        return executeShellCommand("id").contains("uid=0")
    }

    /**
     * Execute a command in the proot environment.
     */
    fun executeProotCommand(command: String, workingDirectory: String = Constants.RUNTIME_DIR): String {
        val prootCommand = "proot -S ${Constants.PROOT_DIR}/libproot.so -b ${Constants.RUNTIME_DIR}:/rootfs -w /rootfs -b /dev -b /proc -b /sys $command"
        return executeShellCommand(prootCommand)
    }

    /**
     * Start the proot environment.
     */
    fun startProotEnvironment(): Boolean {
        return try {
            val result = executeShellCommand(
                "proot -S ${Constants.PROOT_DIR}/libproot.so -b ${Constants.RUNTIME_DIR}:/rootfs -w /rootfs -b /dev -b /proc -b /sys /bin/bash"
            )
            result.isNotEmpty()
        } catch (e: Exception) {
            Log.e(TAG, "Error starting proot environment", e)
            false
        }
    }

    /**
     * Check if proot is available.
     */
    fun isProotAvailable(): Boolean {
        return executeShellCommand("which proot").isNotEmpty()
    }

    /**
     * Check if proot loader is available.
     */
    fun isProotLoaderAvailable(): Boolean {
        return executeShellCommand("ls ${Constants.PROOT_DIR}/libprootloader.so").isNotEmpty()
    }

    /**
     * Get ADB version.
     */
    fun getAdbVersion(): String {
        return executeShellCommand("adb version")
    }

    /**
     * Get device information via ADB.
     */
    fun getDeviceInfo(): Map<String, String> {
        val info = mutableMapOf<String, String>()
        
        try {
            val serial = executeShellCommand("adb get-serialno")
            if (serial.isNotEmpty()) {
                info["serial"] = serial
            }
            
            val model = executeShellCommand("adb shell getprop ro.product.model")
            if (model.isNotEmpty()) {
                info["model"] = model
            }
            
            val manufacturer = executeShellCommand("adb shell getprop ro.product.manufacturer")
            if (manufacturer.isNotEmpty()) {
                info["manufacturer"] = manufacturer
            }
            
            val version = executeShellCommand("adb shell getprop ro.build.version.release")
            if (version.isNotEmpty()) {
                info["android_version"] = version
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting device info via ADB", e)
        }
        
        return info
    }

    /**
     * Execute a command via ADB shell.
     */
    fun executeAdbShellCommand(command: String): String {
        return executeShellCommand("adb shell $command")
    }

    /**
     * Clean up resources.
     */
    fun cleanup() {
        isAdbConnected = false
    }
}
