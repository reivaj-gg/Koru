package com.koru.domain.usecase

import com.koru.domain.model.TranscriptionState
import com.koru.domain.repository.VoiceCaptureRepository
import kotlinx.coroutines.flow.Flow

/**
 * Domain orchestrator for voice capture.
 *
 * Encapsulates the logic of initiating capture by delegating to the
 * corresponding repository injected via Koin.
 *
 * @param repository The [VoiceCaptureRepository] implementation in the Data layer.
 */
class StartVoiceCaptureUseCase(
    private val repository: VoiceCaptureRepository,
) {
    /**
     * Starts the voice capture and recognition flow.
     *
     * @return A stream of [TranscriptionState] with real-time updates.
     */
    operator fun invoke(): Flow<TranscriptionState> = repository.startCapture()
}
