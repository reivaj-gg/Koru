package com.koru.domain.model

/**
 * Represents the specific categories of failures within the voice capture system.
 */
enum class VoiceError {
    /**
     * The user has explicitly denied microphone or speech recognition permissions.
     */
    PERMISSION_DENIED,

    /**
     * The device hardware is either missing, damaged, or currently in use by another app.
     */
    HARDWARE_UNAVAILABLE,

    /**
     * The speech recognition engine failed to process the audio stream into text.
     */
    RECOGNITION_FAILED,

    /**
     * The capture session exceeded the 90-second safety limit defined in business rules.
     */
    TIMEOUT,
}
