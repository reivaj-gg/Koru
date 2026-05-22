package com.koru.presentation.model

/**
 * Representa la posición visual bidimensional absoluta de un nodo en el Canvas.
 *
 * Esta clase es puramente matemática y no tiene dependencias del framework de UI.
 *
 * @property x La coordenada horizontal.
 * @property y La coordenada vertical.
 */
data class NodePosition(
    val x: Float,
    val y: Float,
)
