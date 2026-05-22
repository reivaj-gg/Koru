package com.koru.presentation.viewmodel

import com.koru.presentation.model.VisualNode

/**
 * Estado inmutable de la pantalla principal.
 *
 * Utiliza exclusivamente colecciones inmutables (implementadas aquí con [List] estándar de Kotlin)
 * y tipos primitivos inmutables mediante [val].
 *
 * @property nodes Lista inmutable de nodos procesados matemáticamente listos para dibujar.
 * @property isLoading Indica si el árbol está en proceso de carga o análisis.
 * @property error Mensaje de error a desplegar si ocurre una falla al cargar los datos.
 */
data class TreeState(
    val nodes: List<VisualNode> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
)

/**
 * Intenciones exclusivas que el usuario puede despachar desde la HomeScreen.
 */
sealed class HomeIntent {
    /**
     * Se dispara al tocar un nodo específico calculado matemáticamente por el hit-testing.
     *
     * @property traceId El identificador del trazo impactado por el usuario.
     */
    data class TapNode(
        val traceId: String,
    ) : HomeIntent()

    /**
     * Se dispara al pedir iniciar la captura manual o por voz.
     */
    data object OpenCapture : HomeIntent()

    /**
     * Se dispara cuando la pantalla necesita inicializarse o recargar los datos de la base.
     */
    data object LoadTree : HomeIntent()
}

/**
 * Efectos secundarios inmutables que la UI debe consumir exactamente una vez.
 */
sealed class HomeEffect {
    /**
     * Instruye a la UI para navegar a la pantalla de detalle de un nodo en particular.
     *
     * @property traceId El id del trazo a examinar.
     */
    data class NavigateToTraceDetail(
        val traceId: String,
    ) : HomeEffect()

    /**
     * Despliega un aviso temporal o un banner detallando un problema no fatal.
     *
     * @property message El texto localizado o directo del error.
     */
    data class ShowError(
        val message: String,
    ) : HomeEffect()
}
