package com.iswipemacro.app.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import com.iswipemacro.app.model.GestureStroke
import com.iswipemacro.app.model.TouchPoint
import com.iswipemacro.app.ui.theme.CyberCyan
import com.iswipemacro.app.ui.theme.DeepObsidian
import com.iswipemacro.app.ui.theme.ElectricViolet
import com.iswipemacro.app.ui.theme.NeonPinkAccent

@Composable
fun PracticeCanvas(
    modifier: Modifier = Modifier,
    activeStrokes: List<GestureStroke>,
    ghostPointer: TouchPoint?,
    isRecording: Boolean,
    showGridLines: Boolean = true,
    showGhostPointer: Boolean = true,
    trailColorHex: String = "#00F0FF",
    onTouchDown: (x: Float, y: Float, nx: Float, ny: Float) -> Unit,
    onTouchMove: (x: Float, y: Float, nx: Float, ny: Float) -> Unit,
    onTouchUp: (x: Float, y: Float, nx: Float, ny: Float) -> Unit,
    onSizeChanged: (width: Float, height: Float) -> Unit
) {
    var canvasWidth by remember { mutableStateOf(0f) }
    var canvasHeight by remember { mutableStateOf(0f) }

    val baseNeonColor = try {
        Color(android.graphics.Color.parseColor(trailColorHex))
    } catch (e: Exception) {
        CyberCyan
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DeepObsidian)
            .pointerInput(isRecording) {
                detectDragGestures(
                    onDragStart = { offset ->
                        if (size.width > 0 && size.height > 0) {
                            onTouchDown(
                                offset.x,
                                offset.y,
                                (offset.x / size.width).coerceIn(0f, 1f),
                                (offset.y / size.height).coerceIn(0f, 1f)
                            )
                        }
                    },
                    onDrag = { change, _ ->
                        change.consume()
                        if (size.width > 0 && size.height > 0) {
                            onTouchMove(
                                change.position.x,
                                change.position.y,
                                (change.position.x / size.width).coerceIn(0f, 1f),
                                (change.position.y / size.height).coerceIn(0f, 1f)
                            )
                        }
                    },
                    onDragEnd = {
                        onTouchUp(0f, 0f, 0f, 0f)
                    },
                    onDragCancel = {
                        onTouchUp(0f, 0f, 0f, 0f)
                    }
                )
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            if (canvasWidth != size.width || canvasHeight != size.height) {
                canvasWidth = size.width
                canvasHeight = size.height
                onSizeChanged(size.width, size.height)
            }

            // Draw tactical cyber grid
            if (showGridLines) {
                val gridSpacing = 40f
                var x = 0f
                while (x < size.width) {
                    drawLine(
                        color = CyberCyan.copy(alpha = 0.05f),
                        start = Offset(x, 0f),
                        end = Offset(x, size.height),
                        strokeWidth = 1f
                    )
                    x += gridSpacing
                }
                var y = 0f
                while (y < size.height) {
                    drawLine(
                        color = CyberCyan.copy(alpha = 0.05f),
                        start = Offset(0f, y),
                        end = Offset(size.width, y),
                        strokeWidth = 1f
                    )
                    y += gridSpacing
                }

                // Crosshair at center
                val centerX = size.width / 2f
                val centerY = size.height / 2f
                drawLine(
                    color = CyberCyan.copy(alpha = 0.2f),
                    start = Offset(centerX - 24f, centerY),
                    end = Offset(centerX + 24f, centerY),
                    strokeWidth = 1.5f
                )
                drawLine(
                    color = CyberCyan.copy(alpha = 0.2f),
                    start = Offset(centerX, centerY - 24f),
                    end = Offset(centerX, centerY + 24f),
                    strokeWidth = 1.5f
                )
            }

            // Draw recorded stroke paths
            for (stroke in activeStrokes) {
                if (stroke.points.size < 2) continue
                val path = Path()
                val first = stroke.points.first()
                val startX = first.nx * size.width
                val startY = first.ny * size.height
                path.moveTo(startX, startY)

                for (i in 1 until stroke.points.size) {
                    val pt = stroke.points[i]
                    val px = pt.nx * size.width
                    val py = pt.ny * size.height
                    path.lineTo(px, py)
                }

                // Pass 1: Wide ambient neon bloom
                drawPath(
                    path = path,
                    color = ElectricViolet.copy(alpha = 0.35f),
                    style = Stroke(width = 16f)
                )

                // Pass 2: Electric body stroke
                drawPath(
                    path = path,
                    color = baseNeonColor.copy(alpha = 0.75f),
                    style = Stroke(width = 8f)
                )

                // Pass 3: Ultra crisp white core
                drawPath(
                    path = path,
                    color = Color.White,
                    style = Stroke(width = 2.5f)
                )

                // Start Waypoint Node (Cyan Pip)
                drawCircle(
                    color = CyberCyan,
                    radius = 9f,
                    center = Offset(startX, startY)
                )
                drawCircle(
                    color = Color.White,
                    radius = 4f,
                    center = Offset(startX, startY)
                )

                // End Waypoint Node (Neon Pink Pip)
                val last = stroke.points.last()
                val endX = last.nx * size.width
                val endY = last.ny * size.height
                drawCircle(
                    color = NeonPinkAccent,
                    radius = 8f,
                    center = Offset(endX, endY)
                )
                drawCircle(
                    color = Color.White,
                    radius = 3.5f,
                    center = Offset(endX, endY)
                )
            }

            // Draw Replay Ghost Touch Pointer
            if (showGhostPointer && ghostPointer != null) {
                val gx = ghostPointer.nx * size.width
                val gy = ghostPointer.ny * size.height

                // Outer pulsing target ring
                drawCircle(
                    color = CyberCyan.copy(alpha = 0.2f),
                    radius = 36f,
                    center = Offset(gx, gy)
                )
                drawCircle(
                    color = CyberCyan,
                    radius = 20f,
                    center = Offset(gx, gy),
                    style = Stroke(width = 2.5f)
                )
                drawCircle(
                    color = Color.White,
                    radius = 6f,
                    center = Offset(gx, gy)
                )

                // Reticle crosshair arms
                drawLine(
                    color = CyberCyan,
                    start = Offset(gx - 32f, gy),
                    end = Offset(gx - 20f, gy),
                    strokeWidth = 2f
                )
                drawLine(
                    color = CyberCyan,
                    start = Offset(gx + 20f, gy),
                    end = Offset(gx + 32f, gy),
                    strokeWidth = 2f
                )
                drawLine(
                    color = CyberCyan,
                    start = Offset(gx, gy - 32f),
                    end = Offset(gx, gy - 20f),
                    strokeWidth = 2f
                )
                drawLine(
                    color = CyberCyan,
                    start = Offset(gx, gy + 20f),
                    end = Offset(gx, gy + 32f),
                    strokeWidth = 2f
                )
            }
        }
    }
}
