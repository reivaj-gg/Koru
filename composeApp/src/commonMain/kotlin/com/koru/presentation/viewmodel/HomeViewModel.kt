package com.koru.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.koru.domain.usecase.SaveTraceUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel responsible for managing the state of the Home Screen and Trace Capture flow.
 * Implements an MVI pattern with [HomeState], [HomeIntent], and [HomeEffect].
 *
 * @property saveTraceUseCase Use case for persisting traces.
 */
class HomeViewModel(
    private val saveTraceUseCase: SaveTraceUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<HomeEffect>()
    val effect: SharedFlow<HomeEffect> = _effect.asSharedFlow()

    /**
     * Processes incoming user intents and triggers corresponding state changes or side effects.
     *
     * @param intent The user-initiated action.
     */
    fun dispatch(intent: HomeIntent) {
        when (intent) {
            is HomeIntent.OpenCapture -> {
                _state.update { it.copy(isCapturing = true) }
            }
            is HomeIntent.CloseCapture -> {
                _state.update { it.copy(isCapturing = false) }
            }
            is HomeIntent.SaveTrace -> {
                saveTrace(intent.content)
            }
        }
    }

    private fun saveTrace(content: String) {
        _state.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            val result = saveTraceUseCase(content)
            result.onSuccess {
                _state.update { it.copy(isLoading = false, isCapturing = false) }
                _effect.emit(HomeEffect.TraceSaved)
            }.onFailure { error ->
                _state.update { it.copy(isLoading = false, error = error.message ?: "Failed to save trace") }
                _effect.emit(HomeEffect.ShowError(error.message ?: "Failed to save trace"))
            }
        }
    }
}
