package com.iswipemacro.app.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iswipemacro.app.model.PlaybackState
import com.iswipemacro.app.ui.theme.*
import com.iswipemacro.app.viewmodel.GestureViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: GestureViewModel
) {
    val playbackState by viewModel.playbackState.collectAsState()
    val activeStrokes by viewModel.activeStrokes.collectAsState()
    val ghostPointer by viewModel.ghostPointer.collectAsState()
    val telemetryText by viewModel.telemetryText.collectAsState()
    val selectedProfile by viewModel.selectedProfile.collectAsState()
    val speed by viewModel.replaySpeed.collectAsState()
    val showGridLines by viewModel.showGridLines.collectAsState()
    val showGhostPointer by viewModel.showGhostPointer.collectAsState()
    val trailTheme by viewModel.trailTheme.collectAsState()

    var canvasWidth by remember { mutableStateOf(1080f) }
    var canvasHeight by remember { mutableStateOf(2400f) }

    // Dialog state
    var showSavedSheet by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }
    var showSaveDialog by remember { mutableStateOf(false) }
    var showInfoDialog by remember { mutableStateOf(false) }

    // Pulsing animation for Recording state
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    val currentTrailColorHex = when (trailTheme) {
        "cyber-purple" -> "#9D00FF"
        "matrix-green" -> "#00FF88"
        "solar-gold" -> "#FFB800"
        else -> "#00F0FF"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = CyberCyan.copy(alpha = 0.15f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, CyberCyan.copy(alpha = 0.4f)),
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "⚡",
                                    fontSize = 16.sp
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "iSWIPE",
                                fontWeight = FontWeight.Black,
                                fontSize = 18.sp,
                                color = CyberCyan
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "MACRO",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = Color.White
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { showSavedSheet = true }) {
                        Icon(Icons.Default.Folder, contentDescription = "Saved Profiles", tint = CyberCyan)
                    }
                    IconButton(onClick = { showSettingsDialog = true }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings", tint = Color.LightGray)
                    }
                    IconButton(onClick = { showInfoDialog = true }) {
                        Icon(Icons.Default.Info, contentDescription = "App Info", tint = Color.LightGray)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DeepObsidian
                )
            )
        },
        bottomBar = {
            Surface(
                color = DarkPurpleSurface,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.dp,
                        color = CyberCyan.copy(alpha = 0.25f),
                        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                    )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    // Real-time telemetry ticker
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = telemetryText,
                            color = CyberCyan.copy(alpha = 0.9f),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1,
                            modifier = Modifier.weight(1f)
                        )
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = DeepObsidian,
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color.DarkGray)
                        ) {
                            Text(
                                text = "${speed}x",
                                color = NeonCyanLight,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Buttons deck: Record, Stop, Replay, Clear
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 1. Record / Stop Record Button
                        if (playbackState == PlaybackState.RECORDING) {
                            Button(
                                onClick = { viewModel.stopRecording() },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(50.dp)
                                    .scale(pulseScale),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = DangerRed)
                            ) {
                                Icon(Icons.Default.Stop, contentDescription = "Stop", tint = Color.White)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("STOP", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
                            }
                        } else {
                            Button(
                                onClick = { viewModel.startRecording() },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(50.dp),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = ElectricViolet)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .background(DangerRed, CircleShape)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("RECORD", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
                            }
                        }

                        // 2. Replay / Halt Replay Button
                        if (playbackState == PlaybackState.PLAYING) {
                            Button(
                                onClick = { viewModel.stopReplay() },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(50.dp),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = CyberCyan)
                            ) {
                                Icon(Icons.Default.Pause, contentDescription = "Halt", tint = Color.Black)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("HALT", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.Black)
                            }
                        } else {
                            Button(
                                onClick = { viewModel.startReplay(canvasWidth, canvasHeight) },
                                enabled = activeStrokes.isNotEmpty() && playbackState != PlaybackState.RECORDING,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(50.dp),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = CyberCyan,
                                    disabledContainerColor = Color(0xFF1F2937)
                                )
                            ) {
                                Icon(Icons.Default.PlayArrow, contentDescription = "Replay", tint = if (activeStrokes.isNotEmpty()) Color.Black else Color.Gray)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    "REPLAY",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = if (activeStrokes.isNotEmpty()) Color.Black else Color.Gray
                                )
                            }
                        }

                        // 3. Clear Canvas Button
                        OutlinedButton(
                            onClick = { viewModel.clearCanvas() },
                            enabled = activeStrokes.isNotEmpty() || playbackState != PlaybackState.IDLE,
                            modifier = Modifier.height(50.dp),
                            shape = RoundedCornerShape(14.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color.DarkGray)
                        ) {
                            Icon(Icons.Default.DeleteOutline, contentDescription = "Clear", tint = Color.LightGray)
                        }
                    }

                    // Save Gesture Button (Visible when uncommitted gesture is on canvas)
                    if (activeStrokes.isNotEmpty() && playbackState == PlaybackState.IDLE) {
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedButton(
                            onClick = { showSaveDialog = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(38.dp),
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, CyberCyan.copy(alpha = 0.5f))
                        ) {
                            Icon(Icons.Default.BookmarkAdd, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Save Recorded Gesture to Library", color = CyberCyan, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            PracticeCanvas(
                modifier = Modifier.fillMaxSize(),
                activeStrokes = activeStrokes,
                ghostPointer = ghostPointer,
                isRecording = playbackState == PlaybackState.RECORDING,
                showGridLines = showGridLines,
                showGhostPointer = showGhostPointer,
                trailColorHex = currentTrailColorHex,
                onTouchDown = { x, y, nx, ny -> viewModel.onTouchDown(x, y, nx, ny) },
                onTouchMove = { x, y, nx, ny -> viewModel.onTouchMove(x, y, nx, ny) },
                onTouchUp = { x, y, nx, ny -> viewModel.onTouchUp(x, y, nx, ny) },
                onSizeChanged = { w, h ->
                    canvasWidth = w
                    canvasHeight = h
                }
            )

            // Top overlay badge showing active profile or state
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = DarkPurpleSurface.copy(alpha = 0.85f),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CyberCyan.copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(
                                    if (playbackState == PlaybackState.RECORDING) DangerRed else if (playbackState == PlaybackState.PLAYING) CyberCyan else NeonCyanLight,
                                    CircleShape
                                )
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (playbackState == PlaybackState.RECORDING) "LIVE REC"
                                   else if (playbackState == PlaybackState.PLAYING) "REPLAYING"
                                   else selectedProfile?.name ?: "PRACTICE ARENA",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                Surface(
                    color = DeepObsidian.copy(alpha = 0.8f),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.DarkGray)
                ) {
                    Text(
                        text = "Redmi Note 11T 5G (90Hz)",
                        fontSize = 10.sp,
                        color = TextMuted,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }

    // Sheets & Dialogs
    if (showSavedSheet) {
        SavedGesturesSheet(
            viewModel = viewModel,
            onDismiss = { showSavedSheet = false },
            onPlayProfile = { profile ->
                viewModel.selectProfile(profile)
                viewModel.startReplay(canvasWidth, canvasHeight)
            }
        )
    }

    if (showSettingsDialog) {
        SettingsDialog(
            viewModel = viewModel,
            onDismiss = { showSettingsDialog = false }
        )
    }

    if (showSaveDialog) {
        SaveGestureDialog(
            strokes = activeStrokes,
            onDismiss = { showSaveDialog = false },
            onSave = { name, cat ->
                viewModel.saveCurrentGesture(name, cat)
            }
        )
    }

    if (showInfoDialog) {
        AppInfoDialog(onDismiss = { showInfoDialog = false })
    }
}
