package com.koru.data.repository

import com.koru.domain.model.TranscriptionState
import com.koru.domain.model.VoiceError
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import platform.AVFAudio.AVAudioEngine
import platform.Speech.SFSpeechAudioBufferRecognitionRequest
import platform.Speech.SFSpeechRecognitionTask
import platform.Speech.SFSpeechRecognizer

/**
 * iOS implementation of [NativeAudioRecorder] using [SFSpeechRecognizer].
 *
 * Configures the [AVAudioEngine] and [SFSpeechAudioBufferRecognitionRequest]
 * to process audio buffers locally on the device.
 */
internal actual class NativeAudioRecorder {
    private var audioEngine: AVAudioEngine? = null
    private var recognitionRequest: SFSpeechAudioBufferRecognitionRequest? = null

    actual val transcriptionFlow: Flow<TranscriptionState> =
        callbackFlow {
            val recognizer = SFSpeechRecognizer()

            if (recognizer == null || !recognizer.isAvailable()) {
                trySend(TranscriptionState.Error(VoiceError.HARDWARE_UNAVAILABLE))
                close()
                return@callbackFlow
            }

            val engine = AVAudioEngine()
            audioEngine = engine
            val request = SFSpeechAudioBufferRecognitionRequest()
            recognitionRequest = request
            request.shouldReportPartialResults = true

            // Enforce on-device recognition for privacy
            request.requiresOnDeviceRecognition = true

            val task: SFSpeechRecognitionTask =
                recognizer.recognitionTaskWithRequest(request) { result, error ->
                    if (error != null) {
                        trySend(TranscriptionState.Error(VoiceError.RECOGNITION_FAILED))
                        return@recognitionTaskWithRequest
                    }

                    result?.let {
                        trySend(
                            TranscriptionState.Result(
                                text = it.bestTranscription().formattedString(),
                                isFinal = it.isFinal(),
                            ),
                        )
                    }
                }

            trySend(TranscriptionState.Listening)

            awaitClose {
                engine.stop()
                engine.inputNode().removeTapOnBus(0UL)
                request.endAudio()
                task.cancel()
                audioEngine = null
                recognitionRequest = null
            }
        }

    actual fun stop() {
        recognitionRequest?.endAudio()
        audioEngine?.stop()
    }
}
