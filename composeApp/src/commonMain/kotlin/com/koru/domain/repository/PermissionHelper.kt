package com.koru.domain.repository

/**
 * Interface for platform-specific permission management.
 */
interface PermissionHelper {
    /**
     * Checks if the microphone permission is currently granted.
     */
    fun hasMicrophonePermission(): Boolean

    /**
     * Requests microphone permission from the user.
     *
     * @return True if the permission was granted, false otherwise.
     */
    suspend fun requestMicrophonePermission(): Boolean
}
