package com.koru.data.repository

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.koru.domain.repository.PermissionHelper

/**
 * Android implementation of [MicrophonePermissionHelper].
 */
actual class MicrophonePermissionHelper(
    private val context: Context,
) : PermissionHelper {
    /**
     * Checks if the microphone permission is currently granted.
     * @return true if granted, false otherwise.
     */
    actual override fun hasMicrophonePermission(): Boolean =
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO,
        ) == PackageManager.PERMISSION_GRANTED

    /**
     * Requests microphone permission.
     * @return true if granted, false otherwise.
     */
    actual override suspend fun requestMicrophonePermission(): Boolean = hasMicrophonePermission()
}
