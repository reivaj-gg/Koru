package com.koru.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.koru.domain.model.InsightType
import com.koru.domain.usecase.GetInsightUseCase
import com.koru.domain.usecase.ObserveTracesUseCase
import com.koru.presentation.utils.TreeLayoutCalculator
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel that manages the immutable state of the HomeScreen tree.
 *
 * Exposes a strictly unidirectional state flow and processes domain data
 * through the pure [TreeLayoutCalculator], achieving zero UI/Compose
 * dependencies inside the ViewModel.
 *
 * @param observeTracesUseCase UseCase to observe user's traces.
 * @param layoutCalculator Mathematical calculator for node layout.
 */
class HomeViewModel(
    private val observeTracesUseCase: ObserveTracesUseCase,
    private val getInsightUseCase: GetInsightUseCase,
    private val layoutCalculator: TreeLayoutCalculator,
) : ViewModel() {
    private val _state = MutableStateFlow(HomeState())

    /**
     * Observable state flow for the UI. Strictly immutable.
     */
    val state: StateFlow<HomeState> = _state.asStateFlow()

    private val _effects = MutableSharedFlow<HomeEffect>()

    /**
     * One-time effects to be consumed (e.g. navigation, errors).
     */
    val effects: SharedFlow<HomeEffect> = _effects.asSharedFlow()

    /**
     * Single entry point for user-initiated actions.
     */
    fun handleIntent(intent: HomeIntent) {
        when (intent) {
            is HomeIntent.LoadTree -> loadTree()
            is HomeIntent.TapNode -> handleNodeTap(intent.traceId)
            is HomeIntent.OpenCapture -> {
                // Generally, OpenCapture is handled directly by UI navigation,
                // but it's included in MVI in case we require logging or analytics in the future.
            }
            is HomeIntent.RequestInsight -> requestInsight(intent.traceId, intent.query)
        }
    }

    private fun loadTree() {
        viewModelScope.launch {
            observeTracesUseCase()
                .onStart {
                    _state.update { it.copy(isLoading = true, error = null) }
                }
                .catch { e ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = e.message ?: "Unknown Error",
                        )
                    }
                    _effects.emit(HomeEffect.ShowError(e.message ?: "Unknown Error"))
                }
                .collect { traces ->
                    // The mathematical calculation is performed assuming a virtual logical canvas.
                    // The Compose view then scales this mathematical canvas to the actual resolution.
                    val nodes = layoutCalculator.calculateLayout(traces, canvasWidth = LOGICAL_CANVAS_WIDTH)
                    val branches = layoutCalculator.calculateBranches(nodes)
                    _state.update {
                        it.copy(
                            nodes = nodes,
                            branches = branches,
                            isLoading = false,
                            error = null,
                        )
                    }
                }
        }
    }

    private fun handleNodeTap(traceId: String) {
        viewModelScope.launch {
            _effects.emit(HomeEffect.NavigateToTraceDetail(traceId))
        }
    }

    private fun requestInsight(
        traceId: String,
        query: String,
    ) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            val result = getInsightUseCase(traceId, query, InsightType.IMMEDIATE)
            result.fold(
                onSuccess = { insight ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            latestInsight = insight,
                            error = null,
                        )
                    }
                },
                onFailure = { error ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = error.message ?: "Failed to generate insight",
                        )
                    }
                    _effects.emit(HomeEffect.ShowError(error.message ?: "Failed to generate insight"))
                },
            )
        }
    }

    companion object {
        /**
         * Width of the virtual logical canvas used for layout math.
         * The Compose view scales this to the actual screen resolution.
         */
        const val LOGICAL_CANVAS_WIDTH = 1000f
    }
}
