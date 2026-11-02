package com.iswipemacro.app.util

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

/**
 * Manages subtle haptic feedback for iSwipeMacro.
 * Provides distinct, non-overwhelming tactile cues for:
 * 1. Recording Start (gentle double pulse)
 * 2. Recording Stop (single crisp tick)
 * 3. Replay Start (rising buzz)
 * 4. Replay Stop (double short tick)
 */
class HapticsManager(context: Context) {

    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        vibratorManager?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    var isEnabled: Boolean = true

    /**
     * Subtle vibration when user STARTS recording a gesture.
     */
    fun onRecordStart() {
        if (!isEnabled || vibrator == null || !vibrator.hasVibrator()) return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Gentle double pulse: [wait 0ms, vibrate 25ms, sleep 35ms, vibrate 25ms]
            val timings = longArrayOf(0, 25, 35, 25)
            val amplitudes = intArrayOf(0, 160, 0, 180)
            val effect = VibrationEffect.createWaveform(timings, amplitudes, -1)
            vibrator.vibrate(effect)
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(longArrayOf(0, 25, 35, 25), -1)
        }
    }

    /**
     * Subtle vibration when user STOPS recording a gesture.
     */
    fun onRecordStop() {
        if (!isEnabled || vibrator == null || !vibrator.hasVibrator()) return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(38, 200))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(38)
        }
    }

    /**
     * Subtle vibration when gesture REPLAY begins.
     */
    fun onReplayStart() {
        if (!isEnabled || vibrator == null || !vibrator.hasVibrator()) return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Rising smooth double buzz
            val timings = longArrayOf(0, 20, 25, 30)
            val amplitudes = intArrayOf(0, 120, 0, 210)
            val effect = VibrationEffect.createWaveform(timings, amplitudes, -1)
            vibrator.vibrate(effect)
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(longArrayOf(0, 20, 25, 30), -1)
        }
    }

    /**
     * Subtle vibration when gesture REPLAY is stopped.
     */
    fun onReplayStop() {
        if (!isEnabled || vibrator == null || !vibrator.hasVibrator()) return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Crisp double tick
            val timings = longArrayOf(0, 20, 20, 20)
            val amplitudes = intArrayOf(0, 180, 0, 180)
            val effect = VibrationEffect.createWaveform(timings, amplitudes, -1)
            vibrator.vibrate(effect)
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(longArrayOf(0, 20, 20, 20), -1)
        }
    }

    fun onButtonTap() {
        if (!isEnabled || vibrator == null || !vibrator.hasVibrator()) return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_TICK))
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(15, 120))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(15)
        }
    }
}
