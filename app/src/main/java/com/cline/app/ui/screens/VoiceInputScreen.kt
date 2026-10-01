package com.cline.app.ui.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cline.app.design.animations.Animations
import com.cline.app.design.components.ClineIconButton
import com.cline.app.design.components.ClinePrimaryButton
import com.cline.app.design.tokens.DesignTokens
import com.cline.app.ui.viewmodels.VoiceInputViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.Timer
import kotlin.math.cos
import kotlin.math.sin

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun VoiceInputScreen(
    viewModel: VoiceInputViewModel = viewModel(),
    onTranscriptReady: (String) -> Unit = {},
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    
    var showPermissionDialog by remember { mutableStateOf(false) }
    var showRecordingUI by remember { mutableStateOf(false) }
    var showResults by remember { mutableStateOf(false) }
    
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.onPermissionGranted()
        } else {
            viewModel.onPermissionDenied()
        }
    }

    // Check and request permission
    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val permission = Manifest.permission.RECORD_AUDIO
            val hasPermission = ContextCompat.checkSelfPermission(
                context,
                permission
            ) == PackageManager.PERMISSION_GRANTED
            
            if (!hasPermission) {
                showPermissionDialog = true
            } else {
                viewModel.onPermissionGranted()
            }
        } else {
            viewModel.onPermissionGranted()
        }
    }

    // Handle recording state changes
    LaunchedEffect(viewModel.recordingState) {
        showRecordingUI = viewModel.recordingState == VoiceInputViewModel.RecordingState.RECORDING
    }

    // Handle transcript
    LaunchedEffect(viewModel.transcript) {
        if (viewModel.transcript.isNotBlank() && !showResults) {
            showResults = true
        }
    }

    // Handle errors
    LaunchedEffect(viewModel.error) {
        viewModel.error?.let { error ->
            scope.launch {
                snackbarHostState.showSnackbar(error)
                viewModel.clearError()
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            VoiceInputTopBar(
                isRecording = viewModel.recordingState == VoiceInputViewModel.RecordingState.RECORDING,
                onBack = onBack,
                onClose = {
                    viewModel.stopRecording()
                    onBack()
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            AnimatedContent(
                targetState = when {
                    showPermissionDialog -> VoiceInputState.PERMISSION
                    showRecordingUI -> VoiceInputState.RECORDING
                    showResults -> VoiceInputState.RESULTS
                    else -> VoiceInputState.IDLE
                },
                transitionSpec = {
                    if (targetState == VoiceInputState.RECORDING) {
                        (fadeIn() + scaleIn()).togetherWith(fadeOut() + scaleOut())
                    } else {
                        (slideInVertically() + fadeIn()).togetherWith(slideOutVertically() + fadeOut())
                    }
                },
                label = "voice_input_state"
            ) { state ->
                when (state) {
                    VoiceInputState.IDLE -> {
                        VoiceInputIdle(
                            onStartRecording = {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                } else {
                                    viewModel.startRecording()
                                }
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    VoiceInputState.PERMISSION -> {
                        PermissionDialog(
                            onRequestPermission = {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                }
                            },
                            onDismiss = { showPermissionDialog = false }
                        )
                    }
                    VoiceInputState.RECORDING -> {
                        VoiceInputRecording(
                            viewModel = viewModel,
                            onStopRecording = { viewModel.stopRecording() },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    VoiceInputState.RESULTS -> {
                        VoiceInputResults(
                            transcript = viewModel.transcript,
                            onSend = {
                                onTranscriptReady(viewModel.transcript)
                                viewModel.reset()
                                showResults = false
                            },
                            onRetry = {
                                viewModel.reset()
                                showResults = false
                            },
                            onCancel = {
                                viewModel.reset()
                                showResults = false
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun VoiceInputTopBar(
    isRecording: Boolean,
    onBack: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(DesignTokens.Spacing.md),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Back"
            )
        }
        
        Text(
            text = if (isRecording) "Recording..." else "Voice Input",
            style = MaterialTheme.typography.titleMedium
        )
        
        Box(modifier = Modifier.width(48.dp))
    }
}

@Composable
fun VoiceInputIdle(
    onStartRecording: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(160.dp)
                .background(
                    color = MaterialTheme.colorScheme.surfaceContainer,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            IconButton(
                onClick = onStartRecording,
                modifier = Modifier
                    .size(80.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = CircleShape
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = "Start Recording",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(40.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(DesignTokens.Spacing.lg))
        
        Text(
            text = "Tap to start recording",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Spacer(modifier = Modifier.height(DesignTokens.Spacing.sm))
        
        Text(
            text = "Your voice will be transcribed to text",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun PermissionDialog(
    onRequestPermission: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(DesignTokens.Spacing.md),
        shape = MaterialTheme.shapes.extraLarge,
        color = MaterialTheme.colorScheme.surfaceContainer
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(DesignTokens.Spacing.lg),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.MicOff,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(64.dp)
            )
            
            Spacer(modifier = Modifier.height(DesignTokens.Spacing.md))
            
            Text(
                text = "Microphone Permission Required",
                style = MaterialTheme.typography.headlineSmall
            )
            
            Spacer(modifier = Modifier.height(DesignTokens.Spacing.sm))
            
            Text(
                text = "To use voice input, please grant microphone access.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(DesignTokens.Spacing.lg))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onDismiss) {
                    Text("Not Now")
                }
                Spacer(modifier = Modifier.width(DesignTokens.Spacing.sm))
                ClinePrimaryButton(onClick = onRequestPermission) {
                    Text("Grant Permission")
                }
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun VoiceInputRecording(
    viewModel: VoiceInputViewModel,
    onStopRecording: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    // Animated wave
    val waveAnimation = remember { Animatable(0f) }
    
    LaunchedEffect(Unit) {
        waveAnimation.animateTo(
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(1000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            )
        )
    }

    // Amplitude animation
    val amplitudeValues = remember { List(10) { mutableStateOf(0f) } }
    
    LaunchedEffect(viewModel.recordingState) {
        while (viewModel.recordingState == VoiceInputViewModel.RecordingState.RECORDING) {
            amplitudeValues.forEachIndexed { index, state ->
                state.value = (0.3f + 0.7f * kotlin.math.sin(
                    System.currentTimeMillis() / 200.0 + index * 0.5
                ).toFloat()).coerceIn(0f, 1f)
            }
            delay(50)
        }
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Recording indicator
        Box(
            modifier = Modifier
                .size(200.dp)
                .background(
                    color = MaterialTheme.colorScheme.surfaceContainer,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            // Wave animation
            Canvas(modifier = Modifier.fillMaxSize()) {
                val center = Offset(size.width / 2, size.height / 2)
                val radius = size.minDimension / 2
                
                repeat(3) { i ->
                    val currentRadius = radius * (0.7f + i * 0.15f)
                    val waveHeight = 20f * (1 - i * 0.2f)
                    
                    drawCircle(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f - i * 0.1f),
                        radius = currentRadius,
                        center = center
                    )
                }
                
                // Amplitude bars
                amplitudeValues.forEachIndexed { index, amplitude ->
                    val angle = (index * 36f) * (Math.PI / 180f)
                    val barHeight = 40f * amplitude.value
                    val barWidth = 6f
                    
                    val x = center.x + (radius * 0.6f) * cos(angle).toFloat()
                    val y = center.y + (radius * 0.6f) * sin(angle).toFloat()
                    
                    drawLine(
                        color = MaterialTheme.colorScheme.primary,
                        start = Offset(x, y - barHeight / 2),
                        end = Offset(x, y + barHeight / 2),
                        strokeWidth = barWidth
                    )
                }
            }
            
            // Center button
            IconButton(
                onClick = onStopRecording,
                modifier = Modifier
                    .size(64.dp)
                    .background(
                        color = MaterialTheme.colorScheme.error,
                        shape = CircleShape
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.Stop,
                    contentDescription = "Stop Recording",
                    tint = MaterialTheme.colorScheme.onError,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(DesignTokens.Spacing.lg))
        
        // Timer
        Text(
            text = viewModel.formatDuration(viewModel.recordingDuration),
            style = MaterialTheme.typography.displayMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(DesignTokens.Spacing.sm))
        
        // Status
        Text(
            text = "Listening...",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Spacer(modifier = Modifier.height(DesignTokens.Spacing.sm))
        
        Text(
            text = "Speak naturally. Stop when finished.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(DesignTokens.Spacing.xl))
        
        // Controls
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = DesignTokens.Spacing.xl),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = {
                    viewModel.stopRecording()
                    viewModel.reset()
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Discard",
                    tint = MaterialTheme.colorScheme.error
                )
            }
            
            IconButton(
                onClick = onStopRecording,
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = CircleShape
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.Stop,
                    contentDescription = "Stop",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            
            Box(modifier = Modifier.width(48.dp))
        }
    }
}

@Composable
fun VoiceInputResults(
    transcript: String,
    onSend: () -> Unit,
    onRetry: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(vertical = DesignTokens.Spacing.md),
            shape = MaterialTheme.shapes.medium
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(DesignTokens.Spacing.lg)
            ) {
                Text(
                    text = "Transcript",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Spacer(modifier = Modifier.height(DesignTokens.Spacing.md))
                
                Text(
                    text = transcript,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
        
        Spacer(modifier = Modifier.height(DesignTokens.Spacing.xl))
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = DesignTokens.Spacing.xl),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            TextButton(
                onClick = onRetry,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Replay,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(DesignTokens.Spacing.xs))
                Text("Retry")
            }
            
            ClinePrimaryButton(
                onClick = onSend,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(DesignTokens.Spacing.xs))
                Text("Send")
            }
            
            TextButton(
                onClick = onCancel,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Cancel")
            }
        }
    }
}

@Composable
fun RecordingVisualizer(
    modifier: Modifier = Modifier,
    isRecording: Boolean = true
) {
    val scope = rememberCoroutineScope()
    val dots = remember { List(3) { mutableStateOf(0f) } }
    
    if (isRecording) {
        LaunchedEffect(Unit) {
            while (true) {
                dots.forEachIndexed { index, state ->
                    state.value = if (index == 0) 1f else 0.3f
                }
                delay(200)
                
                dots.forEachIndexed { index, state ->
                    state.value = if (index == 1) 1f else 0.3f
                }
                delay(200)
                
                dots.forEachIndexed { index, state ->
                    state.value = if (index == 2) 1f else 0.3f
                }
                delay(200)
            }
        }
    }
    
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        dots.forEach { alpha ->
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = alpha.value))
            )
            Spacer(modifier = Modifier.width(4.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun VoiceInputScreenPreview() {
    MaterialTheme {
        VoiceInputScreen(
            onTranscriptReady = {},
            onBack = {}
        )
    }
}

// State enum for voice input
private enum class VoiceInputState {
    IDLE,
    PERMISSION,
    RECORDING,
    RESULTS
}
