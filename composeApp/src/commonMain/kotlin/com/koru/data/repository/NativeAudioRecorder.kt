package com.koru.data.repository

import com.koru.domain.model.TranscriptionState
import kotlinx.coroutines.flow.Flow

/**
 * Internal hardware bridge for speech recognition.
 *
 * This class is implemented using expect/actual to access platform-specific
 * speech APIs (SpeechRecognizer on Android, SFSpeechRecognizer on iOS).
 */
internal expect class NativeAudioRecorder {
    /**
     * A cold flow that activates the microphone upon collection and emits
     * real-time transcription updates.
     */
    val transcriptionFlow: Flow<TranscriptionState>

    /**
     * Explicitly stops the current recognition session and releases hardware.
     */
    fun stop()
}
