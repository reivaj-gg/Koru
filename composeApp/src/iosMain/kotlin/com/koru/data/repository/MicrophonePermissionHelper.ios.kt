package com.koru.data.repository

import com.koru.domain.repository.PermissionHelper
import platform.AVFAudio.AVAudioSession
import platform.AVFAudio.AVAudioSessionRecordPermissionGranted

/**
 * iOS implementation of [MicrophonePermissionHelper].
 */
actual class MicrophonePermissionHelper : PermissionHelper {
    actual override fun hasMicrophonePermission(): Boolean {
        return AVAudioSession.sharedInstance().recordPermission() == AVAudioSessionRecordPermissionGranted
    }

    actual override suspend fun requestMicrophonePermission(): Boolean {
        // High-level bridge for the contest architecture.
        return hasMicrophonePermission()
    }
}
