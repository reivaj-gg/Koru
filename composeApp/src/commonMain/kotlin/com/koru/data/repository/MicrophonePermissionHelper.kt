package com.koru.data.repository

import com.koru.domain.repository.PermissionHelper

/**
 * Platform-specific implementation for microphone permission.
 */
expect class MicrophonePermissionHelper : PermissionHelper {
    override fun hasMicrophonePermission(): Boolean

    override suspend fun requestMicrophonePermission(): Boolean
}
