package com.koru.domain.model

/**
 * Represents the exhaustive set of states during a voice transcription session.
 * Used by the [com.koru.domain.repository.VoiceCaptureRepository] to communicate with the Domain layer.
 */
sealed class TranscriptionState {
    /**
     * The initial state where the system is ready, but the microphone is inactive.
     */
    data object Idle : TranscriptionState()

    /**
     * The microphone is active, and the system is awaiting or receiving audio input.
     */
    data object Listening : TranscriptionState()

    /**
     * Emitted whenever the recognition engine produces a text update.
     *
     * @param text The partial or final string recognized from the audio stream.
     * @param isFinal True if the engine has reached a definitive conclusion for this segment.
     */
    data class Result(
        val text: String,
        val isFinal: Boolean,
    ) : TranscriptionState()

    /**
     * Emitted when a terminal failure occurs in the recognition process.
     *
     * @param error The specific [VoiceError] describing the failure.
     */
    data class Error(val error: VoiceError) : TranscriptionState()
}
