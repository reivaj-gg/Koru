package com.koru.data.repository

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import com.koru.domain.model.TranscriptionState
import com.koru.domain.model.VoiceError
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

/**
 * Android implementation of [NativeAudioRecorder] using [SpeechRecognizer].
 *
 * It uses the system's speech recognition engine and enforces on-device
 * recognition for privacy.
 *
 * @param context The application context required to initialize SpeechRecognizer.
 */
internal actual class NativeAudioRecorder(
    private val context: Context,
) {
    private var speechRecognizer: SpeechRecognizer? = null

    actual val transcriptionFlow: Flow<TranscriptionState> =
        callbackFlow {
            if (!SpeechRecognizer.isRecognitionAvailable(context)) {
                trySend(TranscriptionState.Error(VoiceError.HARDWARE_UNAVAILABLE))
                close()
                return@callbackFlow
            }

            val recognizer = SpeechRecognizer.createSpeechRecognizer(context)
            speechRecognizer = recognizer

            val listener =
                object : RecognitionListener {
                    override fun onReadyForSpeech(params: Bundle?) {
                        trySend(TranscriptionState.Listening)
                    }

                    override fun onBeginningOfSpeech() {}

                    override fun onRmsChanged(rmsdB: Float) {}

                    override fun onBufferReceived(buffer: ByteArray?) {}

                    override fun onEndOfSpeech() {}

                    override fun onError(error: Int) {
                        val voiceError =
                            when (error) {
                                SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> VoiceError.PERMISSION_DENIED
                                SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> VoiceError.HARDWARE_UNAVAILABLE
                                SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> VoiceError.TIMEOUT
                                else -> VoiceError.RECOGNITION_FAILED
                            }
                        trySend(TranscriptionState.Error(voiceError))
                    }

                    override fun onResults(results: Bundle?) {
                        val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        val text = matches?.firstOrNull() ?: ""
                        trySend(TranscriptionState.Result(text = text, isFinal = true))
                    }

                    override fun onPartialResults(partialResults: Bundle?) {
                        val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        val text = matches?.firstOrNull() ?: ""
                        trySend(TranscriptionState.Result(text = text, isFinal = false))
                    }

                    override fun onEvent(
                        eventType: Int,
                        params: Bundle?,
                    ) {}
                }

            recognizer.setRecognitionListener(listener)

            val intent =
                Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                    putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                    // Enforce on-device recognition for privacy
                    putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE, true)
                }

            recognizer.startListening(intent)

            awaitClose {
                recognizer.stopListening()
                recognizer.destroy()
                speechRecognizer = null
            }
        }

    actual fun stop() {
        speechRecognizer?.stopListening()
    }
}
