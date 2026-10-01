package com.cline.app.ui.viewmodels

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Handler
import android.os.Looper
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class VoiceInputViewModel : ViewModel() {

    enum class RecordingState {
        IDLE,
        RECORDING,
        PAUSED,
        STOPPED
    }

    // State
    var recordingState by mutableStateOf(RecordingState.IDLE)
        private set

    var transcript by mutableStateOf("")
        private set

    var recordingDuration by mutableStateOf(0L)
        private set

    var error by mutableStateOf<String?>(null)
        private set

    var hasPermission by mutableStateOf(false)
        private set

    // Audio recording
    private var audioRecord: AudioRecord? = null
    private var recordingThread: Thread? = null
    private var isRecording by mutableStateOf(false)
    private var audioBuffer: ByteArrayOutputStream? = null
    private var tempAudioFile: File? = null
    
    private val handler = Handler(Looper.getMainLooper())
    private val timerRunnable = object : Runnable {
        override fun run() {
            if (recordingState == RecordingState.RECORDING) {
                recordingDuration += 1000 // Increment by 1 second
                handler.postDelayed(this, 1000)
            }
        }
    }

    // Sample rate and buffer settings
    companion object {
        private const val SAMPLE_RATE = 44100
        private const val CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO
        private const val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT
        private const val BUFFER_SIZE = AudioRecord.getMinBufferSize(
            SAMPLE_RATE,
            CHANNEL_CONFIG,
            AUDIO_FORMAT
        )
    }

    init {
        createTempFile()
    }

    fun onPermissionGranted() {
        hasPermission = true
    }

    fun onPermissionDenied() {
        hasPermission = false
        error = "Microphone permission is required for voice input"
    }

    fun startRecording() {
        if (!hasPermission) {
            error = "Microphone permission not granted"
            return
        }

        if (recordingState != RecordingState.IDLE) {
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Create audio record
                audioRecord = AudioRecord(
                    MediaRecorder.AudioSource.MIC,
                    SAMPLE_RATE,
                    CHANNEL_CONFIG,
                    AUDIO_FORMAT,
                    BUFFER_SIZE * 2
                )

                audioBuffer = ByteArrayOutputStream()
                
                audioRecord?.startRecording()
                isRecording = true
                recordingState = RecordingState.RECORDING
                recordingDuration = 0
                
                // Start timer
                handler.post(timerRunnable)

                // Start recording thread
                recordingThread = Thread {
                    val buffer = ByteArray(BUFFER_SIZE)
                    var bytesRead: Int
                    
                    while (isRecording && isActive) {
                        bytesRead = audioRecord?.read(buffer, 0, BUFFER_SIZE) ?: 0
                        if (bytesRead > 0) {
                            audioBuffer?.write(buffer, 0, bytesRead)
                        }
                    }
                }.apply { start() }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    error = "Failed to start recording: ${e.message}"
                    stopRecording()
                }
            }
        }
    }

    fun pauseRecording() {
        if (recordingState != RecordingState.RECORDING) {
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            audioRecord?.stop()
            isRecording = false
            recordingState = RecordingState.PAUSED
            handler.removeCallbacks(timerRunnable)
        }
    }

    fun resumeRecording() {
        if (recordingState != RecordingState.PAUSED) {
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                audioRecord?.startRecording()
                isRecording = true
                recordingState = RecordingState.RECORDING
                handler.post(timerRunnable)
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    error = "Failed to resume recording: ${e.message}"
                }
            }
        }
    }

    fun stopRecording() {
        if (recordingState == RecordingState.IDLE) {
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                isRecording = false
                handler.removeCallbacks(timerRunnable)
                
                audioRecord?.stop()
                audioRecord?.release()
                audioRecord = null
                
                // Wait for recording thread to finish
                recordingThread?.join(500)
                recordingThread = null
                
                // Save audio to temp file
                audioBuffer?.let { buffer ->
                    tempAudioFile?.outputStream()?.use { output ->
                        output.write(buffer.toByteArray())
                    }
                }
                
                recordingState = RecordingState.STOPPED
                
                // Process the audio (transcribe)
                processAudio()
                
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    error = "Failed to stop recording: ${e.message}"
                }
            } finally {
                audioBuffer?.close()
                audioBuffer = null
            }
        }
    }

    fun reset() {
        stopRecording()
        transcript = ""
        recordingDuration = 0
        recordingState = RecordingState.IDLE
        error = null
    }

    fun clearError() {
        error = null
    }

    fun formatDuration(millis: Long): String {
        val seconds = (millis / 1000) % 60
        val minutes = (millis / (1000 * 60)) % 60
        val hours = (millis / (1000 * 60 * 60)) % 24
        
        return when {
            hours > 0 -> String.format("%02d:%02d:%02d", hours, minutes, seconds)
            minutes > 0 -> String.format("%02d:%02d", minutes, seconds)
            else -> String.format("%02d", seconds)
        }
    }

    private fun createTempFile() {
        try {
            val dir = File(System.getProperty("java.io.tmpdir") ?: "/tmp")
            tempAudioFile = File.createTempFile("cline_voice_", ".raw", dir)
            tempAudioFile?.deleteOnExit()
        } catch (e: IOException) {
            error = "Failed to create temp file: ${e.message}"
        }
    }

    private suspend fun processAudio() {
        withContext(Dispatchers.IO) {
            try {
                // Simulate transcription (in a real app, this would call an API)
                // For demo purposes, we'll just create a placeholder transcript
                delay(1000) // Simulate processing time
                
                transcript = when {
                    recordingDuration < 2000 -> "Hello, how can I help you?"
                    recordingDuration < 5000 -> "Please tell me about the project"
                    else -> "This is a voice input test. The app successfully recorded ${formatDuration(recordingDuration)} of audio."
                }
                
                recordingState = RecordingState.IDLE
                
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    error = "Failed to process audio: ${e.message}"
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        stopRecording()
        handler.removeCallbacks(timerRunnable)
        tempAudioFile?.delete()
    }
}
