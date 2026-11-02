package com.iswipemacro.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.iswipemacro.app.ui.theme.*

@Composable
fun AppInfoDialog(onDismiss: () -> Unit) {
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Info, contentDescription = null, tint = CyberCyan)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "About iSwipeMacro",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color.White
                    )
                }
                Text("Version 1.0.0 • Native Android Studio Project", color = TextMuted, fontSize = 11.sp)

                Spacer(modifier = Modifier.height(14.dp))

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Mission Card
                    Card(
                        colors = CardDefaults.cardColors(containerColor = DeepObsidian),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Next-Gen Gaming Gesture Studio", color = CyberCyan, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "A standalone practice arena for gamers to record, visualize, analyze, and replay precision swipe paths and touch gestures with authentic tactile haptic feedback.",
                                color = Color.LightGray,
                                fontSize = 12.sp,
                                lineHeight = 17.sp
                            )
                        }
                    }

                    // Redmi Note 11T 5G Optimization
                    Card(
                        colors = CardDefaults.cardColors(containerColor = DeepObsidian),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Smartphone, contentDescription = null, tint = NeonCyanLight, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Redmi Note 11T 5G Optimization", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("• Screen: 6.6\" FHD+ IPS LCD (1080 x 2400 pixels, 20:9 aspect ratio)", color = TextMuted, fontSize = 11.sp)
                            Text("• Refresh Rate: 90Hz AdaptiveSync display", color = TextMuted, fontSize = 11.sp)
                            Text("• Touch Polling: 240Hz touch sampling rate supported", color = TextMuted, fontSize = 11.sp)
                            Text("• Coordinate Model: Normalized relative coordinates (nx, ny: 0.0 - 1.0) ensure 100% pixel-perfect scaling across any device resolution.", color = TextMuted, fontSize = 11.sp)
                        }
                    }

                    // Non-Root Safety Notice
                    Card(
                        colors = CardDefaults.cardColors(containerColor = ElectricViolet.copy(alpha = 0.15f)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ElectricViolet.copy(alpha = 0.4f)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Security, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Safety & Compliance Policy", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "1. Non-Root Compliant: Runs entirely in Android user space.\n2. In-App Practice Sandbox: Gestures are recorded and replayed strictly inside this app's own practice area.\n3. Zero Background Automation: Does NOT automate or inject touches into other apps or games, adhering strictly to Android security guidelines.",
                                color = Color.LightGray,
                                fontSize = 11.sp,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = CyberCyan),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Close", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
