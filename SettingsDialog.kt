package com.iswipemacro.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.iswipemacro.app.ui.theme.*
import com.iswipemacro.app.viewmodel.GestureViewModel

@Composable
fun SettingsDialog(
    viewModel: GestureViewModel,
    onDismiss: () -> Unit
) {
    val speed by viewModel.replaySpeed.collectAsState()
    val repeatCount by viewModel.repeatCount.collectAsState()
    val repeatDelayMs by viewModel.repeatDelayMs.collectAsState()
    val hapticsEnabled by viewModel.hapticsEnabled.collectAsState()
    val hapticIntensity by viewModel.hapticIntensity.collectAsState()
    val trailTheme by viewModel.trailTheme.collectAsState()
    val showGhostPointer by viewModel.showGhostPointer.collectAsState()
    val showGridLines by viewModel.showGridLines.collectAsState()
    val showCoordinatesHUD by viewModel.showCoordinatesHUD.collectAsState()

    var showClearConfirm by remember { mutableStateOf(false) }

    val speedList = listOf(0.25f, 0.5f, 0.75f, 1.0f, 1.5f, 2.0f, 3.0f, 5.0f)
    val loopList = listOf(1, 2, 3, 5, 10, -1)
    val delayList = listOf(0L, 150L, 300L, 500L, 1000L)

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = DarkPurpleSurface,
            border = androidx.compose.foundation.BorderStroke(1.dp, CyberCyan.copy(alpha = 0.4f)),
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Settings, contentDescription = null, tint = CyberCyan)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Macro Studio Settings",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color.White
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.LightGray)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Replay Speed Multiplier
                    Card(
                        colors = CardDefaults.cardColors(containerColor = DeepObsidian),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Replay Speed", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text("${speed}x", color = CyberCyan, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                speedList.take(4).forEach { spd ->
                                    val isCur = speed == spd
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (isCur) CyberCyan else DarkPurpleSurface,
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable { viewModel.replaySpeed.value = spd }
                                    ) {
                                        Text(
                                            text = "${spd}x",
                                            color = if (isCur) Color.Black else Color.LightGray,
                                            fontWeight = if (isCur) FontWeight.Bold else FontWeight.Normal,
                                            fontSize = 11.sp,
                                            modifier = Modifier.padding(vertical = 8.dp),
                                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                speedList.drop(4).forEach { spd ->
                                    val isCur = speed == spd
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (isCur) CyberCyan else DarkPurpleSurface,
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable { viewModel.replaySpeed.value = spd }
                                    ) {
                                        Text(
                                            text = "${spd}x",
                                            color = if (isCur) Color.Black else Color.LightGray,
                                            fontWeight = if (isCur) FontWeight.Bold else FontWeight.Normal,
                                            fontSize = 11.sp,
                                            modifier = Modifier.padding(vertical = 8.dp),
                                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Loop Count & Interval Delay
                    Card(
                        colors = CardDefaults.cardColors(containerColor = DeepObsidian),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Repeat Loop Count", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text(if (repeatCount <= 0) "Infinite ∞" else "${repeatCount}x", color = CyberCyan, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                loopList.forEach { lp ->
                                    val isCur = repeatCount == lp
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (isCur) ElectricViolet else DarkPurpleSurface,
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable { viewModel.repeatCount.value = lp }
                                    ) {
                                        Text(
                                            text = if (lp <= 0) "∞" else "${lp}x",
                                            color = Color.White,
                                            fontWeight = if (isCur) FontWeight.Bold else FontWeight.Normal,
                                            fontSize = 11.sp,
                                            modifier = Modifier.padding(vertical = 8.dp),
                                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Delay Between Loops", color = TextMuted, fontSize = 12.sp)
                                Text("${repeatDelayMs}ms", color = Color.LightGray, fontSize = 12.sp)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                delayList.forEach { dly ->
                                    val isCur = repeatDelayMs == dly
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = if (isCur) CyberCyan.copy(alpha = 0.2f) else DarkPurpleSurface,
                                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isCur) CyberCyan else Color.Transparent),
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable { viewModel.repeatDelayMs.value = dly }
                                    ) {
                                        Text(
                                            text = "${dly}ms",
                                            color = if (isCur) CyberCyan else Color.Gray,
                                            fontSize = 10.sp,
                                            modifier = Modifier.padding(vertical = 6.dp),
                                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Tactile Haptics
                    Card(
                        colors = CardDefaults.cardColors(containerColor = DeepObsidian),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("Subtle Haptic Feedback", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text("Vibration cues on record start/stop and replay", color = TextMuted, fontSize = 11.sp)
                                }
                                Switch(
                                    checked = hapticsEnabled,
                                    onCheckedChange = { viewModel.hapticsEnabled.value = it },
                                    colors = SwitchDefaults.colors(checkedThumbColor = CyberCyan, checkedTrackColor = ElectricViolet)
                                )
                            }

                            if (hapticsEnabled) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    listOf("light", "medium", "strong").forEach { intensity ->
                                        val isCur = hapticIntensity == intensity
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = if (isCur) CyberCyan else DarkPurpleSurface,
                                            modifier = Modifier
                                                .weight(1f)
                                                .clickable { viewModel.hapticIntensity.value = intensity }
                                        ) {
                                            Text(
                                                text = intensity.replaceFirstChar { it.uppercase() },
                                                color = if (isCur) Color.Black else Color.LightGray,
                                                fontWeight = if (isCur) FontWeight.Bold else FontWeight.Normal,
                                                fontSize = 11.sp,
                                                modifier = Modifier.padding(vertical = 6.dp),
                                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Dark Theme Trail Glow Colors
                    Card(
                        colors = CardDefaults.cardColors(containerColor = DeepObsidian),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Dark Theme Trail Color", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf(
                                    Pair("neon-cyan", Color(0xFF00F0FF)),
                                    Pair("cyber-purple", Color(0xFF9D00FF)),
                                    Pair("matrix-green", Color(0xFF00FF88)),
                                    Pair("solar-gold", Color(0xFFFFB800))
                                ).forEach { (id, color) ->
                                    val isCur = trailTheme == id
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (isCur) color.copy(alpha = 0.2f) else DarkPurpleSurface,
                                        border = androidx.compose.foundation.BorderStroke(1.5.dp, if (isCur) color else Color.DarkGray),
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable { viewModel.trailTheme.value = id }
                                    ) {
                                        Box(
                                            modifier = Modifier.padding(vertical = 10.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(14.dp)
                                                    .background(color, CircleShape)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // HUD Toggles
                    Card(
                        colors = CardDefaults.cardColors(containerColor = DeepObsidian),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("Practice HUD Overlays", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Ghost Touch Reticle", color = Color.LightGray, fontSize = 12.sp)
                                Checkbox(
                                    checked = showGhostPointer,
                                    onCheckedChange = { viewModel.showGhostPointer.value = it },
                                    colors = CheckboxDefaults.colors(checkedColor = CyberCyan, checkmarkColor = Color.Black)
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Tactical Grid & Crosshairs", color = Color.LightGray, fontSize = 12.sp)
                                Checkbox(
                                    checked = showGridLines,
                                    onCheckedChange = { viewModel.showGridLines.value = it },
                                    colors = CheckboxDefaults.colors(checkedColor = CyberCyan, checkmarkColor = Color.Black)
                                )
                            }
                        }
                    }

                    // Clear All Profiles
                    if (showClearConfirm) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Wipe all saved profiles?", color = DangerRed, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(
                                    onClick = {
                                        viewModel.clearAllProfiles()
                                        showClearConfirm = false
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = DangerRed)
                                ) {
                                    Text("Yes, Wipe", color = Color.White, fontSize = 11.sp)
                                }
                                OutlinedButton(onClick = { showClearConfirm = false }) {
                                    Text("Cancel", color = Color.LightGray, fontSize = 11.sp)
                                }
                            }
                        }
                    } else {
                        OutlinedButton(
                            onClick = { showClearConfirm = true },
                            modifier = Modifier.fillMaxWidth(),
                            border = androidx.compose.foundation.BorderStroke(1.dp, DangerRed.copy(alpha = 0.5f))
                        ) {
                            Icon(Icons.Default.DeleteForever, contentDescription = null, tint = DangerRed, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Clear All Saved Profiles", color = DangerRed, fontSize = 12.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = CyberCyan),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Apply & Close", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
