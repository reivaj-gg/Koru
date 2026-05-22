package com.koru.data.repository

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.koru.domain.repository.PermissionHelper

/**
 * Android implementation of [MicrophonePermissionHelper].
 *
 * Note: Actual permission request on Android usually requires an Activity.
 * This is a simplified version for the contest architecture.
 */
actual class MicrophonePermissionHelper(
    private val context: Context,
) : PermissionHelper {
    actual override fun hasMicrophonePermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO,
        ) == PackageManager.PERMISSION_GRANTED
    }

    actual override suspend fun requestMicrophonePermission(): Boolean {
        // High-level bridge: Real implementation would use an Activity result launcher.
        // Returning current state for the MVP architecture.
        return hasMicrophonePermission()
    }
}
