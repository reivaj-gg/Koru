package com.koru.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.koru.domain.repository.TraceRepository
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
 * ViewModel que gestiona el estado inmutable del árbol de la HomeScreen.
 *
 * Expone un flujo de estado estrictamente unidireccional y procesa la data
 * del dominio a través del [TreeLayoutCalculator] puro, logrando cero dependencias
 * de UI/Compose dentro del ViewModel.
 *
 * @param traceRepository Repositorio para acceder a los trazos del usuario.
 * @param layoutCalculator Calculador matemático para el layout de los nodos.
 */
class HomeViewModel(
    private val traceRepository: TraceRepository,
    private val layoutCalculator: TreeLayoutCalculator,
) : ViewModel() {
    private val _state = MutableStateFlow(TreeState())

    /**
     * Flujo de estado observabable por la UI. Estrictamente inmutable.
     */
    val state: StateFlow<TreeState> = _state.asStateFlow()

    private val _effects = MutableSharedFlow<HomeEffect>()

    /**
     * Efectos únicos para ser consumidos (ej. navegación, errores).
     */
    val effects: SharedFlow<HomeEffect> = _effects.asSharedFlow()

    /**
     * Único punto de entrada para acciones iniciadas por el usuario.
     */
    fun handleIntent(intent: HomeIntent) {
        when (intent) {
            is HomeIntent.LoadTree -> loadTree()
            is HomeIntent.TapNode -> handleNodeTap(intent.traceId)
            is HomeIntent.OpenCapture -> {
                // Generalmente, OpenCapture lo resuelve la navegación de la UI directamente,
                // pero se incluye en MVI por si en el futuro requerimos logging o análisis.
            }
        }
    }

    private fun loadTree() {
        viewModelScope.launch {
            traceRepository.observeAll()
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
                    // El cálculo matemático se realiza asumiendo un lienzo lógico virtual.
                    // La vista de Compose luego escala este canvas matemático a la resolución real.
                    val nodes = layoutCalculator.calculateLayout(traces, canvasWidth = 1000f)
                    _state.update {
                        it.copy(
                            nodes = nodes,
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
}
