package com.koru.domain.repository

import com.koru.domain.model.TranscriptionState
import kotlinx.coroutines.flow.Flow

/**
 * Contract for platform-specific speech recognition and audio capturing.
 *
 * Implementations in the Data layer must enforce on-device recognition to comply
 * with Koru's privacy and offline-first mandates.
 */
interface VoiceCaptureRepository {
    /**
     * Activates the audio hardware and returns a stream of [TranscriptionState].
     *
     * Implementation Rule: The microphone must be released automatically when
     * the [Flow] collection is cancelled (via CoroutineScope cleanup).
     *
     * @return A cold [Flow] that encapsulates the recognition lifecycle.
     */
    fun startCapture(): Flow<TranscriptionState>

    /**
     * Explicitly terminates the current recording session.
     */
    fun stopCapture()
}
