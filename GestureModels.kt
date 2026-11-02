package com.iswipemacro.app.model

import java.util.UUID

enum class PointType {
    DOWN, MOVE, UP
}

data class TouchPoint(
    val x: Float,
    val y: Float,
    val nx: Float, // Normalized X (0.0f - 1.0f)
    val ny: Float, // Normalized Y (0.0f - 1.0f)
    val timestamp: Long, // Relative milliseconds from gesture start
    val type: PointType = PointType.MOVE,
    val pressure: Float = 1.0f,
    val velocity: Float = 0f
)

data class GestureStroke(
    val id: String = UUID.randomUUID().toString(),
    val points: List<TouchPoint> = emptyList(),
    val startTime: Long = 0L,
    val endTime: Long = 0L,
    val lengthPx: Float = 0f
)

data class GestureProfile(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val description: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val durationMs: Long,
    val strokes: List<GestureStroke>,
    val totalPoints: Int,
    val totalDistancePx: Float,
    val averageVelocityPxPerSec: Float,
    val category: String = "General",
    val colorHex: String = "#00F0FF"
)

enum class PlaybackState {
    IDLE, RECORDING, PLAYING, PAUSED
}
