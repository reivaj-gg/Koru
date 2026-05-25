package com.koru.data.repository

import com.koru.domain.model.TranscriptionState
import com.koru.domain.repository.VoiceCaptureRepository
import kotlinx.coroutines.flow.Flow

/**
 * Implementation of [VoiceCaptureRepository] that bridges to platform-specific
 * hardware via [NativeAudioRecorder].
 *
 * @param nativeRecorder The platform-specific hardware bridge.
 */
internal class VoiceCaptureRepositoryImpl(
    private val nativeRecorder: NativeAudioRecorder,
) : VoiceCaptureRepository {
    override fun startCapture(): Flow<TranscriptionState> = nativeRecorder.transcriptionFlow

    override fun stopCapture() = nativeRecorder.stop()
}
