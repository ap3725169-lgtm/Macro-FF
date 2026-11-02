package com.iswipemacro.app.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.iswipemacro.app.model.*
import com.iswipemacro.app.util.HapticsManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.math.hypot

class GestureViewModel(application: Application) : AndroidViewModel(application) {
    private val haptics = HapticsManager(application)
    private val prefs = application.getSharedPreferences("iswipemacro_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    // Playback state
    private val _playbackState = MutableStateFlow(PlaybackState.IDLE)
    val playbackState: StateFlow<PlaybackState> = _playbackState.asStateFlow()

    // Current active gesture in practice canvas
    private val _activeStrokes = MutableStateFlow<List<GestureStroke>>(emptyList())
    val activeStrokes: StateFlow<List<GestureStroke>> = _activeStrokes.asStateFlow()

    // Replay cursor position for ghost touch indicator
    private val _ghostPointer = MutableStateFlow<TouchPoint?>(null)
    val ghostPointer: StateFlow<TouchPoint?> = _ghostPointer.asStateFlow()

    // Saved profiles list
    private val _savedProfiles = MutableStateFlow<List<GestureProfile>>(emptyList())
    val savedProfiles: StateFlow<List<GestureProfile>> = _savedProfiles.asStateFlow()

    // Currently selected profile
    private val _selectedProfile = MutableStateFlow<GestureProfile?>(null)
    val selectedProfile: StateFlow<GestureProfile?> = _selectedProfile.asStateFlow()

    // Replay settings
    val replaySpeed = MutableStateFlow(1.0f)
    val repeatCount = MutableStateFlow(1)
    val repeatDelayMs = MutableStateFlow(300L)
    val hapticsEnabled = MutableStateFlow(true)
    val hapticIntensity = MutableStateFlow("light")
    val trailTheme = MutableStateFlow("neon-cyan")
    val showGhostPointer = MutableStateFlow(true)
    val showGridLines = MutableStateFlow(true)
    val showCoordinatesHUD = MutableStateFlow(true)

    // Telemetry
    private val _telemetryText = MutableStateFlow("X: 000 | Y: 000 | Ready")
    val telemetryText: StateFlow<String> = _telemetryText.asStateFlow()

    // Internal recording state
    private var recordingStartTime = 0L
    private var currentRecordingStrokePoints = mutableListOf<TouchPoint>()
    private var replayJob: Job? = null

    init {
        loadSavedProfiles()
        if (_savedProfiles.value.isEmpty()) {
            loadDefaultPresets()
        }
    }

    fun startRecording() {
        if (_playbackState.value == PlaybackState.RECORDING) return
        stopReplay()
        _activeStrokes.value = emptyList()
        _ghostPointer.value = null
        _selectedProfile.value = null
        currentRecordingStrokePoints.clear()
        recordingStartTime = System.currentTimeMillis()
        _playbackState.value = PlaybackState.RECORDING

        if (hapticsEnabled.value) {
            haptics.onRecordStart()
        }
        _telemetryText.value = "REC ENGAGED • Draw gesture path inside practice area"
    }

    fun onTouchDown(x: Float, y: Float, nx: Float, ny: Float, pressure: Float = 1.0f) {
        if (_playbackState.value != PlaybackState.RECORDING) return
        val relTime = System.currentTimeMillis() - recordingStartTime
        val point = TouchPoint(x, y, nx, ny, relTime, PointType.DOWN, pressure)
        currentRecordingStrokePoints = mutableListOf(point)
        _telemetryText.value = "DOWN: (${x.toInt()}, ${y.toInt()}) [${String.format("%.2f", nx)}, ${String.format("%.2f", ny)}]"
        if (hapticsEnabled.value) {
            haptics.onButtonTap()
        }
    }

    fun onTouchMove(x: Float, y: Float, nx: Float, ny: Float, pressure: Float = 1.0f) {
        if (_playbackState.value != PlaybackState.RECORDING) return
        val relTime = System.currentTimeMillis() - recordingStartTime
        val lastPt = currentRecordingStrokePoints.lastOrNull()
        val dt = if (lastPt != null && relTime > lastPt.timestamp) (relTime - lastPt.timestamp) / 1000f else 0.016f
        val velocity = if (lastPt != null && dt > 0) hypot(x - lastPt.x, y - lastPt.y) / dt else 0f

        val point = TouchPoint(x, y, nx, ny, relTime, PointType.MOVE, pressure, velocity)
        currentRecordingStrokePoints.add(point)

        // Live preview of active drawing
        val currentList = _activeStrokes.value.toMutableList()
        val tempStroke = GestureStroke(
            points = currentRecordingStrokePoints.toList(),
            startTime = currentRecordingStrokePoints.firstOrNull()?.timestamp ?: 0L,
            endTime = relTime
        )
        if (currentList.isNotEmpty() && currentList.last().id == "active-stroke") {
            currentList[currentList.size - 1] = tempStroke.copy(id = "active-stroke")
        } else {
            currentList.add(tempStroke.copy(id = "active-stroke"))
        }
        _activeStrokes.value = currentList
        _telemetryText.value = "MOVE: (${x.toInt()}, ${y.toInt()}) • Speed: ${velocity.toInt()} px/s • Pts: ${currentRecordingStrokePoints.size}"
    }

    fun onTouchUp(x: Float, y: Float, nx: Float, ny: Float) {
        if (_playbackState.value != PlaybackState.RECORDING) return
        val relTime = System.currentTimeMillis() - recordingStartTime
        val point = TouchPoint(x, y, nx, ny, relTime, PointType.UP)
        currentRecordingStrokePoints.add(point)

        var dist = 0f
        for (i in 0 until currentRecordingStrokePoints.size - 1) {
            val p1 = currentRecordingStrokePoints[i]
            val p2 = currentRecordingStrokePoints[i + 1]
            dist += hypot(p2.x - p1.x, p2.y - p1.y)
        }

        val completedStroke = GestureStroke(
            points = currentRecordingStrokePoints.toList(),
            startTime = currentRecordingStrokePoints.firstOrNull()?.timestamp ?: 0L,
            endTime = relTime,
            lengthPx = dist
        )
        val updated = _activeStrokes.value.filter { it.id != "active-stroke" }.toMutableList()
        updated.add(completedStroke)
        _activeStrokes.value = updated
        currentRecordingStrokePoints.clear()
        _telemetryText.value = "STROKE COMPLETE: ${dist.toInt()}px • ${completedStroke.points.size} points"
    }

    fun stopRecording() {
        if (_playbackState.value != PlaybackState.RECORDING) return
        _playbackState.value = PlaybackState.IDLE

        if (hapticsEnabled.value) {
            haptics.onRecordStop()
        }
        val strokes = _activeStrokes.value
        val totalPts = strokes.sumOf { it.points.size }
        val duration = if (strokes.isNotEmpty()) strokes.maxOf { it.endTime } else 0L
        _telemetryText.value = "RECORDING STOPPED • Pts: $totalPts • Duration: ${duration}ms"
    }

    fun startReplay(canvasWidth: Float, canvasHeight: Float) {
        val strokes = _activeStrokes.value
        if (strokes.isEmpty()) {
            _telemetryText.value = "No gesture recorded yet to replay!"
            return
        }
        stopReplay()
        _playbackState.value = PlaybackState.PLAYING

        if (hapticsEnabled.value) {
            haptics.onReplayStart()
        }

        replayJob = viewModelScope.launch {
            val allPoints = strokes.flatMap { it.points }.sortedBy { it.timestamp }
            if (allPoints.isEmpty()) {
                _playbackState.value = PlaybackState.IDLE
                return@launch
            }

            val loops = if (repeatCount.value <= 0) 9999 else repeatCount.value
            val speed = replaySpeed.value.coerceIn(0.25f, 5.0f)

            for (currentLoop in 1..loops) {
                if (_playbackState.value != PlaybackState.PLAYING) break
                val baseTime = System.currentTimeMillis()

                for (pt in allPoints) {
                    if (_playbackState.value != PlaybackState.PLAYING) break
                    val scaledTargetTime = (pt.timestamp / speed).toLong()
                    val elapsed = System.currentTimeMillis() - baseTime
                    val waitMs = (scaledTargetTime - elapsed).coerceAtLeast(0L)
                    if (waitMs > 0) {
                        delay(waitMs)
                    }

                    val denormX = pt.nx * canvasWidth
                    val denormY = pt.ny * canvasHeight
                    _ghostPointer.value = pt.copy(x = denormX, y = denormY)
                    _telemetryText.value = "REPLAY: (${denormX.toInt()}, ${denormY.toInt()}) • Loop $currentLoop/$loops"
                }

                if (currentLoop < loops && _playbackState.value == PlaybackState.PLAYING) {
                    delay(repeatDelayMs.value)
                }
            }
            stopReplay()
        }
    }

    fun stopReplay() {
        if (replayJob?.isActive == true || _playbackState.value == PlaybackState.PLAYING) {
            replayJob?.cancel()
            replayJob = null
            _ghostPointer.value = null
            _playbackState.value = PlaybackState.IDLE

            if (hapticsEnabled.value) {
                haptics.onReplayStop()
            }
            _telemetryText.value = "REPLAY FINISHED"
        }
    }

    fun clearCanvas() {
        stopReplay()
        if (_playbackState.value == PlaybackState.RECORDING) {
            stopRecording()
        }
        _activeStrokes.value = emptyList()
        _ghostPointer.value = null
        _selectedProfile.value = null
        _telemetryText.value = "Practice Arena Cleared"
        if (hapticsEnabled.value) {
            haptics.onButtonTap()
        }
    }

    fun saveCurrentGesture(name: String, category: String = "General"): Boolean {
        val strokes = _activeStrokes.value
        if (strokes.isEmpty()) return false
        val totalPts = strokes.sumOf { it.points.size }
        val totalDist = strokes.sumOf { it.lengthPx.toDouble() }.toFloat()
        val duration = strokes.maxOfOrNull { it.endTime } ?: 0L
        val avgSpeed = if (duration > 0) (totalDist / (duration / 1000f)) else 0f

        val profile = GestureProfile(
            name = name.ifBlank { "Gesture #${_savedProfiles.value.size + 1}" },
            description = "$category gesture with ${strokes.size} stroke(s) and $totalPts points.",
            durationMs = duration,
            strokes = strokes,
            totalPoints = totalPts,
            totalDistancePx = totalDist,
            averageVelocityPxPerSec = avgSpeed,
            category = category,
            colorHex = if (category == "FPS") "#00F0FF" else if (category == "MOBA") "#9D00FF" else "#0077B6"
        )
        val updated = _savedProfiles.value.toMutableList()
        updated.add(0, profile)
        _savedProfiles.value = updated
        _selectedProfile.value = profile
        persistProfiles(updated)
        return true
    }

    fun selectProfile(profile: GestureProfile) {
        stopReplay()
        if (_playbackState.value == PlaybackState.RECORDING) {
            stopRecording()
        }
        _selectedProfile.value = profile
        _activeStrokes.value = profile.strokes
        _telemetryText.value = "Loaded: ${profile.name} (${profile.totalPoints} pts, ${profile.durationMs}ms)"
        if (hapticsEnabled.value) {
            haptics.onButtonTap()
        }
    }

    fun deleteProfile(profileId: String) {
        val updated = _savedProfiles.value.filter { it.id != profileId }
        _savedProfiles.value = updated
        if (_selectedProfile.value?.id == profileId) {
            _selectedProfile.value = null
        }
        persistProfiles(updated)
        if (hapticsEnabled.value) {
            haptics.onButtonTap()
        }
    }

    fun renameProfile(profileId: String, newName: String) {
        val updated = _savedProfiles.value.map {
            if (it.id == profileId) it.copy(name = newName, updatedAt = System.currentTimeMillis()) else it
        }
        _savedProfiles.value = updated
        if (_selectedProfile.value?.id == profileId) {
            _selectedProfile.value = _selectedProfile.value?.copy(name = newName)
        }
        persistProfiles(updated)
    }

    fun duplicateProfile(profile: GestureProfile) {
        val dup = profile.copy(
            id = java.util.UUID.randomUUID().toString(),
            name = "${profile.name} (Copy)",
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        val updated = _savedProfiles.value.toMutableList()
        updated.add(0, dup)
        _savedProfiles.value = updated
        persistProfiles(updated)
    }

    fun clearAllProfiles() {
        _savedProfiles.value = emptyList()
        _selectedProfile.value = null
        persistProfiles(emptyList())
    }

    fun loadDefaultPresets() {
        // Create 3 preloaded gaming presets
        val flickPoints = mutableListOf<TouchPoint>()
        for (i in 0..30) {
            val t = i / 30f
            val ease = 1f - (1f - t) * (1f - t) * (1f - t)
            val nx = 0.5f
            val ny = 0.75f + (0.25f - 0.75f) * ease
            flickPoints.add(
                TouchPoint(
                    x = nx * 1080f,
                    y = ny * 2400f,
                    nx = nx,
                    ny = ny,
                    timestamp = (t * 280).toLong(),
                    type = if (i == 0) PointType.DOWN else if (i == 30) PointType.UP else PointType.MOVE,
                    pressure = 0.8f - t * 0.3f,
                    velocity = 1800f * (1f - t * 0.7f)
                )
            )
        }
        val flickStroke = GestureStroke(
            id = "preset-flick-stroke",
            points = flickPoints,
            startTime = 0,
            endTime = 280,
            lengthPx = 1200f
        )
        val flickProfile = GestureProfile(
            name = "FPS Quick Snap 90°",
            description = "Instant vertical snap flick gesture designed for tactical shooter aim alignment.",
            durationMs = 280,
            strokes = listOf(flickStroke),
            totalPoints = flickPoints.size,
            totalDistancePx = 1200f,
            averageVelocityPxPerSec = 4285f,
            category = "FPS",
            colorHex = "#00F0FF"
        )

        val mobaPoints = mutableListOf<TouchPoint>()
        for (i in 0..45) {
            val t = i / 45f
            val angle = Math.PI.toFloat() * 0.6f * t
            val nx = (0.25f + 0.55f * kotlin.math.sin(angle)).coerceIn(0.05f, 0.95f)
            val ny = (0.75f - 0.45f * (1f - kotlin.math.cos(angle * 0.8f))).coerceIn(0.05f, 0.95f)
            mobaPoints.add(
                TouchPoint(
                    x = nx * 1080f,
                    y = ny * 2400f,
                    nx = nx,
                    ny = ny,
                    timestamp = (t * 650).toLong(),
                    type = if (i == 0) PointType.DOWN else if (i == 45) PointType.UP else PointType.MOVE,
                    pressure = 0.75f,
                    velocity = 850f
                )
            )
        }
        val mobaStroke = GestureStroke(
            id = "preset-moba-stroke",
            points = mobaPoints,
            startTime = 0,
            endTime = 650,
            lengthPx = 1450f
        )
        val mobaProfile = GestureProfile(
            name = "MOBA Skillshot Arc",
            description = "Curved predictive skillshot targeting path with sustained drag trajectory.",
            durationMs = 650,
            strokes = listOf(mobaStroke),
            totalPoints = mobaPoints.size,
            totalDistancePx = 1450f,
            averageVelocityPxPerSec = 2230f,
            category = "MOBA",
            colorHex = "#9D00FF"
        )

        val presets = listOf(flickProfile, mobaProfile)
        _savedProfiles.value = presets
        _selectedProfile.value = flickProfile
        _activeStrokes.value = flickProfile.strokes
        persistProfiles(presets)
    }

    private fun persistProfiles(profiles: List<GestureProfile>) {
        val json = gson.toJson(profiles)
        prefs.edit().putString("saved_profiles_json", json).apply()
    }

    private fun loadSavedProfiles() {
        val json = prefs.getString("saved_profiles_json", null)
        if (!json.isNullOrBlank()) {
            try {
                val type = object : TypeToken<List<GestureProfile>>() {}.type
                val list: List<GestureProfile> = gson.fromJson(json, type)
                _savedProfiles.value = list
                if (list.isNotEmpty()) {
                    _selectedProfile.value = list[0]
                    _activeStrokes.value = list[0].strokes
                }
            } catch (e: Exception) {
                _savedProfiles.value = emptyList()
            }
        }
    }
}
