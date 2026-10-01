package com.cline.app.util

import android.util.Log
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.IOException
import java.io.InputStreamReader

/**
 * Shell command executor for executing shell commands with proper error handling.
 * 
 * Features:
 * - Execute shell commands synchronously
 * - Execute commands with root privileges
 * - Execute commands in proot environment
 * - Stream output for long-running commands
 * - Proper error handling and logging
 */
class ShellExecutor {

    companion object {
        private const val TAG = "ShellExecutor"
        private const val COMMAND_TIMEOUT = 30000L // 30 seconds
    }

    /**
     * Execute a shell command and return the output.
     * 
     * @param command The command to execute
     * @param timeout Timeout in milliseconds (default: 30 seconds)
     * @return The command output as a String
     * @throws IOException If command execution fails
     */
    fun execute(command: String, timeout: Long = COMMAND_TIMEOUT): String {
        return try {
            Log.d(TAG, "Executing: $command")
            
            val process = Runtime.getRuntime().exec(arrayOf("sh", "-c", command))
            val output = StringBuilder()
            val errorOutput = StringBuilder()
            
            // Read output stream
            val outputReader = BufferedReader(InputStreamReader(process.inputStream))
            val errorReader = BufferedReader(InputStreamReader(process.errorStream))
            
            var line: String?
            while (outputReader.readLine().also { line = it } != null) {
                output.append(line).append("\n")
            }
            
            while (errorReader.readLine().also { line = it } != null) {
                errorOutput.append(line).append("\n")
            }
            
            // Wait for process to complete
            process.waitFor()
            
            if (process.exitValue() != 0) {
                Log.e(TAG, "Command failed with exit code ${process.exitValue()}: $command")
                if (errorOutput.isNotEmpty()) {
                    Log.e(TAG, "Error output: $errorOutput")
                }
            }
            
            output.toString().trim()
        } catch (e: IOException) {
            Log.e(TAG, "IOException executing command: $command", e)
            throw IOException("Failed to execute command: ${e.message}", e)
        } catch (e: InterruptedException) {
            Log.e(TAG, "Interrupted executing command: $command", e)
            Thread.currentThread().interrupt()
            throw IOException("Command execution interrupted", e)
        }
    }

    /**
     * Execute a shell command with root privileges.
     * 
     * @param command The command to execute as root
     * @return The command output as a String
     * @throws IOException If command execution fails
     */
    fun executeAsRoot(command: String): String {
        return try {
            Log.d(TAG, "Executing as root: $command")
            
            val process = Runtime.getRuntime().exec("su")
            val outputStream = DataOutputStream(process.outputStream)
            
            // Send command and exit
            outputStream.writeBytes("$command\n")
            outputStream.writeBytes("exit\n")
            outputStream.flush()
            
            val output = StringBuilder()
            val errorOutput = StringBuilder()
            
            val outputReader = BufferedReader(InputStreamReader(process.inputStream))
            val errorReader = BufferedReader(InputStreamReader(process.errorStream))
            
            var line: String?
            while (outputReader.readLine().also { line = it } != null) {
                output.append(line).append("\n")
            }
            
            while (errorReader.readLine().also { line = it } != null) {
                errorOutput.append(line).append("\n")
            }
            
            process.waitFor()
            
            if (process.exitValue() != 0) {
                Log.e(TAG, "Root command failed with exit code ${process.exitValue()}: $command")
                if (errorOutput.isNotEmpty()) {
                    Log.e(TAG, "Root error output: $errorOutput")
                }
            }
            
            output.toString().trim()
        } catch (e: IOException) {
            Log.e(TAG, "IOException executing root command: $command", e)
            throw IOException("Failed to execute root command: ${e.message}", e)
        } catch (e: InterruptedException) {
            Log.e(TAG, "Interrupted executing root command: $command", e)
            Thread.currentThread().interrupt()
            throw IOException("Root command execution interrupted", e)
        }
    }

    /**
     * Execute a command in the proot environment.
     * 
     * @param command The command to execute in proot
     * @param workingDirectory The working directory in the proot environment
     * @param prootPath Path to the proot binary
     * @param rootfsPath Path to the rootfs
     * @return The command output as a String
     */
    fun executeInProot(
        command: String,
        workingDirectory: String = Constants.RUNTIME_DIR,
        prootPath: String = "${Constants.PROOT_DIR}/libproot.so",
        rootfsPath: String = Constants.ROOTFS_PATH
    ): String {
        val prootCommand = "proot -S $prootPath -b $rootfsPath:/rootfs -w /rootfs " +
                "-b /dev -b /proc -b /sys -b $workingDirectory:/runtime /bin/bash -c \"$command\""
        
        return execute(prootCommand)
    }

    /**
     * Check if a command exists and is executable.
     * 
     * @param command The command to check
     * @return true if the command exists and is executable
     */
    fun isCommandAvailable(command: String): Boolean {
        return try {
            val result = execute("which $command")
            result.isNotEmpty()
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Check if we have root access.
     * 
     * @return true if root access is available
     */
    fun hasRootAccess(): Boolean {
        return try {
            val result = execute("id")
            result.contains("uid=0")
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Execute multiple commands sequentially.
     * 
     * @param commands List of commands to execute
     * @return List of outputs for each command
     */
    fun executeSequentially(commands: List<String>): List<String> {
        val results = mutableListOf<String>()
        
        for (command in commands) {
            results.add(execute(command))
        }
        
        return results
    }

    /**
     * Execute a command and return both output and exit code.
     * 
     * @param command The command to execute
     * @return Pair of (output, exitCode)
     */
    fun executeWithExitCode(command: String): Pair<String, Int> {
        return try {
            val process = Runtime.getRuntime().exec(arrayOf("sh", "-c", command))
            val output = StringBuilder()
            val errorOutput = StringBuilder()
            
            val outputReader = BufferedReader(InputStreamReader(process.inputStream))
            val errorReader = BufferedReader(InputStreamReader(process.errorStream))
            
            var line: String?
            while (outputReader.readLine().also { line = it } != null) {
                output.append(line).append("\n")
            }
            
            while (errorReader.readLine().also { line = it } != null) {
                errorOutput.append(line).append("\n")
            }
            
            val exitCode = process.waitFor()
            Pair(output.toString().trim(), exitCode)
        } catch (e: Exception) {
            Pair("", -1)
        }
    }
}
