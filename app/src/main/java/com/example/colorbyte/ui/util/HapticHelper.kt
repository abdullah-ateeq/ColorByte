package com.example.colorbyte.ui.util

import android.content.Context
import android.os.Build
import android.os.CombinedVibration
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

enum class HapticType {
    LIGHT,
    MEDIUM
}

object HapticHelper {
    fun performHaptic(context: Context, enabled: Boolean, type: HapticType = HapticType.LIGHT) {
        if (!enabled) return

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                val effect = when (type) {
                    HapticType.LIGHT -> VibrationEffect.createPredefined(VibrationEffect.EFFECT_TICK)
                    HapticType.MEDIUM -> VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK)
                }
                vibratorManager?.vibrate(CombinedVibration.createParallel(effect))
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                @Suppress("DEPRECATION")
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                val effect = when (type) {
                    HapticType.LIGHT -> VibrationEffect.createPredefined(VibrationEffect.EFFECT_TICK)
                    HapticType.MEDIUM -> VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK)
                }
                vibrator?.vibrate(effect)
            } else {
                @Suppress("DEPRECATION")
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                val duration = when (type) {
                    HapticType.LIGHT -> 15L
                    HapticType.MEDIUM -> 35L
                }
                @Suppress("DEPRECATION")
                vibrator?.vibrate(duration)
            }
        } catch (_: Exception) {
            // Haptics not supported or permission absent - silently ignore
        }
    }
}
