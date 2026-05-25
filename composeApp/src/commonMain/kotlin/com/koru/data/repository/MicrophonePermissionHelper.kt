package com.koru.data.repository

import com.koru.domain.repository.PermissionHelper

/**
 * Platform-specific implementation for microphone permission.
 * This expect class is resolved by Android and iOS actual implementations.
 */
expect class MicrophonePermissionHelper : PermissionHelper {
    /**
     * Checks if the microphone permission is currently granted.
     * @return true if granted, false otherwise.
     */
    override fun hasMicrophonePermission(): Boolean

    /**
     * Requests the microphone permission from the user asynchronously.
     * @return true if the user grants the permission, false if denied.
     */
    override suspend fun requestMicrophonePermission(): Boolean
}
