package com.koru.data.repository

import com.koru.domain.repository.PermissionHelper
import platform.AVFAudio.AVAudioSession
import platform.AVFAudio.AVAudioSessionRecordPermissionGranted

/**
 * iOS implementation of [MicrophonePermissionHelper].
 */
actual class MicrophonePermissionHelper : PermissionHelper {
    /**
     * Checks if the microphone permission is currently granted.
     * @return true if granted, false otherwise.
     */
    actual override fun hasMicrophonePermission(): Boolean =
        AVAudioSession.sharedInstance().recordPermission() == AVAudioSessionRecordPermissionGranted

    /**
     * Requests microphone permission.
     * @return true if granted, false otherwise.
     */
    actual override suspend fun requestMicrophonePermission(): Boolean = hasMicrophonePermission()
}
