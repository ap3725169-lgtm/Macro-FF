package com.iswipemacro.app.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.iswipemacro.app.model.GestureStroke
import com.iswipemacro.app.ui.theme.*

@Composable
fun SaveGestureDialog(
    strokes: List<GestureStroke>,
    onDismiss: () -> Unit,
    onSave: (name: String, category: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("FPS") }

    val totalPoints = strokes.sumOf { it.points.size }
    val totalDistance = strokes.sumOf { it.lengthPx.toDouble() }.toInt()
    val duration = if (strokes.isNotEmpty()) strokes.maxOf { it.endTime } else 0L

    val categories = listOf("FPS", "MOBA", "Battle Royale", "Rhythm", "General")

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = DarkPurpleSurface,
            border = androidx.compose.foundation.BorderStroke(1.dp, CyberCyan.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.BookmarkAdd, contentDescription = null, tint = CyberCyan)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Save Gesture Profile",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Stats strip
                Surface(
                    color = DeepObsidian,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Duration", color = TextMuted, fontSize = 10.sp)
                            Text("${duration}ms", color = CyberCyan, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Points", color = TextMuted, fontSize = 10.sp)
                            Text("$totalPoints", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Length", color = TextMuted, fontSize = 10.sp)
                            Text("${totalDistance}px", color = NeonCyanLight, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text("Profile Name", color = Color.LightGray, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    placeholder = { Text("e.g. Apex 180 Flick, Quick Combo...", color = Color.DarkGray, fontSize = 13.sp) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyberCyan,
                        unfocusedBorderColor = Color.DarkGray,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.LightGray
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text("Category", color = Color.LightGray, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    categories.forEach { cat ->
                        val isCur = category == cat
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isCur) CyberCyan else DeepObsidian,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { category = cat }
                        ) {
                            Text(
                                text = cat,
                                color = if (isCur) Color.Black else Color.LightGray,
                                fontWeight = if (isCur) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 10.sp,
                                modifier = Modifier.padding(vertical = 8.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                maxLines = 1
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Cancel", color = Color.LightGray)
                    }

                    Button(
                        onClick = {
                            val finalName = if (name.isBlank()) "Gesture #${System.currentTimeMillis() % 1000}" else name.trim()
                            onSave(finalName, category)
                            onDismiss()
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = CyberCyan),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Save Profile", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
