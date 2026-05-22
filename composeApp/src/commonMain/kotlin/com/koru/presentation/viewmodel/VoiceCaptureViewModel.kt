package com.koru.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.koru.domain.model.TranscriptionState
import com.koru.domain.model.VoiceError
import com.koru.domain.repository.PermissionHelper
import com.koru.domain.usecase.SaveTraceUseCase
import com.koru.domain.usecase.StartVoiceCaptureUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Immutable snapshot of the Voice Capture UI state.
 *
 * @param transcription The current text displayed to the user during capture.
 * @param isRecording Flag indicating if the "breathing circle" should be active.
 * @param activeError The current error to display, if any.
 * @param amplitude Real-time audio volume level (0.0 to 1.0) for circle animations.
 * @param isSaving True while [SaveTraceUseCase] is running — prevents double-submit.
 */
data class VoiceCaptureUiState(
    val transcription: String = "",
    val isRecording: Boolean = false,
    val activeError: VoiceError? = null,
    val amplitude: Float = 0f,
    val isSaving: Boolean = false,
)

/**
 * User-initiated actions for the voice capture flow.
 */
sealed class VoiceIntent {
    /**
     * Triggered when the user starts holding the breathing circle.
     */
    data object StartRecording : VoiceIntent()

    /**
     * Triggered when the user releases the breathing circle.
     */
    data object StopRecording : VoiceIntent()

    /**
     * Triggered when the user confirms the final transcribed text to be saved as a Trace.
     *
     * @param text The final version of the transcription after user review.
     */
    data class SaveTranscription(val text: String) : VoiceIntent()
}

/**
 * One-time side effects emitted by the ViewModel during capture.
 */
sealed class VoiceEffect {
    /**
     * Instructs the UI to trigger the system permission dialog for the microphone.
     */
    data object RequestPermissions : VoiceEffect()

    /**
     * Shows a brief informative message to the user.
     *
     * @param message The localized string to be displayed.
     */
    data class ShowToast(val message: String) : VoiceEffect()

    /**
     * Instructs the UI to navigate away after a successful trace save.
     */
    data object TraceSaved : VoiceEffect()
}

/**
 * MVI ViewModel that manages the voice capture cycle.
 *
 * It orchestrates the transcription flow via [StartVoiceCaptureUseCase],
 * verifies permissions before starting, and persists traces via [SaveTraceUseCase].
 *
 * @param startVoiceCaptureUseCase The use case to initiate audio capture.
 * @param permissionHelper Hardware-agnostic helper for microphone access.
 * @param saveTraceUseCase The use case to persist a completed trace locally.
 */
class VoiceCaptureViewModel(
    private val startVoiceCaptureUseCase: StartVoiceCaptureUseCase,
    private val permissionHelper: PermissionHelper,
    private val saveTraceUseCase: SaveTraceUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(VoiceCaptureUiState())

    /**
     * Observable stream of [VoiceCaptureUiState].
     */
    val state: StateFlow<VoiceCaptureUiState> = _state.asStateFlow()

    private val _effects = MutableSharedFlow<VoiceEffect>()

    /**
     * One-time event stream for navigation or alerts.
     */
    val effects: SharedFlow<VoiceEffect> = _effects.asSharedFlow()

    private var captureJob: Job? = null

    /**
     * Entry point for all user actions on the capture screen.
     *
     * @param intent The user intent to process.
     */
    fun handleIntent(intent: VoiceIntent) {
        when (intent) {
            is VoiceIntent.StartRecording -> startRecording()
            is VoiceIntent.StopRecording -> stopRecording()
            is VoiceIntent.SaveTranscription -> save(intent.text)
        }
    }

    /**
     * Checks for permissions and begins the capture flow.
     */
    private fun startRecording() {
        viewModelScope.launch {
            if (permissionHelper.hasMicrophonePermission()) {
                beginCapture()
            } else {
                val granted = permissionHelper.requestMicrophonePermission()
                if (granted) {
                    beginCapture()
                } else {
                    _state.update { it.copy(activeError = VoiceError.PERMISSION_DENIED) }
                    _effects.emit(VoiceEffect.ShowToast("Microphone permission denied"))
                }
            }
        }
    }

    /**
     * Subscribes to the use case stream and updates the UI state.
     */
    private fun beginCapture() {
        captureJob?.cancel()
        captureJob =
            startVoiceCaptureUseCase()
                .onEach { transcriptionState ->
                    when (transcriptionState) {
                        is TranscriptionState.Idle -> {
                            _state.update { it.copy(isRecording = false) }
                        }
                        is TranscriptionState.Listening -> {
                            _state.update { it.copy(isRecording = true, activeError = null) }
                        }
                        is TranscriptionState.Result -> {
                            _state.update { it.copy(transcription = transcriptionState.text) }
                        }
                        is TranscriptionState.Error -> {
                            _state.update { it.copy(isRecording = false, activeError = transcriptionState.error) }
                        }
                    }
                }
                .launchIn(viewModelScope)
    }

    /**
     * Cancels the active capture job and resets recording state.
     */
    private fun stopRecording() {
        captureJob?.cancel()
        _state.update { it.copy(isRecording = false) }
    }

    /**
     * Persists the transcription as a [com.koru.domain.model.Trace] via [SaveTraceUseCase].
     *
     * Guards against content shorter than 3 characters (not meaningful enough to save)
     * and prevents concurrent saves with [VoiceCaptureUiState.isSaving].
     * On success, emits [VoiceEffect.TraceSaved] to trigger navigation.
     * On failure, emits [VoiceEffect.ShowToast] with the error message.
     *
     * @param text The final transcription text to persist.
     */
    private fun save(text: String) {
        if (_state.value.isSaving) return
        if (text.length < 3) {
            viewModelScope.launch {
                _effects.emit(VoiceEffect.ShowToast("Transcription too short to save"))
            }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isSaving = true) }

            saveTraceUseCase(content = text)
                .onSuccess {
                    _state.update { VoiceCaptureUiState() }
                    _effects.emit(VoiceEffect.TraceSaved)
                }
                .onFailure { error ->
                    _state.update { it.copy(isSaving = false) }
                    _effects.emit(
                        VoiceEffect.ShowToast(
                            error.message ?: "Failed to save trace",
                        ),
                    )
                }
        }
    }
}
