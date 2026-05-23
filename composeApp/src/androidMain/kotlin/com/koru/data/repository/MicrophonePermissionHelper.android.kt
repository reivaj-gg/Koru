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
        // Fallback for when no UI controller is bound to request permission directly.
        // It relies on the permission already being granted by the user in settings.
        return hasMicrophonePermission()
    }
}
